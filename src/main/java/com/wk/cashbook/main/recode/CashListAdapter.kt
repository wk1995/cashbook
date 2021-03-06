package com.wk.cashbook.main.recode

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.CashBookConstants
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.cashbook.trade.record.BaseCashItemVH
import com.wk.cashbook.trade.record.CashListItemVH
import com.wk.cashbook.trade.record.CashTotalItemVH
import com.wk.projects.common.communication.constant.BundleKey
import com.wk.projects.common.configuration.WkProjects
import com.wk.projects.common.constant.WkStringConstants.STR_POSITION_LOW
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.resource.WkContextCompat
import com.wk.projects.common.time.date.DayUtil
import com.wk.projects.common.time.date.week.WeekUtil
import com.wk.projects.common.ui.recycler.IRvClickListener
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
            val tvAllIncomeValue = rootView.findViewById<TextView>(R.id.tvAllIncomeValue)
            val tvAllPayValue = rootView.findViewById<TextView>(R.id.tvAllPayValue)
            return CashTotalItemVH(rootView, tvBillTotalDay, tvAllIncomeValue, tvAllPayValue)
        }

        if (TYPE_LIST_ITEM == viewType) {
            val rootView = layoutInflater.inflate(R.layout.cashbook_bill_list_item, parent, false)
            val ivTradeType = rootView.findViewById<ImageView>(R.id.ivTradeType)
            val tvTradeNote = rootView.findViewById<TextView>(R.id.tvTradeNote)
            val tvTradeAmount = rootView.findViewById<TextView>(R.id.tvTradeAmount)
            tvTradeAmount.setTextColor(WkContextCompat.getColor( R.color.color_grey_7E797B))
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
                            rvItemListener?.onItemClick(this@CashListAdapter,itemView,position)
                        }
                        itemView.setOnLongClickListener {
                            val bundle = Bundle()
                            bundle.putInt(STR_POSITION_LOW, getRealPosition(position))
                            bundle.putLong(TradeRecode.TRADE_RECODE_ID, tradeRecodeShowBean.mTradeRecodeId)
                            bundle.putString(BundleKey.LIST_ITEM_NAME, tradeRecodeShowBean.mShowText)
                            rvItemListener?.onItemLongClick(this@CashListAdapter,itemView,position)
                            return@setOnLongClickListener true
                        }
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

    fun getRealPosition(position: Int) = position

    override fun getItemCount() = mTradeRecords.size

    override fun getItemViewType(position: Int): Int {
        val tradeRecord = mTradeRecords[position]
        return if (tradeRecord is TradeRecodeShowBean) {
            TYPE_LIST_ITEM
        } else {
            TYPE_TOTAL_ITEM
        }
    }

    fun isTitle(position: Int) = getItemViewType(position) == TYPE_TOTAL_ITEM

    @SuppressLint("NotifyDataSetChanged")
    fun replaceList(tradeRecords: List<ITradeRecodeShowBean>?) {
        mTradeRecords.clear()
        if(tradeRecords!=null){
            mTradeRecords.addAll(tradeRecords)
        }

        notifyDataSetChanged()
    }

    fun getData(position: Int): ITradeRecodeShowBean {
        return mTradeRecords[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(tradeRecode: ITradeRecodeShowBean) {
        if (!mTradeRecords.contains(tradeRecode)) {
            mTradeRecords.add(tradeRecode)
            mTradeRecords.sortWith { o1, o2 ->
                o1.getTradeTime().compareTo(o2.getTradeTime()) * -1
            }
            notifyDataSetChanged()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun remove(position: Int) {
        mTradeRecords.remove(mTradeRecords[position])
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