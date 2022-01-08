package com.wk.cashbook.main.recode.info

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.projects.common.configuration.WkProjects
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.resource.WkContextCompat

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/03/18
 * desc         :
 */


open class TradeInfoCategoryAdapter(private var categories: MutableList<TradeCategory> = ArrayList(),
                                    private val mTradeInfoCategoryListener: ITradeInfoCategoryListener? = null)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_ITEM = -2
    }

    interface ITradeInfoCategoryListener {
        fun itemClick(tradeInfoCategoryAdapter: TradeInfoCategoryAdapter, view: View, position: Int)
    }

    protected val footViews = HashMap<Int, View>()
    protected val headerViews = HashMap<Int, View>()

    private var selectPosition: Int = -1
    private var lastSelectPosition: Int = -1
    private var selectedId = -1L

    class RootCategoryVH(rootView: View, val tvCommon: TextView) : RecyclerView.ViewHolder(rootView)
    class CategoryVH(rootView: View) : RecyclerView.ViewHolder(rootView)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeader(position) || isFoot(position)) {
            if (holder is CategoryVH) {
                val tvCommon = holder.itemView.findViewById<TextView>(R.id.tvCommon)
                tvCommon?.text = "+"
                tvCommon?.gravity = Gravity.CENTER
                tvCommon.setOnClickListener {
                    mTradeInfoCategoryListener?.itemClick(this, tvCommon, position)
                }
            }
        } else {
            val category = categories[position]
            if (holder is RootCategoryVH) {
                holder.tvCommon.text = category.categoryName
                if (selectPosition == position) {
                    holder.tvCommon.setBackgroundResource(R.color.common_black_2B2A2A)
                    holder.tvCommon.setTextColor(
                            WkContextCompat.getColor( R.color.common_white_C9C9C9)
                    )
                } else {
                    holder.tvCommon.setBackgroundResource(R.color.common_white_C9C9C9)
                    holder.tvCommon.setTextColor(
                            WkContextCompat.getColor( R.color.common_black_2B2A2A)
                    )
                }
                holder.tvCommon.setOnClickListener {
                    mTradeInfoCategoryListener?.itemClick(this, holder.tvCommon, position)
                }
            }
        }
    }


    fun getSelectPosition() = selectPosition

    /**
     * 选择，需要还原上一个，
     * */
    fun selectPosition(position: Int): TradeCategory {
        val target = categories[position]
        if (position == selectPosition) {
            return target
        }
        lastSelectPosition = selectPosition
        selectPosition = position
        WkLog.d("itemCount: $itemCount")
        notifyItemChanged(position)
        if (lastSelectPosition in 0 until itemCount) {
            notifyItemChanged(lastSelectPosition)
        }
        return target
    }


    /**设置类别*/
    fun setSelectTradeCategory(categoryId: Long) {
        selectedId = categoryId
        WkLog.d("categoryId: $categoryId", "wk1995")
        for (i in 0 until categories.size) {
            if (categoryId == categories[i].baseObjId) {
                selectPosition(i)
                return
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (isFoot(position) || isHeader(position)) {
            return position
        }
        return VIEW_TYPE_ITEM
    }

    protected fun isHeader(position: Int) = position < headerViews.size

    protected fun isFoot(position: Int) = position >= itemCount - footViews.size

    fun isItem(position: Int) = !isHeader(position) && !isFoot(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val rootView = LayoutInflater.from(parent.context).inflate(R.layout.common_only_text, parent, false)
            val tvCommon = rootView.findViewById<TextView>(R.id.tvCommon)
            tvCommon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            val lp = tvCommon.layoutParams
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            tvCommon.layoutParams = lp
            tvCommon.gravity = Gravity.CENTER
            return RootCategoryVH(rootView, tvCommon)
        }
        if (viewType < headerViews.size) {
            return CategoryVH(headerViews[viewType] ?: throw Exception("viewType: $viewType"))
        }
        val footType = viewType - categories.size - headerViews.size
        return CategoryVH(footViews[footType] ?: throw Exception("viewType: $footType"))
    }

    override fun getItemCount() = categories.size + footViews.size + headerViews.size

    /**替换数据*/
    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(categories: List<TradeCategory>) {
        WkLog.i("replaceData")
        this.categories.clear()
        this.categories.addAll(categories)
        setSelectTradeCategory(selectedId)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        this.categories.clear()
        resetData()
        notifyDataSetChanged()
    }

    private fun resetData() {
        selectPosition = NumberConstants.number_int_one_Negative
        lastSelectPosition = NumberConstants.number_int_one_Negative
        selectedId = NumberConstants.number_long_one_Negative
    }

    /**添加数据*/
    @SuppressLint("NotifyDataSetChanged")
    fun addCategory(category: TradeCategory) {
        this.categories.add(category)
//        notifyItemChanged(itemCount-footViews.size-headerViews.size)
        notifyDataSetChanged()
    }

    fun addFootView(footView: View) {
        footViews[footViews.size] = footView
    }

    fun addHeaderView(headerView: View) {
        headerViews[footViews.size] = headerView
    }

    fun getItem(position: Int): TradeCategory? {
        if (isFoot(position) || isHeader(position)) {
            return null
        }
        return categories[position - headerViews.size]
    }
}