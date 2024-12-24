package dduw.com.mobile.finalproject.data

import android.graphics.Bitmap
import dduw.com.mobile.finalproject.data.database.Art
import dduw.com.mobile.finalproject.data.database.ArtDao
import dduw.com.mobile.finalproject.data.database.ArtDetail
import dduw.com.mobile.finalproject.data.network.NVService
import dduw.com.mobile.finalproject.data.network.Poi
import dduw.com.mobile.finalproject.data.network.util.ArtService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ArtRepository (private val artService: ArtService, private val nvService: NVService, private val artDao: ArtDao){

    suspend fun getArtsByKeyword(relamCode: String?, from: String?, to: String?, sido: String?, keyword: String?, sortStdr: String) : List<Art> {
        return artService.getArtsByKeyword(relamCode, from, to, sido, keyword, sortStdr)
    }

    suspend fun getArtDetail(seq: String?) : ArtDetail? {
        return artService.getArtDetailBySeq(seq)
    }

    suspend fun getPlaces(lon: Float, lat: Float, categories: String): List<Poi>? {
        return withContext(Dispatchers.IO) {
            val response = nvService.getPlaces(lon, lat, categories)
            response?.searchPoiInfo?.pois?.poi ?: emptyList()
        }
    }

    suspend fun getImage(url: String?) : Bitmap {
        return artService.getImage(url)
    }

    suspend fun insertArt(art: ArtDetail){
        artDao.insertArt(art)
    }

    suspend fun deleteArt(art: ArtDetail){
        artDao.deleteArt(art)
    }

    suspend fun updateArt(art: ArtDetail){
        artDao.updateArt(art)
    }

    suspend fun addReview(seq: String, review: String?){
        artDao.addReview(seq, review)
    }

    suspend fun updateRating(seq: String, rating: Float){
        artDao.updateRating(seq, rating)
    }

    suspend fun updateIsLiked(seq: String, isLiked: Boolean){
        artDao.updateIsLiked(seq, isLiked)
    }

    suspend fun updateIsReviewed(seq: String, isReviewed: Boolean){
        artDao.updateIsReviewed(seq, isReviewed)
    }

    fun getArtBySeq(seq: String): Flow<ArtDetail> {
        return artDao.getArtBySeq(seq)
    }

    fun getLikedArts(): Flow<List<ArtDetail>> {
        return artDao.getLikedArts()
    }

    fun getReviewedArts(): Flow<List<ArtDetail>> {
        return artDao.getReviewedArts()
    }
}