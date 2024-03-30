package com.bagel.noink.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.RecyclerView
import com.bagel.noink.R
import com.bagel.noink.bean.CommunityItemBean
import com.bagel.noink.viewholder.CommunityViewHolder
import com.bagel.noink.viewholder.HistoryViewHolder

class CommunityAdapter(
    private var postList: List<CommunityItemBean>,
    private var navController: NavController
) : RecyclerView.Adapter<CommunityViewHolder>() {
    private fun setPostList(postList: List<CommunityItemBean>) {
        this.postList = postList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_community, parent, false)
        return CommunityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        val item = postList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val bundle = bundleOf(
                "aid" to item.aid.toString()
            )

            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)   // 设置进入动画
                .setExitAnim(R.anim.slide_out_left)   // 设置退出动画
                .setPopEnterAnim(R.anim.slide_in_left)   // 设置返回动画
                .setPopExitAnim(R.anim.slide_out_right)   // 设置返回退出动画
                .build()

            navController.navigate(
                R.id.action_nav_community_to_nav_community_detail,
                bundle,
                navOptions
            )
        }
    }

    override fun onBindViewHolder(
        holder: CommunityViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
