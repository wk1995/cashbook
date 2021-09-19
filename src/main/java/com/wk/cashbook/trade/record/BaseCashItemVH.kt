package com.wk.cashbook.trade.record

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/02/27
 * desc         :
 */

sealed class BaseCashItemVH(rootView: View) : RecyclerView.ViewHolder(rootView)

class CashTotalItemVH(rootView: View, val tvBillTotalDay: TextView,
                      val tvTradeTotalWeek: TextView,
                      val tvAllIncomeValue: TextView,
                      val tvAllPayValue: TextView)
    : BaseCashItemVH(rootView)

class CashListItemVH(rootView: View, val ivTradeType: ImageView,
                     val tvTradeNote: TextView,
                     val tvTradeAmount: TextView) : BaseCashItemVH(rootView)


