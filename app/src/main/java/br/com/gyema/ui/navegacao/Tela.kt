package br.com.gyema.ui.navegacao

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Tela(val rota: String, val titulo: String, val icone: ImageVector) {
    object Inicio : Tela("inicio", "Início", Icons.Outlined.Home)
    object Treinos : Tela("treinos", "Treinos", Icons.Outlined.FitnessCenter)
    object Academia : Tela("academia", "Academia", Icons.Outlined.Business)
    object Comunidade : Tela("comunidade", "Comunidade", Icons.Outlined.Groups)
    object Perfil : Tela("perfil", "Perfil", Icons.Outlined.Person)
    object Evolucao : Tela("evolucao", "Evolução", Icons.Outlined.ShowChart)
    object EmUtilizacao : Tela("em_utilizacao", "Em utilização", Icons.Outlined.Timer)
    object Publicacao : Tela("publicacao", "Publicação", Icons.Outlined.Forum)

    object GerenciarEquipamentos : Tela("gerenciar_equipamentos", "Equipamentos", Icons.Outlined.Build)
    object GerenciarEquipamentosCarregando : Tela("gerenciar_equipamentos_carregando", "Equipamentos (Carregando)", Icons.Outlined.HourglassEmpty)
    object CadastrarEquipamento : Tela("cadastrar_equipamento", "Cadastrar Equipamento", Icons.Outlined.Add)
    object NovaFichaTreino : Tela("nova_ficha_treino", "Nova Ficha", Icons.Outlined.NoteAdd)

    companion object {
        val abas get() = listOf(Inicio, Treinos, Academia, Comunidade, Perfil)
    }
}