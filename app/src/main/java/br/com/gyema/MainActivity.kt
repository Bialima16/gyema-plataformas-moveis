package br.com.gyema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.gyema.ui.components.GyemaBottomBar
import br.com.gyema.ui.navegacao.Tela
import br.com.gyema.ui.telas.TelaCadastrarEquipamento
import br.com.gyema.ui.telas.TelaComunidade
import br.com.gyema.ui.telas.TelaEmUtilizacao
import br.com.gyema.ui.telas.TelaEvolucao
import br.com.gyema.ui.telas.TelaGerenciarEquipamentos
import br.com.gyema.ui.telas.TelaNovaFichaTreino
import br.com.gyema.ui.telas.TelaPerfil
import br.com.gyema.ui.telas.TelaPrincipal
import br.com.gyema.ui.telas.TelaPublicacao
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

            // Aba Treinos com botões de teste para as novas telas
            composable(Tela.Treinos.rota) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Fundo).padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { nav.navigate(Tela.NovaFichaTreino.rota) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Abrir Nova Ficha de Treino")
                        }
                        Button(
                            onClick = { nav.navigate(Tela.GerenciarEquipamentosCarregando.rota) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Abrir Equipamentos (Carregando)")
                        }
                    }
                }
            }

            // Aba Academia ligada ao Gerenciamento de Equipamentos normal
            composable(Tela.Academia.rota) {
                TelaGerenciarEquipamentos(
                    isLoadingInicial = false,
                    onBackClick = { nav.popBackStack() },
                    onCadastrarClick = { nav.navigate(Tela.CadastrarEquipamento.rota) }
                )
            }

            // Rota da Tela de Carregamento (Simula 2s de carregamento)
            composable(Tela.GerenciarEquipamentosCarregando.rota) {
                TelaGerenciarEquipamentos(
                    isLoadingInicial = true,
                    onBackClick = { nav.popBackStack() },
                    onCadastrarClick = { nav.navigate(Tela.CadastrarEquipamento.rota) }
                )
            }

            composable(Tela.GerenciarEquipamentos.rota) {
                TelaGerenciarEquipamentos(
                    isLoadingInicial = false,
                    onBackClick = { nav.popBackStack() },
                    onCadastrarClick = { nav.navigate(Tela.CadastrarEquipamento.rota) }
                )
            }
            composable(Tela.CadastrarEquipamento.rota) {
                TelaCadastrarEquipamento(
                    onBackClick = { nav.popBackStack() },
                    onSaveClick = { nav.popBackStack() }
                )
            }
            composable(Tela.NovaFichaTreino.rota) {
                TelaNovaFichaTreino(
                    onBackClick = { nav.popBackStack() },
                    onSaveClick = { nav.popBackStack() }
                )
            }
        }
    }
}