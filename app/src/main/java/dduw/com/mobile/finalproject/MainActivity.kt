package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.databinding.ActivityMainBinding
import dduw.com.mobile.finalproject.ui.ArtBasicAdapter
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    lateinit var adapter : ArtBasicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(this@MainActivity) // 리스너 설정

        //actionBar title 변경
        getSupportActionBar()?.setTitle("아트로그")

        adapter = ArtBasicAdapter()
        binding.rvArts.adapter = adapter
        binding.rvArts.layoutManager = LinearLayoutManager(this)

        artViewModel.arts.observe(this) { arts ->
            adapter.arts = arts
            adapter.notifyDataSetChanged()
        }

        adapter.setOnItemClickListener(object : ArtBasicAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val seq = adapter.arts?.get(position)?.seq
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val detailedArt = seq?.let { artViewModel.getArtDetail(it) } // API 호출
                        detailedArt?.let {
                            Log.d("확인", detailedArt.toString())
                            artViewModel.insertArt(it) // 상세 데이터 저장
                        }

                        // 비동기 작업 완료 후 UI 스레드에서 Intent 실행
                        withContext(Dispatchers.Main) {
                            val intent = Intent(this@MainActivity, DetailActivity::class.java)
                            intent.putExtra("seq", seq) // seq만 전달
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(view.context, "API 호출 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("API 오류", "에러 발생: ${e.message}")
                    }
                }
            }
        })

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> { // 검색 메뉴
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
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