package com.aurora.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aurora.app.ui.components.button.AuroraSlimButton

@Composable
fun RemoveAdsView(
    modifier: Modifier = Modifier,
    price: String,
    validity: String,
    onRemoveAdsClick: () -> Unit
) {

    Column(
        modifier = modifier
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp)
    ) {
        Text("Tired of waiting? Remove all ads and enjoy uninterrupted Tarot and other divinations for a $validity")
        AuroraSlimButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Remove Ads for $price",
            onClick = onRemoveAdsClick
        )
    }

}

@Preview
@Composable
fun RemoveAdsViewPreview(modifier: Modifier = Modifier) {
    RemoveAdsView(
        modifier = Modifier.background(color = Color.White),
        price = "Rs. 50",
        validity = "1 month",
        onRemoveAdsClick = {}
    )
}
