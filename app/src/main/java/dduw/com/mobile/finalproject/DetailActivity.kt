package dduw.com.mobile.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dduw.com.mobile.finalproject.databinding.ActivityDetailBinding
import dduw.com.mobile.finalproject.ui.ArtViewModel
import dduw.com.mobile.finalproject.ui.ArtViewModelFactory

class DetailActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    //art detail
    val detailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    val artViewModel : ArtViewModel by viewModels {
        ArtViewModelFactory( (application as ArtApplication).artRepository )
    }

    var seq: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(detailBinding.root)

        seq = intent.getStringExtra("seq")

        seq?.let {
            artViewModel.getArtBySeq(it).asLiveData().observe(this) { art ->
                detailBinding.title.text =
                    art.title?.let { title -> HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY) }
                detailBinding.area.text = art.area
                detailBinding.period.text = "${art.startDate} - ${art.endDate}"
                detailBinding.realm.text = art.relamName
                detailBinding.place.text = art.place

                Glide.with(this)
                    .load(art.thumbnail)
                    .into(detailBinding.artImage)
            }
        }

        detailBinding.btnSave.setOnClickListener{
            seq?.let { seq ->
                artViewModel.updateRating(seq, detailBinding.rating.rating)
                artViewModel.addReview(seq, detailBinding.review.text.toString())
                artViewModel.updateIsReviewed(seq, true)
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> { // 홈 메뉴
                val intent = Intent(this@DetailActivity, MainActivity::class.java)
                startActivity(intent)
                return false
            }

        }
        return false
    }
}