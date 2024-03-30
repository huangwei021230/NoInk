package com.bagel.noink.viewholder;

import CommentAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bagel.noink.R
import com.bagel.noink.adapter.CommunityImageAdapter
import com.bagel.noink.bean.CommentItemBean
import com.bagel.noink.bean.CommunityItemBean
import com.bagel.noink.utils.CommunityHttpRequest
import com.bumptech.glide.Glide
import org.json.JSONObject


class CommunityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val TAG: String = "CommunityViewHolder"
    private var communityHttpRequest: CommunityHttpRequest = CommunityHttpRequest()
    private val avatarImageView: ImageView = itemView.findViewById(R.id.avatar)
    private val contentTextView: TextView = itemView.findViewById(R.id.content)
    private val likeCountTextView: TextView = itemView.findViewById(R.id.likeNumber)
    private val commentCountTextView: TextView = itemView.findViewById(R.id.commentNumber)
    private val usernameTextView: TextView = itemView.findViewById(R.id.username)
    private val gridView: GridView = itemView.findViewById(R.id.gridView)

    private val recyclerView: RecyclerView = itemView.findViewById(R.id.commentLayout)

    fun bind(item: CommunityItemBean) {

        // 将数据绑定到视图
        Glide.with(itemView.context)
            .load(item.avatar)
            .override(75, 75)  // 替换 width 和 height 为你想要的具体尺寸值
            .into(avatarImageView)

        contentTextView.text = item.content
        likeCountTextView.text = item.likes.toString()
        // 评论数
        commentCountTextView.text = item.comments.toString()
        usernameTextView.text = item.username.toString()

        gridView.visibility = View.VISIBLE
        // 如果可用，显示 imageUrls 列表中的第一个图像
        item.imageUrls?.let { imageUrls ->
            if (imageUrls.isNotEmpty()) {
                // 创建 CommunityImageAdapter 实例，并设置到 GridView 上
                val imageAdapter = CommunityImageAdapter(itemView.context, imageUrls)
                gridView.adapter = imageAdapter
            } else {
                gridView.visibility = View.GONE // 如果没有图片可用，则隐藏 GridView
            }
        } ?: run {
            gridView.visibility = View.GONE // 如果 imageUrls 为空，则隐藏 GridView
        }
        recyclerView.visibility = View.VISIBLE
        item.commentList?.let { commentItemBeans: List<CommentItemBean> ->
            if(commentItemBeans.isNotEmpty()){
                // 在适当的地方初始化 RecyclerView，设置其布局管理器和适配器
                val commentAdapter = CommentAdapter(itemView.context, commentItemBeans)
                recyclerView.layoutManager = LinearLayoutManager(itemView.context)
                recyclerView.adapter = commentAdapter

            // 更新 Adapter 的数据集（commentItemBeans）后，调用 notifyDataSetChanged() 方法
                commentAdapter.notifyDataSetChanged()

            }else {
                recyclerView.visibility = View.GONE
            }
        }?: run {
            recyclerView.visibility = View.GONE // 如果 imageUrls 为空，则隐藏 GridView
        }

        setLikeButton(item.aid.toString())


    }
    private fun setLikeButton(aid: String){
        var likeButton:ToggleButton = itemView.findViewById(R.id.likeButton)

        likeButton.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // 处理点赞逻辑
                performLikeAction(aid)
            } else {
                // 处理取消点赞逻辑
                performUnlikeAction(aid)
            }
        })

    }
    private fun performLikeAction(aid: String){
        val addLikes: TextView = itemView.findViewById(R.id.likeNumber)
        addLikes.setText((addLikes.text.toString().toInt()+1).toString())
        communityHttpRequest.addLikes(aid, callbackListener = object : CommunityHttpRequest.CommunityCallbackListener{
            override fun onSuccess(responseJson: JSONObject) {
                // 校验类型
                val code = responseJson.getInt("code")
                if (code !in 200..299) {
                    Log.e(TAG, "add likes not success")
                    throw IllegalArgumentException("Invalid 'code' value")
                }

            }
            override fun onFailure(errorMessage: String) {
                Log.e(TAG, errorMessage)
            }
        })
    }
    private fun performUnlikeAction(aid: String){
        val addLikes: TextView = itemView.findViewById(R.id.likeNumber)
        addLikes.setText((addLikes.text.toString().toInt()-1).toString())
    }
    companion object {
        fun create(parent: ViewGroup): CommunityViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_community, parent, false)
            return CommunityViewHolder(view)
        }
    }
}
