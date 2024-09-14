package com.example.aisplitwise.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.aisplitwise.data.local.Group
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GroupCard(group: Group, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp,vertical=8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Group Image
            val context= LocalContext.current
            SubcomposeAsyncImage(modifier = Modifier
                .size(96.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
                .padding(8.dp),
                model = ImageRequest.Builder(context)
                    .data(group.groupImg)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                loading = {
                    Icon(
                        imageVector = Icons.Default.HourglassBottom, // Replace with your icon resource
                        contentDescription = "Create Group", // Replace with your string resource
                        modifier = Modifier.matchParentSize().padding(8.dp)
                    )
                },
                error = {
                    Icon(
                        imageVector = Icons.Default.BrokenImage, // Replace with your icon resource
                        contentDescription = "Create Group", // Replace with your string resource
                        modifier = Modifier.matchParentSize().padding(8.dp)
                    )
                })

            Spacer(modifier = Modifier.width(16.dp))

            // Group Details
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Group Name
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Members Count
                Text(
                    text = "${group.members.size} members",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Creation Date
                Text(
                    text = "Created: ${formatDate(group.createdAt.toDate())}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}
