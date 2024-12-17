package dduw.com.mobile.finalproject.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArt(art: Art)

    @Delete
    suspend fun deleteArt(art: Art)

    @Update
    suspend fun updateArt(art: Art)

    @Query("UPDATE art_table SET review = :review WHERE seq = :seq")
    suspend fun addReview(seq: String, review: String?)

    // 별점 업데이트
    @Query("UPDATE art_table SET rating = :rating WHERE seq = :seq")
    suspend fun updateRating(seq: String, rating: Float)

    // 특정 전시의 '좋아요' 상태를 업데이트
    @Query("UPDATE art_table SET isLiked = :isLiked WHERE seq = :seq")
    suspend fun updateIsLiked(seq: String, isLiked: Boolean)

    // 특정 전시의 '방문' 상태를 업데이트
    @Query("UPDATE art_table SET isReviewed = :isReviewed WHERE seq = :seq")
    suspend fun updateIsReviewed(seq: String, isReviewed: Boolean)

    @Query("SELECT * FROM art_table")
    fun getAllArts() : Flow<List<Art>>

    @Query("SELECT * FROM art_table WHERE seq = :seq")
    fun getArtBySeq(seq: String): Flow<Art>

    // 관심 전시 목록
    @Query("SELECT * FROM art_table WHERE isLiked = 1")
    fun getLikedArts(): Flow<List<Art>>

    // 다녀온 전시 목록
    @Query("SELECT * FROM art_table WHERE isReviewed = 1")
    fun getReviewedArts(): Flow<List<Art>>
}
