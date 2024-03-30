package com.bagel.noink.ui.generate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bagel.noink.R
import com.bagel.noink.activity.CommunityActivity
import com.bagel.noink.activity.HistoryActivity
import com.bagel.noink.adapter.ImageAdapter
import com.bagel.noink.bean.CommunityItemBean
import com.bagel.noink.databinding.FragmentTexteditBinding
import com.bagel.noink.ui.account.AccountViewModel
import com.bagel.noink.utils.AliyunOSSManager
import com.bagel.noink.utils.LoadingDialog
import com.bagel.noink.utils.TextGenHttpRequest
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class TextEditFragment: Fragment() {
    private var _binding: FragmentTexteditBinding? = null
    private val binding get() = _binding!!
    companion object {
        private const val PICK_IMAGES_REQUEST_CODE = 101
        private const val ARG_SELECTED_IMAGE_URIS = "selected_image_uris"
    }
    private var selectedImageUris = mutableListOf<Uri>()
    private lateinit var textGenHttpRequest: TextGenHttpRequest
    private var dialog: LoadingDialog? = null
    // save vars
    private lateinit var createdAt: String
    private lateinit var updatedAt: String
    private lateinit var generatedText: String
    private lateinit var title: EditText
    private lateinit var content: EditText
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
            : View? {
        // 使用 DataBindingUtil.inflate() 进行绑定
        _binding = FragmentTexteditBinding.inflate(inflater,container,false)
        textGenHttpRequest = TextGenHttpRequest()
        title = binding.editTextTitle
        content = binding.editText


        setTags(inflater)
        setImageUploadButton()
        setGenButton()
        setSaveButton()
        setPostUploadButton()
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setPostUploadButton(){
        val postUploadButton:ImageButton = requireActivity().findViewById(R.id.toolbar_upload)
        postUploadButton.setOnClickListener {
            // 在分享到社区前先保存
            saveText()
            AccountViewModel.userInfo?.articleNum = AccountViewModel.userInfo?.articleNum!! + 1
            textGenHttpRequest.sendPostRequest(CommunityItemBean(
                aid = 0,
                title = title.text.toString(),
                avatar = Uri.parse("https://i.postimg.cc/cJW9nd6s/image.jpg"),
                createdAt = createdAt?:getCurrentTime(),
                updatedAt = updatedAt,
                content = content.text.toString(),
                imageUrls = selectedImageUris,
                moods = TextGenViewModel.getStyle()!!,
                events = TextGenViewModel.getType()!!,
                pv = 0,
                likes = 0,
                comments = 0,
                state = 1,
                uid = AccountViewModel.userInfo!!.id.toInt(),
                username = AccountViewModel.userInfo!!.username,
                commentList = emptyList()
            ), object : TextGenHttpRequest.TextGenCallbackListener {
                override fun onSuccess(responseJson: JSONObject) {

                }
                override fun onFailure(errorMessage: String) {
                    // 处理请求失败
                    println("Request failed: $errorMessage")
                }
            })

            activity?.runOnUiThread{
                // 返回到home界面
                requireActivity().supportFragmentManager.popBackStack()
                requireActivity().supportFragmentManager.popBackStack()
                requireActivity().supportFragmentManager.popBackStack()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

    }

    private fun setTags(inflater: LayoutInflater){
        var linearLayout = binding.tags

        // 创建第一个 item_tag 视图并设置内容
        val componentViewStyle: View = inflater.inflate(R.layout.item_tag, linearLayout, false)
        val textViewStyle = componentViewStyle.findViewById<TextView>(R.id.item_tag)
        textViewStyle.text = TextGenViewModel.getStyle()
        linearLayout.addView(componentViewStyle)

        // 创建第二个 item_tag 视图并设置内容
        val componentViewType: View = inflater.inflate(R.layout.item_tag, linearLayout, false)
        val textViewType = componentViewType.findViewById<TextView>(R.id.item_tag)
        textViewType.text = TextGenViewModel.getType()
        linearLayout.addView(componentViewType)


    }

    private fun setImageUploadButton(){
        val imageView = binding.imageView
        imageView.setOnClickListener{
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(galleryIntent, PICK_IMAGES_REQUEST_CODE)
        }
    }
    fun getDriveFile(context: Context, uri: Uri): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(context.cacheDir, name)
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)
            Log.e("File Size", "Size " + file.length())
        } catch (e: Exception) {
            Log.e("Exception", e.message!!)
        }
        return file.path
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 清空已选图片列表，以便重新添加选择的图片
            selectedImageUris.clear()
            // 获取从系统图片选择器返回的所有 Uri
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    val filePath = getDriveFile(requireContext(), uri)
                    // filePath 变量包含实际的本地文件路径
                    val aliyunOSSManager = AliyunOSSManager(context)
                    val aliyunOSSUrl = aliyunOSSManager.uploadImage(filePath,"test"+ generateRandomString(8));
//                            val aliyunOSSUrl = "https://i.postimg.cc/cJW9nd6s/image.jpg"
                    if(aliyunOSSUrl != null){
                        selectedImageUris.add(Uri.parse(aliyunOSSUrl))
                    }
                    // 将选择的图片 Uri 添加到列表中
                }

            } else {
                // 单选图片时处理
                val uri = data?.data!!
                val filePath = getDriveFile(requireContext(), uri)
                val aliyunOSSManager = AliyunOSSManager(context)
                val aliyunOSSUrl = aliyunOSSManager.uploadImage(filePath,"test"+generateRandomString(8));
//                        val aliyunOSSUrl = "https://i.postimg.cc/cJW9nd6s/image.jpg"
                if(aliyunOSSUrl != null){
                    selectedImageUris.add(Uri.parse(aliyunOSSUrl))
                }
                // 将选择的图片 Uri 添加到列表中
            }


        }
        handleSelectedImages(selectedImageUris)
    }
    fun generateRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') // 允许的字符集合
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
    private fun showLoadingDialog() {
        dialog = LoadingDialog(requireActivity())
        dialog?.showWithLock("Loading ...")

    }
    private fun hideLoadingDialog() {
        dialog?.dismiss()
        dialog = null
    }
    private fun handleSelectedImages(imageUris: List<Uri>) {
        binding?.let { bound ->
            val recyclerView: RecyclerView = bound.recyclerView

            val adapter = ImageAdapter(imageUris.toMutableList(), activity as Activity)
            recyclerView.adapter = adapter

            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = layoutManager
        }
    }


    private fun setGenButton(){
        val genButton = binding.buttonGenerate
        genButton.setOnClickListener {
            generateText()
        }
        val regenerateButton = binding.buttonRegenerate
        regenerateButton.setOnClickListener {
            generateText()
        }
    }
    private fun generateText() {
        // 实例化TextGenHttpRequest类


        showLoadingDialog()
        val editText = binding.editText
        TextGenViewModel.updateOriginText(editText.text.toString())
        TextGenViewModel.updateInfoUrls(selectedImageUris)
        // 得到参数
        val length = TextGenViewModel.getLength()!!
        val typeStr = TextGenViewModel.getType()!!
        var origintext = TextGenViewModel.getOriginText()!!
        var style = TextGenViewModel.getStyle()!!

        // 调用sendTextRequest方法发送请求
        textGenHttpRequest.sendTextRequest(
            length = length.toInt(),
            imageUrls = selectedImageUris,
            type = typeStr,
            originText = origintext,
            style = style,
            callbackListener = object : TextGenHttpRequest.TextGenCallbackListener {
                override fun onSuccess(responseJson: JSONObject) {
                    // 处理请求成功的响应JSON对象
                    // 在这里使用responseJson
                    Log.e("responseJson", responseJson.toString())
                    // 处理请求成功的响应JSON对象
                    // 在这里使用responseJson
                    editText.setText(responseJson.getString("generatedText"))
                    createdAt = responseJson.getString("createdAt")
                    updatedAt = responseJson.getString("updatedAt")
                    generatedText = responseJson.getString("generatedText");
                    TextGenViewModel.updateOriginText(responseJson.getString("originText"))
                    TextGenViewModel.updateType(responseJson.getString("type"))
                    activity?.runOnUiThread{
                        binding.buttonRegenerate.visibility = View.VISIBLE
                        binding.buttonSave.visibility = View.VISIBLE
                        binding.buttonGenerate.visibility = View.GONE
                        val toolbarImage: ImageButton = requireActivity().findViewById(R.id.toolbar_upload)
                        toolbarImage.visibility = View.VISIBLE
                    }
                    hideLoadingDialog()
                }
                override fun onFailure(errorMessage: String) {
                    // 处理请求失败
                    println("Request failed: $errorMessage")
                }
            }
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setSaveButton(){
        val saveButton = binding.buttonSave
        saveButton.setOnClickListener {
            saveText()
            AccountViewModel.needToUpdateHistory = true
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(): String {
        val currentTime = ZonedDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        return currentTime.format(formatter)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveText(){
        AccountViewModel.userInfo?.recordNum = AccountViewModel.userInfo?.recordNum!! + 1

        textGenHttpRequest.sendSaveRequest(
            createdAt = createdAt?:getCurrentTime(),
            updatedAt = getCurrentTime(),
            originText = TextGenViewModel.getOriginText()!!,
            title = title.text.toString()?:"this is a title",
            imageUrls = selectedImageUris,
            labels = TextGenViewModel.getStyle()!!,
            generatedText = content.text.toString(),
            type = TextGenViewModel.getType()!!,
            callbackListener = object : TextGenHttpRequest.TextGenCallbackListener {
                override fun onSuccess(responseJson: JSONObject) {
                    // 处理请求成功的响应JSON对象
                    // 在这里使用responseJson
                    Log.i("save succuss", responseJson.getString("code"));
                }
                override fun onFailure(errorMessage: String) {
                    // 处理请求失败
                    println("Request failed: $errorMessage")
                }
            }
        )


    }
    override fun onDestroyView() {
        super.onDestroyView()
        val toolbarImage: ImageButton = requireActivity().findViewById(R.id.toolbar_upload)
        toolbarImage.visibility = View.GONE
        _binding = null
    }
}