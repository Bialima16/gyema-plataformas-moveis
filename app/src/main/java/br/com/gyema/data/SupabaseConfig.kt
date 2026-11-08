package br.com.gyema.data

import br.com.gyema.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject

/**
 * Ponto único de conexão com o Supabase. Use em qualquer parte do app:
 *
 *   Supabase.cliente.auth.signInWith(Email) { ... }        // login, cadastro, senha
 *   Supabase.cliente.from("equipamentos").select()...       // tabelas e views
 *
 * A URL e a chave vêm do arquivo local.properties (não vai para o GitHub):
 *   SUPABASE_URL=https://xxxx.supabase.co
 *   SUPABASE_KEY=sb_publishable_...
 *
 * As tabelas, colunas e regras de acesso estão descritas em supabase/README.md.
 */
object Supabase {

    /** false quando o local.properties ainda não tem a URL e a chave. */
    val configurado: Boolean
        get() = BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_KEY.isNotBlank()

    val cliente: SupabaseClient by lazy {
        check(configurado) {
            "Supabase não configurado: preencha SUPABASE_URL e SUPABASE_KEY no local.properties."
        }
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth)      // login, cadastro e sessão (fica salva no aparelho)
            install(Postgrest) // tabelas e views do banco
        }
    }

    /**
     * Confere se o app alcança o banco: lê as perguntas do teste de prontidão,
     * que podem ser lidas mesmo sem login. Retorna quantas vieram (o esperado é 10).
     */
    suspend fun testarConexao(): Int =
        cliente.from("perguntas_prontidao").select().decodeList<JsonObject>().size
}
