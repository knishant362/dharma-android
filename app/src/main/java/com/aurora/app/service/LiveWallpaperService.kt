package com.aurora.app.service

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.aurora.app.utils.Constants
import timber.log.Timber
import java.io.File

class VideoLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = VideoWallpaperEngine()

    inner class VideoWallpaperEngine : Engine() {

        private var player: ExoPlayer? = null
        private var surfaceHolder: SurfaceHolder? = null

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            surfaceHolder = holder

            Handler(Looper.getMainLooper()).postDelayed({
                Timber.e("LiveWallpaper Video playback started")
                startVideoPlayback(holder)
            }, 100)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            releasePlayer()
        }

        override fun onDestroy() {
            super.onDestroy()
            releasePlayer()
        }

        private fun startVideoPlayback(holder: SurfaceHolder) {
            val videoPath = getVideoPathFromPrefs()
            if (videoPath.isNullOrEmpty()) {
                Timber.e("LiveWallpaper Video URL is null or empty")
                return
            }
            val file = File(videoPath)
            if (!file.exists()) {
                Timber.e("LiveWallpaper Video file not found at ${file.absolutePath}")
                return
            }
            Timber.i("LiveWallpaper Playing video at ${file.absolutePath}")
            releasePlayer()

            player = ExoPlayer.Builder(applicationContext).build().apply {
                setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
                setRepeatMode(Player.REPEAT_MODE_ALL)
                setVideoSurface(holder.surface)
                playWhenReady = true
                prepare()
            }
        }

        private fun releasePlayer() {
            player?.release()
            player = null
        }

        private fun getVideoPathFromPrefs(): String? {
            val prefs = applicationContext.getSharedPreferences(
                Constants.APP_PREFERENCES,
                Context.MODE_PRIVATE
            )
            return prefs.getString(Constants.WALLPAPER_KEY, null)
        }
    }
}
