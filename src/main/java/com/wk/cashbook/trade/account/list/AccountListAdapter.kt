package com.wk.cashbook.trade.account.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.CurrencyType
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.helper.WkBitmapUtil
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

    private val defaultPic by lazy {
        WkBitmapUtil.getBitmap(R.drawable.cashbook_account_type_crash)
    }

    class AccountListVH(rootView: View, val ivAccountItemPic: ImageView, val tvAccountItemName: TextView,
                        val tvAccountItemNote: TextView, private val tvAccountMoney1: TextView,
                        private val tvAccountMoney2: TextView, private val tvAccountMoney3: TextView,
                        private val tvAccountMoney4: TextView)
        : RecyclerView.ViewHolder(rootView) {
        private val showMoneyList by lazy {
            listOf(
                    Pair(tvAccountMoney1, CurrencyType.RenMinBi),
                    Pair(tvAccountMoney2, CurrencyType.Dollar),
                    Pair(tvAccountMoney3, CurrencyType.HongKongDollar),
                    Pair(tvAccountMoney4, CurrencyType.JapaneseYen)
            )
        }

        @SuppressLint("SetTextI18n")
        fun setMoney(money: Map<String, Double>) {
            showMoneyList.forEach {
                val moneyAmount = money[it.second.mCurrencyCode]
                        ?: NumberConstants.number_double_zero
                if (moneyAmount != NumberConstants.number_double_zero) {
                    it.first.visibility = View.VISIBLE
                    it.first.text = it.second.mCurrencyCode + ": " + moneyAmount
                } else {
                    it.first.visibility = View.GONE
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountListVH {
        val rootView = LayoutInflater.from(parent.context)
                .inflate(R.layout.cashbook_account_list_item, parent, false)
        val ivAccountItemPic = rootView.findViewById<ImageView>(R.id.ivAccountItemPic)
        val tvAccountItemName = rootView.findViewById<TextView>(R.id.tvAccountItemName)
        val tvAccountItemNote = rootView.findViewById<TextView>(R.id.tvAccountItemNote)
        val tvAccountMoney1 = rootView.findViewById<TextView>(R.id.tvAccountMoney1)
        val tvAccountMoney2 = rootView.findViewById<TextView>(R.id.tvAccountMoney2)
        val tvAccountMoney3 = rootView.findViewById<TextView>(R.id.tvAccountMoney3)
        val tvAccountMoney4 = rootView.findViewById<TextView>(R.id.tvAccountMoney4)
        return AccountListVH(rootView, ivAccountItemPic, tvAccountItemName, tvAccountItemNote, tvAccountMoney1,
                tvAccountMoney2, tvAccountMoney3, tvAccountMoney4)
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
            ivAccountItemPic.setImageBitmap(WkBitmapUtil.getBitmapByBytes(bean.img, defaultBitmap = defaultPic))
            setMoney(bean.money)
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

    fun removeData(position: Int) {
        mAccounts.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position, itemCount)
    }


    fun updateData(account: AccountListShowBean, position: Int) {
        mAccounts.add(position, account)
        mAccounts.removeAt(position + 1)
        notifyItemChanged(position)
    }

    override fun getItemId(position: Int) =
            mAccounts[position].accountId

}