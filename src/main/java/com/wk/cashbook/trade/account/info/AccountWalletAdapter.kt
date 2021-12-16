package com.wk.cashbook.trade.account.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.projects.common.ui.recycler.BaseRecyclerViewAdapter

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/14
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class AccountWalletAdapter(mWallets:MutableList<AccountWallet> = ArrayList())
    : BaseRecyclerViewAdapter<AccountWallet,AccountWalletAdapter.AccountWalletVH>(mWallets){
    
    class AccountWalletVH(rootView: View,val tvWalletName:TextView,val etWalletAmount:EditText)
        :RecyclerView.ViewHolder(rootView)

    override fun onBindViewHolder(holder: AccountWalletVH, position: Int) {
        val data=getItem(position)
        holder.etWalletAmount.setText(data.amount.toString())
        holder.tvWalletName.text=(data.accountName)
    }



    override fun getItemLayoutResId(parent: ViewGroup, viewType: Int)=R.layout.cashbook_account_wallet_list_item

    override fun createVH(rootView: View): AccountWalletVH {
        val tvWalletName=rootView.findViewById<TextView>(R.id.tvWalletName)
        val etWalletAmount=rootView.findViewById<EditText>(R.id.etWalletAmount)
        return AccountWalletVH(rootView,tvWalletName,etWalletAmount)
    }
}