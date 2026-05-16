package com.weathersnap.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.weathersnap.data.repository.RepoResult
import com.weathersnap.data.repository.WeatherRepository
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.WeatherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val data: WeatherData, val isCached: Boolean) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
    data object Empty : WeatherUiState
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val gson: Gson,
) : ViewModel() {

    private val suggestionCache = HashMap<String, List<City>>()
    private val lastWeatherByCityKey = HashMap<String, WeatherData>()

    private val _cityQuery = MutableStateFlow("")
    val cityQuery: StateFlow<String> = _cityQuery.asStateFlow()

    private val _suggestions = MutableStateFlow<List<City>>(emptyList())
    val suggestions: StateFlow<List<City>> = _suggestions.asStateFlow()

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var lastRequestedCity: City? = null

    init {
        observeCityQuery()
    }

    fun onCityQueryChange(text: String) {
        _cityQuery.value = text
        if (text.length <= 2) {
            _suggestions.value = emptyList()
        }
    }

    fun onSuggestionSelected(city: City) {
        _cityQuery.value = city.name
        _suggestions.value = emptyList()
        fetchWeather(city)
    }

    fun retry() {
        lastRequestedCity?.let { fetchWeather(it) }
    }

    fun weatherToJson(weatherData: WeatherData): String = gson.toJson(weatherData)

    @OptIn(FlowPreview::class)
    private fun observeCityQuery() {
        viewModelScope.launch {
            _cityQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.length <= 2) return@collect
                    val cached = suggestionCache[query]
                    if (cached != null) {
                        _suggestions.value = cached
                        return@collect
                    }

                    when (val result = repository.searchCities(query)) {
                        is RepoResult.Success -> {
                            suggestionCache[query] = result.data
                            _suggestions.value = result.data
                        }

                        RepoResult.Empty -> _suggestions.value = emptyList()
                        is RepoResult.Error -> _suggestions.value = emptyList()
                    }
                }
        }
    }

    private fun fetchWeather(city: City) {
        lastRequestedCity = city
        _uiState.value = WeatherUiState.Loading
        viewModelScope.launch {
            val key = cityKey(city)
            when (val result = repository.getWeather(city)) {
                is RepoResult.Success -> {
                    val data = result.data
                    lastWeatherByCityKey[key] = data
                    repository.saveCachedWeather(key, data)
                    _uiState.value = WeatherUiState.Success(data = data, isCached = false)
                }

                RepoResult.Empty -> _uiState.value = WeatherUiState.Empty
                is RepoResult.Error -> {
                    val cached = lastWeatherByCityKey[key] ?: repository.loadCachedWeather(key)
                    if (cached != null) {
                        _uiState.value = WeatherUiState.Success(data = cached, isCached = true)
                    } else {
                        _uiState.value = WeatherUiState.Error(result.message)
                    }
                }
            }
        }
    }

    private fun cityKey(city: City): String = "${city.name}|${city.country}"
}

