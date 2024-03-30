package com.bagel.noink.ui.generate

import android.net.Uri
import com.bagel.noink.bean.TextGenInfoBean

class TextGenViewModel {
    companion object {
        // 用户信息
        var textGenInfo: TextGenInfoBean? = TextGenInfoBean(
            null,
            "test",
            null,
            null,
            "50",
            null
        )
        fun updateInfoUrls(imageUrl: List<Uri>){
            textGenInfo?.imageUrls = imageUrl
        }
        fun updateOriginText(originText: String){
            textGenInfo?.originText = originText
        }
        fun updateStyle(style: String){
            textGenInfo?.style = style
        }
        fun updateType(type: String){
            textGenInfo?.type = type
        }
        fun updateLength(length: String){
            textGenInfo?.length = length
        }
        fun updateTaskId(taskId: String){
            textGenInfo?.taskId = taskId
        }
        fun getInfoUrls(): List<Uri>? {
            return textGenInfo?.imageUrls
        }
        fun getOriginText(): String? {
            return textGenInfo?.originText
        }
        fun getStyle(): String? {
            return textGenInfo?.style
        }
        fun getType(): String? {
            return textGenInfo?.type
        }
        fun getLength(): String? {
            return textGenInfo?.length
        }
        fun getTaskId(): String? {
            return textGenInfo?.taskId
        }
    }
}