package com.wk.cashbook.trade.info

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.projects.common.log.WkLog

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
        private const val VIEW_TYPE_FOOT = -1
        private const val VIEW_TYPE_HEADER = -3
        const val VIEW_TYPE_ITEM = -2

    }

    interface ITradeInfoCategoryListener {
        fun itemClick(tradeInfoCategoryAdapter: TradeInfoCategoryAdapter, view: View, position: Int)
    }

    protected val footViews = HashMap<Int, View>()
    protected val headerViews = HashMap<Int, View>()

    private var selectPosition: Int = -1
    private var lastSelectPosition: Int = -1
    private var selectedId=-1L

    class RootCategoryVH(rootView: View, val tvCommon: TextView) : RecyclerView.ViewHolder(rootView)
    class CategoryVH(rootView: View) : RecyclerView.ViewHolder(rootView)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        WkLog.d("onBindViewHolder","wk1995")
        if (isHeader(position) || isFoot(position)) {
            if (holder is CategoryVH) {
                val tvCommon = holder.itemView.findViewById<TextView>(R.id.tvCommon)
                tvCommon.setBackgroundResource(R.color.colorAccent)
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
                if (selectPosition==position) {
                    holder.tvCommon.setBackgroundResource(R.color.design_default_color_background)
                } else {
                    holder.tvCommon.setBackgroundResource(R.color.colorAccent)
                }
                holder.tvCommon.setOnClickListener {
                    mTradeInfoCategoryListener?.itemClick(this, holder.tvCommon, position)
                }
            }
        }
    }


    /**
     * 选择，需要还原上一个，
     * */
    fun selectPosition(position: Int): TradeCategory {
        val target = categories[position]
        if (position==selectPosition) {
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
        selectedId=categoryId
        WkLog.d("categoryId: $categoryId","wk1995")
        for(i in 0 until categories.size){
            if(categoryId==categories[i].baseObjId){
                selectPosition(i)
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
        val footType = viewType - categories.size - headerViews.size;
        return CategoryVH(footViews[footType] ?: throw Exception("viewType: $footType"))
    }

    override fun getItemCount() = categories.size + footViews.size + headerViews.size

    /**替换数据*/
    fun replaceData(categories: List<TradeCategory>) {
        this.categories.clear()
        this.categories.addAll(categories)
        setSelectTradeCategory(selectedId)
        notifyDataSetChanged()
    }


    /**添加数据*/
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