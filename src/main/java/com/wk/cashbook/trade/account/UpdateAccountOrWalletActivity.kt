package com.wk.cashbook.trade.account

import android.os.Bundle
import android.view.View
import android.widget.*
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.projects.common.BaseProjectsActivity
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.ui.WkCommonActionBar
import org.litepal.LitePal
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/19
 *      desc   : 创建/更新 账户，钱包数据页面
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class UpdateAccountOrWalletActivity : BaseProjectsActivity() {

    companion object {

        const val TYPE_INVALID = NumberConstants.number_int_one_Negative
        const val TYPE_ACCOUNT = NumberConstants.number_int_zero
        const val TYPE_WALLET = NumberConstants.number_int_one


        const val DATA_ID = "data_id"
        const val DATA_TYPE = "data_type"


    }

    private lateinit var ivCratePic: ImageView
    private lateinit var wbCreateTitle: WkCommonActionBar
    private lateinit var etCreateCaseTime: EditText
    private lateinit var btnCreate: Button
    private lateinit var etCreateName: EditText
    private lateinit var etCreateNote: EditText
    private lateinit var tvCreateAmount: TextView
    private lateinit var spCreateUnit: Spinner

    private var type: Int = TYPE_INVALID


    override
    fun initResLayId() = R.layout.cashbook_account_create_account_or_wallet_activity

    override fun bindView(savedInstanceState: Bundle?, mBaseProjectsActivity: BaseProjectsActivity) {
        ivCratePic = findViewById(R.id.ivCratePic)
        ivCratePic.setImageResource(R.drawable.cashbook_account_type_crash)
        wbCreateTitle = findViewById(R.id.wbCreateTitle)
        etCreateCaseTime = findViewById(R.id.etCreateCaseTime)
        btnCreate = findViewById(R.id.btnCreate)
        etCreateName = findViewById(R.id.etCreateName)
        etCreateNote = findViewById(R.id.etCreateNote)
        tvCreateAmount = findViewById(R.id.tvCreateAmount)
        spCreateUnit = findViewById(R.id.spCreateUnit)
        initData()
    }

    fun initData() {
        type = intent.getIntExtra(DATA_TYPE, TYPE_INVALID)
        showWallet(isWallet())
        val id = intent.getLongExtra(DATA_ID, NumberConstants.number_long_one_Negative)
        if(id<=0){
            return
        }
        when (type) {
            TYPE_ACCOUNT -> {
                Observable.create(Observable.OnSubscribe<TradeAccount> {
                    it.onNext(LitePal.find(TradeAccount::class.java, id))
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {tradeAccount->
                            if(tradeAccount==null){
                                finish()
                            }else{
                                tradeAccount.apply {
                                    etCreateName.setText(accountName)
                                    etCreateNote.setText(note)
                                }
                            }
                        }

            }

            TYPE_WALLET -> {
                Observable.create(Observable.OnSubscribe<AccountWallet> {
                    it.onNext(LitePal.find(AccountWallet::class.java, id))
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {wallet->
                            if(wallet==null){
                                finish()
                            }else{
                                wallet.apply {
                                    etCreateName.setText(accountName)
                                    etCreateNote.setText(note)
                                }
                            }
                        }
            }
            else -> {
                finish()
            }
        }

    }


    private fun isWallet() = type == TYPE_WALLET


    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.btnCreate -> {

            }
        }
    }

    fun showWallet(isWallet: Boolean) {
        val visibility = if (isWallet) {
            View.VISIBLE
        } else {
            View.GONE
        }
        tvCreateAmount.visibility = visibility
        spCreateUnit.visibility = visibility
        etCreateCaseTime.visibility = visibility
    }


    private fun createAccount() {

    }

    private fun createWallet() {

    }
}