package vn.xdeuhug.music.data.repository


import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.await
import vn.xdeuhug.music.data.model.LyricLine
import vn.xdeuhug.music.data.model.LyricWord
import vn.xdeuhug.music.network.RetrofitClient
import java.util.logging.Logger

/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 18 / 09 / 2024
 */
class MusicRepository {
    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition
    private val _duration = MutableLiveData<Int>()
    val duration: LiveData<Int> = _duration
    private val _isPrepared = MutableLiveData<Boolean>()
    val isPrepared: LiveData<Boolean> = _isPrepared

    private var mediaPlayer: MediaPlayer? = null

    private val handler = Handler(Looper.getMainLooper())
    private val updatePositionTask = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    _currentPosition.postValue(it.currentPosition)
                    handler.postDelayed(this, 0)
                }
            }
        }
    }

    init {
        _isPrepared.postValue(false)
    }

    suspend fun fetchLyricsFromApi(): List<LyricLine> {
        return try {
            val response = RetrofitClient.instance.getLyrics().await()
            val lyricLines = response.lyricsParams?.map {
                LyricLine(lyricWords = calculateEndTimeForLyrics(it.lyricWords ?: emptyList()))
            } ?: emptyList()
            Log.e("LyricLines API: ", GsonBuilder().setPrettyPrinting().create().toJson(lyricLines))
            lyricLines
        } catch (e: Exception) {
            Log.e("Error API: ", e.message.toString())
            emptyList()
        }
    }

    //    private fun calculateEndTimeForLyrics(lyrics: List<LyricWord>): List<LyricWord> {
//        val mutableLyrics = lyrics.toMutableList()
//        val wordDuration = 0.2f
//
//        return mutableLyrics.mapIndexed { index, word ->
//            var endTime =
//                if (index < mutableLyrics.size - 1) mutableLyrics[index + 1].startTime else word.startTime + wordDuration
//            if (endTime - word.startTime < wordDuration) {
//                endTime = word.startTime + wordDuration * 2
//                if (index + 1 < mutableLyrics.size) {
//                    mutableLyrics[index + 1].startTime = endTime
//                }
//            }
//            if (word.text.trim() == "thềm" && index - 1 >= 0) {
//                val newWord = word.copy(
//                    text = "bên ",
//                    startTime = word.startTime,
//                    endTime = word.startTime + wordDuration
//                )
//                mutableLyrics.add(index, newWord)
//                mutableLyrics[index + 1] = mutableLyrics[index + 1].copy(startTime = newWord.endTime)
//                endTime = newWord.endTime + wordDuration
//            }
//            Log.e(
//                "Log time data:",
//                "start: ${word.startTime} end: $endTime ${endTime - word.startTime} ${word.text}"
//            )
//
//            word.copy(endTime = endTime)
//        }
//    }
    private fun calculateEndTimeForLyrics(lyrics: List<LyricWord>): List<LyricWord> {
        val mutableLyrics = lyrics.toMutableList()
        val wordDuration = 0.2f
        var index = 0

        while (index < mutableLyrics.size) {
            val word = mutableLyrics[index]
            var endTime =
                if (index < mutableLyrics.size - 1) mutableLyrics[index + 1].startTime else word.startTime + wordDuration
            if (endTime - word.startTime < wordDuration) {
                endTime = word.startTime + wordDuration * 2
                if (index + 1 < mutableLyrics.size) {
                    mutableLyrics[index + 1].startTime = endTime
                }
            }
            if (word.text.trim() == "thềm") {
                val newWord = word.copy(
                    text = "bên",
                    startTime = word.startTime,
                    endTime = word.startTime + wordDuration * 2
                )
                mutableLyrics.add(index, newWord)
                mutableLyrics[index + 1] = mutableLyrics[index + 1].copy(
                    startTime = newWord.endTime, endTime = newWord.endTime + wordDuration * 2
                )
                index++
            } else if (word.text.trim() == "nao " && word.endTime - word.startTime > 2) {
                var updatedEndTime = word.startTime + wordDuration * 2
                mutableLyrics[index] = word.copy(endTime = updatedEndTime)
                if (index + 1 < mutableLyrics.size) {
                    var nextIndex = index + 1
                    var nextWord = mutableLyrics[nextIndex]
                    while (nextIndex < mutableLyrics.size) {
                        nextWord = nextWord.copy(
                            startTime = updatedEndTime,
                            endTime = updatedEndTime + wordDuration * 2
                        )
                        mutableLyrics[nextIndex] = nextWord
                        updatedEndTime = nextWord.endTime
                        nextIndex++
                    }
                }
            } else {
                mutableLyrics[index] = word.copy(endTime = endTime)
            }
            index++
            Log.e(
                "Log time data:",
                "start: ${word.startTime} end: $endTime ${endTime - word.startTime} ${word.text}"
            )
        }

        return mutableLyrics
    }


    fun loadMusic(url: String) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                _duration.postValue(it.duration)
                _isPrepared.postValue(true)
            }
            setOnErrorListener { _, what, extra ->
                Log.e("MediaPlayer Error", "Error $what $extra")
                false
            }
            prepareAsync()
        }
    }

    fun playMusic() {
        if (_isPrepared.value == true) {
            mediaPlayer?.start()
            handler.post(updatePositionTask)
        } else {
            Log.e("MediaPlayer", "MediaPlayer chưa sẵn sàng!")
        }
    }

    fun pauseMusic() {
        if (_isPrepared.value == true) {
            mediaPlayer?.pause()
            handler.removeCallbacks(updatePositionTask)
        } else {
            Log.e("MediaPlayer", "MediaPlayer chưa sẵn sàng")
        }
    }

    fun seekTo(position: Int) {
        if (_isPrepared.value == true) {
            mediaPlayer?.seekTo(position)
        } else {
            Log.e("MediaPlayer", "Không thể seek khi MediaPlayer chưa sẵn sàng!")
        }
    }

    fun seekToAutoPlay(position: Int) {
        mediaPlayer?.let {
            if (_isPrepared.value == true) {
                it.seekTo(position)
                if (!it.isPlaying) {
                    it.start()
                    Log.d("MediaPlayer", "Seek tới: $position và phát lại.")
                } else {
                    //
                }
            } else {
                Log.e("MediaPlayer", "MediaPlayer chưa sẵn sàng!")
            }
        } ?: Log.e("MediaPlayer", "MediaPlayer null!")
    }


    fun release() {
        handler.removeCallbacks(updatePositionTask)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun restart() {
        mediaPlayer?.pause()
        mediaPlayer?.seekTo(0)
    }
}