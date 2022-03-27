package ru.netology.vm

data class LoadingState(
    val newPostNotify: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorDescription: ErrorData? = null,
)
