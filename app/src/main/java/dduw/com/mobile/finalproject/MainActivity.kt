package dduw.com.mobile.finalproject

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.databinding.ActivityMainBinding
import dduw.com.mobile.finalproject.ui.ArtAdapter
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    val TAG = "MAIN_ACTIVITY_TAG"

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val artViewModel: ArtViewModel by viewModels {
        ArtViewModelFactory((application as ArtApplication).artRepository)
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

//        adapter.setOnItemClickListener(object : ArtAdapter.OnItemClickListener {
//            override fun onItemClick(view: View, position: Int) {
//                val seq = adapter.arts?.get(position)?.seq
//                adapter.arts?.get(position)?.let { artViewModel.insertArt(it) }
//
//                val intent = Intent(this@MainActivity, DetailActivity::class.java)
//                intent.putExtra("seq", seq) // seq만 전달
//                startActivity(intent)
//            }
//        })

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> { // 홈 메뉴
                // 현재 날짜 구하기
                val from = Calendar.getInstance()
                val to = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }

                val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val fromDate = dateFormat.format(from.time)
                val toDate = dateFormat.format(to.time)
                // ViewModel에 API 요청
                artViewModel.getArts(null, fromDate, toDate, null, null, "1")

                return false
            }
        }
        return false
    }
}