package com.bagel.noink.ui.history

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.PagerAdapter
import com.bagel.noink.R
import com.bagel.noink.bean.ListItemBean
import com.bagel.noink.databinding.FragmentDetailsBinding
import com.bagel.noink.ui.account.AccountViewModel
import com.bagel.noink.utils.Contants
import com.bagel.noink.utils.HttpRequest
import com.bumptech.glide.Glide
import okhttp3.RequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale


class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.title.text = arguments?.getParcelable<ListItemBean>("listItem")?.title
        binding.text.text = arguments?.getParcelable<ListItemBean>("listItem")?.text

        val moodTags: List<String> = arguments?.getParcelable<ListItemBean>("listItem")?.moodTags ?: emptyList()
        val eventTag: String = arguments?.getParcelable<ListItemBean>("listItem")?.eventTag ?: ""

        var tag = "#$eventTag "
        for (mood in moodTags) {
            tag = "$tag#$mood "
        }

        binding.tags.text = tag

        val createDate = arguments?.getParcelable<ListItemBean>("listItem")?.createDate
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(createDate)

        binding.date.text = "编辑于 $formattedDate"

        val bottomNavContainer : FrameLayout ?= activity?.findViewById(R.id.bottom_nav_container)
        bottomNavContainer?.setVisibility(View.GONE)

        var imageURIs: List<Uri> = arguments?.getParcelable<ListItemBean>("listItem")?.imagesUri ?: emptyList()
        val viewPager = binding.viewPager
        val pagerIndicator = binding.pagerIndicator

        // Set up ViewPager2 and CirclePageIndicator
        val imagePagerAdapter = ImagePagerAdapter(imageURIs)
        viewPager.adapter = imagePagerAdapter
        pagerIndicator.setViewPager(viewPager)

        return root
    }

    private inner class ImagePagerAdapter(private val imageURIs: List<Uri>) : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(container.context)
            Glide.with(container)
                .load(imageURIs[position])
                .into(imageView)
            container.addView(imageView)
            return imageView
        }

        override fun getCount(): Int {
            return imageURIs.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val delete = requireActivity().findViewById<ImageView>(R.id.search)
        delete?.setImageResource(R.drawable.ic_delete_rubbish)

        delete?.setOnClickListener {
            val rid = arguments?.getParcelable<ListItemBean>("listItem")?.id

            val callbackListener = object : HttpRequest.CallbackListener {
                override fun onSuccess(responseJson: JSONObject) {
                    findNavController().popBackStack()
                }

                override fun onFailure(errorMessage: String) {
                    Log.i("HttpRequest", "onFailure: $errorMessage")
                }

            }

            AlertDialog.Builder(context)
                .setTitle("确认删除")
                .setMessage("确认要删除这条历史记录吗？")
                .setPositiveButton("确认") { dialog, which ->
                    val httpRequest = HttpRequest()
                    httpRequest.post(
                        Contants.SERVER_ADDRESS + "/api/record/delete?rid=${rid.toString()}", RequestBody.create(null, byteArrayOf()),
                        "satoken", AccountViewModel.token!!, callbackListener
                    )
                    Log.i("HttpRequest", "/api/record/delete?rid=${rid.toString()}")
                }
                .setNegativeButton("取消") { dialog, which ->
                    // Do nothing
                }
                .show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        val bottomNavContainer : FrameLayout ?= activity?.findViewById(R.id.bottom_nav_container)
        bottomNavContainer?.setVisibility(View.VISIBLE)
    }
}