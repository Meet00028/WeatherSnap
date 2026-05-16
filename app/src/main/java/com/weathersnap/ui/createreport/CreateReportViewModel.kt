package com.weathersnap.ui.createreport

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.weathersnap.data.local.entity.ReportEntity
import com.weathersnap.data.repository.RepoResult
import com.weathersnap.data.repository.WeatherRepository
import com.weathersnap.domain.model.WeatherData
import com.weathersnap.ui.camera.PhotoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateReportUiState(
    val weatherData: WeatherData,
    val notes: String = "",
    val photoResult: PhotoResult? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface CreateReportEvent {
    data object NavigateToReports : CreateReportEvent
}

@HiltViewModel
class CreateReportViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val gson: Gson,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val weatherJson: String = Uri.decode(
        savedStateHandle.get<String>("weatherJson")
            ?: error("Missing weatherJson argument"),
    )

    private val initialWeatherData: WeatherData =
        gson.fromJson(weatherJson, WeatherData::class.java)

    private val _uiState = MutableStateFlow(CreateReportUiState(weatherData = initialWeatherData))
    val uiState: StateFlow<CreateReportUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<CreateReportEvent>(capacity = Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    fun onNotesChange(text: String) {
        _uiState.value = _uiState.value.copy(notes = text)
    }

    fun onPhotoResult(photo: PhotoResult) {
        _uiState.value = _uiState.value.copy(photoResult = photo)
    }

    fun saveReport() {
        val snapshot = _uiState.value
        val photo = snapshot.photoResult ?: run {
            _uiState.value = snapshot.copy(errorMessage = "Please capture a photo before saving.")
            return
        }

        _uiState.value = snapshot.copy(isSaving = true, errorMessage = null)

        viewModelScope.launch {
            val report = ReportEntity(
                cityName = snapshot.weatherData.cityName,
                country = snapshot.weatherData.country,
                condition = snapshot.weatherData.condition,
                temperatureCelsius = snapshot.weatherData.temperatureCelsius,
                humidity = snapshot.weatherData.humidity,
                windSpeedMs = snapshot.weatherData.windSpeedMs,
                pressureHpa = snapshot.weatherData.pressureHpa,
                originalImagePath = photo.originalPath,
                compressedImagePath = photo.compressedPath,
                originalSizeBytes = photo.originalSizeBytes,
                compressedSizeBytes = photo.compressedSizeBytes,
                notes = snapshot.notes,
            )

            when (val result = repository.insertReport(report)) {
                is RepoResult.Success -> {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    eventsChannel.trySend(CreateReportEvent.NavigateToReports)
                }

                is RepoResult.Error -> {
                    _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = result.message)
                }

                RepoResult.Empty -> {
                    _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = "Unable to save report.")
                }
            }
        }
    }
}
