package dduw.com.mobile.finalproject.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dduw.com.mobile.finalproject.data.database.Art
import dduw.com.mobile.finalproject.databinding.ListItemBasicBinding

class ArtBasicAdapter : RecyclerView.Adapter<ArtBasicAdapter.ArtBasicHolder>() {
    var arts: List<Art>? = null

    override fun getItemCount(): Int {
        return arts?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtBasicHolder {
        val itemBasicBinding = ListItemBasicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtBasicHolder(itemBasicBinding)
    }

    override fun onBindViewHolder(holder: ArtBasicHolder, position: Int) {
        holder.itemBasicBinding.artTitleBasic.text =
            arts?.get(position)?.title?.let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY).toString() }
        holder.itemBasicBinding.artDateBasic.text = "${arts?.get(position)?.startDate} - ${arts?.get(position)?.endDate}"
//        // 이미지 URL을 Glide로 로드
        Glide.with(holder.itemBasicBinding.root.context)
            .load(arts?.get(position)?.thumbnail) // 이미지 URL
            .into(holder.itemBasicBinding.artPhotoBasic) // ImageView에 로드

        holder.itemBasicBinding.clItemBasic.setOnClickListener{
            itemClickListener?.onItemClick(it, position)
        }
    }

    class ArtBasicHolder(val itemBasicBinding: ListItemBasicBinding) : RecyclerView.ViewHolder(itemBasicBinding.root)

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }
}