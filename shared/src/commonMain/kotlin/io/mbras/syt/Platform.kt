package io.mbras.syt

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform