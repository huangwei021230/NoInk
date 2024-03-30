package com.bagel.noink.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bagel.noink.R

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
    val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
    val childRecyclerView: RecyclerView = itemView.findViewById(R.id.secondRecyclerView)
    val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
    val likeCountTextView: TextView = itemView.findViewById(R.id.likeCountTextView)
    val likeButton: ToggleButton = itemView.findViewById(R.id.likeButton)
}