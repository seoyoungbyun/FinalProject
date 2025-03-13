package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.data.database.Art
import dduw.com.mobile.finalproject.databinding.ActivityMapSearchBinding
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchMapActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
//검색 화면

    val TAG = "SEARCH_MAP_ACTIVITY_TAG"

    private lateinit var googleMap: GoogleMap
    var isChangedInMap = false

    val binding by lazy {
        ActivityMapSearchBinding.inflate(layoutInflater)
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
        "무용" to "C000",
        "기타" to "L000"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.menu_search
        bottomNavigationView.setOnItemSelectedListener(this@SearchMapActivity) // 리스너 설정

        //actionBar 로고 설정
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)
        }

        binding.btnToRecycler.setOnClickListener{
            val intent = Intent(this@SearchMapActivity, SearchActivity::class.java)
            intent.putExtra("isChanged", isChangedInMap)
            startActivity(intent)
        }

        val isChangedInRecycler = intent.getBooleanExtra("isChanged", false)

        binding.btnMapSearch.setOnClickListener{
            googleMap.clear() // 기존 마커 제거(중첩 현상 방지)

            val defaultRealmValue = "분류 전체"
            val defaultAreaValue = "지역 전체"

            val realm = if (binding.spinnerMapRealm.selectedItem.toString() == defaultRealmValue) null else binding.spinnerMapRealm.selectedItem.toString()
            val realmCode = realmMap[realm]
            val area = if (binding.spinnerMapArea.selectedItem.toString() == defaultAreaValue) null else binding.spinnerMapArea.selectedItem.toString()

            val searchDate = binding.editMapSearchDate.text.toString().takeIf { it.isNotBlank() }
            val date = searchDate?.split("-")
            val from = date?.getOrNull(0)
            val to = date?.getOrNull(1)

            val keyword = binding.editMapSearchTitle.text.toString().takeIf { it.isNotBlank() }

            isChangedInMap = !(realm == null && area == null && from == null && to == null && keyword == null)
            artViewModel.clearArts()

            artViewModel.arts.removeObservers(this)

            artViewModel.getArts(realmCode, from, to, area, keyword, "1")
            artViewModel.arts.observe(this) { arts ->
                val validArts = arts.filter { it.gpsY?.toDoubleOrNull() != null && it.gpsX?.toDoubleOrNull() != null }
                addMarkers(validArts)
            }
        }

        //지도 객체 가져오기
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.searchMap) as SupportMapFragment
        mapFragment.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(map: GoogleMap) {
                googleMap = map
                googleMap.clear() // 기존 마커 제거

                if (isChangedInRecycler){
                    artViewModel.arts.observe(this@SearchMapActivity) { arts ->
                        val validArts = arts.filter { it.gpsY?.toDoubleOrNull() != null && it.gpsX?.toDoubleOrNull() != null }
                        addMarkers(validArts)
                    }
                }
            }
        })

    }

    private fun addMarkers(arts: List<Art>) {
        arts.forEach { art ->
            val lat = art.gpsY?.toDoubleOrNull()
            val lng = art.gpsX?.toDoubleOrNull()

            if (lat != null && lng != null) {
                val location = LatLng(lat, lng)
                val title = HtmlCompat.fromHtml(art.title ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                val place = HtmlCompat.fromHtml(art.place ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                val markerOptions: MarkerOptions = MarkerOptions()
                markerOptions.position(location)
                    .title(title)
                    .snippet(place)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                googleMap.addMarker(markerOptions)
            }
        }

        // 카메라를 첫 번째 마커 위치로 이동
        if (arts.isNotEmpty()) {
            val firstLatLng = arts.firstOrNull()?.let {  LatLng(it.gpsY!!.toDouble(), it.gpsX!!.toDouble()) }
            firstLatLng?.let {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 11F))
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> { // 홈 메뉴
                //Open API 요청->viewModel arts 원상복귀
                artViewModel.getArts(null, null, null, null, null, "1")
                val intent = Intent(this@SearchMapActivity, MainActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_storage -> { // 보관함 메뉴
                val intent = Intent(this@SearchMapActivity, StorageActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_review -> { // 리뷰 메뉴
                val intent = Intent(this@SearchMapActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_map -> {
                val intent = Intent(this@SearchMapActivity, PoiMapActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }

}