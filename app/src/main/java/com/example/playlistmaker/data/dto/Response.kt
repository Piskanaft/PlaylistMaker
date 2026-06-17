package com.example.playlistmaker.data.dto

open class Response {
    var resultCode = 0
    fun isSuccess() = resultCode in 200..299

}