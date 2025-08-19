package com.aurora.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object ShareUtility {

    fun shareToWhatsApp(context: Context, videoPath: String) {
        shareToApp(context, videoPath, "com.whatsapp")
    }

    fun shareToInstagram(context: Context, videoPath: String) {
        shareToApp(context, videoPath, "com.instagram.android")
    }

    fun shareToFacebook(context: Context, videoPath: String) {
        shareToApp(context, videoPath, "com.facebook.katana")
    }

    fun shareToMore(context: Context, videoPath: String) {
        try {
            val uri = getFileUri(context, File(videoPath))
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "video/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Video"))
        } catch (e: Exception) {
            Toast.makeText(context, "Share failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun openFileLocation(context: Context, filePath: String) {
        try {
            val uri = getFileUri(context, File(filePath))
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "video/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareToApp(context: Context, videoPath: String, packageName: String) {
        try {
            val uri = getFileUri(context, File(videoPath))
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "video/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                setPackage(packageName)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            shareToMore(context, videoPath) // Fallback to general share
        }
    }

    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}