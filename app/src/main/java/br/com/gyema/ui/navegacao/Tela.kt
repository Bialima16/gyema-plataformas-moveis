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
    object ResumoTreino : Tela("resumo_treino", "Resumo", Icons.Outlined.CheckCircle)
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
    object RegistroUtilizacao : Tela("registro_utilizacao", "Registro de utilização", Icons.Outlined.Timer)
    object ReportarProblema : Tela("reportar_problema", "Reportar problema", Icons.Outlined.Warning)
    object ProblemaReportado : Tela("problema_reportado", "Problema reportado", Icons.Outlined.CheckCircle)
    object DetalhesTreino : Tela("detalhes_treino", "Detalhes do treino", Icons.Outlined.Description)
    object Execucao : Tela("execucao", "Execução", Icons.Outlined.PlayArrow)
    object MapaAcademia : Tela("mapa_academia", "Mapa da academia", Icons.Outlined.Map)
    object Equipamentos : Tela("equipamentos", "Equipamentos", Icons.Outlined.Construction)
    object DetalhesEquipamento : Tela("detalhes_equipamento", "Detalhes do equipamento", Icons.Outlined.FitnessCenter)

    // Rotas para o fluxo do Instrutor
    // Rotas com argumento: use as funções criar(...) para montar o caminho ao navegar.
    object GerenciarEquipamentos : Tela("gerenciar_equipamentos", "Gerenciar Equipamentos (Instrutor)", Icons.Outlined.Build)
    object CadastrarEquipamento : Tela("cadastrar_equipamento?equipamentoId={equipamentoId}", "Cadastrar Equipamento (Instrutor)", Icons.Outlined.Add) {
        /** Sem id abre o cadastro; com id abre a edição. */
        fun criar(equipamentoId: Int? = null) =
            if (equipamentoId == null) "cadastrar_equipamento" else "cadastrar_equipamento?equipamentoId=$equipamentoId"
    }
    object NovaFichaTreino : Tela("nova_ficha_treino?alunoId={alunoId}", "Nova Ficha de Treino (Instrutor)", Icons.Outlined.NoteAdd) {
        /** Com alunoId a ficha já abre com o aluno selecionado. */
        fun criar(alunoId: String? = null) =
            if (alunoId == null) "nova_ficha_treino" else "nova_ficha_treino?alunoId=$alunoId"
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