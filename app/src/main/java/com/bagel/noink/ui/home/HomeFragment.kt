package com.bagel.noink.ui.home

import RecordCardAdapter
import ScaleInTransformer
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bagel.noink.R
import com.bagel.noink.bean.ListItemBean
import com.bagel.noink.databinding.FragmentHomeCatBinding
import com.bagel.noink.ui.account.AccountViewModel
import com.bagel.noink.utils.AliyunOSSManager
import com.bagel.noink.utils.Contants
import com.bagel.noink.utils.HttpRequest
import me.relex.circleindicator.CircleIndicator3
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeCatBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var navController: NavController
    private lateinit var aliyunOSSManager: AliyunOSSManager
    private var recordCardAdapter: RecordCardAdapter? = null
    var selectedCardPosition: Int = -1
    private var cardCache: List<ListItemBean>? = null
    companion object {
        private const val PICK_IMAGES_REQUEST_CODE = 101 // 更改请求码，以便处理多个图片选择
    }

    // 用于存储选择的多个图片的 Uri 列表
    private val selectedImageUris = mutableListOf<Uri>()

    @SuppressLint("DiscouragedApi", "SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        aliyunOSSManager = AliyunOSSManager(requireContext())

        _binding = FragmentHomeCatBinding.inflate(inflater, container, false)
        val root: View = binding.root
        navController = findNavController()

        val yearMonth: TextView = binding.yearMonth
        val textView: TextView = binding.textHome
        //val bar: View = binding.bottomBar
        val imageView: ImageView = binding.imageView
        val button: Button = binding.uploadButton

        // 计算今天的日期
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        yearMonth.text = "${year}年${month}月${day}日"

        // Set text from ViewModel
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        button.setOnClickListener {
            navController.navigate(R.id.nav_mood)
        }

        val viewPager: ViewPager2 = binding.viewPager
        val emptyList: List<ListItemBean> = listOf()
        recordCardAdapter = RecordCardAdapter(emptyList, navController) { postion ->
            selectedCardPosition = postion
        }
        viewPager.adapter = recordCardAdapter

        viewPager.offscreenPageLimit = 3

        val recyclerView= viewPager.getChildAt(0) as RecyclerView
        val padding = 20
        recyclerView.setPadding(padding, 0, padding, 0)
        recyclerView.clipToPadding = false

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(ScaleInTransformer())
        compositePageTransformer.addTransformer(MarginPageTransformer(10))
        viewPager.setPageTransformer(compositePageTransformer)

        (if (cardCache == null || AccountViewModel.needToUpdateHistory) {
            getCardList()
        } else {
            cardCache
        })?.let {
            recordCardAdapter!!.updateData(
                it
            )
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectedCardPosition = position
                if (selectedCardPosition != 0) {
                    binding.moreCard.visibility = View.GONE
                } else {
                    if (homeViewModel.count_of_card.value!! > 1) {
                        binding.moreCard.visibility = View.GONE
                    }
                }
            }
        })

        val indicator: CircleIndicator3 = binding.indicator
        indicator.setViewPager(viewPager)
        viewPager.adapter?.registerAdapterDataObserver(indicator.getAdapterDataObserver());

        // Restore selected card position if available
        if (selectedCardPosition != -1) {
            // Scroll to selected card position
            viewPager.post {
                viewPager.setCurrentItem(selectedCardPosition, false)
            }
        }
        homeViewModel.updateCount(recordCardAdapter!!.itemCount)

        homeViewModel.count_of_card.observe(viewLifecycleOwner) {
            if (homeViewModel.count_of_card.value!! > 1 && selectedCardPosition == 1) {
                binding.moreCard.visibility = View.GONE
            }
        }
        return root
    }

    fun generateRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') // 允许的字符集合
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getCardList(): MutableList<ListItemBean> {

        val uriDemo =
            Uri.parse("https://quasdo.oss-cn-hangzhou.aliyuncs.com/img/YFLTFY_JPDKCQFOZ2LBZYPQ.png")

        val data1 = ListItemBean(
            1,
            "考研加油",
            "离考研只剩两个月了，最后冲刺一把",
            uriDemo,
            listOf("开心", "努力"),
            "学习",
            listOf(uriDemo),
            Date(1639468800000L)
        )

        val cardList: MutableList<ListItemBean> = ArrayList()
        // 第一张卡片无需使用真实数据
        cardList.add(data1)

        val callbackListener = object : HttpRequest.CallbackListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onSuccess(responseJson: JSONObject) {
                val items = responseJson.getJSONArray("data")

                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val uriStrList: List<String> = item.getString("imageUrl").split(",")
                    val uriList: ArrayList<Uri> = ArrayList()

                    for (uriStr in uriStrList) {
                        val uri = Uri.parse(uriStr)
                        uriList.add(uri)
                    }

                    val dateString = item.getString("createdAt")
                    val year = dateString.substring(0, 4)
                    val month = dateString.substring(5, 7)
                    val day = dateString.substring(8, 10)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val date = dateFormat.parse(dateString)

                    val moodList: List<String> = item.getString("labels").split(",")

                    cardList.add(
                        ListItemBean(
                            item.getInt("rid"),
                            item.getString("title"),
                            item.getString("generatedText"),
                            uriList[0],
                            moodList,
                            item.getString("type"),
                            uriList,
                            date
                        )
                    )
                }
                cardCache = cardList
                AccountViewModel.needToUpdateHistory = false

                // 在主线程上调用 notifyDataSetChanged()
                activity?.runOnUiThread {
                    recordCardAdapter?.notifyDataSetChanged()
                }
            }

            override fun onFailure(errorMessage: String) {
                print(errorMessage)
            }
        }

        val httpRequest = HttpRequest()
        httpRequest.get(
            Contants.SERVER_ADDRESS + "/api/record/threeRecord",
            "satoken",
            AccountViewModel.token!!,
            callbackListener
        )
        return cardList
    }
}

