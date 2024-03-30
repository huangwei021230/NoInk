package com.bagel.noink.adapter

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bagel.noink.R
import com.bumptech.glide.Glide

class ImageAdapter(private var imageUris: MutableList<Uri>, private val activity: Activity) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri = imageUris[position]
        Glide.with(holder.itemView)
            .load(imageUri)
            .centerCrop()
            .into(holder.imageView)

        holder.deleteView.setOnClickListener {
            imageUris.removeAt(position)
            activity?.runOnUiThread{
                notifyItemRemoved(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val deleteView: ImageView = itemView.findViewById(R.id.deleteImageView)
    }
}