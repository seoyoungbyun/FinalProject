package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dduw.com.mobile.finalproject.databinding.ActivityStartBinding
import dduw.com.mobile.finalproject.ui.ArtAdapter
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartActivity : AppCompatActivity() {
    val TAG = "START_ACTIVITY_TAG"

    val binding by lazy {
        ActivityStartBinding.inflate(layoutInflater)
    }

    val artViewModel: ArtViewModel by lazy {
        ViewModelProvider(
            (application as ArtApplication), // Application 범위를 공유
            ArtViewModelFactory(application, (application as ArtApplication).artRepository)
        ).get(ArtViewModel::class.java)
    }

    lateinit var adapter : ArtAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)
        }

        binding.startBtn.setOnClickListener{
            // ViewModel에 API 요청
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    artViewModel.getArts(null, null, null, null, null, "1") // API 요청
                    withContext(Dispatchers.Main) {
                        val intent = Intent(this@StartActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@StartActivity, "데이터를 불러오지 못했습니다.\n${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    Log.e(TAG, "API 호출 오류: ${e.message}")
                }
            }
        }
    }
}