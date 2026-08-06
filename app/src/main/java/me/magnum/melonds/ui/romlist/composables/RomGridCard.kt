package me.magnum.melonds.ui.romlist.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.magnum.melonds.domain.model.rom.Rom

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RomGridCard(
    rom: Rom,
    coverUrl: String?,
    showAchievementBadge: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isThorEnhancedRom = isEnhancedRom(rom.name, rom.fileName)
    Card(
        elevation = 3.dp,
        shape = RoundedCornerShape(12.dp),
        border = if (isThorEnhancedRom) {
            androidx.compose.foundation.BorderStroke(3.dp, Color(0xFFFFD700))
        } else {
            null
        },
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress,
            ),
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                RomCover(
                    rom = rom,
                    coverUrl = coverUrl,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                )
                if (rom.isFavorite) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(24.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Black.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (rom.isDsiWareTitle) {
                        BadgeText("DSi", color = MaterialTheme.colors.secondary)
                        Spacer(Modifier.width(4.dp))
                    }
                    if (showAchievementBadge) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.Black.copy(alpha = 0.55f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }
            }
            Text(
                text = rom.config.customName ?: rom.name,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
fun FolderGridCard(
    name: String,
    relativePath: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = MaterialTheme.colors.secondary
    Card(
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Folder,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(56.dp),
                )
            }
            Text(
                text = name,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun BadgeText(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Bold,
        )
    }
}
