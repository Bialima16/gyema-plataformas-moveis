package br.com.gyema.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.HttpRequestException
import kotlinx.coroutines.CancellationException
import java.io.IOException

// Utilitários usados por todos os repositórios (instrutor, comunidade...).

/** Erro mostrado quando a tela precisa de alguém logado. */
class SemSessaoException(mensagem: String = "Entre na sua conta para continuar.") : Exception(mensagem)

/** Id do usuário logado. Sem sessão, lança [SemSessaoException] com a [mensagem] dada. */
fun usuarioLogadoId(mensagem: String = "Entre na sua conta para continuar."): String =
    Supabase.cliente.auth.currentUserOrNull()?.id ?: throw SemSessaoException(mensagem)

/** Executa [bloco] devolvendo sucesso ou falha, sem engolir o cancelamento da tela. */
suspend fun <T> tentar(bloco: suspend () -> T): Result<T> =
    try {
        Result.success(bloco())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

/** Texto amigável para mostrar na tela a partir de um erro do Supabase. */
fun mensagemDeErro(e: Throwable): String {
    val texto = e.message.orEmpty()
    return when {
        !Supabase.configurado ->
            "O banco não está configurado. Preencha SUPABASE_URL e SUPABASE_KEY no local.properties."
        e is SemSessaoException -> e.message!!
        e is HttpRequestException || e is IOException ->
            "Sem conexão com o servidor. Verifique a internet e tente novamente."
        "duplicate key" in texto && "codigo" in texto ->
            "Já existe um equipamento com essa identificação."
        "duplicate key" in texto -> "Esse registro já existe."
        "row-level security" in texto || "permission denied" in texto ->
            "Sua conta não tem permissão para fazer isso."
        else -> "Não foi possível concluir. Tente novamente."
    }
}
