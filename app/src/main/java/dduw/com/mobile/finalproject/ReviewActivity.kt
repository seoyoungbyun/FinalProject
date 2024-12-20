package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.databinding.ActivityReviewBinding
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory

class ReviewActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    //review detail
    val reviewBinding by lazy {
        ActivityReviewBinding.inflate(layoutInflater)
    }

    var seq: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(reviewBinding.root)

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(this@ReviewActivity) // 리스너 설정

        //actionBar title 변경
        getSupportActionBar()?.setTitle("아트로그")

        val artViewModel: ArtViewModel by lazy {
            ViewModelProvider(
                (application as ArtApplication), // Application 범위를 공유
                ArtViewModelFactory(application, (application as ArtApplication).artRepository)
            ).get(ArtViewModel::class.java)
        }

        seq = intent.getStringExtra("seq")

        seq?.let {
            artViewModel.getArtBySeq(it).asLiveData().observe(this) { art ->
                reviewBinding.reviewRating.rating = art.rating!!
                reviewBinding.reviewBox.setText(art.review)
            }
        }

        reviewBinding.btnUpdate.setOnClickListener {
            seq?.let { seq ->
                artViewModel.updateRating(seq, reviewBinding.reviewRating.rating)
                artViewModel.addReview(seq, reviewBinding.reviewBox.text.toString())
                artViewModel.updateIsReviewed(seq, true)
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> { // 홈 메뉴
                val intent = Intent(this@ReviewActivity, MainActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_search -> { // 검색 메뉴
                val intent = Intent(this@ReviewActivity, SearchActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_storage -> { // 보관함 메뉴
                val intent = Intent(this@ReviewActivity, StorageActivity::class.java)
                startActivity(intent)
                return false
            }
        }
        return false
    }
}