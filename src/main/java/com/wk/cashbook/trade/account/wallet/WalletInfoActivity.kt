package com.wk.cashbook.trade.account.wallet

import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.trade.record.CashListAdapter
import com.wk.cashbook.trade.record.bean.ITradeRecodeShowBean
import com.wk.projects.common.BaseProjectsActivity
import com.wk.projects.common.ui.recycler.IRvClickListener

class WalletInfoActivity : BaseProjectsActivity() , IRvClickListener {

    private lateinit var rvWalletTradeRecode:RecyclerView
    private lateinit var tvWalletInfoName:TextView
    private val mCashListAdapter by lazy {
        CashListAdapter(ArrayList(), this)
    }

    private val mWalletInfoPresent by lazy {
        WalletInfoPresent(this)
    }

    override fun initResLayId()= R.layout.cashbook_wallet_info_activity

    override fun bindView(savedInstanceState: Bundle?, mBaseProjectsActivity: BaseProjectsActivity) {
        mWalletInfoPresent.onCreate()
        rvWalletTradeRecode=findViewById(R.id.rvWalletTradeRecode)
        tvWalletInfoName=findViewById(R.id.tvWalletInfoName)
        rvWalletTradeRecode.adapter=mCashListAdapter
        rvWalletTradeRecode.layoutManager=LinearLayoutManager(this)
        mWalletInfoPresent.initData(intent)
    }

    fun updateData(tradeShowBeans:List<ITradeRecodeShowBean>?){
        mCashListAdapter.replaceList(tradeShowBeans)
    }

    override fun onDestroy() {
        super.onDestroy()
        mWalletInfoPresent.onDestroy()
    }

    fun setWalletName(name:String){
        tvWalletInfoName.text=name
    }
}