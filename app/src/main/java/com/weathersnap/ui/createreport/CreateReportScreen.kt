package com.weathersnap.ui.createreport

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.weathersnap.ui.camera.PhotoResult
import com.weathersnap.ui.navigation.Routes
import com.weathersnap.ui.theme.HumidityBg
import com.weathersnap.ui.theme.HumidityValue
import com.weathersnap.ui.theme.PressureBg
import com.weathersnap.ui.theme.PressureValue
import com.weathersnap.ui.weather.WeatherInfoCard
import com.weathersnap.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    navController: NavHostController,
    viewModel: CreateReportViewModel,
) {
    val state by viewModel.uiState.collectAsState()
    val navBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(navBackStackEntry) {
        navBackStackEntry?.savedStateHandle
            ?.getStateFlow<PhotoResult?>("photo_result", null)
            ?.collect { photo ->
                if (photo != null) {
                    viewModel.onPhotoResult(photo)
                    navBackStackEntry.savedStateHandle.remove<PhotoResult>("photo_result")
                }
            }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CreateReportEvent.NavigateToReports -> {
                    navController.navigate(Routes.Reports) {
                        popUpTo(Routes.Weather) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

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
                                text = "Create Report",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF0D1F08),
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Capture, compress, annotate",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1A3410),
                            )
                        }
                        ElevatedButton(
                            onClick = { navController.popBackStack() },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = Color(0xFF2A3D1A),
                                contentColor = Color(0xFFB8D4A0),
                            ),
                        ) {
                            Text("Back")
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            WeatherInfoCard(data = state.weatherData)

            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF3A4A10),
                                    Color(0xFF5A6E20),
                                    Color(0xFF7A8A30),
                                ),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Crossfade(targetState = state.photoResult, label = "photo_crossfade") { photo ->
                        if (photo == null) {
                            Text(
                                "Photo preview",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.9f, animationSpec = tween(220)),
                                exit = fadeOut(tween(150)),
                            ) {
                                AsyncImage(
                                    model = photo.compressedPath,
                                    contentDescription = "Captured photo",
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = state.photoResult != null,
                    enter = fadeIn(tween(180)),
                    exit = fadeOut(tween(120)),
                ) {
                    val photo = state.photoResult
                    if (photo != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PressureBg)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        "Original",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        "${Formatters.formatKb(photo.originalSizeBytes)} KB",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PressureValue,
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HumidityBg)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        "Compressed",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        "${Formatters.formatKb(photo.compressedSizeBytes)} KB",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = HumidityValue,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navController.navigate(Routes.Camera) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Capture Photo")
            }

            Text("Field Notes", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
            )

            if (state.errorMessage != null) {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = state.errorMessage ?: "",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = viewModel::saveReport,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving,
            ) {
                Text(if (state.isSaving) "Saving..." else "Save Report")
            }
        }
    }
}
