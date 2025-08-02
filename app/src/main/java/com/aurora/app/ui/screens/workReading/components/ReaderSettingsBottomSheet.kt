package com.aurora.app.ui.screens.workReading.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurora.app.R
import com.aurora.app.domain.model.ReaderStyle

@Preview
@Composable
fun ReaderSettingsViewPreview() {
    ReaderSettingsView(true, ReaderStyle.Default)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsView(
    isSheetVisible: Boolean,
    currentStyle: ReaderStyle,
    onDismissRequest: (ReaderStyle) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()

    var readerStyle by remember { mutableStateOf(currentStyle) }

    if (!isSheetVisible) return

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest(readerStyle)
        },
        sheetState = sheetState
    ) {
        ReaderSettingsBottomSheet(
            currentFontSize = readerStyle.fontSize,
            onFontSizeChange = { readerStyle = readerStyle.copy(fontSize = it) },
            currentSpacing = readerStyle.lineHeight,
            onSpacingChange = { readerStyle = readerStyle.copy(lineHeight = it) },
            currentFontFamily = readerStyle.font,
            onFontFamilyChange = { readerStyle = readerStyle.copy(font = it) },
            isDarkTheme = readerStyle.darkTheme,
            onThemeChange = { readerStyle = readerStyle.copy(darkTheme = it) }
        )
    }
}

@Composable
fun ReaderSettingsBottomSheet(
    currentFontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    currentSpacing: Float,
    onSpacingChange: (Float) -> Unit,
    currentFontFamily: String,
    onFontFamilyChange: (String) -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Font Size
        Text(stringResource(R.string.font_size), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("A", fontSize = 14.sp)
            Slider(
                value = currentFontSize,
                onValueChange = onFontSizeChange,
                valueRange = ReaderStyle.fontSizes.first()..ReaderStyle.fontSizes.last(),
                steps = ReaderStyle.fontSizes.size - 2,
                modifier = Modifier.weight(1f)
            )
            Text("A", fontSize = 24.sp)
        }

        Spacer(Modifier.height(16.dp))

        // Line Spacing
        Text(stringResource(R.string.spacing), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row {
            val options = ReaderStyle.lineHeights
            options.forEach { option ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (currentSpacing == option.value) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .clickable { onSpacingChange(option.value) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(option.icon, contentDescription = null)
                }
                Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        FontPicker(
            fonts = ReaderStyle.fonts,
            selectedFont = currentFontFamily,
            onFontSelected = { newFont ->
                onFontFamilyChange(newFont) // store path in readerStyle
            }
        )

        Spacer(Modifier.height(16.dp))

        Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row {
            val themes = listOf(true, false)
            themes.forEach { dark ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (dark) Color.Black else Color.White)
                        .border(
                            2.dp,
                            if (isDarkTheme == dark) Color(0xFFFF6F00) else Color.Gray,
                            CircleShape
                        )
                        .clickable { onThemeChange(dark) }
                )
                Spacer(Modifier.width(16.dp))
            }
        }
    }
}
