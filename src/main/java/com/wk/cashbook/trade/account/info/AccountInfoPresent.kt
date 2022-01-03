package com.wk.cashbook.trade.account.info

import android.content.Intent
import com.wk.cashbook.CashBookActivityRequestCode
import com.wk.cashbook.R
import com.wk.cashbook.trade.account.UpdateAccountOrWalletActivity
import com.wk.cashbook.trade.account.UpdateAccountOrWalletActivity.Companion.DATA_TYPE
import com.wk.cashbook.trade.account.UpdateAccountOrWalletActivity.Companion.TYPE_WALLET
import com.wk.cashbook.trade.account.wallet.WalletInfoActivity
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.projects.common.BaseProjectsPresent
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import com.wk.projects.common.helper.WkBitmapUtil
import com.wk.projects.common.log.WkLog
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/21
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class AccountInfoPresent(private val mAccountInfoActivity: AccountInfoActivity) : BaseProjectsPresent() {
    private var mSubscriptions: CompositeSubscription? = null

    private val mAccountInfoModel by lazy {
        AccountInfoModel()
    }

    private var currentTradeAccountId:Long=TradeAccount.INVALID_ID

    override fun onCreate() {
        mSubscriptions = CompositeSubscription()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions?.clear()
    }

    fun initData(id: Long) {
        currentTradeAccountId=id
        mSubscriptions = CompositeSubscription()
        val defaultPic = WkBitmapUtil.getBitmap(R.drawable.cashbook_account_type_crash)
        WkLog.i("id: $currentTradeAccountId")
        if (currentTradeAccountId > TradeAccount.INVALID_ID) {
            mSubscriptions?.add(Observable.create(Observable.OnSubscribe<TradeAccount> {
                it.onNext(mAccountInfoModel.getTradeAccount(currentTradeAccountId))
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { tradeAccount ->
                        if (tradeAccount == null) {
                            mAccountInfoActivity.finish()
                            return@subscribe
                        }
                        mAccountInfoModel.mCurrentTradeAccount = tradeAccount
                        showData()
                    }
            )
        } else {
            //表示是新增account
            mAccountInfoActivity.setAccountPic(defaultPic)
            mAccountInfoModel.setPicByteArray(WkBitmapUtil.getByteArrayByBitmap(defaultPic))
            mAccountInfoActivity.showAddAccount(true)
        }
    }

    private fun showData() {
        val defaultPic = WkBitmapUtil.getBitmap(R.drawable.cashbook_account_type_crash)
        val tradeAccount = mAccountInfoModel.mCurrentTradeAccount
        val pic = WkBitmapUtil.getBitmapByBytes(tradeAccount.accountPic, defaultBitmap = defaultPic)
        mAccountInfoActivity.setAccountPic(pic)
        mAccountInfoActivity.setAccountName(tradeAccount.accountName)
        mAccountInfoActivity.setNote(tradeAccount.note)
        mAccountInfoActivity.setWallet(tradeAccount.accountWallets)
        mAccountInfoActivity.showAddAccount(false)
        mAccountInfoActivity.clearAddData()
    }

    fun saveOrUpdateAccount(accountName: String, accountNote: String,
                            walletName: String = WkStringConstants.STR_EMPTY,
                            walletNote: String = WkStringConstants.STR_EMPTY,
                            walletTime: String = NumberConstants.number_long_zero.toString(),
                            walletAmount: String = NumberConstants.number_double_zero.toString()) {
        if (accountName.isNotEmpty()) {
            mAccountInfoModel.updateNameAndNote(accountName, accountNote)
            mSubscriptions?.add(Observable.create(Observable.OnSubscribe<Boolean> {
                it.onNext(mAccountInfoModel.saveOrUpdate())
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { success ->
                        if (success) {
                            showData()
                            addAccountWallet(walletName,walletNote,walletTime,walletAmount)
                        } else {
                            mAccountInfoActivity.showToast("保存账号失败")
                        }
                    }
            )
        } else {
            //直接新增钱包
            addAccountWallet(walletName,walletNote,walletTime,walletAmount)
        }
    }

    private fun addAccountWallet(walletName: String = WkStringConstants.STR_EMPTY,
                         walletNote: String = WkStringConstants.STR_EMPTY,
                         walletTime: String = NumberConstants.number_long_zero.toString(),
                         walletAmount: String = NumberConstants.number_double_zero.toString()) {
        if (walletName.isEmpty()) {
            return
        }
        mSubscriptions?.add(Observable.create(Observable.OnSubscribe<Boolean> {
            it.onNext(mAccountInfoModel.addWallet(walletName, walletNote, walletTime, walletAmount))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { success ->
                    if (success) {
                        showData()
                    } else {
                        mAccountInfoActivity.showToast("添加钱包失败")
                    }
                }
        )
    }




    fun gotoCreateWallet(){
        val intent = Intent(mAccountInfoActivity, UpdateAccountOrWalletActivity::class.java)
        intent.putExtra(DATA_TYPE,TYPE_WALLET)
        intent.putExtra(AccountWallet.ACCOUNT_ID,mAccountInfoModel.getAccountId())
        mAccountInfoActivity.startActivityForResult(intent, CashBookActivityRequestCode.REQUEST_CODE_ACCOUNT_LIST_ACTIVITY)
    }

    fun gotoUpdateWallet(walletId:Long){
        val intent = Intent(mAccountInfoActivity, UpdateAccountOrWalletActivity::class.java)
        intent.putExtra(AccountWallet.ACCOUNT_WALLET_ID,walletId)
        intent.putExtra(AccountWallet.ACCOUNT_ID,mAccountInfoModel.getAccountId())
        intent.putExtra(DATA_TYPE,TYPE_WALLET)
        mAccountInfoActivity.startActivityForResult(intent, CashBookActivityRequestCode.REQUEST_CODE_ACCOUNT_LIST_ACTIVITY)
    }

    fun gotoUpdateAccount() {
        val intent = Intent(mAccountInfoActivity, UpdateAccountOrWalletActivity::class.java)
        intent.putExtra(TradeAccount.ACCOUNT_ID, mAccountInfoModel.getAccountId())
        intent.putExtra(DATA_TYPE, UpdateAccountOrWalletActivity.TYPE_ACCOUNT)
        mAccountInfoActivity.startActivityForResult(intent, CashBookActivityRequestCode.REQUEST_CODE_ACCOUNT_LIST_ACTIVITY)
    }

    fun gotoWalletRecodeInfo(walletId:Long){
        val intent = Intent(mAccountInfoActivity, WalletInfoActivity::class.java)
        intent.putExtra(AccountWallet.ACCOUNT_WALLET_ID,walletId)
        mAccountInfoActivity.startActivityForResult(intent, CashBookActivityRequestCode.REQUEST_CODE_ACCOUNT_LIST_ACTIVITY)
    }
}