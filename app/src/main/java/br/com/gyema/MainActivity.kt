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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import br.com.gyema.ui.components.GyemaBottomBar
import br.com.gyema.ui.navegacao.Tela
import br.com.gyema.ui.telas.TelaEvolucao
import br.com.gyema.ui.telas.TelaPerfil
import br.com.gyema.ui.telas.TelaPrincipal
import br.com.gyema.ui.tema.Fundo
import br.com.gyema.ui.tema.GyemaTheme
import br.com.gyema.ui.telas.TelaComunidade
import br.com.gyema.ui.telas.TelaEmUtilizacao
import br.com.gyema.ui.telas.TelaPublicacao
import androidx.compose.material3.Button

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
        NavHost(nav, startDestination = Tela.Inicio.rota, modifier = Modifier.padding(padding)) {
            composable(Tela.Inicio.rota) {
                TelaPrincipal(onMeuTreino = { nav.navigate(Tela.Evolucao.rota) })
            }
            composable(Tela.Perfil.rota) { TelaPerfil() }
            composable(Tela.Evolucao.rota) { TelaEvolucao(onVoltar = { nav.popBackStack() }) }
            composable(Tela.Comunidade.rota) {
                TelaComunidade(onAbrirPost = { nav.navigate(Tela.Publicacao.rota) })
            }
            composable(Tela.Publicacao.rota) { TelaPublicacao(onVoltar = { nav.popBackStack() }) }
            composable(Tela.EmUtilizacao.rota) {
                TelaEmUtilizacao(
                    onVoltar = { nav.popBackStack() },
                    onFinalizar = { nav.popBackStack() }
                )
            }
            composable(Tela.Treinos.rota) {
                Box(Modifier.fillMaxSize().background(Fundo), contentAlignment = Alignment.Center) {
                    Button(onClick = { nav.navigate(Tela.EmUtilizacao.rota) }) {
                        Text("Abrir Em utilização (teste)")
                    }
                }
            }
            composable(Tela.Academia.rota) { EmBreve(Tela.Academia.titulo) }
        }
    }
}

@Composable
private fun EmBreve(titulo: String) {
    Box(Modifier.fillMaxSize().background(Fundo), contentAlignment = Alignment.Center) {
        Text("$titulo — em breve")
    }
}