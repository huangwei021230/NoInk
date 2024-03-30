package com.bagel.noink.utils


import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.service.media.MediaBrowserService.BrowserRoot
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatImageView
import com.bagel.noink.R
import org.jetbrains.annotations.Nullable
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView


class LoadingDialog(context: Context) : Dialog(context) {
    private val progressBar: ProgressBar
    private val messageTextView: TextView
    private var overlay: View? = null
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.loading_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)

        progressBar = findViewById(R.id.progress_bar)
        messageTextView = findViewById(R.id.loading_message)
        overlay = findViewById(R.id.loading_layout)
    }

    fun setMessage(message: String) {
        messageTextView.text = message
    }

    fun show(message: String) {
        setMessage(message)
        show()
    }
    fun showWithLock(message: String) {
        setMessage(message)
        lockScreen()
        show()
    }

    override fun dismiss() {
        super.dismiss()
        unlockScreen()
    }

    private fun lockScreen() {
        overlay?.visibility = View.VISIBLE
        // 禁用触摸事件
        overlay?.setOnTouchListener { _, _ -> true }
    }

    private fun unlockScreen() {
        overlay?.visibility = View.GONE
    }
}
