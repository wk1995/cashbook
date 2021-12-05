package com.wk.cashbook.trade.record

import android.database.Cursor
import android.os.Bundle
import com.wk.cashbook.CashBookSql
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.time.date.DateTime
import com.wk.projects.common.ui.WkToast
import org.litepal.LitePal
import org.litepal.extension.runInTransaction
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
    private var mSubscriptions: CompositeSubscription? = null

    fun initSubscriptions() {
        mSubscriptions = CompositeSubscription()
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
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<Boolean> {
            it.onNext(deleteByItemSql(id))
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    WkLog.i("deleteItem result : $it")
                    if (it) {
                        mCashBookBillListActivity?.removeData(position)
                    }
                }
        )
    }

    fun initData(time: Long) {
        val startTime = DateTime.getMonthStart(time)
        val endTime = DateTime.getMonthEnd(time)
        WkLog.i("startTime: $startTime  endTime  $endTime")
        initTotalData(startTime, endTime)
        initCashBookList(startTime, endTime)
    }

    fun initTotalData(time: Long){
        val startTime = DateTime.getMonthStart(time)
        val endTime = DateTime.getMonthEnd(time)
        initTotalData(startTime,endTime)
    }


    private fun initTotalData(startTime: Long, endTime: Long) {
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<Pair<Double,Double>> { t ->
            val cursor = LitePal.findBySQL(CashBookSql.SQL_QUERY_SUM_AMOUNT_CATEGORY,
                    startTime.toString(), endTime.toString())
            var comeIn = 0.0
            var pay = 0.0
            if (cursor.count != 0) {
                while (cursor.moveToNext()) {
                    val amount = cursor.getDouble(0)
                    val categoryName = cursor.getString(1)
                    WkLog.i("amount: $amount categoryName: $categoryName")
                    if (TradeCategory.isComeIn(categoryName)) {
                        comeIn = amount
                    }

                    if (TradeCategory.isPay(categoryName)) {
                        pay = amount
                    }
                }
            }
            t.onNext(Pair(comeIn,pay))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mCashBookBillListActivity?.initTotalData(it)
                })


    }

    fun initCashBookList(time: Long){
        val startTime = DateTime.getMonthStart(time)
        val endTime = DateTime.getMonthEnd(time)
        initCashBookList(startTime, endTime)
    }

    private fun initCashBookList(startTime: Long, endTime: Long) {

        WkLog.i("initData startTime: ${DateTime.getDateString(startTime)}, endTime: ${DateTime.getDateString(endTime)}")
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<List<TradeRecode>> { t ->
            t?.onNext(TradeRecode.getTradeRecodes("${TradeRecode.TRADE_TIME}>? and ${TradeRecode.TRADE_TIME}<?", startTime.toString(), endTime.toString()))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    WkLog.d("交易记录： $it")
                    mCashBookBillListActivity?.replaceRecodeList(it)
                })
    }

    /**
     * 先获取到交易示例
     *
     * 交易账号
     *
     * 如果交易账号为空的话，直接删除
     *
     * 如果交易账号不为空，
     * 接着获取其根类别判断交易类型
     *
     *
     * */
    private fun deleteByItemSql(tradeRecodeId: Long) =
            LitePal.runInTransaction {
                val tradeRecode = LitePal.find(TradeRecode::class.java, tradeRecodeId)
                if (tradeRecode == null) {
                    WkToast.showToast("tradeRecode ==null")
                    return@runInTransaction false
                }

                val account = LitePal.find(TradeAccount::class.java, tradeRecode.accountId)
                if (account != null) {
                    val rootCategory = TradeRecode.getRootTradeCategory(tradeRecode.categoryId)
                            ?: return@runInTransaction false
                    when {
                        rootCategory.isComeIn() -> {
                            WkLog.i("删除的交易属于收入")
                            account.amount -= tradeRecode.amount
                            if (!account.save()) {
                                WkToast.showToast("收入交易保存失败")
                                return@runInTransaction false
                            }
                        }
                        rootCategory.isPay() -> {
                            WkLog.i("删除的交易属于支出")
                            account.amount += tradeRecode.amount
                            if (!account.save()) {
                                WkToast.showToast("支出交易保存失败")
                                return@runInTransaction false
                            }
                        }
                        rootCategory.isInternalTransfer() -> {
                            WkLog.i("删除的交易属于内部转账")
                            val receiveAccount = LitePal.find(TradeAccount::class.java, tradeRecode.receiveAccountId)
                            if (receiveAccount == null) {
                                WkToast.showToast("属于内部交易，但没有交易对象 删除失败")
                                return@runInTransaction false
                            } else {
                                account.amount += tradeRecode.amount
                                receiveAccount.amount -= tradeRecode.amount
                                if (!account.save() || !receiveAccount.save()) {
                                    WkToast.showToast("内部交易保存失败")
                                    return@runInTransaction false
                                }
                            }
                        }
                    }
                }
                return@runInTransaction LitePal.delete(TradeRecode::class.java, tradeRecodeId) > 0
            }


    fun onDestroy() {
        mSubscriptions?.unsubscribe()
        mSubscriptions?.clear()
    }
}