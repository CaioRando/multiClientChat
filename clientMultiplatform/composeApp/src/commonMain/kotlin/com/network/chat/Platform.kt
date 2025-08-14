package com.network.chat

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform