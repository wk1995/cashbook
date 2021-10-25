package com.wk.cashbook.trade.info.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.trade.account.list.AccountListShowBean
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
class ChooseAccountAdapter(private val mAccounts: MutableList<AccountListShowBean> = ArrayList(),
                           private val mIRvClickListener: IRvClickListener)
    : RecyclerView.Adapter<ChooseAccountAdapter.ChooseAccountVH>() {

    class ChooseAccountVH(root: View, val tvChooseAccountName: TextView,
                          val tvCurrencyTypeName: TextView, val tvCurrencyTypeAmount: TextView)
        : RecyclerView.ViewHolder(root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseAccountVH {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.cashbook_choose_account_list_item,
                parent, false)
        val tvChooseAccountName = rootView.findViewById<TextView>(R.id.tvChooseAccountName)
        val tvCurrencyTypeName = rootView.findViewById<TextView>(R.id.tvCurrencyTypeName)
        val tvCurrencyTypeAmount = rootView.findViewById<TextView>(R.id.tvCurrencyTypeAmount)
        return ChooseAccountVH(rootView, tvChooseAccountName, tvCurrencyTypeName, tvCurrencyTypeAmount)
    }

    override fun onBindViewHolder(holder: ChooseAccountVH, position: Int) {
        holder.apply {
            itemView.setOnClickListener {
                mIRvClickListener.onItemClick(this@ChooseAccountAdapter, itemView, position)
            }
            val showBean = mAccounts[position]
            val money = showBean.money
            tvChooseAccountName.text = showBean.name
            if (money.size <= 1) {
                money.keys.forEach {
                    tvCurrencyTypeName.text = it
                    tvCurrencyTypeAmount.text = money[it].toString()
                }
            }

        }
    }

    override fun getItemCount() = mAccounts.size

    fun updateData(accounts:List<AccountListShowBean>){
        mAccounts.clear()
        mAccounts.addAll(accounts)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return mAccounts[position].accountId
    }
}