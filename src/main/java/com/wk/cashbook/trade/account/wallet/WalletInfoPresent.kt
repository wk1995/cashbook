package com.wk.cashbook.trade.account.wallet

import android.content.Intent
import com.wk.cashbook.CashBookConstants
import com.wk.cashbook.CashBookSql
import com.wk.cashbook.initMoneyToString
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.record.bean.ITradeRecodeShowBean
import com.wk.cashbook.trade.record.bean.TradeRecodeShowBean
import com.wk.cashbook.trade.record.bean.TradeRecodeShowTitleBean
import com.wk.projects.common.BaseProjectsPresent
import com.wk.projects.common.helper.NumberUtil
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.time.date.DateTime
import org.litepal.LitePal
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
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
class WalletInfoPresent(private val mWalletInfoActivity: WalletInfoActivity) : BaseProjectsPresent() {
    private var mSubscriptions: CompositeSubscription? = null

    fun initData(intent: Intent) {
        val id = intent.getLongExtra(AccountWallet.ACCOUNT_WALLET_ID, AccountWallet.INVALID_ID)
        queryWalletRecodeList(id)
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<AccountWallet> { t ->
            t.onNext(LitePal.find(AccountWallet::class.java, id))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { wallet ->
                    mWalletInfoActivity.setWalletName(wallet.accountName)
                })
    }

    private fun queryWalletRecodeList(id: Long) {
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<List<ITradeRecodeShowBean>> { t ->
            val cursor = LitePal.findBySQL(CashBookSql.SQL_QUERY_WALLET_TRADE_RECODE, id.toString(),id.toString(),id.toString())
            val showBeans = ArrayList<ITradeRecodeShowBean>()
            var tradeRecodeShowTitleBean = TradeRecodeShowTitleBean()
            while (cursor.moveToNext()) {
                val tradeRecodeId = cursor.getLong(0)
                val tradeTime = cursor.getLong(1)
                val dayEndTime = DateTime.getDayEnd(tradeTime)
                if (tradeRecodeShowTitleBean.mDayEndTime != dayEndTime) {
                    tradeRecodeShowTitleBean = TradeRecodeShowTitleBean(mDayEndTime = dayEndTime)
                    showBeans.add(tradeRecodeShowTitleBean)
                }
                val amount = NumberUtil.initMoneyToString(cursor.getDouble(2)).toDouble()
                val tradeNote = cursor.getString(3)
                val isPay = cursor.getString(4)
                val tradeType = if(isPay=="yes"){
                    tradeRecodeShowTitleBean.addPayAmount(amount)
                    CashBookConstants.TYPE_ROOT_CATEGORY_PAY
                }else{
                    tradeRecodeShowTitleBean.addInComeAmount(amount)
                    CashBookConstants.TYPE_ROOT_CATEGORY_INCOME
                }
                val tradeRecodeShowBean =
                        TradeRecodeShowBean(tradeRecodeId, amount, tradeNote, tradeTime, tradeType)
                showBeans.add(tradeRecodeShowBean)
            }
            t.onNext(showBeans)
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ tradeRecodeShowBeans -> mWalletInfoActivity.updateData(tradeRecodeShowBeans) }) { t ->
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