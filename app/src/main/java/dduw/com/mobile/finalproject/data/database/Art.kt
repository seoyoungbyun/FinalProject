package dduw.com.mobile.finalproject.data.database

import androidx.room.PrimaryKey

data class Art(
    @PrimaryKey
    val seq: String,
    val title: String?,
    val startDate: String?,
    val endDate: String?,
    val place: String?,
    val thumbnail: String?,
    val gpsX: String?,
    val gpsY: String?
)
