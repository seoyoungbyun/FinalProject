package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.databinding.ActivityStorageBinding
import dduw.com.mobile.finalproject.ui.ArtAdapter
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory

class StorageActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
//보관함

    val TAG = "STORAGE_ACTIVITY_TAG"

    val binding by lazy {
        ActivityStorageBinding.inflate(layoutInflater)
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

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(this) // 리스너 설정

        //actionBar title 변경
        getSupportActionBar()?.setTitle("아트로그")

        adapter = ArtAdapter()
        binding.rvStorages.adapter = adapter
        binding.rvStorages.layoutManager = LinearLayoutManager(this)

        artViewModel.getLikedArts().asLiveData().observe(this){arts->
            adapter.arts = arts
            adapter.notifyDataSetChanged()
        }

        adapter.setOnItemClickListener(object : ArtAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val seq = adapter.arts?.get(position)?.seq
                adapter.arts?.get(position)?.let { artViewModel.insertArt(it) }

                val intent = Intent(this@StorageActivity, DetailActivity::class.java)
                intent.putExtra("seq", seq) // seq만 전달
                startActivity(intent)
            }
        })

        adapter.setOnLikeButtonClickListener(object : ArtAdapter.OnLikeButtonClickListener {
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
                val intent = Intent(this@StorageActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 기존 Activity 제거
                startActivity(intent)
                finish() // 현재 Activity 종료
                return true
            }

            R.id.menu_search -> { // 검색 메뉴
                val intent = Intent(this@StorageActivity, SearchActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_review -> { // 리뷰 메뉴
                val intent = Intent(this@StorageActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return false
            }
        }
        return false
    }

}