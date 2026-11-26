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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument
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
    val entry by nav.currentBackStackEntryAsState()
    val rota = entry?.destination?.route
    val abaAtual = when (rota) {
        Tela.MapaAcademia.rota, Tela.Equipamentos.rota, Tela.GerenciarEquipamentos.rota -> Tela.Academia.rota
        Tela.Grupos.rota -> Tela.Comunidade.rota
        else -> rota
    }

    Scaffold(containerColor = Fundo,
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

            // Fluxo de Login com suporte a redirecionamento de Instrutor
            composable(Tela.Login.rota) {
                TelaLogin(
                    onVoltar = { nav.popBackStack() },
                    onRecuperar = { nav.navigate(Tela.Recuperacao.rota) },
                    onCadastrar = { nav.navigate(Tela.CadastroPasso1.rota) },
                    onLoginSucesso = { isFuncionario ->
                        if (isFuncionario) {
                            nav.navigate(Tela.PainelInstrutor.rota) {
                                popUpTo(Tela.BoasVindas.rota) { inclusive = true }
                            }
                        } else {
                            nav.navigate(Tela.Inicio.rota) {
                                popUpTo(Tela.BoasVindas.rota) { inclusive = true }
                            }
                        }
                    }
                )
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
                TelaPrincipal(
                    onMeuTreino = { nav.navigate(Tela.Treinos.rota) },
                    onAcademia = { nav.navigate(Tela.Academia.rota) },
                    onComunidade = { nav.navigate(Tela.Comunidade.rota) },
                    onChatbot = { nav.navigate(Tela.Chatbot.rota) },
                    onSugestoes = { nav.navigate(Tela.ReportarProblema.criar()) },
                    onNotificacoes = { nav.navigate(Tela.Comunidade.rota) }
                )
            }
            composable(Tela.Treinos.rota) {
                TelaMeusTreinos(
                    onAbrirTreino = { fichaId -> nav.navigate(Tela.DetalhesTreino.criar(fichaId)) },
                    onAbrirExecucao = { execucaoId -> nav.navigate(Tela.ResumoTreino.criar(execucaoId)) },
                    onEvolucao = { nav.navigate(Tela.Evolucao.rota) }
                )
            }
            composable(
                Tela.DetalhesTreino.rota,
                arguments = listOf(navArgument("fichaId") { type = NavType.LongType })
            ) { entrada ->
                TelaDetalhesTreino(
                    fichaId = entrada.arguments?.getLong("fichaId") ?: 0L,
                    onVoltar = { nav.popBackStack() },
                    onIniciar = { fichaId -> nav.navigate(Tela.Execucao.criar(fichaId)) }
                )
            }
            composable(
                Tela.Execucao.rota,
                arguments = listOf(navArgument("fichaId") { type = NavType.LongType })
            ) { entrada ->
                TelaExecucao(
                    fichaId = entrada.arguments?.getLong("fichaId") ?: 0L,
                    onVoltar = { nav.popBackStack() },
                    onConcluido = { execucaoId ->
                        nav.navigate(Tela.ResumoTreino.criar(execucaoId)) {
                            popUpTo(Tela.Execucao.rota) { inclusive = true }
                        }
                    }
                )
            }
            composable(Tela.Academia.rota) {
                TelaAcademiaAgora(
                    onAbrirMapa = { nav.navigate(Tela.MapaAcademia.rota) },
                    onAbrirSetor = { setorId -> nav.navigate(Tela.Equipamentos.criar(setorId)) }
                )
            }
            composable(Tela.MapaAcademia.rota) {
                TelaMapaAcademia(onVoltar = { nav.popBackStack() },
                    onAbrirZona = { setorId -> nav.navigate(Tela.Equipamentos.criar(setorId)) })
            }
            composable(
                Tela.Equipamentos.rota,
                arguments = listOf(navArgument("setorId") { type = NavType.StringType; nullable = true })
            ) { entrada ->
                TelaEquipamentos(
                    setorId = entrada.arguments?.getString("setorId")?.toIntOrNull(),
                    onVoltar = { nav.popBackStack() },
                    onDetalhesEquipamento = { id -> nav.navigate(Tela.DetalhesEquipamento.criar(id)) },
                    onAbrirUso = { nav.navigate(Tela.EmUtilizacao.rota) },
                    onLerQrCode = { nav.navigate(Tela.RegistroUtilizacao.criar()) }
                )
            }
            composable(
                Tela.DetalhesEquipamento.rota,
                arguments = listOf(navArgument("equipamentoId") { type = NavType.IntType })
            ) { entrada ->
                TelaDetalhesEquipamento(
                    equipamentoId = entrada.arguments?.getInt("equipamentoId") ?: 0,
                    onVoltar = { nav.popBackStack() },
                    onIniciarUso = { id -> nav.navigate(Tela.RegistroUtilizacao.criar(id)) },
                    onAbrirUso = { nav.navigate(Tela.EmUtilizacao.rota) },
                    onReportarProblema = { id -> nav.navigate(Tela.ReportarProblema.criar(id)) }
                )
            }
            composable(Tela.Comunidade.rota) {
                TelaComunidade(
                    onAbrirPost = { id -> nav.navigate(Tela.Publicacao.criar(id)) },
                    onGrupos = { nav.navigate(Tela.Grupos.rota) }
                )
            }
            composable(
                Tela.Publicacao.rota,
                arguments = listOf(navArgument("postId") { type = NavType.LongType })
            ) { entrada ->
                TelaPublicacao(
                    postId = entrada.arguments?.getLong("postId") ?: 0L,
                    onVoltar = { nav.popBackStack() }
                )
            }
            composable(Tela.Perfil.rota) {
                TelaPerfil(
                    onSair = {
                        nav.navigate(Tela.BoasVindas.rota) {
                            popUpTo(nav.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onSuporte = { nav.navigate(Tela.AcessibilidadeSuporte.rota) },
                    onNotificacoes = { nav.navigate(Tela.Comunidade.rota) }
                )
            }
            composable(Tela.Evolucao.rota) {
                TelaEvolucao(onVoltar = { nav.popBackStack() })
            }
            composable(Tela.EmUtilizacao.rota) {
                TelaEmUtilizacao(onVoltar = { nav.popBackStack() },
                    onFinalizado = { nav.popBackStack() },
                    onReportar = { id -> nav.navigate(Tela.ReportarProblema.criar(id)) })
            }
            composable(
                Tela.RegistroUtilizacao.rota,
                arguments = listOf(navArgument("equipamentoId") { type = NavType.StringType; nullable = true })
            ) { entrada ->
                TelaRegistroUtilizacao(
                    equipamentoId = entrada.arguments?.getString("equipamentoId")?.toIntOrNull(),
                    onVoltar = { nav.popBackStack() },
                    onUsoIniciado = {
                        nav.navigate(Tela.EmUtilizacao.rota) {
                            popUpTo(Tela.RegistroUtilizacao.rota) { inclusive = true }
                        }
                    },
                    onReportarProblema = { id -> nav.navigate(Tela.ReportarProblema.criar(id)) }
                )
            }
            composable(
                Tela.ReportarProblema.rota,
                arguments = listOf(navArgument("equipamentoId") { type = NavType.StringType; nullable = true })
            ) { entrada ->
                TelaReportarProblema(
                    equipamentoId = entrada.arguments?.getString("equipamentoId")?.toIntOrNull(),
                    onVoltar = { nav.popBackStack() },
                    onEnviarRelato = {
                        nav.navigate(Tela.ProblemaReportado.rota) {
                            popUpTo(Tela.ReportarProblema.rota) { inclusive = true }
                        }
                    }
                )
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
            composable(
                Tela.ResumoTreino.rota,
                arguments = listOf(navArgument("execucaoId") { type = NavType.LongType })
            ) { entrada ->
                TelaResumoTreino(
                    execucaoId = entrada.arguments?.getLong("execucaoId") ?: 0L,
                    onVoltar = {
                        if (!nav.popBackStack(Tela.Treinos.rota, inclusive = false)) nav.navigate(Tela.Treinos.rota)
                    }
                )
            }
            composable(Tela.Grupos.rota) {
                TelaGrupos(onCriar = { nav.navigate(Tela.CriarGrupo.rota) },
                    onChatbot = { nav.navigate(Tela.Chatbot.rota) },
                    onVoltar = { nav.popBackStack() })
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

            // ==========================================
            // TELAS DO INSTRUTOR
            // ==========================================
            composable(Tela.GerenciarEquipamentos.rota) {
                TelaGerenciarEquipamentos(
                    onBackClick = { nav.popBackStack() },
                    onCadastrarClick = { nav.navigate(Tela.CadastrarEquipamento.criar()) },
                    onEquipamentoClick = { id -> nav.navigate(Tela.CadastrarEquipamento.criar(id)) }
                )
            }
            composable(
                Tela.CadastrarEquipamento.rota,
                arguments = listOf(navArgument("equipamentoId") { type = NavType.StringType; nullable = true })
            ) { entrada ->
                TelaCadastrarEquipamento(
                    equipamentoId = entrada.arguments?.getString("equipamentoId")?.toIntOrNull(),
                    onBackClick = { nav.popBackStack() },
                    onSaveClick = { nav.popBackStack() }
                )
            }
            composable(
                Tela.NovaFichaTreino.rota,
                arguments = listOf(navArgument("alunoId") { type = NavType.StringType; nullable = true })
            ) { entrada ->
                TelaNovaFichaTreino(
                    alunoIdInicial = entrada.arguments?.getString("alunoId"),
                    onBackClick = { nav.popBackStack() },
                    onSaveClick = { nav.popBackStack() }
                )
            }
            composable(Tela.RelatosProblemas.rota) {
                TelaRelatosProblemas(
                    onBackClick = { nav.popBackStack() }
                )
            }
            composable(Tela.AcessibilidadeSuporte.rota) {
                TelaAcessibilidadeSuporte(
                    onVoltarClick = { nav.popBackStack() },
                    onFalarSuporteClick = { nav.navigate(Tela.Chatbot.rota) }
                )
            }
            composable(Tela.PainelInstrutor.rota) {
                TelaPainelInstrutor(
                    onMeusAlunosClick = { nav.navigate(Tela.ListaAlunos.rota) },
                    onFichasTreinoClick = { nav.navigate(Tela.NovaFichaTreino.criar()) },
                    onEquipamentosClick = { nav.navigate(Tela.GerenciarEquipamentos.rota) },
                    onRelatosProblemasClick = { nav.navigate(Tela.RelatosProblemas.rota) },
                    onNotificacoesClick = { nav.navigate(Tela.RelatosProblemas.rota) }
                )
            }
            composable(Tela.ListaAlunos.rota) {
                TelaListaAlunos(
                    onVoltarClick = { nav.popBackStack() },
                    onAlunoClick = { alunoId -> nav.navigate(Tela.DetalhesAluno.criar(alunoId)) },
                    onPainelClick = { nav.popBackStack(Tela.PainelInstrutor.rota, inclusive = false) },
                    onFichasClick = { nav.navigate(Tela.NovaFichaTreino.criar()) },
                    onEquipamentosClick = { nav.navigate(Tela.GerenciarEquipamentos.rota) }
                )
            }
            composable(
                Tela.DetalhesAluno.rota,
                arguments = listOf(navArgument("alunoId") { type = NavType.StringType })
            ) { entrada ->
                TelaDetalhesAluno(
                    alunoId = entrada.arguments?.getString("alunoId").orEmpty(),
                    onVoltarClick = { nav.popBackStack() },
                    onCriarNovaFichaClick = { id -> nav.navigate(Tela.NovaFichaTreino.criar(id)) }
                )
            }
        }

    }
}
