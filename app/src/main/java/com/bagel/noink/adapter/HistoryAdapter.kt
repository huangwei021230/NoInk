package com.bagel.noink.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.RecyclerView
import com.bagel.noink.R
import com.bagel.noink.bean.ListItemBean
import com.bagel.noink.viewholder.HistoryViewHolder
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : RecyclerView.Adapter<HistoryViewHolder> {
    private var historyList: List<ListItemBean>
    private val navController: NavController

    constructor(historyList: List<ListItemBean>, navController: NavController) {
        this.historyList = historyList
        this.navController = navController
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.history_recycleview_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItemBean = historyList[position]

        historyItemBean.title?.let {
            holder.tvTitle.text = it
        }

        historyItemBean.text?.let {
            holder.tvText.text = it
        }

        historyItemBean.coverUri?.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .into(holder.ivImage)
        }

        val createDate = historyItemBean.createDate
        Log.i("createDate", "${historyItemBean.createDate}")
        val dayMonthFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

        createDate?.let {
            val formattedDate = dayMonthFormat.format(it)

            // 提取日和月
            val day = formattedDate.substring(0, 2)
            val month = formattedDate.substring(3) + "月"

            // 如果是同一天的第一个条目，则显示日月，否则隐藏
            if (position == 0 || formattedDate != dayMonthFormat.format(historyList[position - 1].createDate!!)) {
                holder.tvDay.visibility = View.VISIBLE
                holder.tvMonth.visibility = View.VISIBLE

                // 设置到对应的 TextView 中
                holder.tvDay.text = day
                holder.tvMonth.text = month

                if (position == 0) {
                    holder.tvLine.visibility = View.GONE
                } else {
                    holder.tvLine.visibility = View.VISIBLE
                }
            } else {
                holder.tvDay.visibility = View.INVISIBLE
                holder.tvMonth.visibility = View.INVISIBLE
                holder.tvLine.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            val bundle = bundleOf(
                "listItem" to historyItemBean
            )

            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)   // 设置进入动画
                .setExitAnim(R.anim.slide_out_left)   // 设置退出动画
                .setPopEnterAnim(R.anim.slide_in_left)   // 设置返回动画
                .setPopExitAnim(R.anim.slide_out_right)   // 设置返回退出动画
                .build()

            navController.navigate(
                R.id.action_nav_history_list_to_nav_history_details,
                bundle,
                navOptions
            )
        }
    }
}