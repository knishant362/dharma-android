package com.aurora.app.utils

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.aurora.app.domain.model.TarotCard
import timber.log.Timber

object AssetImageLoader {
    fun loadBitmapFromAsset(context: Context, imagePath: String): ImageBitmap? {
        return try {
            Timber.e("AssetImageLoader: ${imagePath}")
            context.assets.open(imagePath).use {
                BitmapFactory.decodeStream(it)?.asImageBitmap()
            }
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }
}