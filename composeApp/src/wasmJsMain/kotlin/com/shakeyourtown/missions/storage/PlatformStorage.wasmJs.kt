package com.shakeyourtown.missions.storage

import kotlinx.browser.window

actual object PlatformStorage {
    actual fun getString(key: String): String? {
        return window.localStorage.getItem(key)
    }

    actual fun putString(key: String, value: String) {
        window.localStorage.setItem(key, value)
    }

    actual fun remove(key: String) {
        window.localStorage.removeItem(key)
    }

    actual fun clear() {
        window.localStorage.clear()
    }
}
