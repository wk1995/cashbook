package com.wk.cashbook.trade.account

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.drawToBitmap
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.projects.common.BaseProjectsActivity
import com.wk.projects.common.communication.ActivityResultCode.ResultCode_UpdateAccountOrWalletActivity
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.helper.WkBitmapUtil
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.resource.WkContextCompat
import com.wk.projects.common.ui.WkCommonActionBar
import org.litepal.LitePal
import org.litepal.crud.LitePalSupport
import org.litepal.extension.runInTransaction
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


        const val DATA_TYPE = "data_type"


    }

    private lateinit var ivCratePic: ImageView
    private lateinit var wbCreateTitle: WkCommonActionBar
    private lateinit var etCreateCaseTime: EditText
    private lateinit var btnCreate: Button
    private lateinit var etCreateName: EditText
    private lateinit var etCreateNote: EditText
    private lateinit var etCreateAmount: TextView
    private lateinit var spCreateUnit: Spinner

    private var type: Int = TYPE_INVALID


    private var mLitePalSupport: LitePalSupport? = null

    override
    fun initResLayId() = R.layout.cashbook_account_create_account_or_wallet_activity

    override fun bindView(savedInstanceState: Bundle?, mBaseProjectsActivity: BaseProjectsActivity) {
        ivCratePic = findViewById(R.id.ivCratePic)
        ivCratePic.setImageResource(R.drawable.cashbook_account_type_crash)
        wbCreateTitle = findViewById(R.id.wbCreateTitle)
        wbCreateTitle.setMiddleViewTextColor(WkContextCompat.getColor(this, R.color.common_black_2B2A2A))
        etCreateCaseTime = findViewById(R.id.etCreateCaseTime)
        btnCreate = findViewById(R.id.btnCreate)
        etCreateName = findViewById(R.id.etCreateName)
        etCreateNote = findViewById(R.id.etCreateNote)
        etCreateAmount = findViewById(R.id.etCreateAmount)
        spCreateUnit = findViewById(R.id.spCreateUnit)
        initData()
        initListener()

    }

    private fun initListener() {
        btnCreate.setOnClickListener(this)
        ivCratePic.setOnClickListener(this)
    }

    fun initData() {
        type = intent.getIntExtra(DATA_TYPE, TYPE_INVALID)
        showWallet(isWallet())
        when (type) {
            TYPE_ACCOUNT -> {

                val id = intent.getLongExtra(TradeAccount.ACCOUNT_ID, TradeAccount.INVALID_ID)
                if (id <= TradeAccount.INVALID_ID) {
                    mLitePalSupport = TradeAccount()
                    wbCreateTitle.setMiddleViewText(R.string.cashbook_create_account)
                    return
                }
                wbCreateTitle.setMiddleViewText(R.string.cashbook_edit_account)
                Observable.create(Observable.OnSubscribe<TradeAccount> {
                    it.onNext(LitePal.find(TradeAccount::class.java, id,true))
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { tradeAccount ->
                            if (tradeAccount == null) {
                                WkLog.w("获取账户失败")
                                finish()
                            } else {
                                mLitePalSupport = tradeAccount
                                tradeAccount.apply {
                                    etCreateName.setText(accountName)
                                    etCreateNote.setText(note)
                                }
                            }
                        }

            }

            TYPE_WALLET -> {
                val id = intent.getLongExtra(AccountWallet.ACCOUNT_WALLET_ID, AccountWallet.INVALID_ID)
                if (id <= AccountWallet.INVALID_ID) {
                    mLitePalSupport = AccountWallet()
                    wbCreateTitle.setMiddleViewText(R.string.cashbook_create_wallet)
                    return
                }
                wbCreateTitle.setMiddleViewText(R.string.cashbook_edit_wallet)
                Observable.create(Observable.OnSubscribe<AccountWallet> {
                    it.onNext(LitePal.find(AccountWallet::class.java, id))
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { wallet ->
                            if (wallet == null) {
                                WkLog.w("获取钱包数据失败")
                                finish()
                            } else {
                                mLitePalSupport = wallet
                                wallet.apply {
                                    etCreateName.setText(accountName)
                                    etCreateNote.setText(note)
                                    etCreateAmount.text = amount.toString()
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
                if (isWallet()) {
                    createWallet()
                } else {
                    createAccount()
                }
            }
            R.id.ivCratePic -> {

            }
        }
    }

    private fun showWallet(isWallet: Boolean) {
        val visibility = if (isWallet) {
            View.VISIBLE
        } else {
            View.GONE
        }
        etCreateAmount.visibility = visibility
        spCreateUnit.visibility = visibility
        etCreateCaseTime.visibility = visibility
    }


    private fun createAccount() {
        val account = mLitePalSupport
        if (account !is TradeAccount) {
            return
        }
        account.accountName = etCreateName.text.toString()
        account.note = etCreateNote.text.toString()
        account.accountPic = WkBitmapUtil.getByteArrayByBitmap(ivCratePic.drawToBitmap())
        Observable.create(Observable.OnSubscribe<Boolean> {
            it.onNext(account.saveOrUpdate("id=?", account.baseObjId.toString()))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { success ->
                    if (success) {
                        setResult(ResultCode_UpdateAccountOrWalletActivity)
                        finish()
                    } else {
                        showToast("新增账户失败")
                    }
                }
    }


    private fun createWallet() {
        val accountId = intent.getLongExtra(AccountWallet.ACCOUNT_ID, AccountWallet.INVALID_ID)
        if (accountId < AccountWallet.INVALID_ID) {
            showToast("新增钱包失败")
            return
        }
        val wallet = mLitePalSupport
        if (wallet !is AccountWallet) {
            return
        }
        wallet.accountName = etCreateName.text.toString()
        wallet.note = etCreateNote.text.toString()
        wallet.amount = etCreateAmount.text.toString().toDouble()

        Observable.create(Observable.OnSubscribe<Boolean> {
            it.onNext(LitePal.runInTransaction {
                val save = wallet.saveOrUpdate("id=?", wallet.baseObjId.toString())
                if (!save) {
                    return@runInTransaction false
                }
                val account = LitePal.find(TradeAccount::class.java, accountId)
                account.addWallet(wallet)
                return@runInTransaction account.save()
            })
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { success ->
                    if (success) {
                        setResult(ResultCode_UpdateAccountOrWalletActivity)
                        finish()
                    } else {
                        showToast("新增钱包失败")
                    }
                }
    }
}