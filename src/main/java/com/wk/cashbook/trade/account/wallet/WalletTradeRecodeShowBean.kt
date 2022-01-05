package com.wk.cashbook.trade.account.wallet

import com.wk.cashbook.trade.record.bean.ITradeRecodeShowBean

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/5
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 *
 * @param mTradeRecodeId 交易记录id 用于点击跳转详情页
 * @param mAmount 交易金额
 * @param mShowText 显示文案
 * @param mTradeTime 交易时间，用于排序
 * @param mTradeType 交易类别，用于amount文案颜色
 * */
data class WalletTradeRecodeShowBean(val mTradeRecodeId: Long,
                                     var mAmount: Double,
                                     var mShowText: String,
                                     var mTradeTime: Long,
                                     var mTradeType: Int,
                                     var mBalance:Double) : ITradeRecodeShowBean {
   companion object{
       const val TRADE_RECODE_ID="trade_recode_id"
       const val AMOUNT="amount"
       const val SHOW_NAME="show_Name"
       const val TRADE_TIME="trade_Time"
       const val TRADE_TYPE="trade_Type"
   }

    override fun getTradeTime() =mTradeTime

    override fun getTradeRecodeId()=mTradeRecodeId

    override fun getShowText()=mShowText
}