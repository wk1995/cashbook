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

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/25
 *      desc   : 选择钱包弹框
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class ChooseWalletDialog : BaseSimpleDialog(), IRvClickListener {

    companion object {
        fun create(
            bundle: Bundle? = null,
            simpleOnlyEtDialogListener: TradeRecordInfoPresent? = null
        ): ChooseWalletDialog {
            val mChooseAccountDialog = ChooseWalletDialog()
            mChooseAccountDialog.arguments = bundle
            mChooseAccountDialog.mTradeRecordInfoPresent = simpleOnlyEtDialogListener
            return mChooseAccountDialog
        }
    }

    var mTradeRecordInfoPresent: TradeRecordInfoPresent? = null

    private lateinit var rvChooseWalletAccountList: RecyclerView
    private lateinit var rvChooseWalletList: RecyclerView

    private val mChooseAccountAdapter by lazy {
        val mChooseAccountAdapter = ChooseAccountAdapter()
        mChooseAccountAdapter.mIRvClickListener = this
        mChooseAccountAdapter
    }

    private val mChooseWalletAdapter by lazy {
        val mChooseWalletAdapter = ChooseWalletAdapter()
        mChooseWalletAdapter.mIRvClickListener = this
        mChooseWalletAdapter
    }

    override fun initVSView(vsView: View) {
        rvChooseWalletAccountList = vsView.findViewById(R.id.rvChooseWalletAccountList)
        rvChooseWalletList = vsView.findViewById(R.id.rvChooseWalletList)
        rvChooseWalletAccountList.layoutManager = LinearLayoutManager(mActivity)
        rvChooseWalletList.layoutManager = LinearLayoutManager(mActivity)
        rvChooseWalletAccountList.adapter = mChooseAccountAdapter
        rvChooseWalletList.adapter = mChooseWalletAdapter
        initData()
    }

    override fun initViewSubLayout() = R.layout.cashbook_trade_recode_info_choose_wallet_dialog

    override fun onItemClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        if (adapter == mChooseWalletAdapter) {
            val bundle=Bundle()
            bundle.putLong(
                AccountWallet.ACCOUNT_WALLET_ID,
                mChooseWalletAdapter.getItemId(position)
            )
            mTradeRecordInfoPresent?.showTradeAccount(bundle)
            disMiss()
        }
        if (adapter == mChooseAccountAdapter) {
            val tradeAccount = mChooseAccountAdapter.getItem(position)
            mChooseWalletAdapter.updateData(tradeAccount.chooseWalletShowBeans)
        }
    }

    override fun showOkButton() = false

    override fun showCancelButton() = false

    fun updateAccountData(tradeAccounts: List<ChooseAccountShowBean>) {
        mChooseAccountAdapter.updateData(tradeAccounts)
        if (tradeAccounts.isNotEmpty()) {
            mChooseWalletAdapter.updateData(tradeAccounts[0].chooseWalletShowBeans)
        }
    }

    private fun initData() {
        mTradeRecordInfoPresent?.initAccountList()
    }
}