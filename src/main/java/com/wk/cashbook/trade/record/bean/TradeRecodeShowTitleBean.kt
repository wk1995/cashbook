package com.wk.cashbook.trade.record.bean

import com.wk.projects.common.constant.NumberConstants

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/5
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
data class TradeRecodeShowTitleBean(var mPayAmount: Double = NumberConstants.number_double_zero,
                                    var mInComeAmount: Double = NumberConstants.number_double_zero,
                                    var mDayEndTime: Long=NumberConstants.number_long_zero) : ITradeRecodeShowBean {

    fun addPayAmount(payAmount: Double) {
        mPayAmount+=payAmount
    }

    fun addInComeAmount(inComeAmount: Double) {
        mInComeAmount+=inComeAmount
    }

    override fun getTradeTime()=mDayEndTime
}