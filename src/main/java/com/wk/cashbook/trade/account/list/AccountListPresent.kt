package com.wk.cashbook.trade.account.list

import android.content.Intent
import com.wk.cashbook.CashBookActivityRequestCode.REQUEST_CODE_ACCOUNT_LIST_ACTIVITY
import com.wk.cashbook.trade.account.UpdateAccountOrWalletActivity
import com.wk.cashbook.trade.account.info.AccountInfoActivity
import com.wk.cashbook.trade.data.CurrencyType
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
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<List<AccountListShowBean>> {
            val tradeAccounts = LitePal.findAll(TradeAccount::class.java,true)
            val accountListShowBeans = ArrayList<AccountListShowBean>()
            tradeAccounts.forEach { tradeAccount ->
                val wallet = HashMap<String, Double>()
                val accountWallets = tradeAccount.accountWallets
                accountWallets.forEach { accountWallet ->
                    val walletAmount = wallet[accountWallet.unit]
                            ?: NumberConstants.number_double_zero
                    wallet[accountWallet.unit] = walletAmount + accountWallet.amount
                }
                if (wallet.isEmpty()) {
                    wallet[CurrencyType.RenMinBi.mCurrencyCode] = NumberConstants.number_double_zero
                }
                accountListShowBeans.add(
                        AccountListShowBean(wallet, tradeAccount.accountName,
                                tradeAccount.note, img = tradeAccount.accountPic,accountId = tradeAccount.baseObjId))
            }
            it.onNext(accountListShowBeans)
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    WkLog.i("tradeAccount size: ${it.size}")
                    mAccountListActivity.updateData(it)
                }
        )
    }

    fun gotoAccountInfo(id: Long = TradeAccount.INVALID_ID,
                        position: Int = NumberConstants.number_int_one_Negative){
        val intent = Intent(mAccountListActivity, AccountInfoActivity::class.java)
        intent.putExtra(TradeAccount.ACCOUNT_ID, id)
        intent.putExtra(WkStringConstants.STR_POSITION_LOW, position)
        mAccountListActivity.startActivityForResult(intent, REQUEST_CODE_ACCOUNT_LIST_ACTIVITY)
    }

    fun gotoCreateAccount(id: Long = TradeAccount.INVALID_ID,
                          position: Int = NumberConstants.number_int_one_Negative) {
        val intent = Intent(mAccountListActivity, UpdateAccountOrWalletActivity::class.java)
        intent.putExtra(TradeAccount.ACCOUNT_ID, id)
        intent.putExtra(UpdateAccountOrWalletActivity.DATA_TYPE, UpdateAccountOrWalletActivity.TYPE_ACCOUNT)
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