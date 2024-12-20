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
import dduw.com.mobile.finalproject.data.database.Art
import dduw.com.mobile.finalproject.databinding.ActivitySearchBinding
import dduw.com.mobile.finalproject.ui.ArtBasicAdapter
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory

class SearchActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
//검색 화면

    val TAG = "SEARCH_ACTIVITY_TAG"

    val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    val artViewModel: ArtViewModel by lazy {
        ViewModelProvider(
            (application as ArtApplication), // Application 범위를 공유
            ArtViewModelFactory(application, (application as ArtApplication).artRepository)
        ).get(ArtViewModel::class.java)
    }

    val realmMap = mapOf(
        "분류 전체" to null,
        "미술" to "D000",
        "연극" to "A000",
        "음악" to "B000",
        "영상" to "G000",
        "무용" to "C000",
        "건축" to "E000",
        "기타" to "L000"
    )

    lateinit var adapter : ArtBasicAdapter
    var isChangedInRecycler = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(this@SearchActivity) // 리스너 설정

        //actionBar title 변경
        getSupportActionBar()?.setTitle("아트로그")

        adapter = ArtBasicAdapter()
        binding.rvSearch.adapter = adapter
        binding.rvSearch.layoutManager = LinearLayoutManager(this)

        binding.btnToMap.setOnClickListener{
            val intent = Intent(this@SearchActivity, SearchMapActivity::class.java)
            intent.putExtra("isChanged", isChangedInRecycler)
            startActivity(intent)
        }

        binding.btnSearch.setOnClickListener{
            val defaultRealmValue = "분류 전체"
            val defaultAreaValue = "지역 전체"

            val realm = if (binding.spinnerRealm.selectedItem.toString() == defaultRealmValue) null else binding.spinnerRealm.selectedItem.toString()
            val realmCode = realmMap[realm]
            val area = if (binding.spinnerArea.selectedItem.toString() == defaultAreaValue) null else binding.spinnerArea.selectedItem.toString()

            val searchDate = binding.editSearchDate.text.toString().takeIf { it.isNotBlank() }
            val date = searchDate?.split("-")
            val from = date?.getOrNull(0)
            val to = date?.getOrNull(1)

            val keyword = binding.editSearchTitle.text.toString().takeIf { it.isNotBlank() }

            isChangedInRecycler = !(realm == null && area == null && from == null && to == null && keyword == null)
            artViewModel.getArts(realmCode, from, to, area, keyword, "1")

            artViewModel.arts.observe(this) { arts ->
                adapter.arts = arts
                adapter.notifyDataSetChanged()
            }
        }

        adapter.setOnItemClickListener(object : ArtBasicAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val seq = adapter.arts?.get(position)?.seq
                adapter.arts?.get(position)?.let { artViewModel.insertArt(it) }

                val intent = Intent(this@SearchActivity, DetailActivity::class.java)
                intent.putExtra("seq", seq) // seq만 전달
                startActivity(intent)
            }
        })

        val isChangedInMap = intent.getBooleanExtra("isChanged", false)
        if (isChangedInMap){
            artViewModel.arts.observe(this) { arts ->
                adapter.arts = arts
                adapter.notifyDataSetChanged()
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> { // 홈 메뉴
                artViewModel.getArts(null, null, null, null, null, "1")
                val intent = Intent(this@SearchActivity, MainActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_storage -> { // 보관함 메뉴
                val intent = Intent(this@SearchActivity, StorageActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.menu_review -> { // 리뷰 메뉴
                val intent = Intent(this@SearchActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return false
            }
        }
        return false
    }

}