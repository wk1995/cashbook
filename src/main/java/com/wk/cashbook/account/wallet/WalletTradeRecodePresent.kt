package com.wk.cashbook.account.wallet

import android.content.Intent
import com.wk.cashbook.CashBookConstants
import com.wk.cashbook.CashBookSql
import com.wk.cashbook.initMoneyToString
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.main.recode.ITradeRecodeShowBean
import com.wk.projects.common.BaseProjectsPresent
import com.wk.projects.common.helper.NumberUtil
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.time.date.DateTime
import org.litepal.LitePal
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2022/1/2
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class WalletTradeRecodePresent(private val mWalletTradeRecodeActivity: WalletTradeRecodeActivity) :
    BaseProjectsPresent() {
    private var mSubscriptions: CompositeSubscription? = null

    fun initData(intent: Intent) {
        val id = intent.getLongExtra(AccountWallet.ACCOUNT_WALLET_ID, AccountWallet.INVALID_ID)
        queryWalletRecodeList(id)
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<AccountWallet> { t ->
            t.onNext(LitePal.find(AccountWallet::class.java, id))
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { wallet ->
                mWalletTradeRecodeActivity.setWalletName(wallet.accountName)
                mWalletTradeRecodeActivity.setWalletBalance(NumberUtil.initMoneyToString(wallet.amount))
            })
    }

    private fun queryWalletRecodeList(id: Long) {
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<List<ITradeRecodeShowBean>> { t ->
            val cursor = LitePal.findBySQL(
                CashBookSql.SQL_QUERY_WALLET_TRADE_RECODE,
                id.toString(), id.toString(), id.toString(), id.toString()
            )
            val showBeans = ArrayList<ITradeRecodeShowBean>()
            var tradeRecodeShowTitleBean = WalletTradeRecodeShowTitleBean()
            var sum: Double = Double.NaN
            var lastSum: Double
            while (cursor.moveToNext()) {
                val tradeRecodeId = cursor.getLong(0)
                val tradeTime = cursor.getLong(1)
                val dayEndTime = DateTime.getDayEnd(tradeTime)
                if (tradeRecodeShowTitleBean.mDayEndTime != dayEndTime) {
                    tradeRecodeShowTitleBean =
                        WalletTradeRecodeShowTitleBean(mDayEndTime = dayEndTime)
                    showBeans.add(tradeRecodeShowTitleBean)
                }
                val amount = NumberUtil.initMoneyToString(cursor.getDouble(2)).toDouble()
                val tradeNote = cursor.getString(3)
                val isPay = cursor.getString(4)
                if (sum.isNaN()) {
                    sum = NumberUtil.initMoneyToString(cursor.getDouble(5)).toDouble()
                }
                lastSum = sum
                val tradeType = if (isPay == "yes") {
                    tradeRecodeShowTitleBean.addPayAmount(amount)
                    sum += amount
                    CashBookConstants.TYPE_ROOT_CATEGORY_PAY
                } else {
                    sum -= amount
                    tradeRecodeShowTitleBean.addInComeAmount(amount)
                    CashBookConstants.TYPE_ROOT_CATEGORY_INCOME
                }
                val tradeRecodeShowBean =
                    WalletTradeRecodeShowBean(
                        tradeRecodeId,
                        amount,
                        tradeNote,
                        tradeTime,
                        tradeType,
                        lastSum
                    )
                showBeans.add(tradeRecodeShowBean)
            }
            t.onNext(showBeans)
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ tradeRecodeShowBeans ->
                mWalletTradeRecodeActivity.updateData(
                    tradeRecodeShowBeans
                )
            }) { t ->
                t?.printStackTrace()
                WkLog.e("queryWalletRecodeList error ${t?.message}")
            })


    }

    override fun onCreate() {
        super.onCreate()
        mSubscriptions = CompositeSubscription()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions?.clear()
    }


}