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
    object DetalhesTreino : Tela("detalhes_treino", "Detalhes do treino", Icons.Outlined.Description)
    object Execucao : Tela("execucao", "Execução", Icons.Outlined.PlayArrow)
    object MapaAcademia : Tela("mapa_academia", "Mapa da academia", Icons.Outlined.Map)
    object Equipamentos : Tela("equipamentos", "Equipamentos", Icons.Outlined.Construction)

    companion object {
        val abas get() = listOf(Inicio, Treinos, Academia, Comunidade, Perfil)
    }
}