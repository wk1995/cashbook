package com.wk.cashbook.trade.account.wallet

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.trade.record.CashListAdapter
import com.wk.cashbook.trade.record.bean.ITradeRecodeShowBean
import com.wk.projects.common.BaseProjectsActivity
import com.wk.projects.common.resource.WkContextCompat
import com.wk.projects.common.ui.WkCommonActionBar
import com.wk.projects.common.ui.recycler.IRvClickListener

class WalletTradeRecodeActivity : BaseProjectsActivity(), IRvClickListener {

    private lateinit var rvWalletTradeRecode: RecyclerView
    private lateinit var tvWalletInfoName: TextView
    private lateinit var tvWalletInfoBalance: TextView
    private lateinit var wbWalletInfoTitle: WkCommonActionBar
    private val mCashListAdapter by lazy {
        WalletTradeRecodeAdapter(ArrayList(), this)
    }

    private val mWalletInfoPresent by lazy {
        WalletTradeRecodePresent(this)
    }

    override fun initResLayId() = R.layout.cashbook_wallet_info_activity

    override fun bindView(
        savedInstanceState: Bundle?,
        mBaseProjectsActivity: BaseProjectsActivity
    ) {
        mWalletInfoPresent.onCreate()
        rvWalletTradeRecode = findViewById(R.id.rvWalletTradeRecode)
        tvWalletInfoName = findViewById(R.id.tvWalletInfoName)
        wbWalletInfoTitle = findViewById(R.id.wbWalletInfoTitle)
        tvWalletInfoBalance = findViewById(R.id.tvWalletInfoBalance)
        wbWalletInfoTitle.setMiddleViewText(R.string.cashbook_wallet_info)
        wbWalletInfoTitle.setMiddleViewGravity(Gravity.START or Gravity.CENTER_VERTICAL)
        wbWalletInfoTitle.setMiddleViewTextSize(R.dimen.s16sp)
        rvWalletTradeRecode.adapter = mCashListAdapter
        rvWalletTradeRecode.layoutManager = LinearLayoutManager(this)
        mWalletInfoPresent.initData(intent)
    }

    fun updateData(tradeShowBeans: List<ITradeRecodeShowBean>?) {
        mCashListAdapter.replaceList(tradeShowBeans)
    }

    override fun onDestroy() {
        super.onDestroy()
        mWalletInfoPresent.onDestroy()
    }

    fun setWalletBalance(balance: String) {
        tvWalletInfoBalance.text = WkContextCompat.getStringByFormat(
            R.string.cashbook_wallet_info_balance, balance
        )
    }


    fun setWalletName(name: String) {
        tvWalletInfoName.text = name
    }
}