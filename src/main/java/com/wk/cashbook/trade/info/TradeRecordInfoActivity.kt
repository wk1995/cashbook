package com.wk.cashbook.trade.info

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.databinding.CashbookTradeRecordInfoActivityBinding
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.cashbook.trade.info.account.ChooseAccountDialog
import com.wk.projects.common.BaseProjectsActivity
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.time.date.DateTime
import com.wk.projects.common.ui.TimePickerCreator
import com.wk.projects.common.ui.WkToast

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

    /**账户名称*/
    private lateinit var tvTradeInfoToAccount: TextView

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

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        supportActionBar?.hide()
        //键盘不会顶按钮上去，即键盘会覆盖在页面上
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun initResLayId() = mBind.root

    override fun bindView(savedInstanceState: Bundle?, mBaseProjectsActivity: BaseProjectsActivity) {
        initView()
        initRootCategoryRv()
        initCategoryRv()
        initListener()
        mTradeRecordInfoPresent = TradeRecordInfoPresent(this, intent)
        mTradeRecordInfoPresent.initData()
    }

    private fun initListener() {
        btTradeInfoSave.setOnClickListener(this)
        tvTradeInfoTime.setOnClickListener(this)
        tvTradeInfoAccount.setOnClickListener(this)
        tvTradeInfoToAccount.setOnClickListener(this)
    }

    private fun initView() {
        btTradeInfoSave = mBind.btTradeInfoSave
        tvTradeInfoAmount = mBind.tvTradeInfoAmount
        etTradeInfoNote = mBind.etTradeInfoNote
        tvTradeInfoTime = mBind.tvTradeInfoTime
        tvTradeInfoFlag = mBind.tvTradeInfoFlag
        tvTradeInfoAccount = mBind.tvTradeInfoAccount
        tvTradeInfoToAccount = mBind.tvTradeInfoToAccount
        tvTradeInfoToAccount.visibility = View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        mTradeRecordInfoPresent.onDestroy()
    }

    /**
     * 内部转账view
     * @param isInternalTransfer 是否时内部转账，true 是
     * */
    fun initInternalTransferView(isInternalTransfer: Boolean) {
        tvTradeInfoToAccount.visibility = if (isInternalTransfer) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    /**
     * 根类别相关控件
     * */
    private fun initRootCategoryRv() {
        rvTradeInfoRootCategory.layoutManager = rootCategoryLayoutManage
        rvTradeInfoRootCategory.adapter = mTradeInfoRootCategoryAdapter
    }

    /**类别选择控件*/
    private fun initCategoryRv() {
        rvTradeInfoCategory.layoutManager = categoryLayoutManger
        rvTradeInfoCategory.adapter = mTradeInfoCategoryAdapter
        val footView = inflater.inflate(R.layout.common_only_text, rvTradeInfoCategory, false)
        mTradeInfoCategoryAdapter.addFootView(footView)
    }

    /**
     * 设置根类别列表
     * */
    @MainThread
    fun setRootCategories(rootCategories: List<TradeCategory>) {
        mTradeInfoRootCategoryAdapter.replaceData(rootCategories)
    }

    /**
     * 设置选中的根类别
     * */
    fun setRootCategory(selectRoot: TradeCategory) {
        WkLog.i("setRootCategory")
        mTradeInfoRootCategoryAdapter.setSelectTradeCategory(selectRoot.baseObjId)
        mTradeInfoCategoryAdapter.clear()
        mTradeRecordInfoPresent.initCategoryAsync(selectRoot)
    }

    /**
     * 设置类别列表
     * */
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
            if (mTradeInfoRootCategoryAdapter.getSelectPosition() == position) {
                return
            }
            //选中类别
            mTradeInfoRootCategoryAdapter.selectPosition(position)
            mTradeRecordInfoPresent.setSelectRootCategory(mTradeInfoRootCategoryAdapter.getItem(position))
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
                bundle.putString(TradeRecode.TRADE_TIME, note)
                mTradeRecordInfoPresent.saveTradeRecode(bundle)
            }
            R.id.tvTradeInfoTime -> {
                TimePickerCreator.create(this, R.string.common_str_add) { date, _ ->
                    mTradeRecordInfoPresent.showTradeTime(DateTime.getDayStart(date.time))
                }
            }
            R.id.tvTradeInfoAccount -> {
                val bundle=Bundle()
                ChooseAccountDialog.create(bundle, mTradeRecordInfoPresent).show(this)
            }
            R.id.tvTradeInfoToAccount -> {
                val bundle=Bundle()
                bundle.putInt("accountType",1)
                ChooseAccountDialog.create(bundle, mTradeRecordInfoPresent).show(this)
            }
        }
    }

    fun showNote(note: String) {
        etTradeInfoNote.setText(note)
    }

    fun showAmount(amount: String) {
        tvTradeInfoAmount.setText(amount)
    }

    fun showTradeTime(timeString: String) {
        tvTradeInfoTime.text = timeString
    }

    fun showTradeFlag() {

    }

    fun showTradeAccount(accountName: String?) {
        tvTradeInfoAccount.text = accountName
    }

    fun showTradeToAccount(accountName: String?) {
        tvTradeInfoToAccount.text = accountName
    }


    /**
     * 设置当前的类别
     * */
    fun setTradeCategory(categoryId: Long) {
        mTradeInfoCategoryAdapter.setSelectTradeCategory(categoryId)
    }

    fun goToCashBookList(bundle: Bundle?) {
        if (null == bundle) {
            WkToast.showToast("保存失败")
            return
        }
        val intent1 = Intent()
        intent1.putExtras(bundle)
        setResult(2, intent1)
        finish()
    }
}