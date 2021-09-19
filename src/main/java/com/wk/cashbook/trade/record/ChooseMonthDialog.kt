package com.wk.cashbook.trade.record

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.wk.cashbook.R
import com.wk.projects.common.BaseSimpleDialog
import com.wk.projects.common.constant.NumberConstants
import java.util.*

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/04/20
 * desc         :
 */


class ChooseMonthDialog:BaseSimpleDialog() {

    private lateinit var btnAddYear:Button
    private lateinit var btnAddMonth:Button
    private lateinit var btnReduceYear:Button
    private lateinit var btnReduceMonth:Button
    private lateinit var tvYearValue:TextView
    private lateinit var tvMonthValue:TextView

    companion object {
        fun create(bundle: Bundle? = null, simpleOnlyEtDialogListener: SimpleOnlyEtDialogListener?=null): ChooseMonthDialog {
            val mChooseMonthDialog = ChooseMonthDialog()
            mChooseMonthDialog.arguments = bundle
            mChooseMonthDialog.simpleOnlyEtDialogListener = simpleOnlyEtDialogListener
            return mChooseMonthDialog
        }
    }

    override fun initVSView(vsView: View) {
        btnAddYear=vsView.findViewById(R.id.btnAddYear)
        btnAddMonth=vsView.findViewById(R.id.btnAddMonth)
        btnReduceYear=vsView.findViewById(R.id.btnReduceYear)
        btnReduceMonth=vsView.findViewById(R.id.btnReduceMonth)
        tvYearValue=vsView.findViewById(R.id.tvYearValue)
        tvMonthValue=vsView.findViewById(R.id.tvMonthValue)

        btnAddYear.setOnClickListener(this)
        btnAddMonth.setOnClickListener(this)
        btnReduceYear.setOnClickListener(this)
        btnReduceMonth.setOnClickListener(this)

        val local=System.currentTimeMillis()
        val calendar=Calendar.getInstance()
        calendar.timeInMillis=local
        tvYearValue.text=calendar.get(Calendar.YEAR).toString()
        tvMonthValue.text=(calendar.get(Calendar.MONTH)+1).toString()

    }


    private fun continueAdd(string: String,step:Int=NumberConstants.number_int_one):String{
        val intString =string.toInt()
        return (intString+step).toString()
    }

    private fun continueReduce(string: String,step:Int=NumberConstants.number_int_one):String{
        return continueAdd(string,NumberConstants.number_int_one_Negative)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when(v?.id){
            R.id.btnAddYear->{
                tvYearValue.text=continueAdd(tvYearValue.text.toString())
            }
            R.id.btnAddMonth->{
                tvMonthValue.text=continueAdd(tvMonthValue.text.toString())
            }
            R.id.btnReduceYear->{
                tvYearValue.text=continueReduce(tvYearValue.text.toString())
            }
            R.id.btnReduceMonth->{
                tvMonthValue.text=continueReduce(tvMonthValue.text.toString())
            }
        }

    }


    override fun getOkBundle(): Bundle? {
        val year=tvYearValue.text.toString()
        val month=tvMonthValue.text.toString()
        val bundle=Bundle(2)
        bundle.putString("year",year)
        bundle.putString("month",month)
        return bundle
    }

    override fun getBackgroundDrawable()=ColorDrawable(Color.WHITE)

    override fun initViewSubLayout()= R.layout.cashbook_bill_choose_month_dialog

}