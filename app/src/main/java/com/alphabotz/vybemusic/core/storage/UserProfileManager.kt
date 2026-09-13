package com.alphabotz.vybemusic.core.storage

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val name: String = "Music Lover",
    val avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200"
)

object UserProfileManager {
    private const val PREFS_NAME = "vybe_user_prefs"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_AVATAR = "user_avatar"

    val AVATAR_OPTIONS = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
        "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=200",
        "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=200"
    )

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val name = prefs?.getString(KEY_USER_NAME, "Music Lover") ?: "Music Lover"
            val avatar = prefs?.getString(KEY_USER_AVATAR, AVATAR_OPTIONS.first()) ?: AVATAR_OPTIONS.first()
            _profile.value = UserProfile(name = name, avatarUrl = avatar)
        }
    }

    fun updateProfile(name: String, avatarUrl: String? = null) {
        val cleanName = name.trim().ifBlank { "Music Lover" }
        val currentAvatar = avatarUrl ?: _profile.value.avatarUrl
        _profile.value = UserProfile(name = cleanName, avatarUrl = currentAvatar)

        prefs?.edit()?.apply {
            putString(KEY_USER_NAME, cleanName)
            putString(KEY_USER_AVATAR, currentAvatar)
            apply()
        }
    }
}
