package com.aurora.app.ui.screens.workReading.components

import android.content.Context
import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun FontPicker(
    fonts: List<String>,
    selectedFont: String,
    onFontSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(fonts) { font ->
            val fontFamily = getFontFamilyFromAssets(font)
            val isSelected = font == selectedFont

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onFontSelected(font) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = font.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    fontFamily = fontFamily,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


@Composable
fun getFontFamilyFromAssets(fontName: String): FontFamily {
    val context = LocalContext.current
    return remember(fontName) { FontFamily(loadTypefaceFromAssets(context, fontName)) }
}

fun loadTypefaceFromAssets(context: Context, fontName: String): Typeface {
    return try {
        Typeface.createFromAsset(context.assets, "fonts/$fontName.ttf")
    } catch (e: Exception) {
        Typeface.DEFAULT
    }
}

