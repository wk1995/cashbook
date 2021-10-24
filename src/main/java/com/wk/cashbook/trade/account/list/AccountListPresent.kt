package com.wk.cashbook.trade.account.list

import android.content.Intent
import com.wk.cashbook.CashBookActivityRequestCode.REQUEST_CODE_ACCOUNT_LIST_ACTIVITY
import com.wk.cashbook.trade.account.AccountInfoActivity
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import com.wk.projects.common.log.WkLog
import org.litepal.LitePal
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/13
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class AccountListPresent(private val mAccountListActivity: AccountListActivity) {
    private var mSubscriptions: CompositeSubscription? = null

    fun onCreate() {
        mSubscriptions = CompositeSubscription()
    }

    fun onStart() {

    }


    fun onStop() {

    }

    fun onDestroy() {
        mSubscriptions?.clear()
    }

    fun back() {
        mAccountListActivity.finish()
    }

    fun initData() {
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<List<TradeAccount>> {
            it.onNext(LitePal.findAll(TradeAccount::class.java))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    WkLog.i("tradeAccount size: ${it.size}")
                    val result = it.map { tradeAccount ->
                        val money = HashMap<String, Double>()
                        money[tradeAccount.unit] = tradeAccount.amount
                        AccountListShowBean(money,
                                tradeAccount.accountName, tradeAccount.note, tradeAccount.baseObjId)
                    }
                    mAccountListActivity.updateData(result)
                }
        )
    }

    fun goToInfoActivity(id: Long = TradeAccount.INVALID_ID,
                         position: Int = NumberConstants.number_int_one_Negative) {
        val intent = Intent(mAccountListActivity, AccountInfoActivity::class.java)
        intent.putExtra(TradeAccount.ACCOUNT_ID, id)
        intent.putExtra(WkStringConstants.STR_POSITION_LOW, position)
        mAccountListActivity.startActivityForResult(intent, REQUEST_CODE_ACCOUNT_LIST_ACTIVITY)
    }

    fun deleteData(id: Long?, position: Int) {
        if (id == null) {
            return
        }
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<Int> {
            it.onNext(LitePal.delete(TradeAccount::class.java, id))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it > NumberConstants.number_int_zero) {
                        mAccountListActivity.removeData(position)
                    }
                }
        )
    }

}