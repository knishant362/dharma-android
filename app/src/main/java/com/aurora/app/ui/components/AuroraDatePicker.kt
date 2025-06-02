package com.aurora.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import java.time.LocalDate

@Composable
fun AuroraDatePicker(
    onValueChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    minDate: LocalDate = LocalDate.now().minusYears(120),
    maxDate: LocalDate = LocalDate.now()
) {
    WheelDatePicker(
        modifier = modifier,
        rowCount = 3,
        minDate = minDate,
        maxDate = maxDate,
        textStyle = MaterialTheme.typography.titleMedium,
        textColor = Color(0xFF222222),
        selectorProperties = WheelPickerDefaults.selectorProperties(
            enabled = true,
            shape = RoundedCornerShape(0.dp),
            color = Color(0xFFf1faee).copy(alpha = 0.2f),
            border = BorderStroke(2.dp, Color(0xFFf1faee))
        ),
        onSnappedDate = { snappedDate ->
            onValueChange(snappedDate)
        }
    )
}
