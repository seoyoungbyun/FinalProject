package dduw.com.mobile.finalproject

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.databinding.ActivityDetailMapBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class DetailMapActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    val TAG = "DETAIL_MAP_ACTIVITY"

    private lateinit var googleMap: GoogleMap
    var gpsX = null
    var gpsY = null

    val binding by lazy {
        ActivityDetailMapBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //actionBar title 변경
        getSupportActionBar()?.setTitle("아트로그")

        //지도 객체 가져오기
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(object : OnMapReadyCallback{
            override fun onMapReady(map: GoogleMap) {
                googleMap = map

                // gpsX와 gpsY 값 가져오기
                val gpsX = intent.getStringExtra("gpsX")?.toDoubleOrNull()
                val gpsY = intent.getStringExtra("gpsY")?.toDoubleOrNull()
                val place = intent.getStringExtra("place")
                val title = intent.getStringExtra("title")

                if (gpsX != null && gpsY != null) {
                    // Marker 추가
                    addMarker(gpsY, gpsX, place, title)
                    // 역지오코딩으로 주소 가져오기
                    getAddressFromLoc(gpsY, gpsX)
                } else {
                    getAddressFromLocName(place, title)
                }
            }
        })
    }

    //뒤로 가기 시 DetailActivity 리셋 방지
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { // ActionBar의 뒤로가기 버튼 ID
                val intent = Intent(this, DetailActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) // 기존 Activity 활성화
                startActivity(intent)
                finish() // DetailMapActivity 종료
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addMarker(lat: Double, lng: Double, place: String?, title: String?) {
        val location = LatLng(lat, lng)
        val markerOptions: MarkerOptions = MarkerOptions()

        markerOptions.position(location)
            .title(place)
            .snippet(title)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

        googleMap.addMarker(markerOptions)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17F))
    }

    private fun getAddressFromLoc(lat: Double, lng: Double) {
        val geocoder = Geocoder(this, Locale.KOREAN)
        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1){addresses ->
                CoroutineScope(Dispatchers.Main).launch {
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0].getAddressLine(0)
                        binding.detailMapAddr.text = address // TextView에 주소 표시
                    } else {
                        binding.detailMapAddr.text = "주소를 찾지 못했어요"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "역지오코딩 실패: ${e.message}")
        }
    }

    private fun getAddressFromLocName(place: String?, title: String?) {
        val geocoder = Geocoder(this, Locale.KOREAN)
        try {
            val addresses = place?.let {
                geocoder.getFromLocationName(it, 1){ addresses ->
                    CoroutineScope(Dispatchers.Main).launch {
                        if (addresses.isNotEmpty()) {
                            addMarker(addresses.get(0).latitude, addresses.get(0).longitude, place, title)
                            // 역지오코딩으로 주소 가져오기
                            getAddressFromLoc(addresses.get(0).latitude, addresses.get(0).longitude)
                        } else {
                            binding.detailMapAddr.text = "주소를 찾지 못했어요"
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "지오코딩 실패: ${e.message}")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> { // 검색 메뉴
                val intent = Intent(this@DetailMapActivity, SearchActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_storage -> { // 보관함 메뉴
                val intent = Intent(this@DetailMapActivity, StorageActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_review -> { // 리뷰 메뉴
                val intent = Intent(this@DetailMapActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_map -> {
                val intent = Intent(this@DetailMapActivity, PlaceMapActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }
}