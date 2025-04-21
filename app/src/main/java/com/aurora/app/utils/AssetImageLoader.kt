package com.aurora.app.utils

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.aurora.app.domain.model.TarotCard

object AssetImageLoader {
    fun loadBitmapFromAsset(context: Context, card: TarotCard): ImageBitmap? {
        return try {
            context.assets.open("images/${card.id}.png").use {
                BitmapFactory.decodeStream(it)?.asImageBitmap()
            }
        } catch (_: Exception) {
            null
        }
    }
}