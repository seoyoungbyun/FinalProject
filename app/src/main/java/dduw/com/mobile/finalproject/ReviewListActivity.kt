package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.databinding.ActivityReviewListBinding
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory
import dduw.com.mobile.finalproject.ui.ReviewAdapter

class ReviewListActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
//리뷰 화면

    val TAG = "REVIEW_ACTIVITY_TAG"

    val binding by lazy {
        ActivityReviewListBinding.inflate(layoutInflater)
    }

    val artViewModel: ArtViewModel by lazy {
        ViewModelProvider(
            (application as ArtApplication), // Application 범위를 공유
            ArtViewModelFactory(application, (application as ArtApplication).artRepository)
        ).get(ArtViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(this@ReviewListActivity) // 리스너 설정

        //actionBar title 변경
        getSupportActionBar()?.setTitle("아트로그")

        val adapter = ReviewAdapter()
        binding.rvReviews.adapter = adapter
        binding.rvReviews.layoutManager = LinearLayoutManager(this)

        artViewModel.getReviewedArts().asLiveData().observe(this){arts->
            adapter.arts = arts
            adapter.notifyDataSetChanged()
        }

        adapter.setOnItemClickListener(object : ReviewAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val seq = adapter.arts?.get(position)?.seq

                val intent = Intent(this@ReviewListActivity, ReviewActivity::class.java)
                intent.putExtra("seq", seq) // seq만 전달
                startActivity(intent)
            }
        })

        adapter.setOnLikeButtonClickListener(object : ReviewAdapter.OnLikeButtonClickListener {
            override fun onLikeButtonClick(view: View, position: Int) {
                val art = adapter.arts?.get(position)
                if (art != null) {
                    // 좋아요 상태 변경
                    art.isLiked = !(art.isLiked ?: false)

                    val seq = art.seq
                    artViewModel.updateIsLiked(seq, art.isLiked!!)
                    adapter.notifyItemChanged(position)
                }
            }
        })

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> { // 홈 메뉴
                // MainActivity로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return false
            }
            R.id.menu_search -> { // 검색 메뉴
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_storage -> { // 보관함 메뉴
                val intent = Intent(this, StorageActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_map->{
                val intent = Intent(this@ReviewListActivity, PlaceMapActivity::class.java)
                startActivity(intent)
                return false
            }
        }
        return false
    }

}