package com.shakeyourtown.missions.storage

/**
 * Platform-agnostic key-value storage interface.
 * Implemented via localStorage on WASM and SharedPreferences on Android.
 */
expect object PlatformStorage {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
    fun remove(key: String)
    fun clear()
}
