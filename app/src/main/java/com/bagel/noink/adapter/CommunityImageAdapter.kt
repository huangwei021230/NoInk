package com.bagel.noink.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bagel.noink.R


class CommunityImageAdapter(private val context: Context, private val images:List<Uri>) : BaseAdapter() {

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = if (convertView == null) {
            LayoutInflater.from(context).inflate(getLayoutResource(), parent, false)
        } else {
            convertView
        }

        val imageView: ImageView? = view.findViewById(R.id.photo1)

        // 使用Glide加载图片
        Glide.with(context)
            .load(images[position])
            .into(imageView!!)

        return view
    }

    private fun getLayoutResource(): Int {
        return R.layout.grid_item_one // 这里选择使用R.layout.grid_item_one，你可以根据需要更改布局资源
    }
}
