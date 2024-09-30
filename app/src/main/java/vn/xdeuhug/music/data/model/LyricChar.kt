package vn.xdeuhug.music.data.model

/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 30 / 09 / 2024
 */
data class LyricChar(
    val index: Int,
    val text: Char,
    val startTime: Float,
    val endTime: Float,
    val duration: Float
)