package com.local.data.repository

sealed class DataState<out R> {
    data object Loading : DataState<Nothing>()
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val errorMessage: String, val errorCode: String? = null) : DataState<Nothing>()
}
