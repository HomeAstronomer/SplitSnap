package com.example.aisplitwise.data.repository

sealed class DataState<out R> {
    data class Success<out T>(val data:T):DataState<T>()
    data class Error(val errorMessage:String,val errorCode:String?=null):DataState<Nothing>()
}