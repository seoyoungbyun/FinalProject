package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.databinding.ActivityDetailBinding
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory

class DetailActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    //art detail
    val detailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    val artViewModel: ArtViewModel by lazy {
        ViewModelProvider(
            (application as ArtApplication), // Application 범위를 공유
            ArtViewModelFactory(application, (application as ArtApplication).artRepository)
        ).get(ArtViewModel::class.java)
    }

    var seq: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(detailBinding.root)

        val selectedId = intent.getIntExtra("selectedId", 0)

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = selectedId
        bottomNavigationView.setOnItemSelectedListener(this@DetailActivity) // 리스너 설정

        //actionBar 로고 설정
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)
            setDisplayHomeAsUpEnabled(true)
        }

        seq = intent.getStringExtra("seq")

        seq?.let {
            artViewModel.getArtBySeq(it).asLiveData().observe(this) { art ->
                if (art == null) {
                    Toast.makeText(this@DetailActivity, "해당 데이터를 찾을 수 없습니다", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    detailBinding.title.text =
                        art.title?.let { title ->
                            HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }
                    detailBinding.area.text = art.area
                    detailBinding.period.text = "${art.startDate} - ${art.endDate}"
                    detailBinding.price.text = art.price
                    detailBinding.realm.text = art.relamName
                    detailBinding.place.text = art.place

                    Glide.with(this)
                        .load(art.imgUrl)
                        .into(detailBinding.artImage)

                    if (art.isLiked == true) {
                        detailBinding.detailBtnLike.setImageResource(R.drawable.ic_liked)
                    } else {
                        detailBinding.detailBtnLike.setImageResource(R.drawable.ic_border)
                    }

                    detailBinding.detailBtnLike.setOnClickListener {
                        art.isLiked = !(art.isLiked ?: false)

                        val seq = art.seq
                        artViewModel.updateIsLiked(seq, art.isLiked!!)
                    }

                    detailBinding.btnMap.setOnClickListener {
                        val intent = Intent(this@DetailActivity, DetailMapActivity::class.java)
                        intent.putExtra("gpsX", art.gpsX)
                        intent.putExtra("gpsY", art.gpsY)
                        intent.putExtra("place", art.place)
                        intent.putExtra("title", art.title)
                        startActivity(intent)
                    }

                    if (art.isReviewed == true) {
                        detailBinding.btnSave.text = "제출된 리뷰"
                        detailBinding.rating.rating = art.rating!!
                        detailBinding.review.setText(art.review)
                        detailBinding.btnSave.isEnabled = false
                    } else {
                        detailBinding.btnSave.text = "리뷰 저장"
                        detailBinding.btnSave.isEnabled = true
                    }
                }
            }
        }

        detailBinding.btnSave.setOnClickListener {
            try {
                seq?.let { seq ->
                    artViewModel.updateRating(seq, detailBinding.rating.rating)
                    artViewModel.addReview(seq, detailBinding.review.text.toString())
                    artViewModel.updateIsReviewed(seq, true)
                }

                // 성공 메시지 표시
                Toast.makeText(this, "리뷰가 저장되었습니다", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // 실패 메시지 표시
                Toast.makeText(this, "리뷰 저장에 실패했습니다", Toast.LENGTH_SHORT).show()
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
            R.id.menu_search -> { // 검색 메뉴
                val intent = Intent(this@DetailActivity, SearchActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_storage -> { // 보관함 메뉴
                val intent = Intent(this@DetailActivity, StorageActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_review -> { // 리뷰 메뉴
                val intent = Intent(this@DetailActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_map -> {
                val intent = Intent(this@DetailActivity, PoiMapActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }
}