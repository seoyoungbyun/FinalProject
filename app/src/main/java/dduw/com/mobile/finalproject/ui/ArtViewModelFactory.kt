package dduw.com.mobile.finalproject.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dduw.com.mobile.finalproject.data.database.ArtRepository

class ArtViewModelFactory(private val repo: ArtRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 생성하려는 클래스가 NVViewModel 일 경우 객체 생성
        if (modelClass.isAssignableFrom(ArtViewModel::class.java)) {
            return ArtViewModel(repo) as T
        }
        return IllegalArgumentException("Unknown ViewModel class") as T
    }
}