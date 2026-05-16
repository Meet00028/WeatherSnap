package com.weathersnap.data.repository

sealed class RepoResult<out T> {
    data class Success<T>(val data: T) : RepoResult<T>()
    object Empty : RepoResult<Nothing>()
    data class Error(val message: String, val throwable: Throwable? = null) : RepoResult<Nothing>()
}

