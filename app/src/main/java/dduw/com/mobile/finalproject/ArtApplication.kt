package dduw.com.mobile.finalproject

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import dduw.com.mobile.finalproject.data.database.ArtDatabase
import dduw.com.mobile.finalproject.data.ArtRepository
import dduw.com.mobile.finalproject.data.network.util.ArtService

class ArtApplication : Application(), ViewModelStoreOwner {
    private val appViewModelStore: ViewModelStore by lazy{
        ViewModelStore()
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore

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