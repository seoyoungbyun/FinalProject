package dduw.com.mobile.finalproject.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dduw.com.mobile.finalproject.R
import dduw.com.mobile.finalproject.data.database.ArtDetail
import dduw.com.mobile.finalproject.databinding.ListReviewItemBinding

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ReviewHolder>() {
    var arts: List<ArtDetail>? = null

    override fun getItemCount(): Int {
        return arts?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
        val itemBinding = ListReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
        holder.itemBinding.reviewTitle.text =
            arts?.get(position)?.title?.let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY).toString() }
        holder.itemBinding.reviewRating.text = arts?.get(position)?.rating.toString()
        holder.itemBinding.reviewDate.text = "${arts?.get(position)?.startDate} - ${arts?.get(position)?.endDate}"
        if (arts?.get(position)?.isLiked == true){
            holder.itemBinding.btnLikeReview.setImageResource(R.drawable.ic_liked)
        }else{
            holder.itemBinding.btnLikeReview.setImageResource(R.drawable.ic_border)
        }
//        // 이미지 URL을 Glide로 로드
        Glide.with(holder.itemBinding.root.context)
            .load(arts?.get(position)?.imgUrl) // 이미지 URL
            .into(holder.itemBinding.reviewPhoto) // ImageView에 로드

        holder.itemBinding.rvItem.setOnClickListener{
            clickListener?.onItemClick(it, position)
        }

        holder.itemBinding.rvItem.setOnLongClickListener{
            (longClickListener?.onItemLongClick(it, position) ?: false) as Boolean
        }

        holder.itemBinding.btnLikeReview.setOnClickListener{
            likeButtonClickListener?.onLikeButtonClick(it, position)
        }
    }

    class ReviewHolder(val itemBinding: ListReviewItemBinding) : RecyclerView.ViewHolder(itemBinding.root)

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface OnItemLongClickListener{
        fun onItemLongClick(view: View, position: Int) : Boolean
    }

    interface OnLikeButtonClickListener {
        fun onLikeButtonClick(view: View, position: Int)
    }

    var clickListener: OnItemClickListener? = null
    var longClickListener: OnItemLongClickListener? = null
    var likeButtonClickListener: OnLikeButtonClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.longClickListener = listener
    }

    fun setOnLikeButtonClickListener(listener: OnLikeButtonClickListener) {
        this.likeButtonClickListener = listener
    }
}