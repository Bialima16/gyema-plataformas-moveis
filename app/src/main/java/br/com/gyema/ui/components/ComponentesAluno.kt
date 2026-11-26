package br.com.gyema.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import br.com.gyema.data.SetorOcupacao
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime

fun statusAmbiente(status: String): StatusAmbiente = when (status) {
    "ocupado" -> StatusAmbiente.Ocupado
    "manutencao" -> StatusAmbiente.Manutencao
    else -> StatusAmbiente.Disponivel
}

fun iconeSetor(nome: String?): ImageVector = when (nome) {
    "FitnessCenter" -> Icons.Outlined.FitnessCenter
    "MonitorHeart" -> Icons.Outlined.MonitorHeart
    "SelfImprovement" -> Icons.Outlined.SelfImprovement
    "BusinessCenter" -> Icons.Outlined.BusinessCenter
    else -> Icons.Outlined.FitnessCenter
}

fun iconeEquipamento(grupoMuscular: String?): ImageVector = when (grupoMuscular?.lowercase()) {
    "cardio" -> Icons.AutoMirrored.Outlined.DirectionsRun
    "pernas", "quadríceps", "posterior", "glúteos", "panturrilha" -> Icons.Outlined.Accessibility
    else -> Icons.Outlined.FitnessCenter
}

fun percentualLotacao(setores: List<SetorOcupacao>): Int {
    val usaveis = setores.sumOf { it.totalEquipamentos - it.emManutencao }
    if (usaveis <= 0) return 0
    return setores.sumOf { it.ocupados } * 100 / usaveis
}

fun percentualLotacao(setor: SetorOcupacao): Int = percentualLotacao(listOf(setor))

@Composable
fun rememberAgora(): Instant {
    var agora by remember { mutableStateOf(Instant.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            agora = Instant.now()
        }
    }
    return agora
}

fun segundosRestantes(fimIso: String, agora: Instant): Long {
    val fim = runCatching { OffsetDateTime.parse(fimIso).toInstant() }.getOrNull() ?: return 0
    return Duration.between(agora, fim).seconds.coerceAtLeast(0)
}

fun formatarCronometro(segundos: Long): String {
    val h = segundos / 3600
    val m = (segundos % 3600) / 60
    val s = segundos % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

fun fracaoRestante(inicioIso: String, fimIso: String, agora: Instant): Float {
    val inicio = runCatching { OffsetDateTime.parse(inicioIso).toInstant() }.getOrNull() ?: return 0f
    val fim = runCatching { OffsetDateTime.parse(fimIso).toInstant() }.getOrNull() ?: return 0f
    val total = Duration.between(inicio, fim).seconds
    if (total <= 0) return 0f
    return (Duration.between(agora, fim).seconds.toFloat() / total).coerceIn(0f, 1f)
}

fun textoLotacao(percentual: Int): String = when {
    percentual < 40 -> "Tranquilo (Ótimo para treinar)"
    percentual < 75 -> "Moderado (Ideal para treinar)"
    else -> "Cheio (Pode haver espera)"
}

fun numeroBr(valor: Double, casas: Int = 1): String {
    val texto = "%.${casas}f".format(java.util.Locale.US, valor)
    val semZeros = if ('.' in texto) texto.trimEnd('0').trimEnd('.') else texto
    return semZeros.replace('.', ',')
}

fun minutosEntre(inicioIso: String, fimIso: String?): Long {
    if (fimIso == null) return 0
    return runCatching {
        Duration.between(OffsetDateTime.parse(inicioIso), OffsetDateTime.parse(fimIso)).toMinutes()
    }.getOrDefault(0)
}

fun contagem(n: Int, singular: String, plural: String): String = "$n ${if (n == 1) singular else plural}"
