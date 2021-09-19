package com.wk.cashbook.trade.info

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.wk.cashbook.R
import com.wk.cashbook.databinding.CashbookTradeRecordInfoActivityBinding
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.projects.common.BaseProjectsActivity
import com.wk.projects.common.constant.WkStringConstants
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.time.date.DateTime
import com.wk.projects.common.ui.TimePickerCreator
import com.wk.projects.common.ui.WkToast
import java.text.SimpleDateFormat

class TradeRecordInfoActivity : BaseProjectsActivity(), TradeInfoCategoryAdapter.ITradeInfoCategoryListener {

    private lateinit var mTradeRecordInfoPresent: TradeRecordInfoPresent


    /**保存按钮*/
    private lateinit var btTradeInfoSave: Button

    /**金额*/
    private lateinit var tvTradeInfoAmount: EditText

    /**备注*/
    private lateinit var etTradeInfoNote: EditText

    /**时间*/
    private lateinit var tvTradeInfoTime: TextView

    /**标签*/
    private lateinit var tvTradeInfoFlag: TextView

    /**账户*/
    private lateinit var tvTradeInfoAccount: TextView

    /**
     * 支出，收入，内部转账
     * */
    private val rvTradeInfoRootCategory by lazy {
        mBind.rvTradeInfoRootCategory
    }

    private val rvTradeInfoCategory by lazy {
        mBind.rvTradeInfoCategory
    }

    private val mBind by lazy {
        CashbookTradeRecordInfoActivityBinding.inflate(layoutInflater)
    }
    private val inflater by lazy {
        LayoutInflater.from(this)
    }

    private val mTradeInfoRootCategoryAdapter by lazy {
        TradeInfoRootCategoryAdapter(mTradeInfoCategoryListener = this)
    }
    private val mTradeInfoCategoryAdapter by lazy {
        TradeInfoCategoryAdapter(mTradeInfoCategoryListener = this)
    }
    private val rootCategoryLayoutManage by lazy {
        LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }

    private val categoryLayoutManger by lazy {
        GridLayoutManager(this, 4)
    }

    override fun initResLayId() = mBind.root

    override fun bindView(savedInstanceState: Bundle?, mBaseProjectsActivity: BaseProjectsActivity) {
        initView()
        initRootCategoryRv()
        initCategoryRv()
        var target = intent.getParcelableExtra<TradeRecode>(TradeRecode.TAG)
        var id = intent.getLongExtra("id",0)
        val isUpdate = target != null
        if (target == null) {
            target = TradeRecode(tradeTime = System.currentTimeMillis())
        }
        WkLog.d("target： $target")
        mTradeRecordInfoPresent = TradeRecordInfoPresent(this, target,id)
        mTradeRecordInfoPresent.isUpdate=isUpdate
        mTradeRecordInfoPresent.initRootCategoryAsync()
        btTradeInfoSave = mBind.btTradeInfoSave
        btTradeInfoSave.setOnClickListener(this)
        tvTradeInfoTime.setOnClickListener(this)
    }

    private fun initView() {
        btTradeInfoSave = mBind.btTradeInfoSave
        tvTradeInfoAmount = mBind.tvTradeInfoAmount
        etTradeInfoNote = mBind.etTradeInfoNote
        tvTradeInfoTime = mBind.tvTradeInfoTime
        tvTradeInfoFlag = mBind.tvTradeInfoFlag
        tvTradeInfoAccount = mBind.tvTradeInfoAccount
    }

    private fun initRootCategoryRv() {
        rvTradeInfoRootCategory.layoutManager = rootCategoryLayoutManage
        rvTradeInfoRootCategory.adapter = mTradeInfoRootCategoryAdapter
    }

    private fun initCategoryRv() {
        val footView = inflater.inflate(R.layout.common_only_text, null)
        mTradeInfoCategoryAdapter.addFootView(footView)
        rvTradeInfoCategory.layoutManager = categoryLayoutManger
        rvTradeInfoCategory.adapter = mTradeInfoCategoryAdapter

    }

    @MainThread
    fun setRootCategory(rootCategories: List<TradeCategory>) {
        mTradeInfoRootCategoryAdapter.replaceData(rootCategories)
        mTradeRecordInfoPresent.currentRootCategory = rootCategories[0]
    }

    fun setCategories(categories: List<TradeCategory>) {
        mTradeInfoCategoryAdapter.replaceData(categories)
    }

    fun addCategory(category: TradeCategory) {
        mTradeInfoCategoryAdapter.addCategory(category)
    }

    override fun itemClick(tradeInfoCategoryAdapter: TradeInfoCategoryAdapter, view: View, position: Int) {
        //类别
        if (tradeInfoCategoryAdapter == mTradeInfoCategoryAdapter) {
            //添加类别
            if (!tradeInfoCategoryAdapter.isItem(position)) {
                mTradeRecordInfoPresent.showAddCategoryDialog()
            } else {
                //选中类别
                mTradeRecordInfoPresent.setCategory(mTradeInfoCategoryAdapter.selectPosition(position))
            }
            return
        }

        // 根类别
        if (tradeInfoCategoryAdapter == mTradeInfoRootCategoryAdapter) {
            //选中类别
            mTradeInfoRootCategoryAdapter.selectPosition(position)
            mTradeRecordInfoPresent.currentRootCategory = mTradeInfoRootCategoryAdapter.getItem(position)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btTradeInfoSave -> {
                val amount = tvTradeInfoAmount.text.toString()
                val note = etTradeInfoNote.text.toString()
                val bundle = Bundle()
                bundle.putDouble(TradeRecode.AMOUNT, amount.toDouble())
                bundle.putString(TradeRecode.TRADE_NOTE, note)
                mTradeRecordInfoPresent.saveTradeRecode(bundle)
            }
            R.id.tvTradeInfoTime -> {
                TimePickerCreator.create(this, R.string.common_str_add, OnTimeSelectListener { date, _ ->
                    mTradeRecordInfoPresent.setTradeTime(date.time)
                })
            }
        }
    }

    fun setNote(note: String) {
        etTradeInfoNote.setText(note)
    }

    fun setAmount(amount: String) {
        tvTradeInfoAmount.setText(amount)
    }


    fun setTradeTime(time: Long) {
        tvTradeInfoTime.text = DateTime.getDateString(time)
    }

    fun setTradeFlag() {

    }

    fun setTradeAccount(account: Long) {

    }

    fun setTradeCategory(categoryId: Long) {
        mTradeInfoCategoryAdapter.setSelectTradeCategory(categoryId)
    }

    fun saveResult(tradeRecode: TradeRecode?) {
        if (null == tradeRecode) {
            WkToast.showToast("保存失败")
            return
        }
        val intent1 = Intent()
        intent1.putExtra(TradeRecode.TAG, tradeRecode)
        intent1.putExtra(WkStringConstants.STR_POSITION_LOW,
                intent.getIntExtra(WkStringConstants.STR_POSITION_LOW, -1))
        setResult(2, intent1)
        finish()
    }
}