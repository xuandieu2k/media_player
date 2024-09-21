package vn.xdeuhug.music.helper

import android.annotation.SuppressLint

/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 19 / 09 / 2024
 */
object MusicUtils {

    @SuppressLint("DefaultLocale")
    fun formatDuration(duration: Int): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}