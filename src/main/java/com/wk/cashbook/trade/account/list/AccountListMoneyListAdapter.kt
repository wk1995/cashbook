package com.wk.cashbook.trade.account.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/14
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class AccountListMoneyListAdapter(private val mAmounts: MutableList<Pair<String,Double>> = ArrayList())
    : RecyclerView.Adapter<AccountListMoneyListAdapter.MoneyListVH>() {

    class MoneyListVH(rootView: View, val tvCurrencyTypeAmount: TextView, val tvCurrencyTypeName: TextView)
        : RecyclerView.ViewHolder(rootView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoneyListVH {
        val rootView = LayoutInflater.from(parent.context)
                .inflate(R.layout.cashbook_account_list_item_money_item, parent, false)
        val tvCurrencyTypeAmount = rootView.findViewById<TextView>(R.id.tvCurrencyTypeAmount)
        val tvCurrencyTypeName = rootView.findViewById<TextView>(R.id.tvCurrencyTypeName)
        return MoneyListVH(rootView, tvCurrencyTypeAmount, tvCurrencyTypeName)
    }

    override fun onBindViewHolder(holder: MoneyListVH, position: Int) {
        holder.apply {
            val bean = mAmounts[position]
            tvCurrencyTypeName.text=bean.first
            tvCurrencyTypeAmount.text=bean.second.toString()
        }
    }

    override fun getItemCount() = mAmounts.size

    fun replaceData(amounts: List<Pair<String,Double>>){
        mAmounts.clear()
        mAmounts.addAll(amounts)
        notifyDataSetChanged()
    }
}