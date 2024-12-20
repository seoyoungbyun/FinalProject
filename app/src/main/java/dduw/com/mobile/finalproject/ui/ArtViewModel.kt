package dduw.com.mobile.finalproject.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dduw.com.mobile.finalproject.data.database.Art
import dduw.com.mobile.finalproject.data.ArtRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtViewModel (application: Application, val artRepo: ArtRepository) : AndroidViewModel(application){

    //전시 목록 조회
    private val _arts = MutableLiveData<List<Art>>()
    val arts : LiveData<List<Art>> = _arts

    fun getArts(relamCode: String?, From: String?, to: String?, sido: String?, keyword: String?, sortStdr: String) = viewModelScope.launch{
        var result : List<Art>
        withContext(Dispatchers.IO){
            result = artRepo.getArtsByKeyword(relamCode, From, to, sido, keyword, sortStdr)
        }

        _arts.value = result
    }

    //이미지 표시
    private val _drawable = MutableLiveData<Bitmap>()
    val drawable : LiveData<Bitmap> = _drawable

    fun setImage(url: String?) = viewModelScope.launch{
        var bitmap : Bitmap
        withContext(Dispatchers.IO) {
            bitmap = artRepo.getImage(url)
        }
        _drawable.value = bitmap
    }

    fun insertArt(art: Art) = viewModelScope.launch {
        artRepo.insertArt(art)
    }

    fun deleteArt(art: Art) = viewModelScope.launch {
        artRepo.deleteArt(art)
    }

    fun updateArt(art: Art) = viewModelScope.launch {
        artRepo.updateArt(art)
    }

    fun addReview(seq: String, review: String?) = viewModelScope.launch {
        artRepo.addReview(seq, review)
    }

    fun updateRating(seq: String, rating: Float) = viewModelScope.launch {
        artRepo.updateRating(seq, rating)
    }

    fun updateIsLiked(seq: String, isLiked: Boolean) = viewModelScope.launch {
        artRepo.updateIsLiked(seq, isLiked)
    }

    fun updateIsReviewed(seq: String, isReviewed: Boolean) = viewModelScope.launch {
        artRepo.updateIsReviewed(seq, isReviewed)
    }

    fun getArtBySeq(seq: String): Flow<Art> {
        return artRepo.getArtBySeq(seq)
    }

    fun getLikedArts(): Flow<List<Art>> {
        return artRepo.getLikedArts()
    }

    fun getReviewedArts(): Flow<List<Art>> {
        return artRepo.getReviewedArts()
    }
}