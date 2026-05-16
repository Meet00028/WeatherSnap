package com.weathersnap.ui.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.WeatherData
import com.weathersnap.ui.navigation.Routes
import com.weathersnap.ui.theme.HumidityBg
import com.weathersnap.ui.theme.HumidityValue
import com.weathersnap.ui.theme.OlivePrimary
import com.weathersnap.ui.theme.PressureBg
import com.weathersnap.ui.theme.PressureValue
import com.weathersnap.ui.theme.WindBg
import com.weathersnap.ui.theme.WindValue

fun conditionToEmoji(condition: String): String = when {
    condition.contains("Clear", ignoreCase = true) -> "☀️"
    condition.contains("Mainly clear", ignoreCase = true) -> "🌤️"
    condition.contains("Partly cloudy", ignoreCase = true) -> "⛅"
    condition.contains("Overcast", ignoreCase = true) -> "☁️"
    condition.contains("Fog", ignoreCase = true) -> "🌫️"
    condition.contains("Drizzle", ignoreCase = true) -> "🌦️"
    condition.contains("Rain", ignoreCase = true) -> "🌧️"
    condition.contains("Snow", ignoreCase = true) -> "❄️"
    condition.contains("Thunder", ignoreCase = true) -> "⛈️"
    else -> "🌡️"
}

object WeatherScreenTags {
    const val CityInput = "city_input"
    const val SuggestionsList = "suggestions_list"
    const val WeatherCard = "weather_card"
    const val OfflineBanner = "offline_banner"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    navController: NavHostController,
    viewModel: WeatherViewModel,
) {
    val query by viewModel.cityQuery.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF060C04))
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFB8E0A0), Color(0xFFD4E87A)),
                            ),
                            shape = RoundedCornerShape(16.dp),
                        )
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = "WeatherSnap",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF0D1F08),
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Live weather reports with camera evidence",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1A3410),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        ElevatedButton(
                            onClick = { navController.navigate(Routes.Reports) },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = Color(0xFF2A3D1A),
                                contentColor = Color(0xFFB8D4A0),
                            ),
                        ) {
                            Text("Reports")
                        }
                    }
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::onCityQueryChange,
                    label = { Text("City") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag(WeatherScreenTags.CityInput),
                    singleLine = true,
                )
                ElevatedButton(
                    onClick = { /* search already triggers via debounce */ },
                    colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                        containerColor = OlivePrimary,
                        contentColor = Color(0xFF1A2810),
                    ),
                    shape = RoundedCornerShape(50),
                ) {
                    Text("Search", fontWeight = FontWeight.SemiBold)
                }
            }
            Text(
                text = "Enter more than 2 letters to start city suggestions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            AnimatedVisibility(
                visible = suggestions.isNotEmpty() && query.length > 2,
                enter = fadeIn(tween(150)) + slideInVertically(tween(180)) { it / 3 },
                exit = fadeOut(tween(120)) + slideOutVertically(tween(180)) { it / 3 },
                modifier = Modifier.fillMaxWidth(),
            ) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .testTag(WeatherScreenTags.SuggestionsList),
                    ) {
                        itemsIndexed(suggestions) { index, city ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(delayMillis = index * 30)),
                            ) {
                                SuggestionItem(city = city, onClick = { viewModel.onSuggestionSelected(city) })
                            }
                        }
                    }
                }
            }

            AnimatedContent(
                targetState = uiState,
                label = "weather_state",
            ) { state ->
                when (state) {
                    WeatherUiState.Idle -> {
                        EmptyCard(
                            title = "Search for a city",
                            message = "Pick a suggestion to load the current weather.",
                        )
                    }

                    WeatherUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            androidx.compose.animation.AnimatedVisibility(visible = true, enter = fadeIn()) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    is WeatherUiState.Success -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            this.AnimatedVisibility(
                                visible = state.isCached,
                                enter = fadeIn(tween(180)),
                                exit = fadeOut(tween(120)),
                            ) {
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag(WeatherScreenTags.OfflineBanner),
                                ) {
                                    Text(
                                        text = "Showing cached data — you're offline",
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }

                            WeatherInfoCard(
                                data = state.data,
                                modifier = Modifier.testTag(WeatherScreenTags.WeatherCard),
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = {
                                        navController.navigate(
                                            Routes.createReport(viewModel.weatherToJson(state.data)),
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Create Report")
                                }
                                OutlinedButton(
                                    onClick = { navController.navigate(Routes.Reports) },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Reports")
                                }
                            }
                        }
                    }

                    is WeatherUiState.Error -> {
                        ErrorCard(message = state.message, onRetry = viewModel::retry)
                    }

                    WeatherUiState.Empty -> {
                        EmptyCard(
                            title = "No weather data",
                            message = "Try a different city.",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    city: City,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(city.name, style = MaterialTheme.typography.titleMedium)
            Text(
                city.country,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun WeatherInfoCard(
    data: WeatherData,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${data.cityName}, ${data.country}",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = data.condition,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = conditionToEmoji(data.condition),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "${"%.0f".format(data.temperatureCelsius)}°C",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MetricChip(
                    label = "Humidity",
                    value = "${data.humidity}%",
                    bgColor = HumidityBg,
                    valueColor = HumidityValue,
                    modifier = Modifier.weight(1f),
                )
                MetricChip(
                    label = "Wind",
                    value = "${data.windSpeedMs} m/s",
                    bgColor = WindBg,
                    valueColor = WindValue,
                    modifier = Modifier.weight(1f),
                )
                MetricChip(
                    label = "Pressure",
                    value = "${data.pressureHpa} hPa",
                    bgColor = PressureBg,
                    valueColor = PressureValue,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Report readiness",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "Camera and Room DB enabled",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OlivePrimary,
                )
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    value: String,
    bgColor: androidx.compose.ui.graphics.Color,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = valueColor,
            )
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Something went wrong", style = MaterialTheme.typography.titleMedium)
            Text(message, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
private fun EmptyCard(
    title: String,
    message: String,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
