package br.com.gyema.data

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

// =====================================================================
// Modelos usados pelas telas do instrutor.
// Cada classe tem só as colunas que a tela usa; as consultas pedem
// exatamente essas colunas (Columns.raw), com os mesmos nomes do banco.
// =====================================================================

@Serializable
data class PerfilResumo(
    val id: String,
    val nome: String,
    val perfil: String // "aluno" ou "instrutor"
)

@Serializable
data class Aluno(
    val id: String,
    val nome: String,
    val curso: String? = null,
    val matricula: String? = null
)

@Serializable
data class Exercicio(
    val id: Int,
    val nome: String,
    @SerialName("grupo_muscular") val grupoMuscular: String
)

@Serializable
data class NomeExercicio(val nome: String)

@Serializable
data class ItemFichaResumo(
    val ordem: Int,
    val exercicios: NomeExercicio
)

@Serializable
data class FichaResumo(
    val id: Long,
    val nome: String,
    @SerialName("grupo_foco") val grupoFoco: String? = null,
    val ordem: Int,
    @SerialName("itens_ficha") val itens: List<ItemFichaResumo> = emptyList()
)

@Serializable
data class FichaNomeFoco(
    val nome: String,
    @SerialName("grupo_foco") val grupoFoco: String? = null
)

@Serializable
data class SerieResumo(val exercicios: NomeExercicio)

@Serializable
data class ExecucaoResumo(
    val id: Long,
    val inicio: String,
    val fim: String? = null,
    @SerialName("fichas_treino") val ficha: FichaNomeFoco? = null,
    @SerialName("series_executadas") val series: List<SerieResumo> = emptyList()
)

@Serializable
data class Setor(val id: Int, val nome: String)

@Serializable
data class EquipamentoLista(
    val id: Int,
    val codigo: String,
    val nome: String,
    @SerialName("setor_nome") val setorNome: String,
    val status: String // "disponivel", "ocupado" ou "manutencao"
)

@Serializable
data class VinculoExercicio(
    val tipo: String, // "principal" ou "alternativo"
    val exercicios: Exercicio
)

@Serializable
data class EquipamentoCompleto(
    val id: Int,
    val codigo: String,
    val nome: String,
    @SerialName("setor_id") val setorId: Int,
    @SerialName("grupo_muscular") val grupoMuscular: String? = null,
    @SerialName("capacidade_simultanea") val capacidadeSimultanea: Int,
    @SerialName("tempo_maximo_min") val tempoMaximoMin: Int,
    val status: String,
    @SerialName("instrucoes_uso") val instrucoesUso: String? = null,
    @SerialName("video_url") val videoUrl: String? = null,
    @SerialName("equipamento_exercicios") val exercicios: List<VinculoExercicio> = emptyList()
)

/** O que a tela de cadastro envia para gravar um equipamento. */
data class DadosEquipamento(
    val codigo: String,
    val nome: String,
    val setorId: Int,
    val grupoMuscular: String?,
    val capacidadeSimultanea: Int,
    val tempoMaximoMin: Int,
    val emManutencao: Boolean,
    val instrucoesUso: String?,
    val videoUrl: String?,
    val principais: List<Int>,   // ids dos exercícios
    val alternativos: List<Int>
)

@Serializable
data class NomeCodigo(val nome: String, val codigo: String)

@Serializable
data class NomePessoa(val nome: String)

@Serializable
data class Relato(
    val id: Long,
    val categoria: String,
    val descricao: String,
    val status: String, // "aberto", "em_analise" ou "resolvido"
    @SerialName("criado_em") val criadoEm: String,
    val equipamentos: NomeCodigo,
    val perfis: NomePessoa? = null
)

/** Ficha existente carregada para edição (os itens reaproveitam ItemFichaAluno). */
@Serializable
data class FichaEdicao(
    val id: Long,
    @SerialName("aluno_id") val alunoId: String,
    val nome: String,
    @SerialName("itens_ficha") val itens: List<ItemFichaAluno> = emptyList()
)

/** Um exercício dentro da ficha que o instrutor está montando. */
data class ItemNovaFicha(
    val exercicio: Exercicio,
    val series: Int,
    val repeticoes: Int,
    val cargaKg: Double?,
    val descansoSeg: Int?,
    val observacoes: String?,
    val tecnica: String?
)

// Linhas enviadas nos inserts (sem valores padrão, para todos os campos irem no JSON)
@Serializable
private data class SoId(val id: Long)

@Serializable
private data class LinhaFicha(
    @SerialName("aluno_id") val alunoId: String,
    @SerialName("instrutor_id") val instrutorId: String,
    val nome: String,
    @SerialName("grupo_foco") val grupoFoco: String?,
    val ordem: Int
)

@Serializable
private data class LinhaItemFicha(
    @SerialName("ficha_id") val fichaId: Long,
    @SerialName("exercicio_id") val exercicioId: Int,
    val ordem: Int,
    val series: Int,
    val repeticoes: Int,
    @SerialName("carga_kg") val cargaKg: Double?,
    @SerialName("descanso_seg") val descansoSeg: Int?,
    val observacoes: String?,
    val tecnica: String?
)

@Serializable
private data class LinhaEquipamento(
    val codigo: String,
    val nome: String,
    @SerialName("setor_id") val setorId: Int,
    @SerialName("grupo_muscular") val grupoMuscular: String?,
    @SerialName("capacidade_simultanea") val capacidadeSimultanea: Int,
    @SerialName("tempo_maximo_min") val tempoMaximoMin: Int,
    val status: String,
    @SerialName("instrucoes_uso") val instrucoesUso: String?,
    @SerialName("video_url") val videoUrl: String?
)

@Serializable
private data class LinhaEquipExercicio(
    @SerialName("equipamento_id") val equipamentoId: Int,
    @SerialName("exercicio_id") val exercicioId: Int,
    val tipo: String
)

// =====================================================================
// Consultas e gravações das telas do instrutor.
// As regras de acesso (RLS) ficam no banco: um aluno logado recebe listas
// vazias e não consegue gravar nada aqui.
// =====================================================================
object RepositorioInstrutor {

    private val db get() = Supabase.cliente

    private fun usuarioId(): String = usuarioLogadoId("Entre com uma conta de instrutor para ver estes dados.")

    // ---------- Painel ----------

    suspend fun meuPerfil(): PerfilResumo =
        db.from("perfis").select(Columns.list("id", "nome", "perfil")) {
            filter { eq("id", usuarioId()) }
        }.decodeSingle()

    suspend fun contarRelatosAbertos(): Int =
        db.from("relatos_problema").select(Columns.list("id")) {
            filter { eq("status", "aberto") }
        }.decodeList<SoId>().size

    // ---------- Alunos ----------

    suspend fun listarAlunos(): List<Aluno> {
        usuarioId()
        return db.from("perfis").select(Columns.list("id", "nome", "curso", "matricula")) {
            filter { eq("perfil", "aluno") }
            order("nome", Order.ASCENDING)
        }.decodeList()
    }

    suspend fun buscarAluno(id: String): Aluno =
        db.from("perfis").select(Columns.list("id", "nome", "curso", "matricula")) {
            filter { eq("id", id) }
        }.decodeSingle()

    suspend fun fichasDoAluno(alunoId: String): List<FichaResumo> =
        db.from("fichas_treino")
            .select(Columns.raw("id, nome, grupo_foco, ordem, itens_ficha(ordem, exercicios(nome))")) {
                filter {
                    eq("aluno_id", alunoId)
                    eq("ativa", true)
                }
                order("ordem", Order.ASCENDING)
            }.decodeList<FichaResumo>()
            .map { f -> f.copy(itens = f.itens.sortedBy { it.ordem }) }

    /** Execuções de treino dos últimos [dias] dias, da mais recente para a mais antiga. */
    suspend fun execucoesDoAluno(alunoId: String, dias: Long = 30): List<ExecucaoResumo> =
        db.from("execucoes_treino")
            .select(Columns.raw("id, inicio, fim, fichas_treino(nome, grupo_foco), series_executadas(exercicios(nome))")) {
                filter {
                    eq("usuario_id", alunoId)
                    gte("inicio", Instant.now().minusSeconds(dias * 24 * 3600).toString())
                }
                order("inicio", Order.DESCENDING)
            }.decodeList()

    // ---------- Fichas de treino ----------

    suspend fun listarExercicios(): List<Exercicio> =
        db.from("exercicios").select(Columns.list("id", "nome", "grupo_muscular")) {
            order("nome", Order.ASCENDING)
        }.decodeList()

    /** Ficha com os exercícios na ordem, para a tela de edição. */
    suspend fun buscarFichaEdicao(fichaId: Long): FichaEdicao {
        usuarioId()
        val ficha = db.from("fichas_treino").select(
            Columns.raw(
                "id, aluno_id, nome, itens_ficha(id, ordem, series, repeticoes, carga_kg, descanso_seg, " +
                    "observacoes, tecnica, exercicios(id, nome, grupo_muscular))"
            )
        ) { filter { eq("id", fichaId) } }.decodeSingle<FichaEdicao>()
        return ficha.copy(itens = ficha.itens.sortedBy { it.ordem })
    }

    /**
     * Edita uma ficha existente: troca o nome/foco e substitui os exercícios.
     * O histórico de treinos do aluno não é afetado (as séries feitas apontam para o exercício, não para o item).
     */
    suspend fun atualizarFicha(fichaId: Long, nome: String, itens: List<ItemNovaFicha>) {
        usuarioId()
        val grupoFoco = itens.map { it.exercicio.grupoMuscular }.distinct().joinToString(" e ")
        db.from("fichas_treino").update({
            set("nome", nome)
            set("grupo_foco", grupoFoco.ifBlank { null })
        }) { filter { eq("id", fichaId) } }
        db.from("itens_ficha").delete { filter { eq("ficha_id", fichaId) } }
        db.from("itens_ficha").insert(itens.mapIndexed { i, item ->
            LinhaItemFicha(
                fichaId, item.exercicio.id, i + 1, item.series, item.repeticoes,
                item.cargaKg, item.descansoSeg, item.observacoes, item.tecnica
            )
        })
    }

    /** Grava a ficha e os exercícios. Se os exercícios falharem, a ficha é apagada. */
    suspend fun salvarFicha(alunoId: String, nome: String, itens: List<ItemNovaFicha>) {
        val instrutorId = usuarioId()
        val jaExistentes = db.from("fichas_treino").select(Columns.list("id")) {
            filter {
                eq("aluno_id", alunoId)
                eq("ativa", true)
            }
        }.decodeList<SoId>().size

        val grupoFoco = itens.map { it.exercicio.grupoMuscular }.distinct().joinToString(" e ")

        val fichaId = db.from("fichas_treino").insert(
            LinhaFicha(alunoId, instrutorId, nome, grupoFoco.ifBlank { null }, jaExistentes + 1)
        ) { select(Columns.list("id")) }.decodeSingle<SoId>().id

        try {
            db.from("itens_ficha").insert(itens.mapIndexed { i, item ->
                LinhaItemFicha(
                    fichaId, item.exercicio.id, i + 1, item.series, item.repeticoes,
                    item.cargaKg, item.descansoSeg, item.observacoes, item.tecnica
                )
            })
        } catch (e: Exception) {
            runCatching { db.from("fichas_treino").delete { filter { eq("id", fichaId) } } }
            throw e
        }
    }

    // ---------- Equipamentos ----------

    suspend fun listarEquipamentos(): List<EquipamentoLista> {
        usuarioId()
        return db.from("equipamentos_detalhados")
            .select(Columns.list("id", "codigo", "nome", "setor_nome", "status")) {
                order("nome", Order.ASCENDING)
            }.decodeList()
    }

    suspend fun listarSetores(): List<Setor> =
        db.from("setores").select(Columns.list("id", "nome")) {
            order("nome", Order.ASCENDING)
        }.decodeList()

    suspend fun buscarEquipamento(id: Int): EquipamentoCompleto =
        db.from("equipamentos").select(
            Columns.raw(
                "id, codigo, nome, setor_id, grupo_muscular, capacidade_simultanea, tempo_maximo_min, " +
                    "status, instrucoes_uso, video_url, equipamento_exercicios(tipo, exercicios(id, nome, grupo_muscular))"
            )
        ) { filter { eq("id", id) } }.decodeSingle()

    /**
     * Cadastra (id = null) ou atualiza um equipamento e seus exercícios.
     * Na edição o status só muda quando o instrutor liga ou desliga a manutenção;
     * "ocupado" e "disponível" continuam sendo controlados pelo banco.
     */
    suspend fun salvarEquipamento(id: Int?, dados: DadosEquipamento, statusAtual: String? = null) {
        usuarioId()
        val equipamentoId: Int
        if (id == null) {
            equipamentoId = db.from("equipamentos").insert(
                LinhaEquipamento(
                    dados.codigo, dados.nome, dados.setorId, dados.grupoMuscular,
                    dados.capacidadeSimultanea, dados.tempoMaximoMin,
                    if (dados.emManutencao) "manutencao" else "disponivel",
                    dados.instrucoesUso, dados.videoUrl
                )
            ) { select(Columns.list("id")) }.decodeSingle<SoId>().id.toInt()
        } else {
            equipamentoId = id
            val novoStatus = when {
                dados.emManutencao -> "manutencao"
                statusAtual == "manutencao" -> "disponivel"
                else -> null // mantém disponível/ocupado como está
            }
            db.from("equipamentos").update({
                set("codigo", dados.codigo)
                set("nome", dados.nome)
                set("setor_id", dados.setorId)
                set("grupo_muscular", dados.grupoMuscular)
                set("capacidade_simultanea", dados.capacidadeSimultanea)
                set("tempo_maximo_min", dados.tempoMaximoMin)
                set("instrucoes_uso", dados.instrucoesUso)
                set("video_url", dados.videoUrl)
                if (novoStatus != null) set("status", novoStatus)
            }) { filter { eq("id", equipamentoId) } }

            db.from("equipamento_exercicios").delete { filter { eq("equipamento_id", equipamentoId) } }
        }

        val vinculos = dados.principais.map { LinhaEquipExercicio(equipamentoId, it, "principal") } +
            dados.alternativos.map { LinhaEquipExercicio(equipamentoId, it, "alternativo") }
        if (vinculos.isNotEmpty()) db.from("equipamento_exercicios").insert(vinculos)
    }

    // ---------- Relatos de problemas ----------

    suspend fun listarRelatos(): List<Relato> {
        usuarioId()
        return db.from("relatos_problema")
            .select(Columns.raw("id, categoria, descricao, status, criado_em, equipamentos(nome, codigo), perfis(nome)")) {
                order("criado_em", Order.DESCENDING)
            }.decodeList()
    }

    /** status: "em_analise" ou "resolvido". */
    suspend fun mudarStatusRelato(id: Long, status: String) {
        db.from("relatos_problema").update({
            set("status", status)
            if (status == "resolvido") set("resolvido_em", Instant.now().toString())
        }) { filter { eq("id", id) } }
    }
}
