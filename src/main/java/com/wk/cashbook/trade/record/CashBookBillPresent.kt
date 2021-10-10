package com.wk.cashbook.trade.record

import android.os.Bundle
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import com.wk.projects.common.log.WkLog
import org.litepal.LitePal
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/02/20
 * desc         :
 */


class CashBookBillPresent(private val mCashBookBillListActivity: CashBookBillListActivity?) {
    private var mSubscriptions: CompositeSubscription?=null

    fun initSubscriptions(){
        mSubscriptions=CompositeSubscription()
    }

    /**
     * 获取当前日期的年，月份
     * */
    fun getYearAndMonth(): Pair<Int, Int> {
        val currentTime = System.currentTimeMillis()
        val can = Calendar.getInstance()
        can.timeInMillis = currentTime
        val year = can.get(Calendar.YEAR)
        val month = can.get(Calendar.MONTH) + 1
        WkLog.d("year: $year  month: $month")
        return Pair(year, month)
    }

    fun deleteItem(bundle: Bundle?) {
        val id = bundle?.getLong(TradeRecode.TRADE_RECODE_ID)
                ?: NumberConstants.number_long_zero
        val position = bundle?.getInt(WkStringConstants.STR_POSITION_LOW)
                ?: NumberConstants.number_int_one_Negative
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<Int> {
            it.onNext(LitePal.delete(TradeRecode::class.java, id))
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    WkLog.i("deleteItem result : $it")
                    if (it > 0) {
                        mCashBookBillListActivity?.removeData(position)
                    }
                }
        )
    }

    fun onStop(){
        mSubscriptions?.unsubscribe()
        mSubscriptions?.clear()
    }
}