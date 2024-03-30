package com.bagel.noink.ui.account

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bagel.noink.R

class PersonalItemView(ctx: Context, attrs: AttributeSet) : RelativeLayout(ctx, attrs) {
    var data: TextView? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.item_personal_menu, this)
        @SuppressLint("CustomViewStyleable") val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.PersonaltemView)
        val icon: ImageView? = findViewById(R.id.icon)
        val more: ImageView? = findViewById(R.id.more)
        val line: ImageView? = findViewById(R.id.line)
        val name: TextView? = findViewById(R.id.name)
        data = findViewById(R.id.data)
        icon?.setImageDrawable(typedArray.getDrawable(R.styleable.PersonaltemView_icon))
        name?.text = typedArray.getText(R.styleable.PersonaltemView_name)
        if (typedArray.getBoolean(R.styleable.PersonaltemView_show_more, false)) {
            more?.visibility = VISIBLE;
        }
        if (typedArray.getBoolean(R.styleable.PersonaltemView_show_line, false)) {
            line?.visibility = VISIBLE;
        }
        typedArray.recycle()
    }

    fun setData(data: String) {
        this.data?.text = data
    }
}