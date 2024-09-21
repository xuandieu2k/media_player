package vn.xdeuhug.music.ui.view

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vn.xdeuhug.music.R
import vn.xdeuhug.music.data.repository.MusicRepository
import vn.xdeuhug.music.databinding.ActivityMusicPlayerBinding
import vn.xdeuhug.music.helper.MusicUtils
import vn.xdeuhug.music.ui.adapter.LyricsAdapter
import vn.xdeuhug.music.ui.viewmodel.MusicViewModel
import vn.xdeuhug.music.ui.viewmodel.MusicViewModelFactory


/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 18 / 09 / 2024
 */
class MusicPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMusicPlayerBinding
    private lateinit var viewModel: MusicViewModel
    private lateinit var lyricsAdapter: LyricsAdapter
    private var isPlaying = false
    private var layoutManager = CustomGridLayoutManager(this).apply {
        orientation = LinearLayoutManager.VERTICAL
        setScrollEnabled(flag = true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setUpView()
    }

    private fun setUpView() {
        val repository = MusicRepository()
        val viewModelFactory = MusicViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MusicViewModel::class.java]
        binding.tvStatus.text = getString(R.string.loading_lyrics)
        showLyrics(isShow = false)
        lyricsAdapter = LyricsAdapter()
        binding.rvLyrics.adapter = lyricsAdapter
        binding.rvLyrics.layoutManager = layoutManager
        binding.rvLyrics.itemAnimator = null
        binding.rvLyrics.isNestedScrollingEnabled = false
        binding.seekBar.max = 100
        binding.tvCurrentTime.text = MusicUtils.formatDuration(0)
        binding.tvEndTime.text = MusicUtils.formatDuration(0)

        viewModel.loadMusic("https://storage.googleapis.com/ikara-storage/tmp/beat.mp3")
        viewModel.loadLyricsFromApi()
        viewModel.currentPosition.observe(this) { position ->
            position?.let {
                lyricsAdapter.setCurrentPosition(position.toFloat() / 1000f, binding.rvLyrics)
                binding.tvCurrentTime.text = MusicUtils.formatDuration(position)
                viewModel.duration.value?.let {
                    if (it > 0) {
                        binding.seekBar.progress =
                            ((position.toFloat() / it) * binding.seekBar.max).toInt()
                    }
                }
//                Log.e("Log Position: ", "$position  ---  ${viewModel.duration.value}")
            }
        }

        viewModel.lyrics.observe(this) { lyricLines ->
            if (lyricLines.isNotEmpty()) {
                lyricsAdapter.setLyrics(lyricLines)
            } else {
                binding.tvStatus.text = getString(R.string.not_have_lyrics_this_song)
            }
            showLyrics(isShow = lyricLines.isNotEmpty())
        }

        viewModel.duration.observe(this) {
            it?.let {
                binding.tvEndTime.text = MusicUtils.formatDuration(it)
            }
        }

        viewModel.isPrepared.observe(this) { isPrepared ->
            binding.seekBar.isEnabled = isPrepared
        }

        binding.btnPlayPause.setOnClickListener {
            if (viewModel.isPrepared.value == true) {
                isPlaying = !isPlaying
                binding.btnPlayPause.setImageResource(if (!isPlaying) R.drawable.ic_play else R.drawable.ic_pause)
                if (!isPlaying) {
                    binding.lavMusic.cancelAnimation()
                } else {
                    binding.lavMusic.playAnimation()
                }
                layoutManager.setScrollEnabled(!isPlaying)
                viewModel.playPauseMusic(!isPlaying)
            } else {
                Toast.makeText(this, "MediaPlayer chưa sẵn sàng!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val totalDuration = viewModel.duration.value ?: 0
                    val newPosition = (totalDuration * progress / 100).toFloat()
                    lyricsAdapter.setCurrentPositionWithSeekBar(
                        newPosition / 1000f, binding.rvLyrics
                    )
                    viewModel.seekTo(newPosition.toInt())
                    binding.tvCurrentTime.text =
                        MusicUtils.formatDuration(totalDuration * progress / 100)
                } else {
                    if (progress >= 99) {
                        isPlaying = false
                        lyricsAdapter.resetCurrentPosition()
                        binding.lavMusic.cancelAnimation()
                        viewModel.restart()
                        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                        binding.tvCurrentTime.text = MusicUtils.formatDuration(0)
                        binding.seekBar.progress = 0
                        layoutManager.setScrollEnabled(true)
                        layoutManager.scrollToPosition(0)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                stopView(isStop = true)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                stopView(isStop = false)
            }
        })

        binding.tvTitleMusic.text = "Về đâu mái tóc người thương"
        binding.tvSinger.text = "Quang Lê"
    }

    override fun onPause() {
        super.onPause()
        stopView(isStop = true)
    }

    private fun stopView(isStop: Boolean) {
        layoutManager.setScrollEnabled(isStop)
        binding.btnPlayPause.setImageResource(if (isStop || !isPlaying) R.drawable.ic_play else R.drawable.ic_pause)
        if (isStop) {
            binding.lavMusic.cancelAnimation()
            viewModel.playPauseMusic(isPlaying = true)
            return
        }
        if (isPlaying) {
            binding.lavMusic.playAnimation()
            viewModel.playPauseMusic(isPlaying = false)
            if(binding.seekBar.progress <= 5){
                layoutManager.scrollToPosition(0)
            }
            return
        }
    }

    private fun showLyrics(isShow: Boolean) {
        binding.rvLyrics.isVisible = isShow
        binding.tvStatus.isVisible = !isShow
    }
}