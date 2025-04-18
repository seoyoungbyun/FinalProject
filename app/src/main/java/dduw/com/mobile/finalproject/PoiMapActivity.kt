package dduw.com.mobile.finalproject

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.data.network.Poi
import dduw.com.mobile.finalproject.databinding.ActivityPoiBinding
import dduw.com.mobile.finalproject.ui.ArtAdapter
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory
import dduw.com.mobile.finalproject.ui.CustomInfoWindowAdapter

class PoiMapActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
//주변 공연장

    val TAG = "POIMAP_ACTIVITY_TAG"

    private lateinit var googleMap: GoogleMap
    //지도 초기화 시 사용자 위치로 이동 flag
    private var isCameraAnimated = false
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    val binding by lazy {
        ActivityPoiBinding.inflate(layoutInflater)
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
        bottomNavigationView.selectedItemId = R.id.menu_map
        bottomNavigationView.setOnItemSelectedListener(this@PoiMapActivity) // 리스너 설정

        //actionBar 로고 설정
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(3000)
            .setMinUpdateIntervalMillis(5000)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()

        locationCallback = object : LocationCallback() {
            //사용자 위치 받아오기
            override fun onLocationResult(locationResult: LocationResult) {
                val currentLocation: Location = locationResult.locations[0]
                val targetLoc: LatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                //사용자 위치 원으로 표시
                addMyLocationCircle(targetLoc)
                //주변 공연장 Open API 요청
                artViewModel.getPois(targetLoc.longitude.toFloat(), targetLoc.latitude.toFloat(), "공연장")
                artViewModel.pois.observe(this@PoiMapActivity) { pois ->
                    if (pois != null) {
                        googleMap.clear()
                        addMyLocationCircle(targetLoc)

                        if (!isCameraAnimated) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLoc, 13F))
                            isCameraAnimated = true
                        }

                        pois.forEach { poi ->
                            try {
                                addMarker(poi)
                            } catch (e: Exception) {
                                Log.e(TAG, "Marker 추가 실패: ${e.message}")
                            }
                        }
                    } else {
                        Toast.makeText(this@PoiMapActivity, "장소 데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 내 위치 권한 확인 후 요청
        checkPermissions()

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.aroundMap) as SupportMapFragment
        mapFragment.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(map: GoogleMap) {
                googleMap = map
                //CustomInfoWindow 설정
                googleMap.setInfoWindowAdapter(CustomInfoWindowAdapter(layoutInflater))

                // 마커 클릭 리스너 추가
                googleMap.setOnInfoWindowClickListener { marker ->
                    val poi = marker.tag as? Poi

                    poi.let {
                        //공연장 상세정보 표시
                        AlertDialog.Builder(this@PoiMapActivity).apply {
                            if (poi != null) {
                                val addr1 = "${poi.lowerAddrName} ${poi.firstNo}-${poi.secondNo}"
                                val addr2 = "${poi.upperAddrName} ${poi.middleAddrName} ${poi.roadName} ${poi.buildingNo1}"

                                setTitle("${poi.name}")
                                setMessage("도로명  $addr1\n지번  $addr2\n전화번호  ${poi.telNo}\n\n내 위치로부터 ${poi.radius}km")
                                setNegativeButton("확인", null)
                                create()
                                show()
                            }
                        }
                        false
                    }
                }
            }
        })

    }

    // "주변 공연장 위치" 마커를 추가하는 함수
    private fun addMarker(poi: Poi) {
        val position = LatLng(poi.noorLat.toDouble(), poi.noorLon.toDouble())
        val addr = "${poi.lowerAddrName} ${poi.firstNo}-${poi.secondNo}"
        // "주변 공연장 위치" 마커 추가
        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .title("${poi.name}")
                .snippet("$addr\n내 위치로부터 ${poi.radius}km")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
        marker?.tag = poi
    }

    // "내 위치" 원을 추가하는 함수
    private fun addMyLocationCircle(targetLoc: LatLng) {
        googleMap.addCircle(
            CircleOptions()
                .center(targetLoc)
                .radius(50.0)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(80, 0, 0, 255))
                .strokeWidth(5f)
        )
    }

    // Permission 확인
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions() ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                Log.d(TAG, "정확한 위치 사용")
                startLocationUpdates()
            }
            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                Log.d(TAG, "근사 위치 사용")
                startLocationUpdates()
            }
            else -> {
                Log.d(TAG, "권한 미승인")
            }
        }
    }


    private fun checkPermissions() {
        if ( checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "필요 권한 있음")
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper())
        } else {
            locationPermissionRequest.launch(
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
            )
        }
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            Log.d(TAG, "위치 업데이트 요청 성공")
        } catch (e: SecurityException) {
            Log.e(TAG, "위치 업데이트 요청 실패")
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> { // 홈 메뉴
                val intent = Intent(this@PoiMapActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 기존 Activity 제거
                startActivity(intent)
                finish() // 현재 Activity 종료
                return true
            }

            R.id.menu_search -> { // 검색 메뉴
                val intent = Intent(this@PoiMapActivity, SearchActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_storage -> { // 보관함 메뉴
                val intent = Intent(this@PoiMapActivity, StorageActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_review -> { // 리뷰 메뉴
                val intent = Intent(this@PoiMapActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }

}