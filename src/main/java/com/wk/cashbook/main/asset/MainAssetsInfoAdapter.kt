package com.wk.cashbook.main.asset

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.account.CurrencyTypeManager
import com.wk.cashbook.trade.data.CurrencyType
import com.wk.projects.common.resource.WkContextCompat
import com.wk.projects.common.ui.recycler.BaseRecyclerViewAdapter

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/25
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class MainAssetsInfoAdapter : BaseRecyclerViewAdapter<AssetsInfoShowBean,
        MainAssetsInfoAdapter.MainAssetsInfoAdapterVH>() {

    class MainAssetsInfoAdapterVH(rootView: View, val tvAssetsInfoCash: TextView,
                                  val tvAssetsInfoAssets: TextView,
                                  val tvAssetsInfoNetAssets: TextView,
                                  val tvAssetsInfoUnit: TextView,
                                  val tvAssetsInfoLiabilities: TextView)
        : RecyclerView.ViewHolder(rootView)

    override fun getItemLayoutResId(parent: ViewGroup, viewType: Int) = R.layout.cashbook_main_account_tab_list_item

    override fun createVH(rootView: View, viewType: Int): MainAssetsInfoAdapterVH {
        val tvAssetsInfoCash = rootView.findViewById<TextView>(R.id.tvAssetsInfoCash)
        val tvAssetsInfoAssets = rootView.findViewById<TextView>(R.id.tvAssetsInfoAssets)
        val tvAssetsInfoLiabilities = rootView.findViewById<TextView>(R.id.tvAssetsInfoLiabilities)
        val tvAssetsInfoNetAssets = rootView.findViewById<TextView>(R.id.tvAssetsInfoNetAssets)
        val tvAssetsInfoUnit = rootView.findViewById<TextView>(R.id.tvAssetsInfoUnit)
        return MainAssetsInfoAdapterVH(rootView, tvAssetsInfoCash, tvAssetsInfoAssets,
                tvAssetsInfoNetAssets, tvAssetsInfoUnit, tvAssetsInfoLiabilities)
    }

    override fun onBindViewHolder(holder: MainAssetsInfoAdapterVH, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = mData[getDataReallyPosition(position)]
        holder.apply {
            tvAssetsInfoCash.text = WkContextCompat.getStringByFormat(R.string.cashbook_assets_info_cash, data.cash)
            tvAssetsInfoAssets.text = WkContextCompat.getStringByFormat(R.string.cashbook_assets_info_assets, data.assets)
            tvAssetsInfoLiabilities.text = WkContextCompat.getStringByFormat(R.string.cashbook_assets_info_liabilities, data.liabilities)
            tvAssetsInfoNetAssets.text = WkContextCompat.getStringByFormat(R.string.cashbook_assets_info_net_assets, data.netAssets)
            tvAssetsInfoUnit.text = (CurrencyTypeManager.getCurrencyType(data.unitName)?: CurrencyType.UnKnow).chinese
            listOf<View>(tvAssetsInfoCash,tvAssetsInfoAssets,tvAssetsInfoLiabilities).forEach {
                mIRvClickListener?.onItemChildClick(this@MainAssetsInfoAdapter,it,position)
            }
        }

    }
}