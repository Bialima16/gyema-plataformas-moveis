package br.com.gyema.ui.navegacao

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector

// rotas de navegacao
sealed class Tela(val rota: String, val titulo: String, val icone: ImageVector) {
    object CadastroPasso1 : Tela("cadastro_passo_1", "Cadastro", Icons.Outlined.PersonAdd)
    object CadastroPasso2 : Tela("cadastro_passo_2", "Informações Pessoais", Icons.Outlined.Badge)
    object CadastroPasso3 : Tela("cadastro_passo_3", "Teste de Prontidão", Icons.AutoMirrored.Outlined.Assignment)
    object RegistroUtilizacao : Tela("registro_utilizacao", "Registro de Utilização", Icons.Outlined.Timer)
    object ReportarProblema : Tela("reportar_problema", "Reportar Problema", Icons.Outlined.Warning)
    object ProblemaReportado : Tela("problema_reportado", "Problema Reportado", Icons.Outlined.CheckCircle)

    companion object {
        val abas get() = emptyList<Tela>()
    }
}
