import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ToggleButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bagel.noink.R
import com.bagel.noink.bean.CommentItemBean
import com.bagel.noink.utils.CommunityHttpRequest
import com.bumptech.glide.Glide
import org.json.JSONObject

class CommentAdapter(private val context: Context, private val commentList: List<CommentItemBean>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val communityHttpRequest: CommunityHttpRequest = CommunityHttpRequest()
    private val TAG = "CommentAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.usernameTextView.text = comment.username
        holder.commentTextView.text = comment.content
        Glide.with(context)
            .load(comment.avatar)
            .into(holder.avatarImageView)
        // 设置头像等操作，如果需要的话
        // 例如：holder.avatarImageView.setImageResource(R.drawable.avatar_placeholder)

        holder.likeCountTextView.text = comment.likes.toString()
        // 处理点赞按钮的逻辑
        holder.likeButton.setOnCheckedChangeListener { buttonView, isChecked ->
            val cid = commentList[position].cid
            if (isChecked) {
                // 处理点赞逻辑
                communityHttpRequest.addCommentLikes(
                    cid.toString(),
                    object : CommunityHttpRequest.CommunityCallbackListener {
                        override fun onSuccess(responseJson: JSONObject) {
                            // 处理成功响应
                            val code = responseJson.getInt("code")
                        }
                        override fun onFailure(errorMessage: String) {
                            // 处理失败情况
                            Log.e(TAG, errorMessage)
                        }
                    })
                holder.likeCountTextView.text = (comment.likes + 1).toString()
                comment.likes = comment.likes + 1
            } else {
                // 处理取消点赞逻辑
                holder.likeCountTextView.text = (comment.likes - 1).toString()
                comment.likes = comment.likes - 1
            }
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        val likeButton: ToggleButton = itemView.findViewById(R.id.likeButton)
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
        val likeCountTextView: TextView = itemView.findViewById(R.id.likeCountTextView)
    }
}
