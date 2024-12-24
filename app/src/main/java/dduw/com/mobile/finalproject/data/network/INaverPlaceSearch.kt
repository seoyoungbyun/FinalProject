package dduw.com.mobile.finalproject.data.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface INaverPlaceSearch {

    @GET("tmap/pois/search/around")
    suspend fun getPlacesByKeyword(
        @Query("version") version: Int = 1,
        @Query("centerLon") centerLon: Float,
        @Query("centerLat") centerLat: Float,
        @Query("categories") categories: String,
        @Query("count") count: Int = 1,
        @Header("accept") accept: String = "application/json",
        @Header("appKey") appKey: String
    ): Root
}