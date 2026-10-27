package br.com.gyema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import br.com.gyema.ui.components.GyemaBottomBar
import br.com.gyema.ui.navegacao.Tela
import br.com.gyema.ui.tema.*
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
    var mostrarMenuTeste by remember { mutableStateOf(false) }
    val entry by nav.currentBackStackEntryAsState()
    val rota = entry?.destination?.route
    val abaAtual = when (rota) {
        Tela.MapaAcademia.rota, Tela.Equipamentos.rota -> Tela.Academia.rota
        else -> rota
    }
    Scaffold(containerColor = Fundo,
        floatingActionButton = {
            if (Tela.abas.any { it.rota == abaAtual }) {
                SmallFloatingActionButton(onClick = { mostrarMenuTeste = true },
                    containerColor = Laranja) { Text("☰", color = Color.White) }
            }
        },
        bottomBar = {
            if (Tela.abas.any { it.rota == abaAtual }) {
                GyemaBottomBar(abaAtual) { tela ->
                    nav.navigate(tela.rota) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = Tela.BoasVindas.rota,
            modifier = Modifier.fillMaxSize().padding(padding)) {
            composable(Tela.BoasVindas.rota) {
                TelaBoasVindas(onEntrar = { nav.navigate(Tela.Login.rota) },
                    onCadastrar = { nav.navigate(Tela.CadastroPasso1.rota) },
                    onDemonstracao = { nav.navigate(Tela.Inicio.rota) })
            }
            composable(Tela.Login.rota) {
                TelaLogin(onVoltar = { nav.popBackStack() },
                    onRecuperar = { nav.navigate(Tela.Recuperacao.rota) },
                    onCadastrar = { nav.navigate(Tela.CadastroPasso1.rota) })
            }
            composable(Tela.Recuperacao.rota) {
                TelaRecuperacao(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.CadastroPasso1.rota) {
                TelaCadastroPasso1(onVoltar = { nav.popBackStack() },
                    onAvancar = { nav.navigate(Tela.CadastroPasso2.rota) },
                    onLogin = { nav.navigate(Tela.Login.rota) })
            }
            composable(Tela.CadastroPasso2.rota) {
                TelaCadastroPasso2(onVoltar = { nav.popBackStack() },
                    onAvancar = { nav.navigate(Tela.CadastroPasso3.rota) })
            }
            composable(Tela.CadastroPasso3.rota) {
                TelaCadastroPasso3(onVoltar = { nav.popBackStack() },
                    onAvancar = { nav.navigate(Tela.Inicio.rota) })
            }
            composable(Tela.Inicio.rota) {
                TelaPrincipal(onMeuTreino = { nav.navigate(Tela.Treinos.rota) })
            }
            composable(Tela.Treinos.rota) {
                TelaMeusTreinos(onAbrirTreino = { nav.navigate(Tela.DetalhesTreino.rota) })
            }
            composable(Tela.DetalhesTreino.rota) {
                TelaDetalhesTreino(onVoltar = { nav.popBackStack() },
                    onIniciar = { nav.navigate(Tela.Execucao.rota) })
            }
            composable(Tela.Execucao.rota) {
                TelaExecucao(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.Academia.rota) {
                TelaAcademiaAgora(onAbrirMapa = { nav.navigate(Tela.MapaAcademia.rota) })
            }
            composable(Tela.MapaAcademia.rota) {
                TelaMapaAcademia(onVoltar = { nav.popBackStack() },
                    onAbrirZona = { nav.navigate(Tela.Equipamentos.rota) })
            }
            composable(Tela.Equipamentos.rota) {
                TelaEquipamentos(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.Comunidade.rota) {
                TelaComunidade(onAbrirPost = { nav.navigate(Tela.Publicacao.rota) })
            }
            composable(Tela.Publicacao.rota) {
                TelaPublicacao(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.Perfil.rota) { TelaPerfil() }
            composable(Tela.Evolucao.rota) {
                TelaEvolucao(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.EmUtilizacao.rota) {
                TelaEmUtilizacao(onVoltar = { nav.popBackStack() },
                    onFinalizar = { nav.navigate(Tela.ResumoTreino.rota) },
                    onReportar = { nav.navigate(Tela.ReportarProblema.rota) })
            }
            composable(Tela.RegistroUtilizacao.rota) {
                TelaRegistroUtilizacao(onVoltarTreino = { nav.popBackStack() },
                    onFinalizarUtilizacao = { nav.navigate(Tela.ResumoTreino.rota) },
                    onReportarProblema = { nav.navigate(Tela.ReportarProblema.rota) })
            }
            composable(Tela.ReportarProblema.rota) {
                TelaReportarProblema(onVoltar = { nav.popBackStack() },
                    onEnviarRelato = { nav.navigate(Tela.ProblemaReportado.rota) })
            }
            composable(Tela.ProblemaReportado.rota) {
                TelaProblemaReportado(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.DetalhesExercicio.rota) {
                TelaDetalhesExercicio(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.EquipamentoOcupado.rota) {
                TelaEquipamentoOcupado(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.ResumoTreino.rota) {
                TelaResumoTreino(onVoltar = { nav.navigate(Tela.Treinos.rota) })
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
        }
        if (mostrarMenuTeste) {
            val atalhos = listOf(Tela.Grupos, Tela.Chatbot, Tela.DetalhesExercicio,
                Tela.EquipamentoOcupado, Tela.ResumoTreino, Tela.EmUtilizacao,
                Tela.RegistroUtilizacao, Tela.ReportarProblema, Tela.Evolucao,
                Tela.CadastroPasso1)
            AlertDialog(onDismissRequest = { mostrarMenuTeste = false },
                title = { Text("Navegação de teste") },
                text = {
                    Column(Modifier.heightIn(max = androidx.compose.ui.unit.Dp(420f))
                        .verticalScroll(rememberScrollState())) {
                        atalhos.forEach { tela ->
                            TextButton(onClick = {
                                mostrarMenuTeste = false
                                nav.navigate(tela.rota)
                            }) { Text(tela.titulo) }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { mostrarMenuTeste = false }) { Text("Fechar") }
                })
        }
    }
}
