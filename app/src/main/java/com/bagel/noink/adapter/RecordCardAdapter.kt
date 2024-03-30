import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.size
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bagel.noink.R
import com.bagel.noink.bean.ListItemBean
import com.bagel.noink.ui.generate.TextGenViewModel
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class RecordCardAdapter(
    private var cards: List<ListItemBean>,
    private val navController: NavController,
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_CARD_NEW = 1
        private const val VIEW_TYPE_CARD_RECORD = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_CARD_NEW
        } else {
            VIEW_TYPE_CARD_RECORD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CARD_NEW -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_card_new, parent, false)
                CardViewHolder1(view)
            }

            VIEW_TYPE_CARD_RECORD -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
                CardViewHolder2(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val card = cards[position]
        when (holder) {
            is CardViewHolder1 -> {
                // 绑定 CardViewHolder1 的数据和视图
                holder.bind(card)
            }

            is CardViewHolder2 -> {
                // 绑定 CardViewHolder2 的数据和视图
                holder.bind(card, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    inner class CardViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // CardViewHolder1 的视图组件和绑定方法
        private val yearMonth: TextView = itemView.findViewById(R.id.yearMonth)
        private val text_home: TextView = itemView.findViewById(R.id.text_home)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val uploadButton: Button = itemView.findViewById(R.id.uploadButton)
        fun bind(card: ListItemBean) {
            // 绑定 CardViewHolder1 的数据和视图

            // 计算今天的日期
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            yearMonth.text = "${year}年${month}月${day}日"

            text_home.text = "记录下今天干的事情"
            uploadButton.setOnClickListener {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)   // 设置进入动画
                    .setExitAnim(R.anim.slide_out_left)   // 设置退出动画
                    .setPopEnterAnim(R.anim.slide_in_left)   // 设置返回动画
                    .setPopExitAnim(R.anim.slide_out_right)   // 设置返回退出动画
                    .build()
                itemView.findNavController().navigate(R.id.nav_mood, null, navOptions)
            }
        }
    }

    inner class CardViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // CardViewHolder2 的视图组件和绑定方法
        private val dataView: TextView = itemView.findViewById(R.id.yearMonth)
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val contentView: TextView = itemView.findViewById(R.id.content)
        private val tag_style: TextView = itemView.findViewById(R.id.tag_style)
        private val tag_mood: TextView = itemView.findViewById(R.id.tag_mood)

        fun bind(card: ListItemBean, position: Int) {
            // 绑定 CardViewHolder2 的数据和视图
            val format = SimpleDateFormat("yyyy年M月d日", Locale.getDefault())
            dataView.text = format.format(card.createDate)
            titleTextView.text = card.title
            tag_style.text = card.eventTag
            tag_mood.text = card.moodTags[0]
            // 使用 Glide 加载并绑定 Uri 到 imageView
            Glide.with(itemView.context)
                .load(card.coverUri)
                .into(imageView)
            contentView.text = card.text

            itemView.setOnClickListener {
                onItemClick(position)
                val bundle = bundleOf(
                    "listItem" to card
                )

                val navOptions = NavOptions.Builder()
                    .setEnterAnim(R.anim.scale_up)   // 设置进入动画
//                    .setExitAnim(R.anim.slide_out_left)   // 设置退出动画
                    .setPopEnterAnim(R.anim.fade_in)   // 设置返回动画
                    .setPopExitAnim(R.anim.scale_down)   // 设置返回退出动画
                    .build()

                navController.navigate(R.id.action_nav_home_to_nav_card_details, bundle, navOptions)
            }
        }
    }

    fun updateData(newCards: List<ListItemBean>) {
        cards = newCards
    }

}