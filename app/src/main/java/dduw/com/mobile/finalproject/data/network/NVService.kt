package dduw.com.mobile.finalproject.data.network

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import dduw.com.mobile.finalproject.R
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NVService(val context: Context) {
    private val TAG = "NVService"
    private val service : INaverPlaceSearch

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(context.resources.getString(R.string.url_naver))
            .addConverterFactory( GsonConverterFactory.create() )
            .build()

        service = retrofit.create(INaverPlaceSearch::class.java)
    }


    // Naver OpenAPI 를 이용하여 장소 검색결과 반환
    suspend fun getPlaces(lon: Float, lat: Float, categories: String): Root? {
        val appKey = context.resources.getString(R.string.appKey)

        Log.d(TAG, "Request: lon=$lon, lat=$lat, categories=$categories, appKey=$appKey")
        return try {
            service.getPlacesByKeyword(
                version = 1,
                centerLon = lon,
                centerLat = lat,
                categories = categories,
                count = 15, // Adjusted count for better results
                appKey = appKey
            )
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP Error: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unknown Error: ${e.message}")
            null
        }
    }

}