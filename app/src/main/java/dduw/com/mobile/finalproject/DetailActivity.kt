package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
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

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(this@DetailActivity) // 리스너 설정

        //actionBar title 변경
        getSupportActionBar()?.setTitle("아트로그")

        // 액션 바에 뒤로가기 버튼 표시
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d("확인", seq.toString())
        seq = intent.getStringExtra("seq")
        Log.d("확인", seq.toString())

        seq?.let {
            artViewModel.getArtBySeq(it).asLiveData().observe(this) { art ->
                detailBinding.title.text =
                    art.title?.let { title ->
                        HtmlCompat.fromHtml(
                            title,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                    }
                detailBinding.area.text = art.area
                detailBinding.period.text = "${art.startDate} - ${art.endDate}"
                detailBinding.price.text = art.price
                detailBinding.realm.text = art.relamName
                detailBinding.place.text = art.place

                Glide.with(this)
                    .load(art.imgUrl)
                    .into(detailBinding.artImage)

                if (art.isLiked == true){
                    detailBinding.detailBtnLike.setImageResource(R.drawable.ic_liked)
                }else{
                    detailBinding.detailBtnLike.setImageResource(R.drawable.ic_border)
                }

                detailBinding.detailBtnLike.setOnClickListener{
                    art.isLiked = !(art.isLiked ?: false)

                    val seq = art.seq
                    artViewModel.updateIsLiked(seq, art.isLiked!!)
                }

                detailBinding.btnMap.setOnClickListener{
                    val intent = Intent(this@DetailActivity, DetailMapActivity::class.java)
                    intent.putExtra("gpsX", art.gpsX)
                    intent.putExtra("gpsY", art.gpsY)
                    intent.putExtra("place", art.place)
                    intent.putExtra("title", art.title)
                    startActivity(intent)
                }
            }
        }

        detailBinding.btnSave.setOnClickListener {
            seq?.let { seq ->
                artViewModel.updateRating(seq, detailBinding.rating.rating)
                artViewModel.addReview(seq, detailBinding.review.text.toString())
                artViewModel.updateIsReviewed(seq, true)
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
                return false
            }

            R.id.menu_storage -> { // 보관함 메뉴
                val intent = Intent(this@DetailActivity, StorageActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_review -> { // 리뷰 메뉴
                val intent = Intent(this@DetailActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return false
            }
        }
        return false
    }
}