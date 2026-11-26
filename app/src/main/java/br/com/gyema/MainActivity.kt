package br.com.gyema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import br.com.gyema.data.RepositorioInstrutor
import br.com.gyema.data.Supabase
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.GyemaBottomBar
import br.com.gyema.ui.navegacao.Tela
import br.com.gyema.ui.tema.*
import br.com.gyema.ui.telas.*
import android.content.Intent
import androidx.compose.ui.Alignment
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

// estado compartilhado do cadastro
class EstadoCadastro {
    var nome by mutableStateOf("")
    var email by mutableStateOf("")
    var senha by mutableStateOf("")
    var cpf by mutableStateOf("")
    var matricula by mutableStateOf("")
    var dataNascimento by mutableStateOf("")
    var genero by mutableStateOf("")
    var peso by mutableStateOf("")
    var altura by mutableStateOf("")
    var contatoEmergencia by mutableStateOf("")

    fun limpar() {
        nome = ""
        email = ""
        senha = ""
        cpf = ""
        matricula = ""
        dataNascimento = ""
        genero = ""
        peso = ""
        altura = ""
        contatoEmergencia = ""
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { GyemaTheme { GyemaApp() } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@Composable
fun GyemaApp() {
    val nav = rememberNavController()
    val estadoCadastro = remember { EstadoCadastro() }

    // controle de sessao
    val sessionStatusState = if (Supabase.configurado) {
        Supabase.cliente.auth.sessionStatus.collectAsState()
    } else null
    val status = sessionStatusState?.value ?: SessionStatus.NotAuthenticated(isSignOut = false)

    LaunchedEffect(status) {
        when (status) {
            is SessionStatus.Authenticated -> {
                val rotaAtual = nav.currentDestination?.route
                if (rotaAtual == null || rotaAtual == Tela.BoasVindas.rota) {
                    val destino = runCatching {
                        val perfil = RepositorioInstrutor.meuPerfil()
                        if (perfil.perfil.lowercase() == "instrutor") {
                            Tela.PainelInstrutor.rota
                        } else {
                            Tela.Inicio.rota
                        }
                    }.getOrDefault(Tela.Inicio.rota)

                    nav.navigate(destino) {
                        popUpTo(Tela.BoasVindas.rota) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is SessionStatus.NotAuthenticated -> {
                val rotaAtual = nav.currentDestination?.route
                if (rotaAtual != null && rotaAtual != Tela.BoasVindas.rota && rotaAtual != Tela.Login.rota && rotaAtual != Tela.Recuperacao.rota && !rotaAtual.startsWith("cadastro")) {
                    nav.navigate(Tela.BoasVindas.rota) {
                        popUpTo(nav.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            else -> {}
        }
    }

    // carregando inicial
    if (status is SessionStatus.Initializing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Fundo),
            contentAlignment = Alignment.Center
        ) {
            EstadoCarregando()
        }
        return
    }

    val entry by nav.currentBackStackEntryAsState()
    val rota = entry?.destination?.route
    val abaAtual = when (rota) {
        Tela.MapaAcademia.rota, Tela.Equipamentos.rota -> Tela.Academia.rota
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
                    onCadastrar = { nav.navigate(Tela.CadastroPasso1.rota) })
            }

            // fluxo de login e redirecionamento
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
                TelaCadastroPasso1(
                    onVoltar = { nav.popBackStack() },
                    onAvancar = { nome, email, senha ->
                        estadoCadastro.nome = nome
                        estadoCadastro.email = email
                        estadoCadastro.senha = senha
                        nav.navigate(Tela.CadastroPasso2.rota)
                    },
                    onLogin = { nav.navigate(Tela.Login.rota) },
                    nomeInicial = estadoCadastro.nome,
                    emailInicial = estadoCadastro.email,
                    senhaInicial = estadoCadastro.senha
                )
            }
            composable(Tela.CadastroPasso2.rota) {
                TelaCadastroPasso2(
                    onVoltar = { nav.popBackStack() },
                    onAvancar = { cpf, matricula, dataNascimento, genero, peso, altura ->
                        estadoCadastro.cpf = cpf
                        estadoCadastro.matricula = matricula
                        estadoCadastro.dataNascimento = dataNascimento
                        estadoCadastro.genero = genero
                        estadoCadastro.peso = peso
                        estadoCadastro.altura = altura
                        nav.navigate(Tela.CadastroPasso3.rota)
                    },
                    cpfInicial = estadoCadastro.cpf,
                    matriculaInicial = estadoCadastro.matricula,
                    dataNascimentoInicial = estadoCadastro.dataNascimento,
                    generoInicial = estadoCadastro.genero,
                    pesoInicial = estadoCadastro.peso,
                    alturaInicial = estadoCadastro.altura
                )
            }
            composable(Tela.CadastroPasso3.rota) {
                val scope = rememberCoroutineScope()
                var carregando by remember { mutableStateOf(false) }
                var erroGeral by remember { mutableStateOf<String?>(null) }

                TelaCadastroPasso3(
                    onVoltar = { nav.popBackStack() },
                    onConcluir = { contato, respostas ->
                        estadoCadastro.contatoEmergencia = contato
                        if (Supabase.configurado) {
                            carregando = true
                            erroGeral = null
                            scope.launch {
                                tentar {
                                    // "27/06/1995" -> "1995-06-27" (formato que o banco aceita)
                                    val dataBanco = estadoCadastro.dataNascimento.trim().split("/")
                                        .takeIf { it.size == 3 }
                                        ?.let { (d, m, a) -> "$a-${m.padStart(2, '0')}-${d.padStart(2, '0')}" }

                                    // Os dados vao junto com a criacao da conta: o gatilho do banco
                                    // preenche "perfis" e "respostas_prontidao" (README do supabase).
                                    // Valor vazio ou fora do formato e ignorado, sem quebrar o cadastro.
                                    Supabase.cliente.auth.signUpWith(Email) {
                                        this.email = estadoCadastro.email.trim()
                                        this.password = estadoCadastro.senha
                                        this.data = buildJsonObject {
                                            put("nome", estadoCadastro.nome.trim())
                                            put("cpf", estadoCadastro.cpf.trim())
                                            put("matricula", estadoCadastro.matricula.trim())
                                            dataBanco?.let { put("data_nascimento", it) }
                                            put("genero", estadoCadastro.genero.trim())
                                            put("peso_kg", estadoCadastro.peso)
                                            put("altura_cm", estadoCadastro.altura)
                                            put("contato_emergencia", estadoCadastro.contatoEmergencia.trim())
                                            putJsonObject("prontidao") {
                                                respostas.forEach { (id, resposta) -> put(id.toString(), resposta) }
                                            }
                                        }
                                    }
                                }.onSuccess {
                                    carregando = false
                                    estadoCadastro.limpar()
                                    nav.navigate(Tela.Inicio.rota) {
                                        popUpTo(Tela.BoasVindas.rota) { inclusive = true }
                                    }
                                }.onFailure { throwable ->
                                    carregando = false
                                    val texto = throwable.message.orEmpty()
                                    erroGeral = when {
                                        "already registered" in texto || "already been registered" in texto ->
                                            "Este e-mail já tem uma conta. Entre pelo login ou use outro e-mail."
                                        "Password should be" in texto || "weak_password" in texto ->
                                            "A senha é fraca. Use pelo menos 6 caracteres."
                                        "valid email" in texto || "email_address_invalid" in texto ->
                                            "E-mail inválido. Confira o endereço digitado."
                                        else -> mensagemDeErro(throwable)
                                    }
                                }
                            }
                        } else {
                            nav.navigate(Tela.Inicio.rota) {
                                popUpTo(Tela.BoasVindas.rota) { inclusive = true }
                            }
                        }
                    },
                    contatoInicial = estadoCadastro.contatoEmergencia,
                    carregando = carregando,
                    erroGeral = erroGeral,
                    onLimparErro = { erroGeral = null }
                )
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
                val scope = rememberCoroutineScope()
                TelaPerfil(
                    onSair = {
                        scope.launch {
                            if (Supabase.configurado) {
                                runCatching { Supabase.cliente.auth.signOut() }
                            }
                            nav.navigate(Tela.BoasVindas.rota) {
                                popUpTo(nav.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
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
                arguments = listOf(
                    navArgument("alunoId") { type = NavType.StringType; nullable = true },
                    navArgument("fichaId") { type = NavType.StringType; nullable = true }
                )
            ) { entrada ->
                TelaNovaFichaTreino(
                    alunoIdInicial = entrada.arguments?.getString("alunoId"),
                    fichaId = entrada.arguments?.getString("fichaId")?.toLongOrNull(),
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
                val scopeInstrutor = rememberCoroutineScope()
                TelaPainelInstrutor(
                    onMeusAlunosClick = { nav.navigate(Tela.ListaAlunos.rota) },
                    onFichasTreinoClick = { nav.navigate(Tela.NovaFichaTreino.criar()) },
                    onEquipamentosClick = { nav.navigate(Tela.GerenciarEquipamentos.rota) },
                    onRelatosProblemasClick = { nav.navigate(Tela.RelatosProblemas.rota) },
                    onNotificacoesClick = { nav.navigate(Tela.RelatosProblemas.rota) },
                    onSairClick = {
                        scopeInstrutor.launch {
                            if (Supabase.configurado) {
                                runCatching { Supabase.cliente.auth.signOut() }
                            }
                            nav.navigate(Tela.BoasVindas.rota) {
                                popUpTo(nav.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
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
                    onCriarNovaFichaClick = { id -> nav.navigate(Tela.NovaFichaTreino.criar(id)) },
                    onEditarFichaClick = { id, fichaId -> nav.navigate(Tela.NovaFichaTreino.criar(id, fichaId)) }
                )
            }
        }

    }
}
