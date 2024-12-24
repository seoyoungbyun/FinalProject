package dduw.com.mobile.finalproject.data.network.util

import android.util.Xml
import dduw.com.mobile.finalproject.data.database.Art
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class ArtParser {
    private val ns: String? = null

    companion object {
        val FAULT_RESULT = "faultResult"
        val UPPER_TAG = "msgBody"
        val PERFOR_LIST_TAG = "perforList"
        val SEQ_TAG = "seq"
        val TITLE_TAG = "title"
        val STARTDATE_TAG = "startDate"
        val ENDDATE_TAG = "endDate"
        val PLACE_TAG = "place"
        val THUMBNAIL_TAG = "thumbnail"
        val GPSX_TAG = "gpsX"
        val GPSY_TAG = "gpsY"
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream?) : List<Art> {

        inputStream.use { inputStream ->
            val parser : XmlPullParser = Xml.newPullParser()

            /*Parser 의 동작 정의, next() 호출 전 반드시 호출 필요*/
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)

            /* Paring 대상이 되는 inputStream 설정 */
            parser.setInput(inputStream, null)

            /*Parsing 대상 태그의 상위 태그까지 이동*/
            while (parser.name != UPPER_TAG) {
                parser.next()
            }

            return readArt(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readArt(parser: XmlPullParser) : List<Art> {
        val arts = mutableListOf<Art>()

        parser.require(XmlPullParser.START_TAG, ns, "msgBody")
        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == PERFOR_LIST_TAG) {
                arts.add( readArtInfo(parser) )
            } else {
                skip(parser)
            }
        }

        return arts
    }


    @Throws(XmlPullParserException::class, IOException::class)
    private fun readArtInfo(parser: XmlPullParser) : Art {
        parser.require(XmlPullParser.START_TAG, ns, PERFOR_LIST_TAG)
        var seq : String = "null"
        var title : String? = null
        var thumbnail: String? = null
        var startDate: String? = null
        var endDate: String? = null
        var place: String? = null
        var gpsX: String? = null
        var gpsY: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                SEQ_TAG -> seq = readTextInTag(parser, SEQ_TAG)
                TITLE_TAG -> title = readTextInTag(parser, TITLE_TAG)
                THUMBNAIL_TAG -> thumbnail = readTextInTag(parser, THUMBNAIL_TAG)
                STARTDATE_TAG -> startDate = readTextInTag(parser, STARTDATE_TAG)
                ENDDATE_TAG -> endDate = readTextInTag(parser, ENDDATE_TAG)
                PLACE_TAG -> place = readTextInTag(parser, PLACE_TAG)
                GPSX_TAG -> gpsX = readTextInTag(parser, ArtDetailParser.GPSX_TAG)
                GPSY_TAG -> gpsY = readTextInTag(parser, ArtDetailParser.GPSY_TAG)
                else -> skip(parser)
            }
        }
        return Art(seq, title, startDate, endDate, place, thumbnail, gpsX, gpsY)
    }


    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTextInTag (parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, ns, tag)
        var text = ""

        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.text
            parser.nextTag()
        }
        parser.require(XmlPullParser.END_TAG, ns, tag)

        return text
    }


    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {

        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

}