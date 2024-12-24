package dduw.com.mobile.finalproject.data.network

// Root Data Class
data class Root(
    val searchPoiInfo: SearchPoiInfo,
)

data class SearchPoiInfo(
    val totalCount: Long,
    val count: Long,
    val page: Long,
    val pois: Pois,
)

data class Pois(
    val poi: List<Poi>,
)

data class Poi(
    val id: String,
    val name: String,
    val telNo: String,
    val noorLat: String,
    val noorLon: String,
    val upperAddrName: String,
    val middleAddrName: String,
    val lowerAddrName: String,
    val detailAddrName: String,
    val mlClass: String,
    val firstNo: String,
    val secondNo: String,
    val roadName: String,
    val radius: String
)
