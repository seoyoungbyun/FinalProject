package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

        val artViewModel : ArtViewModel by viewModels {
            ArtViewModelFactory( (application as ArtApplication).artRepository )
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
                val intent = Intent(this@ReviewActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_storage -> { // 보관함 메뉴
                val intent = Intent(this@ReviewActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_review -> { // 리뷰 메뉴
                val intent = Intent(this@ReviewActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return false
            }
        }
        return false
    }
}