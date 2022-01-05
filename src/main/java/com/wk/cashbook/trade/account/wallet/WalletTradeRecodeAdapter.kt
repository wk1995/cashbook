package com.wk.cashbook.trade.account.wallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wk.cashbook.CashBookConstants
import com.wk.cashbook.R
import com.wk.cashbook.initMoneyToString
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.cashbook.trade.record.bean.ITradeRecodeShowBean
import com.wk.projects.common.communication.constant.BundleKey
import com.wk.projects.common.configuration.WkProjects
import com.wk.projects.common.constant.WkStringConstants.STR_POSITION_LOW
import com.wk.projects.common.helper.NumberUtil
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.resource.WkContextCompat
import com.wk.projects.common.time.date.DayUtil
import com.wk.projects.common.time.date.week.WeekUtil
import com.wk.projects.common.ui.recycler.BaseRecyclerViewAdapter
import com.wk.projects.common.ui.recycler.IRvClickListener
import java.util.*

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/02/24
 * desc         : 首页交易记录list 适配器
 */


class WalletTradeRecodeAdapter(private var mTradeRecords: MutableList<ITradeRecodeShowBean>,
                               var rvItemListener: IRvClickListener? = null) :
        BaseRecyclerViewAdapter<ITradeRecodeShowBean, BaseWalletRecodeItemVH>() {

    companion object {
        private const val TYPE_TOTAL_ITEM = 0
        private const val TYPE_LIST_ITEM = 1

    }

    override fun getItemLayoutResId(parent: ViewGroup, viewType: Int): Int {
        return if (TYPE_TOTAL_ITEM == viewType) {
            R.layout.cashbook_wallet_trade_recode_total_item
        } else {
            R.layout.cashbook_wallet_trade_recode_list_item
        }
    }

    override fun createVH(rootView: View, viewType: Int): BaseWalletRecodeItemVH {
        return if (TYPE_TOTAL_ITEM == viewType) {
            val tvBillTotalDay = rootView.findViewById<TextView>(R.id.tvBillTotalDay)
            val tvAllIncomeValue = rootView.findViewById<TextView>(R.id.tvAllIncomeValue)
            val tvAllPayValue = rootView.findViewById<TextView>(R.id.tvAllPayValue)
            WalletRecodeTotalItemVH(rootView, tvBillTotalDay, tvAllIncomeValue, tvAllPayValue)
        } else {
            val ivTradeType = rootView.findViewById<ImageView>(R.id.ivTradeType)
            val tvTradeNote = rootView.findViewById<TextView>(R.id.tvTradeNote)
            val tvTradeAmount = rootView.findViewById<TextView>(R.id.tvTradeAmount)
            val tvWalletTradeRecodeBalance = rootView.findViewById<TextView>(R.id.tvWalletTradeRecodeBalance)
            tvTradeAmount.setTextColor(WkContextCompat.getColor(R.color.color_grey_7E797B))
            WalletRecodeListItemVH(rootView, ivTradeType, tvTradeNote, tvTradeAmount,tvWalletTradeRecodeBalance)
        }
    }

    override fun onBindViewHolder(holder: BaseWalletRecodeItemVH, position: Int) {
        val tradeRecodeShowBean = mTradeRecords[getDataReallyPosition(position)]
        when (holder) {
            //汇总
            is WalletRecodeTotalItemVH -> {
                if (tradeRecodeShowBean is WalletTradeRecodeShowTitleBean) {
                    val date = Date(tradeRecodeShowBean.mDayEndTime)
                    val allPay = tradeRecodeShowBean.mPayAmount
                    val allIncome = tradeRecodeShowBean.mInComeAmount
                    val week = WeekUtil.getWeek(date).chinese
                    val dayOfMonth = DayUtil.getDayOfMonth(date)
                    holder.apply {
                        tvAllIncomeValue.text = allIncome.toString()
                        tvAllPayValue.text = allPay.toString()
                        tvBillTotalDay.text =
                                String.format(WkProjects.getApplication().getString(R.string.cashbook_title_day_info_format,
                                        dayOfMonth.toString(), week))
                    }

                }
            }
            //列表项目
            is WalletRecodeListItemVH -> {
                if (tradeRecodeShowBean is WalletTradeRecodeShowBean) {
                    val note = tradeRecodeShowBean.mShowText
                    val amount = tradeRecodeShowBean.mAmount
                    holder.apply {
                        itemView.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putInt(STR_POSITION_LOW, getDataReallyPosition(position))
                            bundle.putLong(TradeRecode.TRADE_RECODE_ID, tradeRecodeShowBean.mTradeRecodeId)
                            WkLog.d("click id: ${tradeRecodeShowBean.mTradeRecodeId}")
                            rvItemListener?.onItemClick(this@WalletTradeRecodeAdapter, itemView, position)
                        }
                        itemView.setOnLongClickListener {
                            val bundle = Bundle()
                            bundle.putInt(STR_POSITION_LOW, getDataReallyPosition(position))
                            bundle.putLong(TradeRecode.TRADE_RECODE_ID, tradeRecodeShowBean.mTradeRecodeId)
                            bundle.putString(BundleKey.LIST_ITEM_NAME, tradeRecodeShowBean.mShowText)
                            rvItemListener?.onItemLongClick(this@WalletTradeRecodeAdapter, itemView, position)
                            return@setOnLongClickListener true
                        }
                        tvWalletTradeRecodeBalance.text=NumberUtil.initMoneyToString(tradeRecodeShowBean.mBalance)
                        tvTradeAmount.text =
                                when (tradeRecodeShowBean.mTradeType) {
                                    CashBookConstants.TYPE_ROOT_CATEGORY_INCOME -> {
                                        "+$amount"
                                    }
                                    CashBookConstants.TYPE_ROOT_CATEGORY_PAY -> {
                                        "-$amount"
                                    }
                                    else -> {
                                        amount.toString()
                                    }
                                }

                        tvTradeNote.text = note
                        ivTradeType.setImageResource(
                                when (tradeRecodeShowBean.mTradeType) {
                                    CashBookConstants.TYPE_ROOT_CATEGORY_INCOME -> {
                                        R.drawable.cashbook_trade_type_income
                                    }
                                    CashBookConstants.TYPE_ROOT_CATEGORY_PAY -> {
                                        R.drawable.cashbook_trade_type_pay
                                    }
                                    else -> {
                                        R.drawable.cashbook_trade_type_internal_transfer
                                    }
                                }


                        )
                    }
                }
            }
        }
    }

    override fun getItemCount() = mTradeRecords.size

    override fun getItemViewType(position: Int): Int {
        val tradeRecord = mTradeRecords[position]
        return if (tradeRecord is WalletTradeRecodeShowBean) {
            TYPE_LIST_ITEM
        } else {
            TYPE_TOTAL_ITEM
        }
    }

    fun isTitle(position: Int) = getItemViewType(position) == TYPE_TOTAL_ITEM

    @SuppressLint("NotifyDataSetChanged")
    fun replaceList(tradeRecords: List<ITradeRecodeShowBean>?) {
        mTradeRecords.clear()
        if (tradeRecords != null) {
            mTradeRecords.addAll(tradeRecords)
        }

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(tradeRecode: ITradeRecodeShowBean, position: Int, needSort: Boolean = false) {
        mTradeRecords[position] = tradeRecode
        if (needSort) {
            mTradeRecords.sortWith { o1, o2 ->
                o1.getTradeTime().compareTo(o2.getTradeTime()) * -1
            }
            notifyDataSetChanged()
        } else {
            notifyItemChanged(position)
        }
    }


}