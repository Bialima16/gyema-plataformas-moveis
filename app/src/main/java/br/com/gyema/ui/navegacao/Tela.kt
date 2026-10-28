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
    object Publicacao : Tela("publicacao", "Publicação", Icons.Outlined.Forum)
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

    // Rotas para o fluxo do Instrutor
    object GerenciarEquipamentos : Tela("gerenciar_equipamentos", "Gerenciar Equipamentos (Instrutor)", Icons.Outlined.Build)
    object GerenciarEquipamentosCarregando : Tela("gerenciar_equip_carregando", "Equipamentos (Carregando) (Instrutor)", Icons.Outlined.HourglassEmpty)
    object CadastrarEquipamento : Tela("cadastrar_equipamento", "Cadastrar Equipamento (Instrutor)", Icons.Outlined.Add)
    object NovaFichaTreino : Tela("nova_ficha_treino", "Nova Ficha de Treino (Instrutor)", Icons.Outlined.NoteAdd)
    object GerenciarEquipamentosVazio : Tela("gerenc_equip_vazio", "Equipamentos Vazio (Instrutor)", Icons.Outlined.Inbox)
    object GerenciarEquipamentosErro : Tela("gerenc_equip_erro", "Equipamentos Erro (Instrutor)", Icons.Outlined.ErrorOutline)
    object RelatosProblemas : Tela("relatos_problemas", "Relatos de Problemas (Instrutor)", Icons.Outlined.ReportProblem)

    companion object {
        val abas get() = listOf(Inicio, Treinos, Academia, Comunidade, Perfil)
    }
}