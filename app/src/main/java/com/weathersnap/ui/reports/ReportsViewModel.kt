package com.weathersnap.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.data.local.entity.ReportEntity
import com.weathersnap.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    repository: WeatherRepository,
) : ViewModel() {
    val reports: StateFlow<List<ReportEntity>> =
        repository.getAllReports()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

