package com.alphabotz.vybemusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alphabotz.vybemusic.core.model.JamParticipant
import com.alphabotz.vybemusic.core.model.JamSession
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.ui.theme.*

/**
 * Spotify Jam inspired collaborative group listening room.
 * Allows host & friends to stream in sync, vote on songs, and manage queue.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VybeJamSheet(
    activeJam: JamSession?,
    currentTrack: Track?,
    onStartJam: (String) -> Unit,
    onJoinJam: (String, String) -> Unit,
    onLeaveJam: () -> Unit,
    modifier: Modifier = Modifier
) {
    var roomCodeInput by remember { mutableStateOf("") }
    var userNameInput by remember { mutableStateOf("Guest") }
    var showJoinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(VybeSurface, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .padding(24.dp)
    ) {
        // Sheet Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = "Vybe Jam",
                tint = VybePrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Vybe Jam",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Listen together in real-time with friends",
                    fontSize = 12.sp,
                    color = VybeTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (activeJam == null) {
            // Not in a Jam: Show options to Start or Join
            Card(
                colors = CardDefaults.cardColors(containerColor = VybeSurfaceElevated),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Start a Collaborative Session",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Invite nearby friends or share your room code to control the music and add tracks to the live queue.",
                        fontSize = 13.sp,
                        color = VybeTextSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Button(
                        onClick = { onStartJam("Adarsh") },
                        colors = ButtonDefaults.buttonColors(containerColor = VybePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start a Vybe Jam", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showJoinDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Join with Room Code")
                    }
                }
            }
        } else {
            // Active Jam Session Screen
            Card(
                colors = CardDefaults.cardColors(containerColor = VybeSurfaceElevated),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Room Code Box
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VybeBackground, RoundedCornerShape(14.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Column {
                            Text(
                                text = "ROOM CODE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VybeCyan,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = activeJam.roomCode,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                        }
                        IconButton(onClick = { /* Share room code */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Participants List
                    Text(
                        text = "CONNECTED LISTENERS (${activeJam.participants.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VybeTextTertiary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(activeJam.participants) { participant ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box {
                                    AsyncImage(
                                        model = participant.avatarUrl,
                                        contentDescription = participant.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, if (participant.isHost) VybePrimary else VybeCyan, CircleShape)
                                    )
                                    if (participant.isHost) {
                                        Text(
                                            text = "👑",
                                            fontSize = 12.sp,
                                            modifier = Modifier.align(Alignment.TopEnd)
                                        )
                                    }
                                }
                                Text(
                                    text = participant.name,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Live Synced Playback indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VybeEmerald.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Sync", tint = VybeEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "All listeners synchronized to Host audio",
                            fontSize = 12.sp,
                            color = VybeEmerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onLeaveJam,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Leave Vybe Jam", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
