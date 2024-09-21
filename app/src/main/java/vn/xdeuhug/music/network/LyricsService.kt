package vn.xdeuhug.music.network

import retrofit2.Call
import retrofit2.http.GET
import vn.xdeuhug.music.data.model.LyricsResponse

/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 18 / 09 / 2024
 */

interface LyricsService {
    @GET("ikara/lyrics.xml")
    fun getLyrics(): Call<LyricsResponse>
}