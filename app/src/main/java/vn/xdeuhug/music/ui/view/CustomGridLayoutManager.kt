package vn.xdeuhug.music.ui.view

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager



/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 20 / 09 / 2024
 */
class CustomGridLayoutManager(context: Context?) : LinearLayoutManager(context) {
    private var isScrollEnabled = true

    fun setScrollEnabled(flag: Boolean) {
        this.isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically()
    }
}