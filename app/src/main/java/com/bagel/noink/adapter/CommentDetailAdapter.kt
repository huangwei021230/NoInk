import android.net.Uri
import android.content.Context

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ImageView
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bagel.noink.R
import com.bagel.noink.bean.CommentItemBean
import com.bagel.noink.ui.community.CommentViewModel
import com.bagel.noink.utils.CommunityHttpRequest
import com.bagel.noink.viewholder.CommentViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject


class CommentDetailAdapter(private val commentList: List<CommentItemBean>, private val editText: TextInputEditText, private val context: Context) :
    RecyclerView.Adapter<CommentViewHolder>() {
    private val TAG = "CommentDetailAdapter"
    private val communityHttpRequest: CommunityHttpRequest = CommunityHttpRequest()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.likeCountTextView.text = comment.likes.toString()
        holder.usernameTextView.text = comment.username
        holder.commentTextView.text = comment.content
        // 设置其他评论的信息

        Glide.with(holder.itemView.context)
            .load(comment.avatar)
            .into(holder.avatarImageView)

        // 设置子评论列表的适配器和数据
        val childCommentAdapter = ChildCommentAdapter(comment.commentList ?: emptyList(), editText, context)
        holder.childRecyclerView.adapter = childCommentAdapter
        // 设置 RecyclerView 的布局管理器，可以根据需要设置
        holder.childRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, RecyclerView.VERTICAL, false)
        childCommentAdapter.notifyDataSetChanged()

        val textView = holder.itemView.findViewById<TextView>(R.id.commentTextView)
        textView.setOnClickListener {
            CommentViewModel.updateCommentItemBean(comment)
            CommentViewModel.updatePid(comment.cid)
            editText.requestFocus()

            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }

        holder.likeButton.setOnCheckedChangeListener { buttonView, isChecked ->
            val cid = comment.cid
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



    // 子评论的适配器
    inner class ChildCommentAdapter(private val childCommentList: List<CommentItemBean>, private val editText: TextInputEditText, private val context: Context) :
        RecyclerView.Adapter<ChildCommentAdapter.ChildCommentViewHolder>() {
        // 内部 ViewHolder 类
        inner class ChildCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
            val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
            val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
            val likeCountTextView: TextView = itemView.findViewById(R.id.likeCountTextView)
            val likeButton: ToggleButton = itemView.findViewById(R.id.likeButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildCommentViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_comment_child, parent, false)
            return ChildCommentViewHolder(view)
        }

        override fun onBindViewHolder(holder: ChildCommentViewHolder, position: Int) {
            val comment = childCommentList[position]

            holder.likeCountTextView.text = comment.likes.toString()
            holder.usernameTextView.text = comment.username
            holder.commentTextView.text = comment.content
            Glide.with(holder.itemView.context)
                .load(comment.avatar)
                .into(holder.avatarImageView)

            val textView = holder.itemView.findViewById<TextView>(R.id.commentTextView)
            textView.setOnClickListener {
                CommentViewModel.updateCommentItemBean(comment)
                CommentViewModel.updatePid(comment.cid)
                editText.requestFocus()

                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }

            holder.likeButton.setOnCheckedChangeListener { buttonView, isChecked ->
                val cid = comment.cid
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
            return childCommentList.size
        }
    }



}
