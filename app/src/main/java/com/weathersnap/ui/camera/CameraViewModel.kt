package com.weathersnap.ui.camera

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.util.CompressionResult
import com.weathersnap.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun compressAsync(
        context: Context,
        originalFile: File,
        onDone: (CompressionResult) -> Unit,
    ) {
        _isProcessing.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            runCatching { ImageCompressor.compressJpeg60(context, originalFile) }
                .onSuccess { result ->
                    _isProcessing.value = false
                    withContext(kotlinx.coroutines.Dispatchers.Main) { onDone(result) }
                }
                .onFailure {
                    _isProcessing.value = false
                    _errorMessage.value = "Failed to compress photo."
                }
        }
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }
}
