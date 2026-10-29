package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.tema.*

@Composable
fun TelaAcessibilidadeSuporte(
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    onFalarSuporteClick: () -> Unit = {},
    traducaoLibrasAtiva: Boolean = false,
    onTraducaoLibrasChange: (Boolean) -> Unit = {}
) {
    var librasAtiva by rememberSaveable { mutableStateOf(traducaoLibrasAtiva) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // cabecalho
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SuperficieEscura)
                    .clickable(onClick = onVoltarClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Acessibilidade e Suporte",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // traducao em libras
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "TRADUÇÃO EM LIBRAS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextoSecundario,
                letterSpacing = 0.5.sp
            )

            CardGyema(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ativar tradução em Libras",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Recurso disponível via integração com o AVA da Unifor",
                            fontSize = 12.sp,
                            color = TextoSecundario,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = librasAtiva,
                        onCheckedChange = { checked ->
                            librasAtiva = checked
                            onTraducaoLibrasChange(checked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Laranja,
                            uncheckedThumbColor = TextoSecundario,
                            uncheckedTrackColor = SuperficieEscura
                        )
                    )
                }
            }
        }

        // suporte tecnico
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "SUPORTE TÉCNICO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextoSecundario,
                letterSpacing = 0.5.sp
            )

            CardGyema(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(LaranjaEscuro.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = null,
                                tint = Laranja,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Precisa de ajuda com o aplicativo?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Borda, RoundedCornerShape(12.dp))
                            .clickable(onClick = onFalarSuporteClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Falar com o suporte",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Laranja
                        )
                    }
                }
            }
        }
    }
}

// preview da tela
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaAcessibilidadeSuporte() {
    GyemaTheme {
        TelaAcessibilidadeSuporte(
            onVoltarClick = {},
            onFalarSuporteClick = {}
        )
    }
}
