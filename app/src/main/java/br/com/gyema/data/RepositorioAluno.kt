package br.com.gyema.data

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.OffsetDateTime

@Serializable
data class PerfilAluno(
    val id: String,
    val nome: String,
    val email: String,
    val matricula: String? = null,
    val cpf: String? = null,
    val curso: String? = null,
    @SerialName("data_nascimento") val dataNascimento: String? = null,
    val genero: String? = null,
    @SerialName("peso_kg") val pesoKg: Double? = null,
    @SerialName("altura_cm") val alturaCm: Double? = null,
    @SerialName("contato_emergencia") val contatoEmergencia: String? = null
)

@Serializable
data class EdicaoPerfilAluno(
    val nome: String,
    val matricula: String?,
    val cpf: String?,
    val curso: String?,
    @SerialName("data_nascimento") val dataNascimento: String?,
    val genero: String?,
    @SerialName("peso_kg") val pesoKg: Double?,
    @SerialName("altura_cm") val alturaCm: Double?,
    @SerialName("contato_emergencia") val contatoEmergencia: String?
)

@Serializable
data class SetorOcupacao(
    val id: Int,
    val nome: String,
    val icone: String? = null,
    val status: String,
    @SerialName("total_equipamentos") val totalEquipamentos: Int,
    val disponiveis: Int,
    val ocupados: Int,
    @SerialName("em_manutencao_qtd") val emManutencao: Int
)

@Serializable
data class EquipamentoAluno(
    val id: Int,
    val codigo: String,
    val nome: String,
    @SerialName("setor_id") val setorId: Int,
    @SerialName("setor_nome") val setorNome: String,
    @SerialName("grupo_muscular") val grupoMuscular: String? = null,
    @SerialName("tempo_maximo_min") val tempoMaximoMin: Int,
    val status: String
)

@Serializable
data class NomeSetorAluno(val nome: String)

@Serializable
data class DetalheEquipamentoAluno(
    val id: Int,
    val codigo: String,
    val nome: String,
    @SerialName("grupo_muscular") val grupoMuscular: String? = null,
    @SerialName("capacidade_simultanea") val capacidadeSimultanea: Int,
    @SerialName("tempo_maximo_min") val tempoMaximoMin: Int,
    val status: String,
    @SerialName("instrucoes_uso") val instrucoesUso: String? = null,
    @SerialName("video_url") val videoUrl: String? = null,
    val setores: NomeSetorAluno,
    @SerialName("equipamento_exercicios") val exercicios: List<VinculoExercicio> = emptyList()
)

@Serializable
data class EquipamentoDaUtilizacao(
    val id: Int,
    val nome: String,
    val codigo: String,
    @SerialName("grupo_muscular") val grupoMuscular: String? = null,
    val setores: NomeSetorAluno
)

@Serializable
data class UtilizacaoAtiva(
    val id: Long,
    val inicio: String,
    @SerialName("fim_previsto") val fimPrevisto: String,
    val equipamentos: EquipamentoDaUtilizacao
)

@Serializable
data class NotificacaoAluno(
    val id: String,
    val tipo: String,
    val titulo: String,
    val mensagem: String,
    @SerialName("criado_em") val criadoEm: String
)

@Serializable
data class ExecucaoAluno(
    val id: Long,
    val inicio: String,
    val fim: String? = null
)

@Serializable
data class CargaMensalAluno(
    val mes: String,
    @SerialName("carga_max_kg") val cargaMaxKg: Double? = null,
    @SerialName("volume_kg") val volumeKg: Double? = null
)

@Serializable
data class IdItemAluno(val id: Long)

@Serializable
data class FichaAluno(
    val id: Long,
    val nome: String,
    @SerialName("grupo_foco") val grupoFoco: String? = null,
    val ordem: Int,
    @SerialName("itens_ficha") val itens: List<IdItemAluno> = emptyList()
)

@Serializable
data class ItemFichaAluno(
    val id: Long,
    val ordem: Int,
    val series: Int,
    val repeticoes: Int,
    @SerialName("carga_kg") val cargaKg: Double? = null,
    @SerialName("descanso_seg") val descansoSeg: Int? = null,
    val observacoes: String? = null,
    val tecnica: String? = null,
    val exercicios: Exercicio
)

@Serializable
data class FichaDetalheAluno(
    val id: Long,
    val nome: String,
    @SerialName("grupo_foco") val grupoFoco: String? = null,
    @SerialName("itens_ficha") val itens: List<ItemFichaAluno> = emptyList()
)

@Serializable
data class ExecucaoIniciada(val id: Long, val inicio: String)

@Serializable
data class SerieExecutadaAluno(
    @SerialName("numero_serie") val numeroSerie: Int,
    val repeticoes: Int,
    @SerialName("carga_kg") val cargaKg: Double? = null,
    val exercicios: NomeExercicio
)

@Serializable
data class ResumoExecucaoAluno(
    val id: Long,
    val inicio: String,
    val fim: String? = null,
    @SerialName("fichas_treino") val ficha: FichaNomeFoco? = null,
    @SerialName("series_executadas") val series: List<SerieExecutadaAluno> = emptyList()
)

@Serializable
private data class AlunoNovaUtilizacao(
    @SerialName("equipamento_id") val equipamentoId: Int,
    @SerialName("usuario_id") val usuarioId: String
)

@Serializable
private data class AlunoNovoRelato(
    @SerialName("equipamento_id") val equipamentoId: Int,
    @SerialName("usuario_id") val usuarioId: String,
    val categoria: String,
    val descricao: String
)

@Serializable
private data class AlunoNovaExecucao(
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("ficha_id") val fichaId: Long
)

@Serializable
private data class AlunoNovaSerie(
    @SerialName("execucao_id") val execucaoId: Long,
    @SerialName("exercicio_id") val exercicioId: Int,
    @SerialName("numero_serie") val numeroSerie: Int,
    val repeticoes: Int,
    @SerialName("carga_kg") val cargaKg: Double?
)

@Serializable
private data class AlunoUltimaFicha(@SerialName("ficha_id") val fichaId: Long? = null)

object RepositorioAluno {

    private val db get() = Supabase.cliente

    private const val COLUNAS_UTILIZACAO =
        "id, inicio, fim_previsto, equipamentos(id, nome, codigo, grupo_muscular, setores(nome))"

    private val COLUNAS_EQUIPAMENTO =
        Columns.list("id", "codigo", "nome", "setor_id", "setor_nome", "grupo_muscular", "tempo_maximo_min", "status")

    suspend fun meuPerfil(): PerfilAluno =
        db.from("perfis").select(
            Columns.list(
                "id", "nome", "email", "matricula", "cpf", "curso", "data_nascimento",
                "genero", "peso_kg", "altura_cm", "contato_emergencia"
            )
        ) {
            filter { eq("id", usuarioLogadoId()) }
        }.decodeSingle()

    suspend fun salvarPerfil(dados: EdicaoPerfilAluno): PerfilAluno {
        db.from("perfis").update(dados) {
            filter { eq("id", usuarioLogadoId()) }
        }
        return meuPerfil()
    }

    suspend fun minhasExecucoes(): List<ExecucaoAluno> =
        db.from("execucoes_treino").select(Columns.list("id", "inicio", "fim")) {
            filter { eq("usuario_id", usuarioLogadoId()) }
            order("inicio", Order.DESCENDING)
        }.decodeList()

    suspend fun minhasCargasMensais(): List<CargaMensalAluno> =
        db.from("evolucao_carga_mensal").select(Columns.list("mes", "carga_max_kg", "volume_kg")) {
            filter { eq("usuario_id", usuarioLogadoId()) }
            order("mes", Order.DESCENDING)
        }.decodeList()

    suspend fun minhasNotificacoes(): List<NotificacaoAluno> =
        db.from("notificacoes").select(Columns.list("id", "tipo", "titulo", "mensagem", "criado_em")) {
            filter { eq("usuario_id", usuarioLogadoId()) }
            order("criado_em", Order.DESCENDING)
            limit(30)
        }.decodeList()

    suspend fun ocupacaoSetores(): List<SetorOcupacao> {
        usuarioLogadoId()
        encerrarUsosVencidos()
        return db.from("ocupacao_setores").select(
            Columns.list(
                "id", "nome", "icone", "status", "total_equipamentos",
                "disponiveis", "ocupados", "em_manutencao_qtd"
            )
        ) { order("id", Order.ASCENDING) }.decodeList()
    }

    suspend fun listarEquipamentos(setorId: Int? = null): List<EquipamentoAluno> {
        usuarioLogadoId()
        encerrarUsosVencidos()
        return db.from("equipamentos_detalhados").select(COLUNAS_EQUIPAMENTO) {
            if (setorId != null) filter { eq("setor_id", setorId) }
            order("nome", Order.ASCENDING)
        }.decodeList()
    }

    suspend fun buscarEquipamento(id: Int): DetalheEquipamentoAluno {
        usuarioLogadoId()
        encerrarUsosVencidos()
        return db.from("equipamentos").select(
            Columns.raw(
                "id, codigo, nome, grupo_muscular, capacidade_simultanea, tempo_maximo_min, status, " +
                    "instrucoes_uso, video_url, setores(nome), " +
                    "equipamento_exercicios(tipo, exercicios(id, nome, grupo_muscular))"
            )
        ) { filter { eq("id", id) } }.decodeSingle()
    }

    suspend fun buscarPorCodigo(codigo: String): EquipamentoAluno? {
        usuarioLogadoId()
        return db.from("equipamentos_detalhados").select(COLUNAS_EQUIPAMENTO) {
            filter { eq("codigo", codigo.trim().uppercase()) }
        }.decodeList<EquipamentoAluno>().firstOrNull()
    }

    suspend fun buscarEquipamentoResumo(id: Int): EquipamentoAluno {
        usuarioLogadoId()
        encerrarUsosVencidos()
        return db.from("equipamentos_detalhados").select(COLUNAS_EQUIPAMENTO) {
            filter { eq("id", id) }
        }.decodeSingle()
    }

    suspend fun minhaUtilizacaoAtiva(): UtilizacaoAtiva? {
        val eu = usuarioLogadoId()
        encerrarUsosVencidos()
        return db.from("utilizacoes").select(Columns.raw(COLUNAS_UTILIZACAO)) {
            filter {
                eq("usuario_id", eu)
                exact("fim", null)
            }
        }.decodeList<UtilizacaoAtiva>().firstOrNull()
    }

    suspend fun iniciarUso(equipamentoId: Int): UtilizacaoAtiva {
        val eu = usuarioLogadoId()
        return db.from("utilizacoes")
            .insert(AlunoNovaUtilizacao(equipamentoId, eu)) {
                select(Columns.raw(COLUNAS_UTILIZACAO))
            }.decodeSingle()
    }

    suspend fun finalizarUso(uso: UtilizacaoAtiva) {
        val inicio = OffsetDateTime.parse(uso.inicio).toInstant()
        val fim = maxOf(Instant.now(), inicio)
        db.from("utilizacoes").update({
            set("fim", fim.toString())
        }) {
            filter { eq("id", uso.id) }
        }
    }

    suspend fun enviarRelato(equipamentoId: Int, categoria: String, descricao: String) {
        db.from("relatos_problema").insert(
            AlunoNovoRelato(equipamentoId, usuarioLogadoId(), categoria, descricao.trim())
        )
    }

    suspend fun minhasFichas(): List<FichaAluno> =
        db.from("fichas_treino").select(Columns.raw("id, nome, grupo_foco, ordem, itens_ficha(id)")) {
            filter {
                eq("aluno_id", usuarioLogadoId())
                eq("ativa", true)
            }
            order("ordem", Order.ASCENDING)
        }.decodeList()

    suspend fun fichaDoUltimoTreino(): Long? =
        db.from("execucoes_treino").select(Columns.list("ficha_id")) {
            filter { eq("usuario_id", usuarioLogadoId()) }
            order("inicio", Order.DESCENDING)
            limit(1)
        }.decodeList<AlunoUltimaFicha>().firstOrNull()?.fichaId

    suspend fun buscarFicha(fichaId: Long): FichaDetalheAluno {
        usuarioLogadoId()
        val ficha = db.from("fichas_treino").select(
            Columns.raw(
                "id, nome, grupo_foco, itens_ficha(id, ordem, series, repeticoes, carga_kg, descanso_seg, " +
                    "observacoes, tecnica, exercicios(id, nome, grupo_muscular))"
            )
        ) { filter { eq("id", fichaId) } }.decodeSingle<FichaDetalheAluno>()
        return ficha.copy(itens = ficha.itens.sortedBy { it.ordem })
    }

    suspend fun historicoTreinos(limite: Long = 30): List<ExecucaoResumo> =
        db.from("execucoes_treino")
            .select(Columns.raw("id, inicio, fim, fichas_treino(nome, grupo_foco), series_executadas(exercicios(nome))")) {
                filter { eq("usuario_id", usuarioLogadoId()) }
                order("inicio", Order.DESCENDING)
                limit(limite)
            }.decodeList()

    suspend fun iniciarExecucao(fichaId: Long): ExecucaoIniciada =
        db.from("execucoes_treino")
            .insert(AlunoNovaExecucao(usuarioLogadoId(), fichaId)) { select(Columns.list("id", "inicio")) }
            .decodeSingle()

    suspend fun registrarSerie(execucaoId: Long, exercicioId: Int, numeroSerie: Int, repeticoes: Int, cargaKg: Double?) {
        db.from("series_executadas").insert(AlunoNovaSerie(execucaoId, exercicioId, numeroSerie, repeticoes, cargaKg))
    }

    suspend fun finalizarExecucao(execucao: ExecucaoIniciada) {
        val inicio = OffsetDateTime.parse(execucao.inicio).toInstant()
        val fim = maxOf(Instant.now(), inicio)
        db.from("execucoes_treino").update({ set("fim", fim.toString()) }) {
            filter { eq("id", execucao.id) }
        }
    }

    suspend fun descartarExecucao(execucaoId: Long) {
        db.from("execucoes_treino").delete { filter { eq("id", execucaoId) } }
    }

    suspend fun resumoExecucao(execucaoId: Long): ResumoExecucaoAluno {
        usuarioLogadoId()
        return db.from("execucoes_treino").select(
            Columns.raw(
                "id, inicio, fim, fichas_treino(nome, grupo_foco), " +
                    "series_executadas(numero_serie, repeticoes, carga_kg, exercicios(nome))"
            )
        ) { filter { eq("id", execucaoId) } }.decodeSingle()
    }

    private suspend fun encerrarUsosVencidos() {
        runCatching { db.postgrest.rpc("encerrar_utilizacoes_expiradas") }
    }
}

fun mensagemErroAluno(e: Throwable): String {
    val texto = e.message.orEmpty()
    val motivoDoBanco = Regex("Equipamento [^\\n]*? está (ocupado|em manutenção)\\.").find(texto)?.value
    return when {
        motivoDoBanco != null -> motivoDoBanco
        "utilizacoes_uma_ativa_por_usuario" in texto ->
            "Você já está usando outro equipamento. Finalize esse uso antes de iniciar outro."
        else -> mensagemDeErro(e)
    }
}
