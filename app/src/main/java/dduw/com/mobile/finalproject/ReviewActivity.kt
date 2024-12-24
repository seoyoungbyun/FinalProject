package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
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

    val artViewModel: ArtViewModel by lazy {
        ViewModelProvider(
            (application as ArtApplication), // Application 범위를 공유
            ArtViewModelFactory(application, (application as ArtApplication).artRepository)
        ).get(ArtViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(reviewBinding.root)

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(this@ReviewActivity) // 리스너 설정

        //actionBar title 변경
        getSupportActionBar()?.setTitle("아트로그")

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

        reviewBinding.btnShare.setOnClickListener{
            seq?.let {
                artViewModel.getArtBySeq(it).asLiveData().observe(this) { art ->
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        val title = HtmlCompat.fromHtml(art.title ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                        val reviewText = HtmlCompat.fromHtml(art.review ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                        putExtra(Intent.EXTRA_TEXT, "평점: ${art.rating}\n리뷰: $reviewText")
                        putExtra(Intent.EXTRA_SUBJECT, "$title 리뷰")
                    }
                    startActivity(Intent.createChooser(intent, ""))
                }
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

            R.id.menu_map->{
                val intent = Intent(this@ReviewActivity, PlaceMapActivity::class.java)
                startActivity(intent)
                return false
            }
        }
        return false
    }
}