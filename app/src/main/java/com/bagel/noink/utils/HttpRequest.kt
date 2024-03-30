package com.bagel.noink.utils

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class HttpRequest {
    interface CallbackListener {
        fun onSuccess(responseJson: JSONObject)
        fun onFailure(errorMessage: String)
    }

    private val client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()

    fun get(url: String, callbackListener: CallbackListener) {
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = "Failed to connect to the backend"
                callbackListener.onFailure(errorMessage)
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, callbackListener)
            }
        })
    }
    fun get(url: String, headerName: String, headerValue:String, callbackListener: CallbackListener) {
        val request = Request.Builder().url(url).addHeader(headerName,headerValue).build()
        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = "Failed to connect to the backend"
                callbackListener.onFailure(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, callbackListener)
            }
        })
    }
    fun get(url: String, params: Map<String, String>, headerName: String, headerValue: String, callbackListener: CallbackListener){
        val urlBuilder = StringBuilder(url)
        urlBuilder.append("?") // 添加参数到 URL 中
        // 构建参数
        for ((key, value) in params) {
            urlBuilder.append(key).append("=").append(value).append("&")
        }
        val requestUrl = urlBuilder.toString().removeSuffix("&") // 移除末尾多余的 "&"
        val request = Request.Builder().url(requestUrl).addHeader(headerName,headerValue).build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = "Failed to connect to the backend"
                callbackListener.onFailure(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, callbackListener)
            }
        })
    }

    fun post(url: String, requestBody: RequestBody, callbackListener: CallbackListener) {
        val request = Request.Builder().url(url).post(requestBody).build()
        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = "Failed to connect to the backend"
                e.message?.let { Log.e("114514", it) }
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, callbackListener)
            }
        })
    }
    fun post(url: String, requestBody: RequestBody,  headerName: String, headerValue: String, callbackListener: CallbackListener) {
        val request = Request.Builder().url(url).post(requestBody).addHeader(headerName,headerValue).build()
        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = "Failed to connect to the backend"
                e.message?.let { Log.e("114514", it) }
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, callbackListener)
            }
        })
    }


    fun patch(url: String, requestBody: RequestBody,  headerName: String, headerValue: String, callbackListener: CallbackListener) {
        val request = Request.Builder().url(url).patch(requestBody).addHeader(headerName,headerValue).build()
        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = "Failed to connect to the backend"
                e.message?.let { Log.e("114514", it) }
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, callbackListener)
            }
        })
    }

    private fun handleResponse(response: Response, callbackListener: CallbackListener) {


        if (response.isSuccessful) {
            val responseBody = response.body?.string()

            if (responseBody != null) {
                try {
                    val responseJson = JSONObject(responseBody)
                    callbackListener.onSuccess(responseJson)
                } catch (e: Exception) {
                    val errorMessage = "Failed to parse JSON response"
                    callbackListener.onFailure(errorMessage + e.message)
                }
            } else {
                val errorMessage = "Empty response body"
                callbackListener.onFailure(errorMessage)
            }
        } else {
            val errorMessage = "Request failed: ${response.code}"
            callbackListener.onFailure(errorMessage)
        }
    }
}