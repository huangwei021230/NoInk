package com.bagel.noink.utils

import android.net.Uri
import com.bagel.noink.bean.CommentItemBean
import com.bagel.noink.bean.CommunityItemBean
import com.bagel.noink.ui.account.AccountViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class CommunityHttpRequest {
    interface CommunityCallbackListener {
        fun onSuccess(responseJson: JSONObject)
        fun onFailure(errorMessage: String)
    }
    private val httpRequest = HttpRequest()


    fun getCommunityList(callbackListener: CommunityCallbackListener) {
        val url = Contants.SERVER_ADDRESS + "/api/article/datum" // 请根据实际情况替换为正确的 API 地址
        val headerName = "satoken";
        val headerValue = AccountViewModel.token!!
        httpRequest.get(url, headerName, headerValue, object : HttpRequest.CallbackListener {
            override fun onSuccess(responseJson: JSONObject) {
                callbackListener.onSuccess(responseJson)
            }

            override fun onFailure(errorMessage: String) {
                callbackListener.onFailure(errorMessage)
            }
        })
    }
    fun getCommunityDetail(aid: String, callbackListener: CommunityCallbackListener){
        val url = Contants.SERVER_ADDRESS + "/api/article/details"
        val headerName = "satoken";
        val headerValue = AccountViewModel.token!!
        val params = mapOf(
            "aid" to aid,
        )
        httpRequest.get(url, params, headerName, headerValue, object : HttpRequest.CallbackListener {
            override fun onSuccess(responseJson: JSONObject) {
                callbackListener.onSuccess(responseJson)
            }

            override fun onFailure(errorMessage: String) {
                callbackListener.onFailure(errorMessage)
            }
        })

    }
    fun addLikes(aid: String, callbackListener: CommunityCallbackListener){
        val url = Contants.SERVER_ADDRESS +"/api/article/like"
        val headerName = "satoken";
        val headerValue = AccountViewModel.token!!
        val params = mapOf(
            "aid" to aid,
        )
        httpRequest.get(url, params, headerName, headerValue, object : HttpRequest.CallbackListener{
            override fun onSuccess(responseJson: JSONObject) {
                callbackListener.onSuccess(responseJson)
            }

            override fun onFailure(errorMessage: String) {
                callbackListener.onFailure(errorMessage)
            }
        })
    }
    fun addCommentLikes(cid: String, callbackListener: CommunityCallbackListener) {
        val url = Contants.SERVER_ADDRESS + "/api/comment/like?cid=$cid" // 将 cid 作为查询参数附加
        val headerName = "satoken"
        val headerValue = AccountViewModel.token!!

        // 创建一个空的RequestBody实例，因为我们不发送请求体
        val requestBody = "".toRequestBody(null)

        httpRequest.post(url, requestBody, headerName, headerValue, object : HttpRequest.CallbackListener {
            override fun onSuccess(responseJson: JSONObject) {
                callbackListener.onSuccess(responseJson)
            }
            override fun onFailure(errorMessage: String) {
                callbackListener.onFailure(errorMessage)
            }
        })
    }

    fun addComment(pid:Int, itemBean: CommentItemBean, callbackListener: CommunityCallbackListener){
        val url = Contants.SERVER_ADDRESS + "/api/comment/save"
        val headerName = "satoken";
        val headerValue = AccountViewModel.token!!;
        val jsonBody = JSONObject().apply {
            put("pid", pid)
            put("createAt", itemBean.createAt)
            put("updateAt", itemBean.updateAt)
            put("content", itemBean.content)
            put("aid", itemBean.aid)
            put("state", itemBean.state)
            put("commentUser", itemBean.commentUser)
            put("likes", itemBean.likes)
        }
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)
        httpRequest.post(url, requestBody, headerName, headerValue, object : HttpRequest.CallbackListener {
            override fun onSuccess(responseJson: JSONObject) {
                callbackListener.onSuccess(responseJson)
            }
            override fun onFailure(errorMessage: String) {
                callbackListener.onFailure(errorMessage)
            }
        })
    }

}