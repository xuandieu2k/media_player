package vn.xdeuhug.music.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.UpdateAppearance
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.xdeuhug.music.R
import vn.xdeuhug.music.data.model.LyricLine
import vn.xdeuhug.music.databinding.ItemLyricsBinding

/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 19 / 09 / 2024
 */
class LyricsAdapter : RecyclerView.Adapter<LyricsAdapter.ViewHolder>() {
    private var lyrics: List<LyricLine> = emptyList()
    private var currentPosition: Float = 0f
    private var lastMatchedIndex = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setLyrics(newLyrics: List<LyricLine>) {
        lyrics = newLyrics
        notifyDataSetChanged()
    }

    fun setCurrentPosition(position: Float, recyclerView: RecyclerView) {
        val previousPosition = currentPosition
        currentPosition = position
        val currentLineIndex = findCurrentLineIndex(currentPosition)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val recyclerViewHeight = recyclerView.height
        val offset = recyclerViewHeight / 2 - (recyclerView.getChildAt(0)?.height ?: 0) / 2
        layoutManager.scrollToPositionWithOffset(currentLineIndex, offset)
        val affectedRange = findAffectedRange(previousPosition, currentPosition)
        affectedRange.forEach { index ->
            notifyItemChanged(index)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetCurrentPosition() {
        currentPosition = 0f
        notifyDataSetChanged()
    }

    fun setCurrentPositionWithSeekBar(position: Float, recyclerView: RecyclerView) {
        currentPosition = position
        val currentLineIndex = findCurrentLineIndex(currentPosition)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val recyclerViewHeight = recyclerView.height
        val offset = recyclerViewHeight / 2 - (recyclerView.getChildAt(0)?.height ?: 0) / 2
        layoutManager.scrollToPositionWithOffset(currentLineIndex, offset)
        notifyItemRangeChanged(0, itemCount)
    }


    private fun findCurrentLineIndex(currentPosition: Float): Int {
        lyrics.forEachIndexed { index, lyricLine ->
            lyricLine.lyricWords?.forEach { word ->
                if (currentPosition in word.startTime..word.endTime) {
                    lastMatchedIndex = index
                    return lastMatchedIndex
                }
            }
        }
        return lastMatchedIndex
    }

    private fun findAffectedRange(previousPosition: Float, currentPosition: Float): List<Int> {
        val affectedItems = mutableListOf<Int>()

        lyrics.forEachIndexed { index, lyricLine ->
            val isAffected = lyricLine.lyricWords?.any { word ->
                val startTime = word.startTime
                val endTime = word.endTime
                (previousPosition in startTime..endTime) || (currentPosition in startTime..endTime)
            } ?: false

            if (isAffected) {
                affectedItems.add(index)
            }
        }

        return affectedItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLyricsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun getItemCount(): Int {
        return lyrics.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(lyrics[position], currentPosition)
    }

        class ViewHolder(private val binding: ItemLyricsBinding, private var context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(lyricLine: LyricLine, currentPosition: Float) {
            val lyricWords = lyricLine.lyricWords ?: emptyList()
            val builder = SpannableStringBuilder()

            lyricWords.forEach { word ->
                val wordStartTime = word.startTime
                val wordEndTime = word.endTime

                val spannableWord = SpannableString(word.text)
                val color: Int

                when {
                    currentPosition >= wordEndTime -> {
                        color = Color.YELLOW
                    }

                    currentPosition in wordStartTime..wordEndTime -> {
                        val ratio =
                            (currentPosition - wordStartTime) / (wordEndTime - wordStartTime)
                        color = Color.argb(255, 255, (255 * ratio).toInt(), 0)

                    }

                    else -> {
                        color = context.getColor(R.color.textColor)
                    }
                }

                spannableWord.setSpan(
                    ForegroundColorSpan(color),
                    0,
                    word.text.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                builder.append(spannableWord).append(" ")
            }

            binding.tvLyrics.text = builder
        }
    }
//    class ViewHolder(private val binding: ItemLyricsBinding, private var context: Context) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun onBind(lyricLine: LyricLine, currentPosition: Float) {
//            val lyricWords = lyricLine.lyricWords ?: emptyList()
//            val builder = SpannableStringBuilder()
//
//            lyricWords.forEach { word ->
//                val wordStartTime = word.startTime
//                val wordEndTime = word.endTime
//
//                val spannableWord = SpannableString(word.text)
//
//                if (currentPosition in wordStartTime..wordEndTime) {
//                    val ratio = (currentPosition - wordStartTime) / (wordEndTime - wordStartTime)
//                    spannableWord.setSpan(
//                        GradientColorSpan(
//                            context,
//                            Color.YELLOW,
//                            context.getColor(R.color.textColor),
//                            ratio,
//                            word.text
//                        ),
//                        0,
//                        word.text.length,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                } else if (currentPosition >= wordEndTime) {
//                    spannableWord.setSpan(
//                        ForegroundColorSpan(Color.YELLOW),
//                        0,
//                        word.text.length,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                } else {
//                    spannableWord.setSpan(
//                        ForegroundColorSpan(context.getColor(R.color.textColor)),
//                        0,
//                        word.text.length,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                }
//
//                builder.append(spannableWord).append(" ")
//            }
//
//            binding.tvLyrics.text = builder
//        }
//    }
//
//    class GradientColorSpan(
//        private val context: Context,
//        private val startColor: Int,
//        private val endColor: Int,
//        private val ratio: Float,
//        private val text: String
//    ) : CharacterStyle(), UpdateAppearance {
//
//        override fun updateDrawState(tp: TextPaint) {
//            val textWidth = tp.measureText(text)
//            val shader = LinearGradient(
//                0f, 0f, textWidth * ratio, 0f,
//                startColor,
//                endColor,
//                Shader.TileMode.CLAMP
//            )
//            tp.shader = shader
//        }
//    }

}

