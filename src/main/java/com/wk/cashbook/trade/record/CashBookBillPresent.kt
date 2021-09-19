package com.wk.cashbook.trade.record

import com.wk.cashbook.trade.record.CashBookBillListActivity
import com.wk.projects.common.log.WkLog
import java.util.*

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/02/20
 * desc         :
 */


class CashBookBillPresent(val mCashBookBillListActivity: CashBookBillListActivity?) {

    /**
     * 获取当前日期的年，月份
     * */
    fun getYearAndMonth():Pair<Int,Int>{
        val currentTime=System.currentTimeMillis()
        val can=Calendar.getInstance()
        can.timeInMillis=currentTime
        val year=can.get(Calendar.YEAR)
        val month=can.get(Calendar.MONTH)+1
        WkLog.d("year: $year  month: $month")
        return Pair(year,month)
    }
}