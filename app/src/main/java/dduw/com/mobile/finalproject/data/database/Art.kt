package dduw.com.mobile.finalproject.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "art_table")
data class Art(
    @PrimaryKey
    val seq: String,
    val title: String?,
    val startDate: String?,
    val endDate: String?,
    val place: String?,
    val relamName: String?,
    val area: String?,
    val thumbnail: String?,
    val gpsX: String?,
    val gpsY: String?,
    val rating: Float?,
    val review: String?,
    var isLiked: Boolean?,
    var isReviewed: Boolean?
)
