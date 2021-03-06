package com.wk.cashbook.account.info

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.initMoneyToString
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.projects.common.helper.NumberUtil
import com.wk.projects.common.resource.WkContextCompat
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
class AccountWalletAdapter(mWallets: MutableList<AccountWallet> = ArrayList())
    : BaseRecyclerViewAdapter<AccountWallet, AccountWalletAdapter.AccountWalletVH>(mWallets) {

    class AccountWalletVH(rootView: View, val tvWalletName: TextView, val etWalletAmount: TextView,
                          val tvWalletPayTotal: TextView, val tvWalletInComeTotal: TextView)
        : RecyclerView.ViewHolder(rootView)

    override fun onBindViewHolder(holder: AccountWalletVH, position: Int) {
        val data = getItem(position)
        holder.apply {
            etWalletAmount.text =WkContextCompat.getString(R.string.cashbook_list_wallet_balance).format(NumberUtil.initMoneyToString(data.amount))
            tvWalletName.text = (data.accountName)
            itemView.setOnClickListener {
                mIRvClickListener?.onItemClick(this@AccountWalletAdapter, tvWalletName, position)
            }
            tvWalletName.setOnClickListener {
                mIRvClickListener?.onItemChildClick(this@AccountWalletAdapter, tvWalletName, position)
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return getItem(position).baseObjId
    }

    override fun getItemLayoutResId(parent: ViewGroup, viewType: Int) = R.layout.cashbook_account_wallet_list_item

    override fun createVH(rootView: View, viewType: Int): AccountWalletVH {
        val tvWalletName = rootView.findViewById<TextView>(R.id.tvWalletName)
        val etWalletAmount = rootView.findViewById<TextView>(R.id.etWalletAmount)
        val tvWalletPayTotal = rootView.findViewById<TextView>(R.id.tvWalletPayTotal)
        val tvWalletInComeTotal = rootView.findViewById<TextView>(R.id.tvWalletInComeTotal)
        return AccountWalletVH(rootView, tvWalletName, etWalletAmount, tvWalletPayTotal, tvWalletInComeTotal)
    }
}