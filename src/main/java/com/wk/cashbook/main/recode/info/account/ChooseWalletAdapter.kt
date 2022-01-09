package com.wk.cashbook.main.recode.info.account

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.account.CurrencyTypeManager
import com.wk.cashbook.initMoneyToString
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.projects.common.helper.NumberUtil
import com.wk.projects.common.ui.recycler.BaseRecyclerViewAdapter
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
class ChooseWalletAdapter(mAccounts: MutableList<ChooseWalletShowBean> = ArrayList())
    : BaseRecyclerViewAdapter<ChooseWalletShowBean,ChooseWalletAdapter.ChooseAccountVH>(mAccounts) {

    companion object {
        private const val TYPE_ITEM_WALLET = 0
        private const val TYPE_ITEM_ACCOUNT = 1
    }

    class ChooseAccountVH(
        root: View, val tvChooseAccountName: TextView,
        val tvCurrencyTypeSymbol: TextView? = null, val tvCurrencyTypeAmount: TextView? = null
    ) : RecyclerView.ViewHolder(root)


    override fun getItemLayoutResId(parent: ViewGroup, viewType: Int) =
         if (viewType == TYPE_ITEM_WALLET) {
            R.layout.cashbook_choose_wallet_list_item_wallet
        } else {
            R.layout.cashbook_choose_wallet_list_item_account
        }


    override fun createVH(rootView: View, viewType: Int): ChooseAccountVH {
        return if(viewType==TYPE_ITEM_WALLET) {
            val tvChooseAccountName = rootView.findViewById<TextView>(R.id.tvChooseAccountName)
            val tvCurrencyTypeSymbol = rootView.findViewById<TextView>(R.id.tvCurrencyTypeSymbol)
            val tvCurrencyTypeAmount = rootView.findViewById<TextView>(R.id.tvCurrencyTypeAmount)
            ChooseAccountVH(rootView, tvChooseAccountName, tvCurrencyTypeSymbol, tvCurrencyTypeAmount)
        }else{
            val tvChooseAccountName = rootView.findViewById<TextView>(R.id.tvChooseAccountName)
            ChooseAccountVH(rootView, tvChooseAccountName)
        }
    }

    override fun onBindViewHolder(holder: ChooseAccountVH, position: Int) {
        holder.apply {
            val showBean = getItem(position)
            tvChooseAccountName.text = showBean.name
            val walletId=getItemId(position)
            if(walletId!=AccountWallet.INVALID_ID){
                itemView.setOnClickListener {
                    mIRvClickListener?.onItemClick(this@ChooseWalletAdapter, itemView, position)
                }
                val money = showBean.money
                tvCurrencyTypeSymbol?.text = CurrencyTypeManager.getCurrencyType(money.first)?.mSymbol?:"¤"
                tvCurrencyTypeAmount?.text = money.second
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val walletId=getItemId(position)
        return if(walletId!=AccountWallet.INVALID_ID){
             TYPE_ITEM_WALLET
        }else{
            TYPE_ITEM_ACCOUNT
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).walletId
    }
}