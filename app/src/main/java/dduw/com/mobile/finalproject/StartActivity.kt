package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dduw.com.mobile.finalproject.databinding.ActivityStartBinding
import dduw.com.mobile.finalproject.ui.ArtAdapter
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StartActivity : AppCompatActivity() {
    val TAG = "MAIN_ACTIVITY_TAG"

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

        binding.startBtn.setOnClickListener{
            // 현재 날짜 구하기
            val from = Calendar.getInstance()
            val to = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }

            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val fromDate = dateFormat.format(from.time)
            val toDate = dateFormat.format(to.time)
            // ViewModel에 API 요청
            artViewModel.getArts(null, fromDate, toDate, null, null, "1")

            val intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}