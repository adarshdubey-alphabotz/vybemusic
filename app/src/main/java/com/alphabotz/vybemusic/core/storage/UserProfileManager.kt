package com.alphabotz.vybemusic.core.storage

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val name: String = "Music Lover",
    val avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
    val favoriteArtists: Set<String> = emptySet(),
    val hasCompletedOnboarding: Boolean = false
)

object UserProfileManager {
    private const val PREFS_NAME = "vybe_user_prefs"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_AVATAR = "user_avatar"
    private const val KEY_FAVORITE_ARTISTS = "favorite_artists"
    private const val KEY_HAS_COMPLETED_ONBOARDING = "has_completed_onboarding"

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
            val artists = prefs?.getStringSet(KEY_FAVORITE_ARTISTS, emptySet()) ?: emptySet()
            val onboarding = prefs?.getBoolean(KEY_HAS_COMPLETED_ONBOARDING, false) ?: false

            _profile.value = UserProfile(
                name = name,
                avatarUrl = avatar,
                favoriteArtists = artists,
                hasCompletedOnboarding = onboarding
            )
        }
    }

    fun updateProfile(name: String, avatarUrl: String? = null) {
        val cleanName = name.trim().ifBlank { "Music Lover" }
        val currentAvatar = avatarUrl ?: _profile.value.avatarUrl
        _profile.value = _profile.value.copy(name = cleanName, avatarUrl = currentAvatar)

        prefs?.edit()?.apply {
            putString(KEY_USER_NAME, cleanName)
            putString(KEY_USER_AVATAR, currentAvatar)
            apply()
        }
    }

    fun saveArtists(artists: Set<String>) {
        _profile.value = _profile.value.copy(
            favoriteArtists = artists,
            hasCompletedOnboarding = true
        )
        prefs?.edit()?.apply {
            putStringSet(KEY_FAVORITE_ARTISTS, artists)
            putBoolean(KEY_HAS_COMPLETED_ONBOARDING, true)
            apply()
        }
    }

    fun resetOnboarding() {
        _profile.value = _profile.value.copy(hasCompletedOnboarding = false)
        prefs?.edit()?.putBoolean(KEY_HAS_COMPLETED_ONBOARDING, false)?.apply()
    }
}
