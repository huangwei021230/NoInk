package com.bagel.noink.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bagel.noink.R
import com.bagel.noink.viewholder.SearchResultViewHolder
import com.bagel.noink.bean.ListItemBean
import com.bumptech.glide.Glide


class SearchResultAdapter : RecyclerView.Adapter<SearchResultViewHolder> {

    private var searchList: List<ListItemBean>
    private val navController: NavController

    constructor(searchList: List<ListItemBean>, navController: NavController) {
        this.searchList = searchList
        this.navController = navController
    }

    fun setSearchList(searchList: List<ListItemBean>) {
        this.searchList = searchList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.search_result_recycleview_item, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val searchItemBean = searchList[position]

        searchItemBean.title?.let {
            holder.tvTitle.text = it
        }

        searchItemBean.text?.let {
            holder.tvText.text = it
        }

        searchItemBean.coverUri?.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .into(holder.ivImage)
        }

        val createDate = searchItemBean.createDate
        val dayMonthFormat = java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault())
        createDate?.let {
            val formattedDate = dayMonthFormat.format(it)

            // 提取日和月
            val day = formattedDate.substring(0, 2)
            val month = formattedDate.substring(3) + "月"

            // 如果是同一天的第一个条目，则显示日月，否则隐藏
            if (position == 0 || formattedDate != dayMonthFormat.format(searchList[position - 1].createDate!!)) {
                holder.tvDay.visibility = View.VISIBLE
                holder.tvMonth.visibility = View.VISIBLE

                // 设置到对应的 TextView 中
                holder.tvDay.text = day
                holder.tvMonth.text = month
            } else {
                holder.tvDay.visibility = View.INVISIBLE
                holder.tvMonth.visibility = View.INVISIBLE
            }
        }

        holder.itemView.setOnClickListener {
            val bundle = bundleOf(
                "listItem" to searchItemBean
            )
            navController.navigate(R.id.action_nav_search_result_to_nav_result_details, bundle)
        }
    }
}