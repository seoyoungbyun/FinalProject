package dduw.com.mobile.finalproject

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.databinding.ActivityPlaceBinding
import dduw.com.mobile.finalproject.databinding.ActivityStorageBinding
import dduw.com.mobile.finalproject.ui.ArtAdapter
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class PlaceMapActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
//보관함

    val TAG = "PLACE_ACTIVITY_TAG"

    private lateinit var googleMap: GoogleMap
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    val binding by lazy {
        ActivityPlaceBinding.inflate(layoutInflater)
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
        bottomNavigationView.setOnItemSelectedListener(this@PlaceMapActivity) // 리스너 설정

        //actionBar title 변경
        getSupportActionBar()?.setTitle("아트로그")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(3000)
            .setMinUpdateIntervalMillis(5000)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val currentLocation: Location = locationResult.lastLocation ?: return
                val targetLoc: LatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLoc, 13F))

                artViewModel.getPlaces(targetLoc.longitude.toFloat(), targetLoc.latitude.toFloat(), "공연장")
                artViewModel.places.observe(this@PlaceMapActivity) { places ->
                    if (places != null) {
                        googleMap.clear()
                        places.forEach { place ->
                            try {
                                val position = LatLng(place.noorLat.toDouble(), place.noorLon.toDouble())
                                val addr = "${place.lowerAddrName} ${place.firstNo}-${place.secondNo}"

                                googleMap.addMarker(
                                    MarkerOptions()
                                        .position(position)
                                        .title(place.name)
                                        .snippet(addr + "\n내 위치로부터 ${place.radius}")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Marker 추가 실패: ${e.message}")
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@PlaceMapActivity,
                            "장소 데이터를 불러오지 못했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
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
            }
        })

    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // Permission 확인
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions() ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) ->
                Log.d(TAG, "정확한 위치 사용")
            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) ->
                Log.d(TAG, "근사 위치 사용")
            else ->
                Log.d(TAG, "권한 미승인")
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> { // 홈 메뉴
                val intent = Intent(this@PlaceMapActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 기존 Activity 제거
                startActivity(intent)
                finish() // 현재 Activity 종료
                return true
            }

            R.id.menu_search -> { // 검색 메뉴
                val intent = Intent(this@PlaceMapActivity, SearchActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_storage -> { // 검색 메뉴
                val intent = Intent(this@PlaceMapActivity, StorageActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_review -> { // 리뷰 메뉴
                val intent = Intent(this@PlaceMapActivity, ReviewListActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }

}