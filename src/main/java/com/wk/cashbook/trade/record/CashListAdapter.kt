package com.wk.cashbook.trade.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.cashbook.trade.record.bean.ITradeRecodeShowBean
import com.wk.cashbook.trade.record.bean.TradeRecodeShowBean
import com.wk.cashbook.trade.record.bean.TradeRecodeShowTitleBean
import com.wk.projects.common.communication.IRvClickListener
import com.wk.projects.common.communication.constant.BundleKey
import com.wk.projects.common.constant.WkStringConstants.STR_POSITION_LOW
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.resource.WkContextCompat
import com.wk.projects.common.time.date.DayUtil
import com.wk.projects.common.time.date.week.WeekUtil
import java.util.*

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/02/24
 * desc         : 首页交易记录list 适配器
 */


class CashListAdapter(private var mTradeRecords: MutableList<ITradeRecodeShowBean>,
                      var rvItemListener: IRvClickListener? = null) : RecyclerView.Adapter<BaseCashItemVH>() {

    companion object {
        private const val TYPE_TOTAL_ITEM = 0
        private const val TYPE_LIST_ITEM = 1

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCashItemVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        if (TYPE_TOTAL_ITEM == viewType) {
            val rootView = layoutInflater.inflate(R.layout.cashbook_bill_total_item, parent, false)
            val tvBillTotalDay = rootView.findViewById<TextView>(R.id.tvBillTotalDay)
            val tvTradeTotalWeek = rootView.findViewById<TextView>(R.id.tvTradeTotalWeek)
            val tvAllIncomeValue = rootView.findViewById<TextView>(R.id.tvAllIncomeValue)
            val tvAllPayValue = rootView.findViewById<TextView>(R.id.tvAllPayValue)
            return CashTotalItemVH(rootView, tvBillTotalDay, tvTradeTotalWeek, tvAllIncomeValue, tvAllPayValue)
        }

        if (TYPE_LIST_ITEM == viewType) {
            val rootView = layoutInflater.inflate(R.layout.cashbook_bill_list_item, parent, false)
            rootView.setBackgroundResource(R.color.common_white_F1F0F0)
            val ivTradeType = rootView.findViewById<ImageView>(R.id.ivTradeType)
            val tvTradeNote = rootView.findViewById<TextView>(R.id.tvTradeNote)
            val tvTradeAmount = rootView.findViewById<TextView>(R.id.tvTradeAmount)
            tvTradeAmount.setTextColor(WkContextCompat.getColor(parent.context, R.color.color_grey_7E797B))
            return CashListItemVH(rootView, ivTradeType, tvTradeNote, tvTradeAmount)
        }
        throw IllegalAccessException("viewType: $viewType is illegal")
    }

    override fun onBindViewHolder(holder: BaseCashItemVH, position: Int) {
        val tradeRecodeShowBean = mTradeRecords[getRealPosition(position)]
        when (holder) {
            //汇总
            is CashTotalItemVH -> {
                if (tradeRecodeShowBean is TradeRecodeShowTitleBean) {
                    val date = Date(tradeRecodeShowBean.mDayEndTime)
                    val allPay = tradeRecodeShowBean.mPayAmount
                    val allIncome = tradeRecodeShowBean.mInComeAmount
                    val week = WeekUtil.getWeek(date).name
                    val dayOfMonth = DayUtil.getDayOfMonth(date)
                    holder.apply {
                        tvAllIncomeValue.text = allIncome.toString()
                        tvAllPayValue.text = allPay.toString()
                        tvTradeTotalWeek.text = week
                        tvBillTotalDay.text = dayOfMonth.toString()
                    }

                }
            }
            //列表项目
            is CashListItemVH -> {
                if (tradeRecodeShowBean is TradeRecodeShowBean) {
                    val note = tradeRecodeShowBean.mShowText
                    val amount = tradeRecodeShowBean.mAmount
                    holder.apply {
                        itemView.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putInt(STR_POSITION_LOW, getRealPosition(position))
                            bundle.putLong(TradeRecode.TRADE_RECODE_ID, tradeRecodeShowBean.mTradeRecodeId)
                            WkLog.d("click id: ${tradeRecodeShowBean.mTradeRecodeId}")
                            rvItemListener?.onItemClick(bundle)
                        }
                        itemView.setOnLongClickListener {
                            val bundle = Bundle()
                            bundle.putInt(STR_POSITION_LOW, getRealPosition(position))
                            bundle.putLong(TradeRecode.TRADE_RECODE_ID, tradeRecodeShowBean.mTradeRecodeId)
                            bundle.putString(BundleKey.LIST_ITEM_NAME, tradeRecodeShowBean.mShowText)
                            return@setOnLongClickListener rvItemListener?.onItemLongClick(bundle)
                                    ?: false
                        }
                        tvTradeAmount.text = amount.toString()
                        tvTradeNote.text = note
                    }
                }
            }
        }
    }

    private fun getRealPosition(position: Int) = position

    override fun getItemCount() = mTradeRecords.size

    override fun getItemViewType(position: Int): Int {
        val tradeRecord = mTradeRecords[position]
        return if (tradeRecord is TradeRecodeShowBean) {
            TYPE_LIST_ITEM
        } else {
            TYPE_TOTAL_ITEM
        }
    }

    fun replaceList(tradeRecords: List<ITradeRecodeShowBean>) {
        mTradeRecords.clear()
        mTradeRecords.addAll(tradeRecords)
        notifyDataSetChanged()
    }

    fun getData(position: Int): ITradeRecodeShowBean {
        return mTradeRecords[position]
    }

    fun addData(tradeRecode: ITradeRecodeShowBean) {
        if (!mTradeRecords.contains(tradeRecode)) {
            mTradeRecords.add(tradeRecode)
            mTradeRecords.sortWith { o1, o2 ->
                o1.getTradeTime().compareTo(o2.getTradeTime()) * -1
            }
            notifyDataSetChanged()
        }

    }

    fun remove(position: Int) {
        mTradeRecords.remove(mTradeRecords[position])
        notifyDataSetChanged()
    }

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