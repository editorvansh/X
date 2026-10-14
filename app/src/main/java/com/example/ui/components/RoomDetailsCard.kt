package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomDetails
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EsportsCardBg
import com.example.ui.theme.FieryOrange
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleBright
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun RoomDetailsCard(
    roomDetails: RoomDetails?,
    isRoomReleased: Boolean,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    if (isRoomReleased && roomDetails != null) listOf(ElectricBlue, NeonPurpleBright)
                    else listOf(Color(0xFF33314F), Color(0xFF222038))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("room_details_card"),
        colors = CardDefaults.cardColors(containerColor = EsportsCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MeetingRoom,
                        contentDescription = "Room",
                        tint = if (isRoomReleased) ElectricBlue else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = AppStrings.roomDetails(language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = if (isRoomReleased) SuccessGreen.copy(alpha = 0.2f) else FieryOrange.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isRoomReleased) "RELEASED" else "PENDING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isRoomReleased) SuccessGreen else FieryOrange,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isRoomReleased && roomDetails != null && roomDetails.roomId.isNotBlank()) {
                // Room ID Display
                CredentialRow(
                    label = AppStrings.roomId(language),
                    value = roomDetails.roomId,
                    icon = Icons.Default.MeetingRoom,
                    onCopy = {
                        copyToClipboard(context, "Room ID", roomDetails.roomId)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Password Display
                CredentialRow(
                    label = AppStrings.roomPassword(language),
                    value = roomDetails.roomPassword,
                    icon = Icons.Default.Key,
                    onCopy = {
                        copyToClipboard(context, "Room Password", roomDetails.roomPassword)
                    }
                )

                if (!roomDetails.instructions.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Admin Note: ${roomDetails.instructions}",
                        style = MaterialTheme.typography.bodySmall.copy(color = ElectricBlue)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Privacy",
                        tint = SuccessGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Verified participant credentials. Do not share outside your squad.",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF16152B), RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Lock",
                        tint = FieryOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = AppStrings.roomNotReleased(language),
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                    )
                }
            }
        }
    }
}

@Composable
private fun CredentialRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121124), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFF2A284A), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        Button(
            onClick = onCopy,
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextWhite, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Copy", style = MaterialTheme.typography.labelSmall.copy(color = TextWhite))
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied!", Toast.LENGTH_SHORT).show()
}
