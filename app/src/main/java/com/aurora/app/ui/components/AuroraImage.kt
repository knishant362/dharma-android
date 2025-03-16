package com.aurora.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.aurora.app.R

@Composable
fun AuroraImage(
    modifier: Modifier = Modifier,
    image: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .clickable { onClick.invoke() }
            .background(MaterialTheme.colorScheme.surface),
    ) {

        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(8.dp)),
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(getRandomColor())
                )
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(getRandomColor()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(36.dp),
                        painter = painterResource(id = R.drawable.app_icon),
                        tint = Color.White,
                        contentDescription = ""
                    )
                }

            }
        )
    }
}

fun getRandomColor(): Color {
    val colors = listOf(
        Color(0xFFFF6347),
        Color(0xFF00BFFF),
        Color(0xFFFFA500),
        Color(0xFFFF1493),
        Color(0xFFFFD700),
        Color(0xFF7FFF00),
        Color(0xFF8B0000),
        Color(0xFF6A5ACD),
        Color(0xFFFFA07A),
        Color(0xFF87CEFA),
        Color(0xFFDDA0DD),
        Color(0xFFFFE4E1),
        Color(0xFFF0E68C),
    )
    return colors.random()
}