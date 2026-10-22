package br.com.gyema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.gyema.ui.components.GyemaBottomBar
import br.com.gyema.ui.navegacao.Tela
import br.com.gyema.ui.telas.TelaCadastroPasso1
import br.com.gyema.ui.telas.TelaCadastroPasso2
import br.com.gyema.ui.telas.TelaCadastroPasso3
import br.com.gyema.ui.telas.TelaProblemaReportado
import br.com.gyema.ui.telas.TelaRegistroUtilizacao
import br.com.gyema.ui.telas.TelaReportarProblema
import br.com.gyema.ui.tema.Fundo
import br.com.gyema.ui.tema.GyemaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GyemaTheme { GyemaApp() }
        }
    }
}

@Composable
fun GyemaApp() {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rota = entrada?.destination?.route
    val mostrarBarra = Tela.abas.any { it.rota == rota }

    Scaffold(
        containerColor = Fundo,
        bottomBar = {
            if (mostrarBarra) {
                GyemaBottomBar(rota) { tela ->
                    nav.navigate(tela.rota) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { padding ->
        // rotas de navegacao
        NavHost(nav, startDestination = Tela.CadastroPasso1.rota, modifier = Modifier.padding(padding)) {
            // fluxo de cadastro
            composable(Tela.CadastroPasso1.rota) {
                TelaCadastroPasso1(
                    onVoltar = { nav.popBackStack() },
                    onAvancar = { nav.navigate(Tela.CadastroPasso2.rota) },
                    onLogin = { }
                )
            }
            composable(Tela.CadastroPasso2.rota) {
                TelaCadastroPasso2(
                    onVoltar = { nav.popBackStack() },
                    onAvancar = { nav.navigate(Tela.CadastroPasso3.rota) }
                )
            }
            composable(Tela.CadastroPasso3.rota) {
                TelaCadastroPasso3(
                    onVoltar = { nav.popBackStack() },
                    onAvancar = { nav.navigate(Tela.RegistroUtilizacao.rota) }
                )
            }

            // fluxo de utilizacao e relato de problema
            composable(Tela.RegistroUtilizacao.rota) {
                TelaRegistroUtilizacao(
                    onVoltarTreino = { nav.popBackStack() },
                    onFinalizarUtilizacao = { nav.popBackStack() },
                    onReportarProblema = { nav.navigate(Tela.ReportarProblema.rota) }
                )
            }
            // navegacao para tela de sucesso
            composable(Tela.ReportarProblema.rota) {
                TelaReportarProblema(
                    onVoltar = { nav.popBackStack() },
                    onEnviarRelato = {
                        nav.navigate(Tela.ProblemaReportado.rota) {
                            popUpTo(Tela.ReportarProblema.rota) { inclusive = true }
                        }
                    }
                )
            }
            composable(Tela.ProblemaReportado.rota) {
                TelaProblemaReportado(
                    onVoltar = { nav.navigate(Tela.RegistroUtilizacao.rota) }
                )
            }
        }
    }
}
