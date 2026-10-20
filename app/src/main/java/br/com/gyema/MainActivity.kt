package br.com.gyema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.gyema.ui.components.GyemaBottomBar
import br.com.gyema.ui.navegacao.Tela
import br.com.gyema.ui.telas.TelaAcademiaAgora
import br.com.gyema.ui.telas.TelaDetalhesTreino
import br.com.gyema.ui.telas.TelaEquipamentos
import br.com.gyema.ui.telas.TelaExecucao
import br.com.gyema.ui.telas.TelaMapaAcademia
import br.com.gyema.ui.telas.TelaMeusTreinos
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

    // Mapa e Equipamentos pertencem à aba Academia
    val rotaDaAba = when (rota) {
        Tela.MapaAcademia.rota, Tela.Equipamentos.rota -> Tela.Academia.rota
        else -> rota
    }
    val mostrarBarra = Tela.abas.any { it.rota == rotaDaAba }

    Scaffold(
        containerColor = Fundo,
        bottomBar = {
            if (mostrarBarra) {
                GyemaBottomBar(rotaDaAba) { tela ->
                    nav.navigate(tela.rota) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { padding ->
        NavHost(nav, startDestination = Tela.Treinos.rota, modifier = Modifier.padding(padding)) {
            composable(Tela.Treinos.rota) {
                TelaMeusTreinos(onAbrirTreino = { nav.navigate(Tela.DetalhesTreino.rota) })
            }
            composable(Tela.DetalhesTreino.rota) {
                TelaDetalhesTreino(
                    onVoltar = { nav.popBackStack() },
                    onIniciar = { nav.navigate(Tela.Execucao.rota) }
                )
            }
            composable(Tela.Execucao.rota) {
                TelaExecucao(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.Academia.rota) {
                TelaAcademiaAgora(onAbrirMapa = { nav.navigate(Tela.MapaAcademia.rota) })
            }
            composable(Tela.MapaAcademia.rota) {
                TelaMapaAcademia(
                    onVoltar = { nav.popBackStack() },
                    onAbrirZona = { nav.navigate(Tela.Equipamentos.rota) }
                )
            }
            composable(Tela.Equipamentos.rota) {
                TelaEquipamentos(onVoltar = { nav.popBackStack() })
            }
            listOf(Tela.Inicio, Tela.Comunidade, Tela.Perfil).forEach { t ->
                composable(t.rota) { EmBreve(t.titulo) }
            }
        }
    }
}

@Composable
private fun EmBreve(titulo: String) {
    Box(Modifier.fillMaxSize().background(Fundo), contentAlignment = Alignment.Center) {
        Text("$titulo — em breve")
    }
}