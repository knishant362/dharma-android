package com.aurora.app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.SecureFlagPolicy
import com.aurora.app.ui.components.button.AuroraButton
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSheet(
    show: Boolean,
    onDismiss: () -> Unit,
    initialDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val nestedScrollConnection = rememberNestedScrollInteropConnection()
    var selectedDate by remember { mutableStateOf(initialDate) }

    if (show) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss.invoke() },
            sheetState = sheetState,
            contentColor = Color.White,
            containerColor = Color.White,
            properties = ModalBottomSheetProperties(
                securePolicy = SecureFlagPolicy.SecureOn,
                shouldDismissOnBackPress = true
            ),

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .nestedScroll(nestedScrollConnection),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AuroraDatePicker(
                    onValueChange = { selectedDate = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuroraButton(
                    text = "SUBMIT",
                    onClick = {
                        onDateSelected(selectedDate)
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}
