package com.alphabotz.vybemusic.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alphabotz.vybemusic.core.model.JamSession
import com.alphabotz.vybemusic.core.model.Track
import com.alphabotz.vybemusic.core.storage.UserProfileManager
import com.alphabotz.vybemusic.ui.theme.*

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
    val context = LocalContext.current
    val profile by UserProfileManager.profile.collectAsState()

    var showJoinDialog by remember { mutableStateOf(false) }
    var inputRoomCode by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF141522), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .padding(24.dp)
    ) {
        // Sheet Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(VybeVolt.copy(alpha = 0.2f))
                    .border(1.dp, VybeVolt.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Vybe Jam",
                    tint = VybeVolt,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Vybe Jam",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Listen together in real-time with friends",
                    fontSize = 13.sp,
                    color = Color(0xFFA0A5BA)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        if (activeJam == null) {
            // Not in a Jam: Start or Join Options
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0x22FFFFFF))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Start Collaborative Session",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Start a listening room as '${profile.name}'. Friends can join using your unique room code.",
                        fontSize = 13.sp,
                        color = Color(0xFFA0A5BA),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Button(
                        onClick = { onStartJam(profile.name) },
                        colors = ButtonDefaults.buttonColors(containerColor = VybeVolt),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start a Vybe Jam", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showJoinDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4DFFFFFF)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Join with Room Code", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Active Jam Session Screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0x22FFFFFF))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column {
                    // Room Code Box
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF090A10))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Column {
                            Text(
                                text = "ROOM CODE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = VybeVolt,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = activeJam.roomCode,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Copy Code Button
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Vybe Jam Code", activeJam.roomCode)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Room code copied: ${activeJam.roomCode}", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code", tint = Color.White)
                            }

                            // Share Room Code Button
                            IconButton(onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "🎶 Join my Vybe Jam live listening room! Room Code: ${activeJam.roomCode}\nDownload Vybe Music: https://github.com/adarshdubey-alphabotz/vybemusic"
                                    )
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Vybe Jam Room Code")
                                context.startActivity(shareIntent)
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "Share Code", tint = VybeVolt)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Connected Listeners List
                    Text(
                        text = "CONNECTED LISTENERS (${activeJam.participants.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA0A5BA),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
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
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, if (participant.isHost) VybeVolt else VybeCyan, CircleShape)
                                    )
                                    if (participant.isHost) {
                                        Text(
                                            text = "👑",
                                            fontSize = 14.sp,
                                            modifier = Modifier.align(Alignment.TopEnd)
                                        )
                                    }
                                }
                                Text(
                                    text = participant.name,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Audio Sync Status Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(VybeVolt.copy(alpha = 0.15f))
                            .border(1.dp, VybeVolt.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Sync",
                            tint = VybeVolt,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "All listeners synchronized to Host audio",
                            fontSize = 12.sp,
                            color = VybeVolt,
                            fontWeight = FontWeight.Bold
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

    // Join Jam Interactive Dialog
    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            containerColor = Color(0xFF181A28),
            title = {
                Text("Join Vybe Jam", color = Color.White, fontWeight = FontWeight.Black)
            },
            text = {
                Column {
                    Text(
                        "Enter the 6-character room code shared by your friend:",
                        color = Color(0xFFA0A5BA),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputRoomCode,
                        onValueChange = { inputRoomCode = it.uppercase() },
                        placeholder = { Text("e.g. VYBE-1234", color = Color.Gray) },
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputRoomCode.isNotBlank()) {
                            onJoinJam(inputRoomCode.trim(), profile.name)
                            showJoinDialog = false
                            Toast.makeText(context, "Joined room ${inputRoomCode.trim()}!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VybeVolt),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Join Room", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}
