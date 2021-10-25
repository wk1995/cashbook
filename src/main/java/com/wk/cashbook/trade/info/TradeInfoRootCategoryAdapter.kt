package com.wk.cashbook.trade.info

import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
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


class TradeInfoRootCategoryAdapter(private var categories: MutableList<TradeCategory> = ArrayList(),
                                  var mTradeInfoCategoryListener: ITradeInfoCategoryListener? = null)
    : TradeInfoCategoryAdapter(categories,mTradeInfoCategoryListener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val rootView = LayoutInflater.from(parent.context).inflate(R.layout.common_only_text, parent, false)
            val tvCommon = rootView.findViewById<TextView>(R.id.tvCommon)
            tvCommon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            tvCommon.setPadding(0,0,0,0)
            tvCommon.gravity=Gravity.CENTER
            val lp = tvCommon.layoutParams
            lp.width = parent.context.resources.getDimensionPixelOffset(R.dimen.d60dp)
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT
            tvCommon.layoutParams = lp
            tvCommon.gravity=Gravity.CENTER
            return RootCategoryVH(rootView, tvCommon)
        }
        if (viewType < headerViews.size) {
            return CategoryVH(headerViews[viewType] ?: throw Exception("viewType: $viewType"))
        }
        val footType=viewType-categories.size-headerViews.size;
        return CategoryVH(footViews[footType] ?: throw Exception("viewType: $footType"))
    }

}