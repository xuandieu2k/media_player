package vn.xdeuhug.music.ui.view

/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 18 / 09 / 2024
 */
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import androidx.appcompat.widget.AppCompatTextView
import vn.xdeuhug.music.R
import vn.xdeuhug.music.data.model.LyricLine
import kotlin.math.abs

class CustomLyricsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val paint = Paint().apply {
        color = context.getColor(R.color.textColor)
        textSize = 40f
        isAntiAlias = true
    }
    private val highlightedPaint = Paint().apply {
        color = Color.YELLOW
        textSize = 40f
        isAntiAlias = true
    }

    private var lyrics: List<LyricLine> = emptyList()
    private var currentPosition: Float = 0f
    private val scroller = OverScroller(context)
    private var lastTouchY = 0f
    private var scrollOffsetY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var isDragging = false

    fun setLyrics(lyrics: List<LyricLine>) {
        this.lyrics = lyrics
        invalidate()
    }

    fun setCurrentPosition(position: Float) {
        this.currentPosition = position
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var yPos = 100f + scrollOffsetY
        lyrics.forEach { line ->
            val lyricWords = line.lyricWords ?: emptyList()
            var xPos = 40f

            lyricWords.forEach { word ->
                val wordStartTime = word.startTime
                val wordEndTime = word.endTime
                val paintToUse = when {
                    currentPosition >= wordEndTime -> highlightedPaint.apply { alpha = 255 }
                    currentPosition in wordStartTime..wordEndTime -> {
                        val ratio = (currentPosition - wordStartTime) / (wordEndTime - wordStartTime)
                        highlightedPaint.apply { alpha = (255 * ratio).toInt() }
                    }
                    else -> paint.apply { alpha = 255 }
                }

                canvas.drawText(word.text, xPos, yPos, paintToUse)
                xPos += paintToUse.measureText(word.text)
            }

            yPos += 60f
        }
    }

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                lastTouchY = event.y
//                isDragging = true
//            }
//
//            MotionEvent.ACTION_MOVE -> {
//                val dy = lastTouchY - event.y
//                if (!isDragging && abs(dy) > touchSlop) {
//                    isDragging = true
//                }
//                if (isDragging) {
//                    scrollOffsetY -= dy
//                    lastTouchY = event.y
//                    invalidate()
//                }
//            }
//
//            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                isDragging = false
//            }
//        }
//        return true
//    }
//
//    override fun computeScroll() {
//        if (scroller.computeScrollOffset()) {
//            scrollOffsetY = scroller.currY.toFloat()
//            invalidate()
//        }
//    }
//
//    private fun computeMaxScroll(): Int {
//        return (lyrics.size * 60 - height).coerceAtLeast(0)
//    }
}