package com.shakeyourtown.missions.storage

/**
 * Android implementation using a simple in-memory map with file-backed persistence.
 * For a production app this should use SharedPreferences or DataStore,
 * but for the MVP this keeps things dependency-free.
 *
 * NOTE: On Android, you would replace this with SharedPreferences injection.
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
