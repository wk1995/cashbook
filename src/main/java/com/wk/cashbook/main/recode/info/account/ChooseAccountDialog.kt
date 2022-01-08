package com.wk.cashbook.main.recode.info.account

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.cashbook.main.recode.info.TradeRecordInfoPresent
import com.wk.projects.common.BaseSimpleDialog
import com.wk.projects.common.ui.recycler.IRvClickListener
import org.litepal.LitePal
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/25
 *      desc   : 选择账户弹框
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class ChooseAccountDialog : BaseSimpleDialog(), IRvClickListener {

    companion object {
        fun create(bundle: Bundle? = null, simpleOnlyEtDialogListener: TradeRecordInfoPresent? = null)
                : ChooseAccountDialog {
            val mChooseAccountDialog = ChooseAccountDialog()
            mChooseAccountDialog.arguments = bundle
            mChooseAccountDialog.mTradeRecordInfoPresent = simpleOnlyEtDialogListener
            return mChooseAccountDialog
        }
    }

    var mTradeRecordInfoPresent: TradeRecordInfoPresent? = null

    private lateinit var rvCommon: RecyclerView

    private val mChooseAccountAdapter by lazy {
        ChooseWalletAdapter(mIRvClickListener = this)
    }

    override fun initVSView(vsView: View) {
        rvCommon = vsView.findViewById(R.id.rvCommon)
        rvCommon.layoutManager = LinearLayoutManager(mActivity)
        rvCommon.adapter = mChooseAccountAdapter
        initData()
    }

    override fun initViewSubLayout() = R.layout.common_only_recycler

    override fun onItemClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        if (adapter == mChooseAccountAdapter) {
            arguments?.putLong(AccountWallet.ACCOUNT_WALLET_ID, mChooseAccountAdapter.getItemId(position))
            mTradeRecordInfoPresent?.showTradeAccount(arguments)
            disMiss()
        }
    }

    override fun showOkButton() = false

    override fun showCancelButton() = false


    private fun initData() {
        Observable.create(Observable.OnSubscribe<List<TradeAccount>> {
            it.onNext(LitePal.findAll(TradeAccount::class.java, true))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { tradeAccounts ->
                    val data = ArrayList<ChooseWalletShowBean>()
                    tradeAccounts.forEach { tradeAccount ->
                        val accountWallets= tradeAccount.accountWallets
                        if(accountWallets.isNullOrEmpty()){
                            return@forEach
                        }
                        data.add(ChooseWalletShowBean(AccountWallet.INVALID_ID, name = tradeAccount.accountName))
                        accountWallets.forEach { tradeWallet ->
                            val map = HashMap<String, Double>()
                            map[tradeWallet.unit] = tradeWallet.amount
                            data.add(ChooseWalletShowBean(tradeWallet.baseObjId, Pair(tradeWallet.unit, tradeWallet.amount),
                                    tradeWallet.accountName))
                        }
                    }
                    mChooseAccountAdapter.updateData(data)
                }
    }
}