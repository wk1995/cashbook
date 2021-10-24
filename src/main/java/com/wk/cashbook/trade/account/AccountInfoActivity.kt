package com.wk.cashbook.trade.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.wk.cashbook.CashBookActivityResultCode.RESULT_CODE_ACCOUNT_INFO_ACTIVITY
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.projects.common.BaseProjectsActivity
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import com.wk.projects.common.constant.WkStringConstants.STR_POSITION_LOW
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.ui.WkToast
import org.litepal.LitePal
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

/**
 * 资金渠道详情
 * */
class AccountInfoActivity : BaseProjectsActivity() {
    private lateinit var viewTitleLeft1: View
    private lateinit var etAccountName: EditText
    private lateinit var etAccountMoney: EditText
    private lateinit var btnAccountSave: Button

    private var mCurrentTradeAccount: TradeAccount? = null
    private var mSubscriptions: CompositeSubscription? = null

    override fun initResLayId() = R.layout.cashbook_account_info_activity

    override fun bindView(savedInstanceState: Bundle?, mBaseProjectsActivity: BaseProjectsActivity) {
        initView()
        initListener()
        initData()
    }

    private fun initView() {
        viewTitleLeft1 = findViewById(R.id.viewTitleLeft1)
        etAccountName = findViewById(R.id.etAccountName)
        etAccountMoney = findViewById(R.id.etAccountMoney)
        btnAccountSave = findViewById(R.id.btnAccountSave)
    }

    private fun setName(name: String) {
        etAccountName.setText(name)
    }

    private fun setNote(note: String) {
    }

    private fun setMoney(amount: Double) {
        etAccountMoney.setText(amount.toString())
    }


    private fun initListener() {
        viewTitleLeft1.setOnClickListener(this)
        btnAccountSave.setOnClickListener(this)
    }

    private fun initData() {
        val id = intent.getLongExtra(TradeAccount.ACCOUNT_ID, TradeAccount.INVALID_ID)
        mSubscriptions = CompositeSubscription()
        WkLog.i("id: $id")
        if (id > TradeAccount.INVALID_ID) {
            mSubscriptions?.add(Observable.create(Observable.OnSubscribe<TradeAccount> {
                it.onNext(LitePal.find(TradeAccount::class.java, id))
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mCurrentTradeAccount = it
                        setName(mCurrentTradeAccount?.accountName ?: WkStringConstants.STR_EMPTY)
                        setMoney(mCurrentTradeAccount?.amount ?: NumberConstants.number_double_zero)
                    }
            )
        } else {
            mCurrentTradeAccount = TradeAccount(WkStringConstants.STR_EMPTY)
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.viewTitleLeft1 -> {
                finish()
            }
            R.id.btnAccountSave -> {
                mCurrentTradeAccount?.apply {
                    accountName = etAccountName.text.toString()
                    amount = try {
                        etAccountMoney.text.toString().toDouble()
                    } catch (e: NumberFormatException) {
                        NumberConstants.number_double_zero
                    }
                    mSubscriptions?.add(
                            Observable.create(Observable.OnSubscribe<Boolean> {
                                it.onNext(saveOrUpdate("id = ?",baseObjId.toString()))
                            }).subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        if (it) {
                                            WkToast.showToast("保存成功")
                                            val resultIntent = Intent()
                                            resultIntent.putExtra(TradeAccount.ACCOUNT_ID, baseObjId)
                                            resultIntent.putExtra(TradeAccount.ACCOUNT_NAME, accountName)
                                            resultIntent.putExtra(TradeAccount.NOTE, note)
                                            resultIntent.putExtra(TradeAccount.UNIT, unit)
                                            resultIntent.putExtra(TradeAccount.AMOUNT, amount)
                                            resultIntent.putExtra(STR_POSITION_LOW,
                                                    intent.getIntExtra(STR_POSITION_LOW, NumberConstants.number_int_one_Negative))
                                            setResult(RESULT_CODE_ACCOUNT_INFO_ACTIVITY, resultIntent)
                                            finish()
                                        } else {
                                            WkToast.showToast("保存失败")
                                        }
                                    }
                    )
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions?.clear()
    }
}