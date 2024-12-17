package dduw.com.mobile.finalproject.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dduw.com.mobile.finalproject.data.database.Art
import dduw.com.mobile.finalproject.databinding.ListItemBinding

class ArtAdapter : RecyclerView.Adapter<ArtAdapter.ArtHolder>() {
    var arts: List<Art>? = null

    override fun getItemCount(): Int {
        return arts?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        val itemBinding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.itemBinding.artTitle.text =
            arts?.get(position)?.title?.let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY).toString() }
        holder.itemBinding.artDate.text = "${arts?.get(position)?.startDate} - ${arts?.get(position)?.endDate}"
//        // 이미지 URL을 Glide로 로드
        Glide.with(holder.itemBinding.root.context)
            .load(arts?.get(position)?.thumbnail) // 이미지 URL
            .into(holder.itemBinding.artPhoto) // ImageView에 로드

        holder.itemBinding.clItem.setOnClickListener{
            clickListener?.onItemClick(it, position)
        }
    }

    class ArtHolder(val itemBinding: ListItemBinding) : RecyclerView.ViewHolder(itemBinding.root)

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    var clickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }
}