package vn.xdeuhug.music.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import vn.xdeuhug.music.data.model.LyricLine
import vn.xdeuhug.music.data.repository.MusicRepository

/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 18 / 09 / 2024
 */
class MusicViewModel(private val repository: MusicRepository) : ViewModel() {
    val currentPosition: LiveData<Int> = repository.currentPosition
    val duration: LiveData<Int> = repository.duration
    val isPrepared: LiveData<Boolean> = repository.isPrepared

    private val _lyrics = MutableLiveData<List<LyricLine>>()
    val lyrics: LiveData<List<LyricLine>> = _lyrics

    fun loadMusic(url: String) {
        repository.loadMusic(url)
    }

    fun playPauseMusic(isPlaying: Boolean) {
        if (isPlaying) {
            repository.pauseMusic()
        } else {
            repository.playMusic()
        }
    }

    fun seekTo(position: Int) {
        repository.seekTo(position)
    }

    fun seekToAutoPlay(position: Int) {
        repository.seekToAutoPlay(position)
    }

    fun restart(){
        repository.restart()
    }

    fun loadLyricsFromApi() {
        viewModelScope.launch {
            val lyricLines = repository.fetchLyricsFromApi()
            _lyrics.postValue(lyricLines)
            Log.e("Data API: ", Gson().toJson(lyricLines))
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.release()
    }
}

class MusicViewModelFactory(
    private val repository: MusicRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
            return MusicViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}