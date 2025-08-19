package com.aurora.app.ui.screens.status.tools

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.aurora.app.ui.screens.status.model.UserProfile
import com.aurora.app.ui.screens.status.tools.overlay.OverlayProperties
import com.aurora.app.ui.screens.status.tools.overlay.drawScaledOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FixedVideoOverlayExporter(private val context: Activity) {

    companion object {
        private const val TAG = "FixedVideoExporter"
    }

    suspend fun exportVideoWithOverlayFast(
        videoUrl: String,
        userProfile: UserProfile,
        overlayProperties: OverlayProperties,
        onProgress: (ExportProgress) -> Unit = {}
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            onProgress(ExportProgress(0.1f, "Starting export..."))

            // Step 1: Download video
            val inputVideoPath = downloadVideo(videoUrl) { progress ->
                onProgress(ExportProgress(progress * 0.2f, "Downloading video..."))
            }
            onProgress(ExportProgress(0.2f, "Video downloaded"))

            // Step 2: Create static overlay image (PNG with transparency)
            val overlayImagePath =
                createStaticOverlayImage(context, userProfile, inputVideoPath, overlayProperties)
            onProgress(ExportProgress(0.3f, "Overlay created"))

            // Step 3: Process with FFmpeg (FASTEST - hardware accelerated)
            val outputPath = processVideoWithFFmpeg(inputVideoPath, overlayImagePath) { progress ->
                onProgress(ExportProgress(0.3f + progress * 0.7f, "Processing video..."))
            }

            // Cleanup temp files
            cleanupTempFiles(inputVideoPath, overlayImagePath)

            onProgress(ExportProgress(1.0f, "Export completed!", true))
            Result.success(outputPath)

        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Export failed")
            onProgress(ExportProgress(0f, "Export failed: ${e.message}"))
            Result.failure(e)
        }
    }

    private suspend fun downloadVideo(
        videoUrl: String,
        onProgress: (Float) -> Unit = {}
    ): String = withContext(Dispatchers.IO) {

        val fileName = "temp_video_${System.currentTimeMillis()}.mp4"
        val outputFile = File(context.cacheDir, fileName)

        try {
            val url = URL(videoUrl)
            val connection = url.openConnection()
            val fileSize = connection.contentLength

            connection.getInputStream().use { input ->
                FileOutputStream(outputFile).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalBytesRead = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead

                        if (fileSize > 0) {
                            val progress = totalBytesRead.toFloat() / fileSize
                            onProgress(progress)
                        }
                    }
                }
            }

            if (!outputFile.exists() || outputFile.length() == 0L) {
                throw Exception("Failed to download video")
            }

            outputFile.absolutePath

        } catch (e: Exception) {
            if (outputFile.exists()) outputFile.delete()
            throw Exception("Download failed: ${e.message}")
        }
    }


    private suspend fun processVideoWithFFmpeg(
        inputVideoPath: String,
        overlayImagePath: String,
        onProgress: (Float) -> Unit = {}
    ): String = suspendCoroutine { continuation ->

        val outputFile =
            File(context.getExternalFilesDir("exports"), "video_${System.currentTimeMillis()}.mp4")

        // Ensure output directory exists
        outputFile.parentFile?.mkdirs()

        // FFmpeg command for fast overlay processing (IMAGE OVERLAY - NO FONT ISSUES)
        val command = buildString {
            append("-i \"$inputVideoPath\" ")          // Input video
            append("-i \"$overlayImagePath\" ")        // Input overlay image
            append("-filter_complex \"[0:v][1:v] overlay=0:0\" ")  // Overlay at position 0,0
            append("-c:a copy ")                       // Copy audio without re-encoding (FAST)
            append("-c:v libx264 ")                    // H.264 video codec
            append("-preset fast ")                    // Fast encoding preset
            append("-crf 23 ")                         // Constant Rate Factor (quality)
            append("-movflags +faststart ")            // Fast start for web streaming
            append("-y ")                              // Overwrite output file
            append("\"${outputFile.absolutePath}\"")   // Output file
        }

        Timber.tag(TAG).d("FFmpeg command: $command")

        // Execute FFmpeg command
        FFmpegKit.executeAsync(command) { session ->
            try {
                val returnCode = session.returnCode

                if (ReturnCode.isSuccess(returnCode)) {
                    Timber.tag(TAG).d("FFmpeg processing successful")
                    if (outputFile.exists() && outputFile.length() > 0) {
                        continuation.resumeWith(Result.success(outputFile.absolutePath))
                    } else {
                        continuation.resumeWithException(Exception("Output file not created"))
                    }
                } else {
                    val error =
                        session.failStackTrace ?: "FFmpeg processing failed with code: $returnCode"
                    Timber.tag(TAG).e("FFmpeg failed: $error")
                    continuation.resumeWithException(Exception(error))
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "FFmpeg processing error")
                continuation.resumeWithException(e)
            }
        }

        // Simulate progress updates (FFmpeg doesn't provide easy real-time progress)
        CoroutineScope(Dispatchers.IO).launch {
            var progress = 0f
            while (progress < 0.95f && continuation.context.isActive) {
                delay(300)
                progress += 0.15f
                onProgress(progress)
            }
        }
    }


    private fun cleanupTempFiles(vararg paths: String) {
        paths.forEach { path ->
            try {
                File(path).delete()
            } catch (e: Exception) {
                Timber.tag(TAG).w("Failed to delete temp file: $path")
            }
        }
    }
}

private suspend fun createStaticOverlayImage(
    context: Activity,
    userProfile: UserProfile,
    videoPath: String,
    overlayProperties: OverlayProperties
): String = withContext(Dispatchers.IO) {
    val overlayFile = File(context.cacheDir, "overlay_${System.currentTimeMillis()}.png")

    // Get video dimensions
    val (videoWidth, videoHeight) = getVideoDimensions(videoPath)

    // Create bitmap and draw with scaling
    val bitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Draw overlay with automatic scaling
    drawScaledOverlay(canvas, userProfile, videoWidth, videoHeight, overlayProperties)

    // Save as PNG
    try {
        FileOutputStream(overlayFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    } finally {
        bitmap.recycle()
    }

    overlayFile.absolutePath
}

private suspend fun getVideoDimensions(videoPath: String): Pair<Int, Int> {
    return withContext(Dispatchers.IO) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoPath)

            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 720
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 1280

            retriever.release()
            Pair(width, height)
        } catch (e: Exception) {
            Timber.e("VideoExporter : Could not get video dimensions: ${e.message}")
            Pair(720, 1280)
        }
    }
}