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

    fun interpolate(start: Int, end: Int, ratio: Float): Int {
        return (start + (end - start) * ratio).toInt()
    }

    fun smoothStep(edge0: Float, edge1: Float, x: Float): Float {
        val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
        return t * t * (3 - 2 * t)
    }
}