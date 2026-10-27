package br.com.gyema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.gyema.ui.navegacao.Tela
import br.com.gyema.ui.tema.GyemaTheme
import br.com.gyema.ui.telas.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { GyemaTheme { GyemaApp() } }
    }
}

@Composable
fun GyemaApp() {
    val nav = rememberNavController()
    Scaffold { padding ->
        NavHost(navController = nav, startDestination = Tela.BoasVindas.rota,
            modifier = Modifier.fillMaxSize().padding(padding)) {
            composable(Tela.BoasVindas.rota) {
                TelaBoasVindas(onEntrar = { nav.navigate(Tela.Login.rota) })
            }
            composable(Tela.Login.rota) {
                TelaLogin(onVoltar = { nav.popBackStack() },
                    onRecuperar = { nav.navigate(Tela.Recuperacao.rota) })
            }
            composable(Tela.Recuperacao.rota) {
                TelaRecuperacao(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.DetalhesExercicio.rota) {
                TelaDetalhesExercicio(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.EquipamentoOcupado.rota) {
                TelaEquipamentoOcupado(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.Grupos.rota) {
                TelaGrupos(onCriar = { nav.navigate(Tela.CriarGrupo.rota) },
                    onChatbot = { nav.navigate(Tela.Chatbot.rota) })
            }
            composable(Tela.CriarGrupo.rota) {
                TelaCriarGrupo(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.CriarGrupoValidacao.rota) {
                TelaCriarGrupo(onVoltar = { nav.popBackStack() }, iniciarComErro = true)
            }
            composable(Tela.Chatbot.rota) {
                TelaChatbot(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.ResumoTreino.rota) {
                TelaResumoTreino(onVoltar = { nav.popBackStack() })
            }
        }
    }
}