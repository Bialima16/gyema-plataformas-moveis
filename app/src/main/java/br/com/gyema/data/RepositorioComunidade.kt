package br.com.gyema.data

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// =====================================================================
// Modelos da Comunidade (feed, publicação, grupos).
// O nome de outras pessoas vem de feed_posts e perfis_publicos, que mostram
// só nome, curso e perfil (a tabela perfis fica fechada para alunos).
// =====================================================================

@Serializable
data class Post(
    val id: Long,
    @SerialName("autor_id") val autorId: String,
    @SerialName("autor_nome") val autorNome: String,
    val categoria: String? = null,
    val texto: String,
    @SerialName("criado_em") val criadoEm: String,
    val curtidas: Int,
    val comentarios: Int,
    @SerialName("curtido_por_mim") val curtidoPorMim: Boolean
)

@Serializable
private data class LinhaComentario(
    val id: Long,
    @SerialName("autor_id") val autorId: String,
    val texto: String,
    @SerialName("criado_em") val criadoEm: String
)

data class Comentario(
    val id: Long,
    val autorId: String,
    val autorNome: String,
    val texto: String,
    val criadoEm: String
)

@Serializable
private data class PerfilPublico(val id: String, val nome: String)

@Serializable
private data class SoPerfil(val perfil: String)

@Serializable
private data class LinhaGrupo(
    val id: Long,
    val nome: String,
    val descricao: String,
    val privacidade: String, // "publico" ou "privado"
    @SerialName("criador_id") val criadorId: String
)

@Serializable
private data class Membro(
    @SerialName("grupo_id") val grupoId: Long,
    @SerialName("usuario_id") val usuarioId: String
)

data class Grupo(
    val id: Long,
    val nome: String,
    val descricao: String,
    val publico: Boolean,
    val membros: Int,
    val souMembro: Boolean,
    val souCriador: Boolean
)

// Linhas enviadas nos inserts
@Serializable
private data class NovoPost(
    @SerialName("autor_id") val autorId: String,
    val categoria: String?,
    val texto: String
)

@Serializable
private data class NovaCurtida(
    @SerialName("post_id") val postId: Long,
    @SerialName("usuario_id") val usuarioId: String
)

@Serializable
private data class NovoComentario(
    @SerialName("post_id") val postId: Long,
    @SerialName("autor_id") val autorId: String,
    val texto: String
)

@Serializable
private data class NovoGrupo(
    val nome: String,
    val descricao: String,
    val privacidade: String,
    @SerialName("criador_id") val criadorId: String
)

// =====================================================================
// Consultas e gravações. As regras (quem pode apagar, entrar em grupo
// privado etc.) são garantidas pelo RLS no banco.
// =====================================================================
object RepositorioComunidade {

    private val db get() = Supabase.cliente

    private const val COLUNAS_POST =
        "id, autor_id, autor_nome, categoria, texto, criado_em, curtidas, comentarios, curtido_por_mim"

    /** Id de quem está logado (a tela usa para saber o que é "meu"). */
    fun meuId(): String = usuarioLogadoId()

    /** Instrutores podem apagar publicações e comentários de qualquer pessoa. */
    suspend fun souInstrutor(): Boolean =
        db.from("perfis").select(Columns.list("perfil")) {
            filter { eq("id", usuarioLogadoId()) }
        }.decodeSingleOrNull<SoPerfil>()?.perfil == "instrutor"

    // ---------- Feed ----------

    /** Publicações mais recentes. Com [soMinhas], só as do usuário logado. */
    suspend fun listarFeed(soMinhas: Boolean = false): List<Post> {
        val eu = usuarioLogadoId()
        return db.from("feed_posts").select(Columns.raw(COLUNAS_POST)) {
            if (soMinhas) filter { eq("autor_id", eu) }
            order("criado_em", Order.DESCENDING)
            limit(50)
        }.decodeList()
    }

    suspend fun buscarPost(id: Long): Post =
        db.from("feed_posts").select(Columns.raw(COLUNAS_POST)) {
            filter { eq("id", id) }
        }.decodeSingle()

    suspend fun publicar(texto: String, categoria: String?) {
        db.from("posts").insert(NovoPost(usuarioLogadoId(), categoria, texto))
    }

    suspend fun excluirPost(id: Long) {
        db.from("posts").delete { filter { eq("id", id) } }
    }

    suspend fun curtir(postId: Long) {
        db.from("curtidas").insert(NovaCurtida(postId, usuarioLogadoId()))
    }

    suspend fun descurtir(postId: Long) {
        val eu = usuarioLogadoId()
        db.from("curtidas").delete {
            filter {
                eq("post_id", postId)
                eq("usuario_id", eu)
            }
        }
    }

    // ---------- Comentários ----------

    suspend fun listarComentarios(postId: Long): List<Comentario> {
        val linhas = db.from("comentarios")
            .select(Columns.list("id", "autor_id", "texto", "criado_em")) {
                filter { eq("post_id", postId) }
                order("criado_em", Order.ASCENDING)
            }.decodeList<LinhaComentario>()
        if (linhas.isEmpty()) return emptyList()

        val nomes = nomesDosUsuarios(linhas.map { it.autorId }.distinct())
        return linhas.map {
            Comentario(it.id, it.autorId, nomes[it.autorId] ?: "Usuário", it.texto, it.criadoEm)
        }
    }

    suspend fun comentar(postId: Long, texto: String) {
        db.from("comentarios").insert(NovoComentario(postId, usuarioLogadoId(), texto))
    }

    suspend fun excluirComentario(id: Long) {
        db.from("comentarios").delete { filter { eq("id", id) } }
    }

    private suspend fun nomesDosUsuarios(ids: List<String>): Map<String, String> =
        db.from("perfis_publicos").select(Columns.list("id", "nome")) {
            filter { isIn("id", ids) }
        }.decodeList<PerfilPublico>().associate { it.id to it.nome }

    // ---------- Grupos ----------

    /** Grupos que o usuário pode ver: os públicos e os privados de que participa. */
    suspend fun listarGrupos(): List<Grupo> {
        val eu = usuarioLogadoId()
        val grupos = db.from("grupos")
            .select(Columns.list("id", "nome", "descricao", "privacidade", "criador_id")) {
                order("nome", Order.ASCENDING)
            }.decodeList<LinhaGrupo>()
        if (grupos.isEmpty()) return emptyList()

        val membros = db.from("membros_grupo").select(Columns.list("grupo_id", "usuario_id")) {
            filter { isIn("grupo_id", grupos.map { it.id }) }
        }.decodeList<Membro>()
        val porGrupo = membros.groupBy { it.grupoId }

        return grupos.map { g ->
            val lista = porGrupo[g.id].orEmpty()
            Grupo(
                id = g.id,
                nome = g.nome,
                descricao = g.descricao,
                publico = g.privacidade == "publico",
                membros = lista.size,
                souMembro = lista.any { it.usuarioId == eu },
                souCriador = g.criadorId == eu
            )
        }
    }

    /** Cria o grupo. Quem cria entra como membro automaticamente (trigger no banco). */
    suspend fun criarGrupo(nome: String, descricao: String, publico: Boolean) {
        db.from("grupos").insert(
            NovoGrupo(nome, descricao, if (publico) "publico" else "privado", usuarioLogadoId())
        )
    }

    suspend fun entrarNoGrupo(grupoId: Long) {
        db.from("membros_grupo").insert(Membro(grupoId, usuarioLogadoId()))
    }

    suspend fun sairDoGrupo(grupoId: Long) {
        val eu = usuarioLogadoId()
        db.from("membros_grupo").delete {
            filter {
                eq("grupo_id", grupoId)
                eq("usuario_id", eu)
            }
        }
    }
}
