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
class ChooseAccountAdapter(mAccounts: MutableList<ChooseAccountShowBean> = ArrayList())
    : BaseRecyclerViewAdapter<ChooseAccountShowBean, ChooseAccountAdapter.ChooseAccountVH>(mAccounts) {


    class ChooseAccountVH(root: View, val tvCommon: TextView)
        : RecyclerView.ViewHolder(root)


    override fun getItemLayoutResId(parent: ViewGroup, viewType: Int)=R.layout.common_only_text

    override fun createVH(rootView: View, viewType: Int): ChooseAccountVH {
        val tvCommon = rootView.findViewById<TextView>(R.id.tvCommon)
       return  ChooseAccountVH(rootView, tvCommon)
    }

    override fun onBindViewHolder(holder: ChooseAccountVH, position: Int) {
        holder.apply {
            val showBean = getItem(position)
            tvCommon.text = showBean.accountName
            itemView.setOnClickListener {
                mIRvClickListener?.onItemClick(this@ChooseAccountAdapter,itemView,position)
            }
        }
    }


}