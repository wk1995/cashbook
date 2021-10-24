package com.wk.cashbook.trade.account.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.projects.common.ui.recycler.IRvClickListener

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/13
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class AccountListAdapter(private val mAccounts: MutableList<AccountListShowBean> = ArrayList(),
                         private val mIRvClickListener: IRvClickListener)
    : RecyclerView.Adapter<AccountListAdapter.AccountListVH>() {

    class AccountListVH(rootView: View, val ivAccountItemPic: ImageView, val tvAccountItemName: TextView,
                        val tvAccountItemNote: TextView, val rvAccountItemMoney: RecyclerView,
                        val llAccountItemMoney: LinearLayout)
        : RecyclerView.ViewHolder(rootView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountListVH {
        val rootView = LayoutInflater.from(parent.context)
                .inflate(R.layout.cashbook_account_list_item, parent, false)
        val ivAccountItemPic = rootView.findViewById<ImageView>(R.id.ivAccountItemPic)
        val tvAccountItemName = rootView.findViewById<TextView>(R.id.tvAccountItemName)
        val tvAccountItemNote = rootView.findViewById<TextView>(R.id.tvAccountItemNote)
        val rvAccountItemMoney = rootView.findViewById<RecyclerView>(R.id.rvAccountItemMoney)
        val llAccountItemMoney = rootView.findViewById<LinearLayout>(R.id.llAccountItemMoney)
        rvAccountItemMoney.layoutManager = GridLayoutManager(parent.context, 2)
        rvAccountItemMoney.adapter = AccountListMoneyListAdapter()
        return AccountListVH(rootView, ivAccountItemPic, tvAccountItemName, tvAccountItemNote,
                rvAccountItemMoney, llAccountItemMoney)
    }

    override fun onBindViewHolder(holder: AccountListVH, position: Int) {
        holder.apply {
            itemView.setOnClickListener {
                mIRvClickListener.onItemClick(this@AccountListAdapter, itemView, position)
            }
            itemView.setOnLongClickListener {
                mIRvClickListener.onItemLongClick(this@AccountListAdapter, itemView, position)
                true
            }
            val bean = mAccounts[position]
            tvAccountItemName.text = bean.name
            tvAccountItemNote.text = bean.note
            val money = bean.money
            if (money.size <= 1) {
                rvAccountItemMoney.visibility = View.GONE
                llAccountItemMoney.visibility = View.VISIBLE
                money.keys.forEach {
                    llAccountItemMoney.findViewById<TextView>(R.id.tvCurrencyTypeName).text = it
                    llAccountItemMoney.findViewById<TextView>(R.id.tvCurrencyTypeAmount).text = money[it].toString()
                }
            } else {
                llAccountItemMoney.visibility = View.GONE
                rvAccountItemMoney.visibility = View.VISIBLE
                val adapter = rvAccountItemMoney.adapter
                if (adapter is AccountListMoneyListAdapter) {
                    adapter.replaceData(money.map {
                        Pair(it.key, it.value)
                    })
                }
            }


        }
    }

    override fun getItemCount() = mAccounts.size

    fun updateData(accounts: List<AccountListShowBean>) {
        mAccounts.clear()
        mAccounts.addAll(accounts)
        notifyDataSetChanged()
    }

    fun addData(account: AccountListShowBean) {
        mAccounts.add(account)
        notifyItemInserted(itemCount - 1)
    }

    fun removeData(position: Int){
        mAccounts.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position,itemCount)
    }


    fun updateData(account:AccountListShowBean,position: Int){
        mAccounts.add(position,account)
        mAccounts.removeAt(position+1)
        notifyItemChanged(position)
    }

    override fun getItemId(position: Int) =
            mAccounts[position].accountId

}