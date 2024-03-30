package com.bagel.noink.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bagel.noink.R
import com.bumptech.glide.Glide

class CommunitySingleImageAdapter (private val context: Context, private val images: List<Uri>) : BaseAdapter() {

    private val positionToImageViewMap = mapOf(
        0 to R.id.photo1,
        1 to R.id.photo2,
        2 to R.id.photo3,
        3 to R.id.photo4,
        4 to R.id.photo5,
        6 to R.id.photo6,
        7 to R.id.photo7,
        8 to R.id.photo8,
        9 to R.id.photo9,
    )
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

        assert(position <= 9)
        val imageView: ImageView? = positionToImageViewMap[0]?.let { view.findViewById(it) }
        Glide.with(context)
            .load(images[position]) // 加载 Uri
            .into(imageView!!) // 设置图像到 ImageView

        return view
    }

    private fun getLayoutResource(): Int {
        return R.layout.grid_item_one;
    }

}