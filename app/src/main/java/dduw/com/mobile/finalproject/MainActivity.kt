package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.data.database.Art
import dduw.com.mobile.finalproject.databinding.ActivityMainBinding
import dduw.com.mobile.finalproject.ui.ArtAdapter
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
//홈화면

    val TAG = "MAIN_ACTIVITY_TAG"

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
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
        binding.rvArts.adapter = adapter
        binding.rvArts.layoutManager = LinearLayoutManager(this)

        artViewModel.arts.observe(this) { arts ->
            adapter.arts = arts
            adapter.notifyDataSetChanged()
        }

        adapter.setOnItemClickListener(object : ArtAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val seq = adapter.arts?.get(position)?.seq
                adapter.arts?.get(position)?.let { artViewModel.insertArt(it) }

                val intent = Intent(this@MainActivity, DetailActivity::class.java)
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
            R.id.menu_search -> { // 검색 메뉴
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_storage -> { // 보관함 메뉴
                val intent = Intent(this@MainActivity, StorageActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_review -> { // 리뷰 메뉴
                val intent = Intent(this@MainActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return false
            }
        }
        return false
    }

}