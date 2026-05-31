package com.hdt.basecompose.style

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data object Empty   : UiState<Nothing>()
    data class  Success<T>(val data: T) : UiState<T>()
    data class  Error(val message: String, val code: Int = -1) : UiState<Nothing>()
}

val <T> UiState<T>.isLoading get() = this is UiState.Loading
val <T> UiState<T>.isSuccess get() = this is UiState.Success
val <T> UiState<T>.isError   get() = this is UiState.Error
val <T> UiState<T>.dataOrNull get() = (this as? UiState.Success)?.data
