package dduw.com.mobile.finalproject.data

import android.graphics.Bitmap
import dduw.com.mobile.finalproject.data.database.Art
import dduw.com.mobile.finalproject.data.database.ArtDao
import dduw.com.mobile.finalproject.data.network.util.ArtService
import kotlinx.coroutines.flow.Flow

class ArtRepository (private val artService: ArtService, private val artDao: ArtDao){

    suspend fun getArtsByKeyword(relamCode: String?, from: String?, to: String?, sido: String?, keyword: String?, sortStdr: String) : List<Art> {
        return artService.getArtsByKeyword(relamCode, from, to, sido, keyword, sortStdr)
    }

    suspend fun getImage(url: String?) : Bitmap {
        return artService.getImage(url)
    }

    suspend fun insertArt(art: Art){
        artDao.insertArt(art)
    }

    suspend fun deleteArt(art: Art){
        artDao.deleteArt(art)
    }

    suspend fun updateArt(art: Art){
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

    fun getArtBySeq(seq: String): Flow<Art> {
        return artDao.getArtBySeq(seq)
    }

    fun getLikedArts(): Flow<List<Art>> {
        return artDao.getLikedArts()
    }

    fun getReviewedArts(): Flow<List<Art>> {
        return artDao.getReviewedArts()
    }
}