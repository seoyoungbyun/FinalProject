package dduw.com.mobile.finalproject.data.network

import android.content.Context
import android.util.Log
import dduw.com.mobile.finalproject.R
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class POIService(val context: Context) {
    private val TAG = "POIService"
    private val service : PoiSearch

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(context.resources.getString(R.string.url_tmap))
            .addConverterFactory( GsonConverterFactory.create() )
            .build()

        service = retrofit.create(PoiSearch::class.java)
    }

    //TMAP OpenAPI 를 이용하여 주변 공연장 검색결과 반환
    suspend fun getPois(lon: Float, lat: Float, categories: String): Root? {
        val appKey = context.resources.getString(R.string.appKey)

        return try {
            service.getPoisByCategories(
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