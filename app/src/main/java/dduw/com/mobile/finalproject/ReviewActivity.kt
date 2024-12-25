package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
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
        bottomNavigationView.selectedItemId = R.id.menu_review
        bottomNavigationView.setOnItemSelectedListener(this@ReviewActivity) // 리스너 설정

        //actionBar 로고 설정
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)
            setDisplayHomeAsUpEnabled(true)
        }

        seq = intent.getStringExtra("seq")

        seq?.let {
            artViewModel.getArtBySeq(it).asLiveData().observe(this) { art ->
                reviewBinding.reviewRating.rating = art.rating!!
                reviewBinding.reviewBox.setText(art.review)
            }
        }

        reviewBinding.btnUpdate.setOnClickListener {
            try {
                seq?.let { seq ->
                    artViewModel.updateRating(seq, reviewBinding.reviewRating.rating)
                    artViewModel.addReview(seq, reviewBinding.reviewBox.text.toString())
                    artViewModel.updateIsReviewed(seq, true)
                }

                // 성공 메시지 표시
                Toast.makeText(this, "리뷰가 수정되었습니다", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // 실패 메시지 표시
                Toast.makeText(this, "리뷰 수정에 실패했습니다", Toast.LENGTH_SHORT).show()
            }
        }

        reviewBinding.btnShare.setOnClickListener{
            seq?.let {
                artViewModel.getArtBySeq(it).asLiveData().observe(this) { art ->
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        val title = HtmlCompat.fromHtml(art.title ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                        val reviewText = HtmlCompat.fromHtml(art.review ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                        putExtra(Intent.EXTRA_TEXT, "$title 리뷰\n평점: ${art.rating}\n리뷰: $reviewText")
                        putExtra(Intent.EXTRA_SUBJECT, "$title 리뷰")
                    }
                    startActivity(Intent.createChooser(intent, ""))
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { // 액션 바의 뒤로가기 버튼 클릭
                onBackPressed() // 기본 뒤로가기 동작
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> { // 홈 메뉴
                val intent = Intent(this@ReviewActivity, MainActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_search -> { // 검색 메뉴
                val intent = Intent(this@ReviewActivity, SearchActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_storage -> { // 보관함 메뉴
                val intent = Intent(this@ReviewActivity, StorageActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_map->{
                val intent = Intent(this@ReviewActivity, PoiMapActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }
}