package com.alphabotz.vybemusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alphabotz.vybemusic.core.storage.UserProfileManager
import com.alphabotz.vybemusic.ui.theme.*

/**
 * Authentic YouTube Music inspired Account & Profile Screen.
 * Includes user avatar & handle, 2026 Recap banner, listening stats,
 * and YouTube Music style settings.
 */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenArtistPicker: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val profile by UserProfileManager.profile.collectAsState()
    val remainingSleep by com.alphabotz.vybemusic.core.playback.SleepTimerManager.remainingSeconds.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showSleepDialog by remember { mutableStateOf(false) }
    var showEqDialog by remember { mutableStateOf(false) }
    var activeEqPreset by remember { mutableStateOf("Dolby Atmos Virtualizer") }

    val handle = "@" + profile.name.lowercase().replace(" ", "")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VybeBackground)
            .statusBarsPadding()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 120.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // YouTube Music Profile Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    // Avatar with glowing ring
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(3.dp, VybeVolt, CircleShape)
                            .clickable { showEditDialog = true }
                    ) {
                        AsyncImage(
                            model = profile.avatarUrl,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = profile.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = handle,
                        fontSize = 14.sp,
                        color = Color(0xFFA0A5BA),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Edit Profile Pill Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0x22FFFFFF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x35FFFFFF)),
                        modifier = Modifier.clickable { showEditDialog = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = VybeVolt,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Edit Profile",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // YouTube Music 2026 Recap Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF6B21A8), Color(0xFFDB2777))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "YOUR 2026 RECAP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = VybeVolt,
                                letterSpacing = 1.2.sp
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Your year in music is here",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Explore your top tracks, artists, and listening minutes.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Listening Stats Grid (YouTube Music Style)
            item {
                Text(
                    text = "STATS & AUDIO QUALITY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFA0A5BA),
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 10.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    StatCard(
                        title = "142",
                        subtitle = "Tracks Streamed",
                        icon = Icons.Default.MusicNote,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "18.4h",
                        subtitle = "Listening Time",
                        icon = Icons.Default.Schedule,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "320k",
                        subtitle = "Lossless Active",
                        icon = Icons.Default.GraphicEq,
                        accent = VybeVolt,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // YouTube Music Style Settings Menu Items
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "PREFERENCES & SETTINGS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFA0A5BA),
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x18FFFFFF))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                ) {
                    YtmSettingRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "Tune Your Taste",
                        subtitle = if (profile.favoriteArtists.isNotEmpty()) "${profile.favoriteArtists.size} favorite artists selected" else "Select artists to customize your feed",
                        onClick = onOpenArtistPicker
                    )
                    HorizontalDivider(color = Color(0x15FFFFFF), thickness = 0.8.dp)

                    YtmSettingRow(
                        icon = Icons.Default.HighQuality,
                        title = "Streaming Quality",
                        subtitle = "Lossless 320kbps AAC (Studio Master)",
                        onClick = {}
                    )
                    HorizontalDivider(color = Color(0x15FFFFFF), thickness = 0.8.dp)

                    YtmSettingRow(
                        icon = Icons.Default.Tune,
                        title = "Equalizer & Sound Effects",
                        subtitle = activeEqPreset,
                        onClick = { showEqDialog = true }
                    )
                    HorizontalDivider(color = Color(0x15FFFFFF), thickness = 0.8.dp)

                    val sleepSub = remainingSleep?.let { s -> "Active: ${s / 60}m ${s % 60}s left" } ?: "Turn off music automatically"
                    YtmSettingRow(
                        icon = Icons.Default.Bedtime,
                        title = "Sleep Timer",
                        subtitle = sleepSub,
                        onClick = { showSleepDialog = true }
                    )
                    HorizontalDivider(color = Color(0x15FFFFFF), thickness = 0.8.dp)

                    YtmSettingRow(
                        icon = Icons.Default.Group,
                        title = "Vybe Jam Rooms",
                        subtitle = "Spotify-style shared live sync session",
                        onClick = {}
                    )
                    HorizontalDivider(color = Color(0x15FFFFFF), thickness = 0.8.dp)

                    YtmSettingRow(
                        icon = Icons.Default.Info,
                        title = "About Vybe Music",
                        subtitle = "v1.4.0 • Built for Adarsh Dubey",
                        onClick = {}
                    )
                }
            }
        }
    }

    // Interactive Profile Customizer Dialog
    if (showEditDialog) {
        var newNameInput by remember { mutableStateOf(profile.name) }
        var selectedAvatarUrl by remember { mutableStateOf(profile.avatarUrl) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = Color(0xFF181A28),
            title = {
                Text("Customize YouTube Music Profile", color = Color.White, fontWeight = FontWeight.Black)
            },
            text = {
                Column {
                    Text("Display Name", color = Color(0xFFA0A5BA), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newNameInput,
                        onValueChange = { newNameInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VybeVolt,
                            unfocusedBorderColor = Color(0x4DFFFFFF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Choose Channel Avatar", color = Color(0xFFA0A5BA), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(UserProfileManager.AVATAR_OPTIONS) { avUrl ->
                            val isSelected = avUrl == selectedAvatarUrl
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.5.dp,
                                        if (isSelected) VybeVolt else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { selectedAvatarUrl = avUrl }
                            ) {
                                AsyncImage(
                                    model = avUrl,
                                    contentDescription = "Avatar Option",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        UserProfileManager.updateProfile(newNameInput, selectedAvatarUrl)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VybeVolt),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }

    // Sleep Timer Dialog
    if (showSleepDialog) {
        AlertDialog(
            onDismissRequest = { showSleepDialog = false },
            containerColor = Color(0xFF181A28),
            title = {
                Text("Sleep Timer", color = Color.White, fontWeight = FontWeight.Black)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val presets = listOf(15, 30, 45, 60)
                    presets.forEach { min ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    com.alphabotz.vybemusic.core.playback.SleepTimerManager.startTimer(context, min)
                                    showSleepDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = VybeVolt, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("$min Minutes", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                com.alphabotz.vybemusic.core.playback.SleepTimerManager.setStopAtEndOfSong(context, true)
                                showSleepDialog = false
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MusicOff, contentDescription = null, tint = VybeVolt, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("End of This Song", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    if (remainingSleep != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    com.alphabotz.vybemusic.core.playback.SleepTimerManager.cancelTimer()
                                    showSleepDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Turn Off Sleep Timer", color = Color.Red, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSleepDialog = false }) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }

    // Equalizer & Audio FX Preset Dialog
    if (showEqDialog) {
        val presets = listOf(
            "Dolby Atmos Virtualizer" to "Wide immersive spatial soundstage",
            "Dynamic Bass Boost" to "+6dB deep punchy sub-bass",
            "Studio Vocal Clarity" to "Enhanced vocal midrange presence",
            "Acoustic Pure Lossless" to "Flat reference frequency curve",
            "Club & Electronic Heat" to "High-energy V-shape equalization"
        )
        AlertDialog(
            onDismissRequest = { showEqDialog = false },
            containerColor = Color(0xFF181A28),
            title = {
                Text("Equalizer Presets", color = Color.White, fontWeight = FontWeight.Black)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    presets.forEach { (name, desc) ->
                        val isSelected = activeEqPreset == name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0x26D2F802) else Color.Transparent)
                                .border(1.dp, if (isSelected) VybeVolt else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable {
                                    activeEqPreset = name
                                    showEqDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, color = if (isSelected) VybeVolt else Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(desc, color = Color(0xFFA0A5BA), fontSize = 11.sp)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = VybeVolt, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showEqDialog = false }) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x18FFFFFF))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = accent
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFFA0A5BA),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun YtmSettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFFA0A5BA)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.35f),
            modifier = Modifier.size(16.dp)
        )
    }
}
