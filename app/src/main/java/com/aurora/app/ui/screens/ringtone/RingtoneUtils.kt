package com.aurora.app.ui.screens.ringtone

import android.content.ContentValues
import android.content.Context
import android.media.RingtoneManager
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import com.aurora.app.domain.model.dashboard.Ringtone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

suspend fun downloadAndSetRingtone(
    context: Context,
    ringtone: Ringtone,
    urlString: String,
    updateStatus: (String) -> Unit,
    onSuccess: () -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            withContext(Dispatchers.Main) {
                updateStatus("Downloading ${ringtone.name}...")
            }

            // Save MP3 directly to MediaStore Ringtones
            val fileName = "${ringtone.name}.mp3"
            val values = ContentValues().apply {
                put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
                put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_RINGTONES)
                put(MediaStore.Audio.Media.IS_RINGTONE, true)
                put(MediaStore.Audio.Media.IS_MUSIC, false)
                put(MediaStore.Audio.Media.IS_ALARM, false)
                put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw Exception("Could not insert audio file in MediaStore")

            try {
                val url = URL(urlString)
                url.openStream().use { input ->
                    resolver.openOutputStream(uri)?.use { output ->
                        input.copyTo(output)
                    } ?: throw Exception("Failed to open output stream")
                }
            } catch (e: Exception) {
                resolver.delete(uri, null, null)
                throw e
            }

            withContext(Dispatchers.Main) {
                if (Settings.System.canWrite(context)) {
                    RingtoneManager.setActualDefaultRingtoneUri(
                        context,
                        RingtoneManager.TYPE_RINGTONE,
                        uri
                    )
                    updateStatus("Set '${ringtone.name}' as ringtone!")
                    onSuccess()
                    Toast.makeText(context, "Ringtone set!", Toast.LENGTH_SHORT).show()
                } else {
                    updateStatus("Please grant 'Modify system settings' permission")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                updateStatus("Error: ${e.message}")
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}