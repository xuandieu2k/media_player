package vn.xdeuhug.music.data.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Text
/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 18 / 09 / 2024
 */

data class LyricWord(
    @field:Attribute(name = "va")
    var startTime: Float = 0f,  // Thời gian bắt đầu phát từ (giây)
//    @field:Attribute(name = "ve")
    var endTime: Float = 0f,    // Thời gian kết thúc (giây)
    @field:Text
    var text: String = ""  // Nội dung từ
)