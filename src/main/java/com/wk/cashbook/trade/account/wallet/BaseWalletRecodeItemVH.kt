package com.wk.cashbook.trade.account.wallet

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

sealed class BaseWalletRecodeItemVH(rootView: View) : RecyclerView.ViewHolder(rootView)

class WalletRecodeTotalItemVH(rootView: View, val tvBillTotalDay: TextView,
                      val tvAllIncomeValue: TextView,
                      val tvAllPayValue: TextView)
    : BaseWalletRecodeItemVH(rootView)

class WalletRecodeListItemVH(rootView: View, val ivTradeType: ImageView,
                     val tvTradeNote: TextView,
                     val tvTradeAmount: TextView,
                     val tvWalletTradeRecodeBalance:TextView) : BaseWalletRecodeItemVH(rootView)


