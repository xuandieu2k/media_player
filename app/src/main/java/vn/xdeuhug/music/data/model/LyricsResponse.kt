package vn.xdeuhug.music.data.model

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
/**
 * @Author: NGUYEN XUAN DIEU
 * @Date: 18 / 09 / 2024
 */
@Root(name = "data", strict = false)
data class LyricsResponse(
    @field:ElementList(entry = "param", inline = true)
    var lyricsParams: List<LyricsParam>? = null
)

@Root(name = "param", strict = false)
data class LyricsParam(
    @field:ElementList(entry = "i", inline = true)
    var lyricWords: List<LyricWord>? = null
)