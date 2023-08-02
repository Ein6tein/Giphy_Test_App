package com.chernishenko.giphytestapp.exceptions

class RequestFailedException : Exception() {
    override val message: String
        get() = "Oops, something went wrong. Please try again later."
}