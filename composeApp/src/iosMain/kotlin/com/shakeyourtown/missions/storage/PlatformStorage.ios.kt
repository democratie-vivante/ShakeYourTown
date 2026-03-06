package com.shakeyourtown.missions.storage

/**
 * iOS implementation using a simple in-memory map.
 * For a production app this should use NSUserDefaults.
 * For now the WASM target is the priority.
 */
actual object PlatformStorage {
    private val store = mutableMapOf<String, String>()

    actual fun getString(key: String): String? = store[key]

    actual fun putString(key: String, value: String) {
        store[key] = value
    }

    actual fun remove(key: String) {
        store.remove(key)
    }

    actual fun clear() {
        store.clear()
    }
}
