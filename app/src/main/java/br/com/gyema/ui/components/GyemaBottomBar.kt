package br.com.gyema.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.navegacao.Tela
import br.com.gyema.ui.tema.*

@Composable
fun GyemaBottomBar(rotaAtual: String?, onNavegar: (Tela) -> Unit) {
    NavigationBar(containerColor = Superficie, tonalElevation = 0.dp) {
        Tela.abas.forEach { tela ->
            NavigationBarItem(
                selected = rotaAtual == tela.rota,
                onClick = { onNavegar(tela) },
                icon = { Icon(tela.icone, contentDescription = tela.titulo) },
                label = { Text(tela.titulo, fontSize = 10.sp) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Laranja,
                    selectedTextColor = Laranja,
                    unselectedIconColor = TextoSecundario,
                    unselectedTextColor = TextoSecundario,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PreviewGyemaBottomBar() {
    GyemaTheme { GyemaBottomBar(rotaAtual = "inicio", onNavegar = {}) }
}