package com.wk.cashbook.main.recode.info.account

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.account.CurrencyTypeManager
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.projects.common.ui.recycler.IRvClickListener

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/25
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class ChooseWalletAdapter(private val mAccounts: MutableList<ChooseWalletShowBean> = ArrayList(),
                          private val mIRvClickListener: IRvClickListener)
    : RecyclerView.Adapter<ChooseWalletAdapter.ChooseAccountVH>() {

    companion object{
        private const val TYPE_ITEM_WALLET=0
        private const val TYPE_ITEM_ACCOUNT=1
    }

    class ChooseAccountVH(root: View, val tvChooseAccountName: TextView,
                          val tvCurrencyTypeSymbol: TextView?=null, val tvCurrencyTypeAmount: TextView?=null)
        : RecyclerView.ViewHolder(root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseAccountVH {
        return if(viewType==TYPE_ITEM_WALLET) {
            val rootView = LayoutInflater.from(parent.context).inflate(R.layout.cashbook_choose_wallet_list_item_wallet,
                    parent, false)
            val tvChooseAccountName = rootView.findViewById<TextView>(R.id.tvChooseAccountName)
            val tvCurrencyTypeSymbol = rootView.findViewById<TextView>(R.id.tvCurrencyTypeSymbol)
            val tvCurrencyTypeAmount = rootView.findViewById<TextView>(R.id.tvCurrencyTypeAmount)
            ChooseAccountVH(rootView, tvChooseAccountName, tvCurrencyTypeSymbol, tvCurrencyTypeAmount)
        }else{
            val rootView = LayoutInflater.from(parent.context).inflate(R.layout.cashbook_choose_wallet_list_item_account,
                    parent, false)
            val tvChooseAccountName = rootView.findViewById<TextView>(R.id.tvChooseAccountName)
            ChooseAccountVH(rootView, tvChooseAccountName)
        }
    }

    override fun onBindViewHolder(holder: ChooseAccountVH, position: Int) {
        holder.apply {
            val showBean = mAccounts[position]
            tvChooseAccountName.text = showBean.name
            val walletId=getItemId(position)
            if(walletId!=AccountWallet.INVALID_ID){
                itemView.setOnClickListener {
                    mIRvClickListener.onItemClick(this@ChooseWalletAdapter, itemView, position)
                }
                val money = showBean.money
                tvCurrencyTypeSymbol?.text = CurrencyTypeManager.getCurrencyType(money.first)?.mSymbol?:"¤"
                tvCurrencyTypeAmount?.text = money.second.toString()
            }
        }
    }

    override fun getItemCount() = mAccounts.size

    override fun getItemViewType(position: Int): Int {
        val walletId=mAccounts[position].walletId
        return if(walletId!=AccountWallet.INVALID_ID){
             TYPE_ITEM_WALLET
        }else{
            TYPE_ITEM_ACCOUNT
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(accounts: List<ChooseWalletShowBean>) {
        mAccounts.clear()
        mAccounts.addAll(accounts)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return mAccounts[position].walletId
    }
}