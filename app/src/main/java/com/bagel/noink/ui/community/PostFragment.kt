package com.bagel.noink.ui.community

import CommentDetailAdapter
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.bagel.noink.R
import com.bagel.noink.bean.CommentItemBean
import com.bagel.noink.bean.CommunityItemBean
import com.bagel.noink.databinding.FragmentPostBinding
import com.bagel.noink.ui.account.AccountViewModel
import com.bagel.noink.utils.CommunityHttpRequest
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.log

class PostFragment : Fragment(R.layout.fragment_post) {
    private val TAG: String = "PostFragment"

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val communityHttpRequest: CommunityHttpRequest = CommunityHttpRequest()

    private lateinit var communityItemBean: CommunityItemBean

    private lateinit var commentDetailAdapter: CommentDetailAdapter
    private lateinit var imagePagerAdapter: ImagePagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val receivedAid = arguments?.getString("aid") ?: "" // 获取传递的 aid 数据
        var comments: MutableList<CommentItemBean>? = null
        communityHttpRequest.getCommunityDetail(
            receivedAid,
            object : CommunityHttpRequest.CommunityCallbackListener {
                override fun onSuccess(responseJson: JSONObject) {
                    communityItemBean = createCommunityItem(responseJson) ?: return
                    CommentViewModel.updateCommunityItemBean(communityItemBean)
                    val title = communityItemBean.title
                    val content = communityItemBean.content
                    val createDate = communityItemBean.createdAt
                    val likeCount = communityItemBean.likes
                    val commentCount = communityItemBean.comments

                    val commentCountTextView: TextView = binding.commentCountTextView
                    activity?.runOnUiThread {
                        commentCountTextView.text = commentCount.toString()
                    }
                    val likeCountTextView: TextView = binding.likeCountTextView
                    activity?.runOnUiThread {
                        likeCountTextView.text = likeCount.toString()
                    }
                    val likeButton: ToggleButton = binding.likeButton

                    likeButton.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            // 处理点赞逻辑
                            communityHttpRequest.addCommentLikes(
                                communityItemBean.aid.toString(),
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
                            activity?.runOnUiThread {
                                likeCountTextView.text =
                                    (likeCountTextView.text.toString().toInt() + 1).toString()
                            }
                        } else {
                            // 处理取消点赞逻辑
                            activity?.runOnUiThread {
                                likeCountTextView.text =
                                    (likeCountTextView.text.toString().toInt() - 1).toString()
                            }
                        }
                    })


                    activity?.runOnUiThread {
                        binding.title.text = title
                        binding.text.text = content
                        binding.date.text = "编辑于 $createDate"
                    }


                    val imageUris: List<Uri>? = communityItemBean.imageUrls
                    val viewPager = binding.viewPager
                    val pagerIndicator = binding.pagerIndicator
                    imagePagerAdapter = imageUris?.let { ImagePagerAdapter(it) }!!
                    activity?.runOnUiThread {
                        viewPager.adapter = imagePagerAdapter
                        pagerIndicator.setViewPager(viewPager)
                    }
                    val commentEditText: TextInputEditText = binding.commentEditText
                    commentEditText.imeOptions = EditorInfo.IME_ACTION_SEND
                    commentEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)
                    activity?.runOnUiThread {
                        val recyclerView: RecyclerView = binding.commentDetailRecyclerView
                        val layoutManager = LinearLayoutManager(context)
                        recyclerView.layoutManager = layoutManager

                        comments = communityItemBean.commentList as MutableList<CommentItemBean>?
                        commentDetailAdapter = comments?.let {
                            CommentDetailAdapter(
                                it, commentEditText, context!!
                            )
                        }!!

                        recyclerView.adapter = commentDetailAdapter
                        commentDetailAdapter.notifyDataSetChanged()
                    }



                    commentEditText.setOnEditorActionListener { _, actionId, _ ->
                        if (actionId == EditorInfo.IME_ACTION_SEND) {
                            val commentText = commentEditText.text.toString().trim()
                            if (commentText.isNotEmpty()) {
                                val commentItem = CommentItemBean(
                                    0,
                                    -1,
                                    getCurrentTime(),
                                    getCurrentTime(),
                                    commentText,
                                    receivedAid.toInt(),
                                    1,
                                    AccountViewModel.userInfo?.id!!.toInt(),
                                    AccountViewModel.userInfo?.username!!,
                                    0,
                                    null,
                                    Uri.parse("https://i.postimg.cc/cJW9nd6s/image.jpg")
                                )
                                addComment(CommentViewModel.pid, commentItem)
                                if (CommentViewModel.pid != -1) {
                                    CommentViewModel.commentItemBean?.commentList =
                                        CommentViewModel.commentItemBean?.commentList?.toMutableList()
                                            ?.apply {
                                                add(commentItem)
                                            }
                                } else {
                                    comments?.add(commentItem)
                                }

                                commentEditText.text = null

                                CommentViewModel.updatePid(-1)

                                activity?.runOnUiThread {
                                    commentDetailAdapter.notifyDataSetChanged()
                                }
                            }
                            true
                        } else {
                            false
                        }
                    }


                }

                override fun onFailure(errorMessage: String) {
                    Log.e(TAG, "Failed to fetch community detail: $errorMessage")
                    // Handle failure appropriately
                }
            })




        return root
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(System.currentTimeMillis())
    }

    private fun addComment(pid: Int, itemBean: CommentItemBean) {
        communityHttpRequest.addComment(
            pid,
            itemBean,
            object : CommunityHttpRequest.CommunityCallbackListener {
                override fun onSuccess(responseJson: JSONObject) {
                    Log.e(TAG, "add comment success")

                }

                override fun onFailure(errorMessage: String) {
                    Log.e(TAG, "Failed to add comment: $errorMessage")
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun createCommunityItem(dataObjectOrigin: JSONObject?): CommunityItemBean? {
        dataObjectOrigin ?: return null

        var dataObject = dataObjectOrigin.optJSONObject("data")!!
        val aid = dataObject.optInt("aid", 0)
        val title = dataObject.optString("title", "")
        // temp avatar
        //val avatar = Uri.parse("https://i.postimg.cc/cJW9nd6s/image.jpg")
        val avatar = Uri.parse(dataObject.optString("userprofile", ""))
        val createdAt = dataObject.optString("createdAt", "").substring(0, 19)
        val updatedAt = dataObject.optString("updatedAt", "")
        val content = dataObject.optString(
            "conte" +
                    "nt", ""
        )
        val imageUrl = stringToUriList(dataObject.optString("imageUrl", ""))
        val moods = dataObject.optString("moods", "")
        val events = dataObject.optString("events", "")
        val pv = dataObject.optInt("pv", 0)
        val likes = dataObject.optInt("likes", 0)
        val comments = dataObject.optInt("comments", 0)
        val state = dataObject.optInt("state", 0)
        val uid = dataObject.optInt("uid", 0)
        val username = dataObject.optString("username", "")

        val communityItem = CommunityItemBean(
            aid, title, avatar, createdAt, updatedAt, content, imageUrl,
            moods, events, pv, likes, comments, state, uid, username, listOf()
        )

        val commentsArray = dataObject.optJSONArray("commentList")
        if (commentsArray != null) {
            val commentList = mutableListOf<CommentItemBean>()

            for (j in 0 until commentsArray.length()) {
                val commentObject = commentsArray.optJSONObject(j)
                val commentItem = createCommentItem(commentObject)
                commentItem?.let { commentList.add(it) }
            }
            communityItem.commentList = commentList
        }


        return communityItem
    }

    private fun createCommentItem(commentObject: JSONObject?): CommentItemBean? {
        commentObject ?: return null

        val cid = commentObject.optInt("cid", 0)
        val pid = commentObject.optInt("pid", 0)
        val createAt = commentObject.optString("createAt", "")
        val updateAt = commentObject.optString("updateAt", "")
        val content = commentObject.optString("content", "")
        val aid = commentObject.optInt("aid", 0)
        val state = commentObject.optInt("state", 0)
        val commentUser = commentObject.optInt("commentUser", 0)
        val username = commentObject.optString("username", "")
        val likes = commentObject.optInt("likes", 0)
        val avatar = Uri.parse(commentObject.optString("userprofile", ""))

        val commentItem = CommentItemBean(
            cid, pid, createAt, updateAt, content,
            aid, state, commentUser, username, likes, null, avatar
        )

        val childCommentListObject = commentObject.optJSONArray("commentList")
        if (childCommentListObject != null) {
            val childCommentList = mutableListOf<CommentItemBean>()

            for (k in 0 until childCommentListObject.length()) {
                val childCommentObject = childCommentListObject.optJSONObject(k)
                val childCommentItem = createCommentItem(childCommentObject)
                childCommentItem?.let { childCommentList.add(it) }
            }
            commentItem.commentList = childCommentList
        }

        return commentItem
    }

    fun stringToUriList(inputString: String): List<Uri> {
        val uriList = mutableListOf<Uri>()
        val parts = inputString.split(",")

        for (part in parts) {
            val trimmedPart = part.trim()
            if (trimmedPart.isNotEmpty()) {
                try {
                    val uri = Uri.parse(trimmedPart)
                    uriList.add(uri)
                } catch (e: Exception) {
                    // 捕获异常，如果字符串不符合 URI 格式，跳过该部分
                    e.printStackTrace()
                }
            }
        }
        return uriList
    }

    private inner class ImagePagerAdapter(private val imageURIs: List<Uri>) : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(container.context)
            Glide.with(container)
                .load(imageURIs[position])
                .into(imageView)
            container.addView(imageView)
            return imageView
        }

        override fun getCount(): Int {
            return imageURIs.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}
