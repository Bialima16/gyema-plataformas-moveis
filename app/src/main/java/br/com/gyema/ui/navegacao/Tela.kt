package br.com.gyema.ui.navegacao

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Tela(val rota: String, val titulo: String, val icone: ImageVector) {
    object BoasVindas : Tela("boas_vindas", "Boas-vindas", Icons.Outlined.Home)
    object Login : Tela("login", "Login", Icons.Outlined.Login)
    object Recuperacao : Tela("recuperacao", "Recuperação", Icons.Outlined.Lock)
    object DetalhesExercicio : Tela("detalhes_exercicio", "Exercício", Icons.Outlined.FitnessCenter)
    object EquipamentoOcupado : Tela("equipamento_ocupado", "Equipamento ocupado", Icons.Outlined.Warning)
    object ResumoTreino : Tela("resumo_treino/{execucaoId}", "Resumo", Icons.Outlined.CheckCircle) {
        fun criar(execucaoId: Long) = "resumo_treino/$execucaoId"
    }
    object Grupos : Tela("grupos", "Grupos", Icons.Outlined.Groups)
    object CriarGrupo : Tela("criar_grupo", "Criar grupo", Icons.Outlined.GroupAdd)
    object CriarGrupoValidacao : Tela("criar_grupo_validacao", "Validação de grupo", Icons.Outlined.Warning)
    object Chatbot : Tela("chatbot", "Assistente", Icons.Outlined.Chat)
    object Inicio : Tela("inicio", "Início", Icons.Outlined.Home)
    object Treinos : Tela("treinos", "Treinos", Icons.Outlined.FitnessCenter)
    object Academia : Tela("academia", "Academia", Icons.Outlined.Business)
    object Comunidade : Tela("comunidade", "Comunidade", Icons.Outlined.Groups)
    object Perfil : Tela("perfil", "Perfil", Icons.Outlined.Person)
    object Evolucao : Tela("evolucao", "Evolução", Icons.Outlined.ShowChart)
    object EmUtilizacao : Tela("em_utilizacao", "Em utilização", Icons.Outlined.Timer)
    object Publicacao : Tela("publicacao/{postId}", "Publicação", Icons.Outlined.Forum) {
        fun criar(postId: Long) = "publicacao/$postId"
    }
    object CadastroPasso1 : Tela("cadastro_passo_1", "Cadastro", Icons.Outlined.PersonAdd)
    object CadastroPasso2 : Tela("cadastro_passo_2", "Informações pessoais", Icons.Outlined.Badge)
    object CadastroPasso3 : Tela("cadastro_passo_3", "Teste de prontidão", Icons.Outlined.Assignment)
    object RegistroUtilizacao : Tela("registro_utilizacao?equipamentoId={equipamentoId}", "Registro de utilização", Icons.Outlined.Timer) {
        fun criar(equipamentoId: Int? = null) =
            if (equipamentoId == null) "registro_utilizacao" else "registro_utilizacao?equipamentoId=$equipamentoId"
    }
    object ReportarProblema : Tela("reportar_problema?equipamentoId={equipamentoId}", "Reportar problema", Icons.Outlined.Warning) {
        fun criar(equipamentoId: Int? = null) =
            if (equipamentoId == null) "reportar_problema" else "reportar_problema?equipamentoId=$equipamentoId"
    }
    object ProblemaReportado : Tela("problema_reportado", "Problema reportado", Icons.Outlined.CheckCircle)
    object DetalhesTreino : Tela("detalhes_treino/{fichaId}", "Detalhes do treino", Icons.Outlined.Description) {
        fun criar(fichaId: Long) = "detalhes_treino/$fichaId"
    }
    object Execucao : Tela("execucao/{fichaId}", "Execução", Icons.Outlined.PlayArrow) {
        fun criar(fichaId: Long) = "execucao/$fichaId"
    }
    object MapaAcademia : Tela("mapa_academia", "Mapa da academia", Icons.Outlined.Map)
    object Equipamentos : Tela("equipamentos?setorId={setorId}", "Equipamentos", Icons.Outlined.Construction) {
        fun criar(setorId: Int? = null) =
            if (setorId == null) "equipamentos" else "equipamentos?setorId=$setorId"
    }
    object DetalhesEquipamento : Tela("detalhes_equipamento/{equipamentoId}", "Detalhes do equipamento", Icons.Outlined.FitnessCenter) {
        fun criar(equipamentoId: Int) = "detalhes_equipamento/$equipamentoId"
    }

    // Rotas para o fluxo do Instrutor
    // Rotas com argumento: use as funções criar(...) para montar o caminho ao navegar.
    object GerenciarEquipamentos : Tela("gerenciar_equipamentos", "Gerenciar Equipamentos (Instrutor)", Icons.Outlined.Build)
    object CadastrarEquipamento : Tela("cadastrar_equipamento?equipamentoId={equipamentoId}", "Cadastrar Equipamento (Instrutor)", Icons.Outlined.Add) {
        /** Sem id abre o cadastro; com id abre a edição. */
        fun criar(equipamentoId: Int? = null) =
            if (equipamentoId == null) "cadastrar_equipamento" else "cadastrar_equipamento?equipamentoId=$equipamentoId"
    }
    object NovaFichaTreino : Tela("nova_ficha_treino?alunoId={alunoId}&fichaId={fichaId}", "Nova Ficha de Treino (Instrutor)", Icons.Outlined.NoteAdd) {
        /** Com alunoId a ficha já abre com o aluno selecionado; com fichaId abre a edição dessa ficha. */
        fun criar(alunoId: String? = null, fichaId: Long? = null): String {
            val params = listOfNotNull(alunoId?.let { "alunoId=$it" }, fichaId?.let { "fichaId=$it" })
            return if (params.isEmpty()) "nova_ficha_treino" else "nova_ficha_treino?" + params.joinToString("&")
        }
    }
    object RelatosProblemas : Tela("relatos_problemas", "Relatos de Problemas (Instrutor)", Icons.Outlined.ReportProblem)
    object PainelInstrutor : Tela("painel_instrutor", "Painel do Instrutor (Instrutor)", Icons.Outlined.Dashboard)
    object ListaAlunos : Tela("lista_alunos", "Lista de Alunos (Instrutor)", Icons.Outlined.Group)
    object DetalhesAluno : Tela("detalhes_aluno/{alunoId}", "Detalhes do Aluno (Instrutor)", Icons.Outlined.Person) {
        fun criar(alunoId: String) = "detalhes_aluno/$alunoId"
    }
    object AcessibilidadeSuporte : Tela("acessibilidade_suporte", "Acessibilidade e Suporte", Icons.Outlined.AccessibilityNew)

    companion object {
        val abas get() = listOf(Inicio, Treinos, Academia, Comunidade, Perfil)
    }
}