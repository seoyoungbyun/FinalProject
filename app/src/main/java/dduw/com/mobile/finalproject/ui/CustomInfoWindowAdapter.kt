package dduw.com.mobile.finalproject.ui

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import dduw.com.mobile.finalproject.R

class CustomInfoWindowAdapter(private val inflater: LayoutInflater) : GoogleMap.InfoWindowAdapter {

    // 기본 윈도우를 사용하지 않으므로 null 반환
    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    // Custom InfoWindow 레이아웃을 반환
    override fun getInfoContents(marker: Marker): View {
        val view = inflater.inflate(R.layout.custom_info_window, null)

        // 제목 설정
        val titleTextView = view.findViewById<TextView>(R.id.title)
        titleTextView.text = marker.title

        // Snippet 설정
        val snippetTextView = view.findViewById<TextView>(R.id.snippet)
        snippetTextView.text = marker.snippet

        return view
    }
}
