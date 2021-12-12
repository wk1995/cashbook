package com.wk.cashbook.trade.account.list

import android.annotation.SuppressLint
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
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
class AccountListMoneyListAdapter(private val mAmounts: MutableList<Pair<String, Double>> = ArrayList())
    : RecyclerView.Adapter<AccountListMoneyListAdapter.MoneyListVH>() {

    class MoneyListVH(rootView: View, val tvCurrencyTypeAmount: TextView, val tvCurrencyTypeName: TextView)
        : RecyclerView.ViewHolder(rootView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoneyListVH {
        val rootView = LayoutInflater.from(parent.context)
                .inflate(R.layout.cashbook_account_list_item_money_item, parent, false)

        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            if (parent is RecyclerView) {
                val maxX = parent.width / rootView.width
                val maxY = parent.height / rootView.height
                val lp = rootView.layoutParams
                //未满一行
                if (maxX >= itemCount) {
                    val avgWidth = parent.width / itemCount
                    lp.width = avgWidth
                    lp.height = MATCH_PARENT
                } else if (maxX * maxY >= itemCount) {
                    //行数
                    val row = itemCount / maxX
                    val avgHeight = parent.height / row
                    lp.height = avgHeight
                }
                rootView.layoutParams = lp
                if (rootView is LinearLayout) {
                    rootView.gravity = if (itemCount == 1) {
                        Gravity.END or Gravity.CENTER_VERTICAL
                    } else {
                        Gravity.CENTER
                    }
                }
            }
        }


        val tvCurrencyTypeAmount = rootView.findViewById<TextView>(R.id.tvCurrencyTypeAmount)
        val tvCurrencyTypeName = rootView.findViewById<TextView>(R.id.tvCurrencyTypeName)
        return MoneyListVH(rootView, tvCurrencyTypeAmount, tvCurrencyTypeName)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MoneyListVH, position: Int) {
        holder.apply {
            val bean = mAmounts[position]
            tvCurrencyTypeName.text = "${bean.first}："
            tvCurrencyTypeAmount.text = bean.second.toString()
        }
    }

    override fun getItemCount() = mAmounts.size

    fun replaceData(amounts: List<Pair<String, Double>>) {
        mAmounts.clear()
        mAmounts.addAll(amounts)
        notifyDataSetChanged()
    }
}