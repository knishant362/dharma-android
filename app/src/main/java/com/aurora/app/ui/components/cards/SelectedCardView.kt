package com.aurora.app.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aurora.app.ui.screens.tarotSelect.SelectableTarotCard
import com.aurora.app.utils.AssetImageLoader

@Composable
fun SelectedCardView(
    modifier: Modifier = Modifier,
    card: SelectableTarotCard?,
    isRevealed: Boolean,
    onClick: (SelectableTarotCard) -> Unit
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
    ) {

        card?.let {
            if (isRevealed) {

                val context = LocalContext.current
                val bitmap by remember(card.id) {
                    mutableStateOf(AssetImageLoader.loadBitmapFromAsset(context, card.frontImage))
                }
                bitmap?.let { it1 ->
                    Image(
                        bitmap = it1,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onClick(card) },
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                val imageRes = card.backImageRes
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onClick(card) },
                    contentScale = ContentScale.Crop
                )
            }

        }
    }
}