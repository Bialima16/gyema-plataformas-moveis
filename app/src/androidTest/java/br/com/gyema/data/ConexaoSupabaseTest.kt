package br.com.gyema.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Teste da conexão com o Supabase.
 * Como rodar: abra o emulador, clique com o botão direito neste arquivo → Run 'ConexaoSupabaseTest'.
 * Precisa de SUPABASE_URL e SUPABASE_KEY no local.properties e de internet no emulador.
 */
@RunWith(AndroidJUnit4::class)
class ConexaoSupabaseTest {

    @Test
    fun localPropertiesConfigurado() {
        assertTrue(
            "Preencha SUPABASE_URL e SUPABASE_KEY no local.properties e sincronize o Gradle.",
            Supabase.configurado
        )
    }

    @Test
    fun bancoRespondeComAsPerguntasDeProntidao() = runBlocking {
        assertEquals("Quantidade de perguntas lidas do banco", 10, Supabase.testarConexao())
    }
}
