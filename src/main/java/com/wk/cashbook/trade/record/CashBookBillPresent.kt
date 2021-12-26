package com.wk.cashbook.trade.record

import android.content.Intent
import android.os.Bundle
import com.wk.cashbook.CashBookConstants
import com.wk.cashbook.CashBookSql
import com.wk.cashbook.CashBookSql.SQL_QUERY_ASSETS_INFO
import com.wk.cashbook.CashBookSql.SQL_QUERY_TRADE_RECODE_GROUP_DATE
import com.wk.cashbook.trade.account.CurrencyTypeManager
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.trade.data.CurrencyType
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.cashbook.trade.record.bean.AssetsInfoShowBean
import com.wk.cashbook.trade.record.bean.ITradeRecodeShowBean
import com.wk.cashbook.trade.record.bean.TradeRecodeShowBean
import com.wk.cashbook.trade.record.bean.TradeRecodeShowTitleBean
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
import kotlin.collections.ArrayList

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
    fun getYearAndMonth(currentTime: Long): Pair<Int, Int> {
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

    fun initTotalData(time: Long) {
        val startTime = DateTime.getMonthStart(time)
        val endTime = DateTime.getMonthEnd(time)
        initTotalData(startTime, endTime)
    }


    private fun initTotalData(startTime: Long, endTime: Long) {
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<Pair<Double, Double>> { t ->
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
            t.onNext(Pair(comeIn, pay))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mCashBookBillListActivity?.initTotalData(it)
                })


    }


    fun initAssetsInfo() {
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<List<AssetsInfoShowBean>> { t ->
            val assetsInfoBeans = ArrayList<AssetsInfoShowBean>()
            val cursor = LitePal.findBySQL(SQL_QUERY_ASSETS_INFO)
            if (cursor.count != 0) {
                while (cursor.moveToNext()) {
                    val unit = (CurrencyTypeManager.getCurrencyType(cursor.getString(0))?: CurrencyType.UnKnow).chinese
                    val assets = cursor.getDouble(1).toString()
                    val liabilities = cursor.getDouble(2).toString()
                    val netAssets = cursor.getDouble(3).toString()
                    val cash = cursor.getDouble(4).toString()
                    assetsInfoBeans.add(AssetsInfoShowBean(unit, cash, assets, liabilities, netAssets))
                }
            }
            t.onNext(assetsInfoBeans)
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { assetsInfoShowBeans ->
                    mCashBookBillListActivity?.replaceAssetsInfoData(assetsInfoShowBeans)
                }
        )
    }


    /**交易记录列表*/
    fun initCashBookList(time: Long) {
        val startTime = DateTime.getMonthStart(time)
        val endTime = DateTime.getMonthEnd(time)
        initCashBookList(startTime, endTime)
    }

    private fun initCashBookList(startTime: Long, endTime: Long) {
        WkLog.i("initData startTime: ${DateTime.getDateString(startTime)}, " +
                "endTime: ${DateTime.getDateString(endTime)}")
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<List<ITradeRecodeShowBean>> { t ->
            val cursor = LitePal.findBySQL(SQL_QUERY_TRADE_RECODE_GROUP_DATE, startTime.toString(), endTime.toString())
            val showBeans = ArrayList<ITradeRecodeShowBean>()
            var tradeRecodeShowTitleBean = TradeRecodeShowTitleBean()
            if (cursor.count != 0) {
                while (cursor.moveToNext()) {
                    val tradeRecodeId = cursor.getLong(0)
                    val tradeTime = cursor.getLong(1)
                    val dayEndTime = DateTime.getDayEnd(tradeTime)
                    if (tradeRecodeShowTitleBean.mDayEndTime != dayEndTime) {
                        tradeRecodeShowTitleBean = TradeRecodeShowTitleBean(mDayEndTime = dayEndTime)
                        showBeans.add(tradeRecodeShowTitleBean)
                    }
                    val amount = cursor.getDouble(2)
                    val tradeNote = cursor.getString(3)
                    val showText = if (tradeNote.isEmpty()) {
                        val categoryName = cursor.getString(5)
                        categoryName
                    } else {
                        tradeNote
                    }
                    val rootCategoryName = cursor.getString(4)
                    val tradeType = when {
                        TradeCategory.isComeIn(rootCategoryName) -> {
                            tradeRecodeShowTitleBean.addInComeAmount(amount)
                            CashBookConstants.TYPE_ROOT_CATEGORY_INCOME
                        }
                        TradeCategory.isInternalTransfer(rootCategoryName) -> {
                            CashBookConstants.TYPE_ROOT_CATEGORY_INTERNAL_TRANSFER
                        }
                        else -> {
                            tradeRecodeShowTitleBean.addPayAmount(amount)
                            CashBookConstants.TYPE_ROOT_CATEGORY_PAY
                        }
                    }
                    val tradeRecodeShowBean =
                            TradeRecodeShowBean(tradeRecodeId, amount, showText, tradeTime, tradeType)
                    showBeans.add(tradeRecodeShowBean)
                }
            }
            t.onNext(showBeans)
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

                val account = LitePal.find(AccountWallet::class.java, tradeRecode.accountId)
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
                            val receiveAccount = LitePal.find(AccountWallet::class.java, tradeRecode.receiveAccountId)
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

    fun updateData(data: Intent?) {
        val tradeRecodeId = data?.getLongExtra(TradeRecodeShowBean.TRADE_RECODE_ID, TradeRecode.INVALID_ID)
        if (tradeRecodeId == null) {
            WkLog.e("tradeRecodeId is null")
            return
        }

        if (tradeRecodeId == TradeRecode.INVALID_ID) {
            WkLog.e("tradeRecodeId is INVALID_ID")
            return
        }
        if (tradeRecodeId == TradeRecode.INIT_ID) {
            WkLog.e("tradeRecodeId is INIT_ID")
            return
        }
//        val position=data.getIntExtra(WkStringConstants.STR_POSITION_LOW,NumberConstants.number_int_one_Negative)
        mCashBookBillListActivity?.initData()
        /*if(position==-1) {
            mCashBookBillListActivity?.initData()
            return
        }
        val tradeTime=data.getLongExtra(TradeRecodeShowBean.TRADE_TIME,NumberConstants.number_long_zero)
        val tradeRecodeShowBean =mCashBookBillListActivity?.getData(position)?:return
        if(tradeRecodeShowBean.getTradeTime()!=tradeTime){
            mCashBookBillListActivity.initData()
        }else{//表示没有修改时间
            // 但如果修改了类型，则标题中的数据也需要变化，这点cover不到，所以还是直接更新所有数据
            val amount=data.getDoubleExtra(TradeRecodeShowBean.AMOUNT,NumberConstants.number_double_zero)
            val showName=data.getStringExtra(TradeRecodeShowBean.SHOW_NAME)?:WkStringConstants.STR_EMPTY
            val tradeType=data.getIntExtra(TradeRecodeShowBean.TRADE_TYPE,CashBookConstants.TYPE_ROOT_CATEGORY_UNKNOWN)
            if(tradeType==CashBookConstants.TYPE_ROOT_CATEGORY_UNKNOWN){
                return
            }
            val tradeShowBean=TradeRecodeShowBean(tradeRecodeId,amount,showName,tradeTime,tradeType)
            mCashBookBillListActivity.replaceData(tradeShowBean,
                    position,tradeRecodeShowBean.getTradeTime()!=tradeTime)
        }*/

    }

    fun onDestroy() {
        mSubscriptions?.unsubscribe()
        mSubscriptions?.clear()
    }
}