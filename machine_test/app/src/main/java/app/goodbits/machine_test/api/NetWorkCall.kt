package com.tailormade.api

import java.lang.Exception

public sealed class NetWorkCall <out T>{
    data class Success<out T>(val response: T):NetWorkCall<T>()
    data class Error(val exception: String):NetWorkCall<Nothing>()
    object Loading : NetWorkCall<Nothing>()
}