package dduw.com.mobile.finalproject.data.network.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import dduw.com.mobile.finalproject.R
import dduw.com.mobile.finalproject.data.database.Art
import dduw.com.mobile.finalproject.data.database.ArtDetail
import java.io.InputStream

class ArtService(val context: Context) {
    private val TAG = "ArtService"

    //공연전시 OpenAPI 를 이용하여 공연전시 검색결과 반환
    suspend fun getArtsByKeyword(realmCode: String?, from: String?, to: String?, sido: String?, keyword: String?, sortStdr: String) : List<Art> {

        val address : String = context.resources.getString(R.string.url)

        val params = HashMap<String, String>()
        params["rows"] = 20.toString()
        realmCode?.let { params["realmCode"] = it }
        from?.let { params["from"] = it }
        to?.let { params["to"] = it }
        sido?.let { params["sido"] = it }
        keyword?.let { params["keyword"] = it }
        params["sortStdr"] = sortStdr
        params["serviceKey"] = context.resources.getString(R.string.serviceKey)

        val result: InputStream? = try {
            Log.d(TAG, params.toString())
            NetworkUtil(context).sendRequest(NetworkUtil.GET, address, params)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        //네트워크 오류 발생 시 빈 리스트 반환 후 처리
        return result?.let { ArtParser().parse(it) } ?: emptyList()
    }

    suspend fun getArtDetailBySeq(seq: String?) : ArtDetail? {

        val address : String = context.resources.getString(R.string.url_detail)

        val params = HashMap<String, String>()
        seq?.let { params["seq"] = it }
        params["serviceKey"] = context.resources.getString(R.string.serviceKey)

        val result: InputStream? = try {
            Log.d(TAG, params.toString())
            NetworkUtil(context).sendRequest(NetworkUtil.GET, address, params)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return result?.let { ArtDetailParser().parse(it) } ?: null
    }

    // Glide 를 사용하여 책 이미지를 가져와 Bitmap 으로 반환
    suspend fun getImage(url: String?) : Bitmap {

        val futureTarget: FutureTarget<Bitmap> =
            Glide.with(context)
                .asBitmap()
                .load(url)
                .submit()

        val bitmap = futureTarget.get()
        Glide.with(context).clear(futureTarget)

        return bitmap
    }
}