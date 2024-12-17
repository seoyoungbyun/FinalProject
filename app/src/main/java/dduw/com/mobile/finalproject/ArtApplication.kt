package dduw.com.mobile.finalproject

import android.app.Application
import dduw.com.mobile.finalproject.data.database.ArtDatabase
import dduw.com.mobile.finalproject.data.database.ArtRepository
import dduw.com.mobile.finalproject.data.network.util.ArtService

class ArtApplication : Application() {
    val artService by lazy {
        ArtService(this)
    }

    val artDatabase by lazy {
        ArtDatabase.getDatabase(this)
    }

    val artRepository by lazy {
        ArtRepository(artService, artDatabase.artDao())
    }
}