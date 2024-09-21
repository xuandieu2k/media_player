@file:Suppress("DEPRECATION")
package vn.xdeuhug.music.network

import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 18 / 09 / 2024
 */

object RetrofitClient {
    private const val BASE_URL = "https://storage.googleapis.com/ikara-storage/"

    val instance: LyricsService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()

        retrofit.create(LyricsService::class.java)
    }
}