package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dduw.com.mobile.finalproject.databinding.ActivityStartBinding
import dduw.com.mobile.finalproject.ui.ArtAdapter
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory

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

        //actionBar title 변경
        getSupportActionBar()?.setTitle("아트로그")

        binding.startBtn.setOnClickListener{
            // ViewModel에 API 요청
            artViewModel.getArts(null, null, null, null, null, "1")

            val intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}