package dduw.com.mobile.finalproject

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    val TAG = "MAIN_ACTIVITY_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(this) // 리스너 설정

        //actionBar title 변경
        getSupportActionBar()?.setTitle("아트로그")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> { // 홈 메뉴
//                val intent = Intent(this@MainActivity, )
//                startActivity(intent)
//                return false
                Log.d(TAG, "홈 메뉴 클릭")
            }
        }
        return false
    }
}