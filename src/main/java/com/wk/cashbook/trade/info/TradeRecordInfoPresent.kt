package com.wk.cashbook.trade.info

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.projects.common.BaseSimpleDialog
import com.wk.projects.common.SimpleOnlyEtDialog
import com.wk.projects.common.configuration.WkConfiguration
import com.wk.projects.common.configuration.WkProjects
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.time.date.DateTime
import com.wk.projects.common.ui.WkToast
import org.litepal.LitePal
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author      :wangkang
 *
 * email        :shenlong.wang@tuya.com
 *
 * create date  : 2021/03/16
 *
 * desc         :
 */

class TradeRecordInfoPresent(private val mTradeRecordInfoActivity: TradeRecordInfoActivity,
                             private val intent: Intent)
    : BaseSimpleDialog.SimpleOnlyEtDialogListener {
    private val mSubscriptions by lazy { CompositeSubscription() }

    private val mTradeInfoModel by lazy {
        TradeInfoModel(intent, this)
    }

    fun initData() {
        initRootCategoryAsync()
        mTradeInfoModel.initData()
    }

    fun onDestroy() {
        mSubscriptions.clear()
    }

    /**显示备注*/
    fun showNote(note: String) {
        mTradeRecordInfoActivity.showNote(note)
    }

    /**显示金额*/
    fun showAmount(amount: String) {
        mTradeRecordInfoActivity.showAmount(amount)
    }

    /**显示交易时间*/
    fun showTradeTime(time: Long) {
        mTradeRecordInfoActivity.showTradeTime(
                DateTime.getDateString(time, SimpleDateFormat("MM-dd", Locale.getDefault()))
        )
    }


    /**显示标签*/
    fun showTradeFlag() {

    }

    /**
     * 显示当前的类别
     * */
    fun showTradeCategory(categoryId: Long) {
        mTradeRecordInfoActivity.setTradeCategory(categoryId)
    }

    /**
     * 显示根类别
     * */
    fun showRootCategory(categoryId: Long) {
        WkLog.i("showRootCategory")
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<TradeCategory> {
            it.onNext(LitePal.find(TradeCategory::class.java, categoryId))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val parentId = it.parentId
                    //表示还有父类
                    if (parentId != TradeCategory.INVALID_ID) {
                        showRootCategory(parentId)
                    } else {//表示已经是根类了
                        WkLog.i("showRootCategory:  ${it.baseObjId}")
                        mTradeInfoModel.setRootCategory(it)
                        mTradeRecordInfoActivity.setRootCategory(it)
                    }
                }
        )

    }


    /**
     * 设置账号
     * */
    fun showTradeAccount(accountId: Long) {
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<TradeAccount> { t ->
            t?.onNext(
                    LitePal.find(TradeAccount::class.java, accountId)
            )
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val accountName = it?.accountName
                    mTradeRecordInfoActivity.showTradeAccount(
                            if (TextUtils.isEmpty(accountName)) {
                                WkProjects.getApplication().getString(R.string.cashbook_no_select_account)
                            } else {
                                accountName
                            })
                }
        )
    }


    /**
     * 获取最顶的类别
     * 支出，收入，内部转账
     * */
    private fun initRootCategoryAsync() {
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<List<TradeCategory>> { t ->
            t?.onNext(TradeCategory.getRootCategory())
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mTradeRecordInfoActivity.setRootCategories(it)
                    mTradeInfoModel.showRootCategory()
                }
        )
    }


    /**
     * 显示当前的类别
     * @param parent 当前根类别
     * */
    fun initCategoryAsync(parent: TradeCategory?) {
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<List<TradeCategory>> { t ->
            t?.onNext(TradeCategory.getSubCategory(parent))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    WkLog.d(it.toString())
                    mTradeRecordInfoActivity.setCategories(it)
                    mTradeInfoModel.showTradeCategory()
                }
        )
    }

    /**
     * 设置当前交易的类别
     * */
    fun setCategory(category: TradeCategory?) {
        val selectCategoryId = category?.baseObjId ?: return
        mTradeInfoModel.setCategoryId(selectCategoryId)
        mTradeRecordInfoActivity.setTradeCategory(selectCategoryId)
    }

    fun setSelectRootCategory(category: TradeCategory?) {
        mTradeRecordInfoActivity.setRootCategory(category ?: return)
        mTradeInfoModel.setRootCategory(category)
    }

    /**添加类别的弹窗*/
    fun showAddCategoryDialog() {
        val simpleOnlyEtDialog = SimpleOnlyEtDialog.create(simpleOnlyEtDialogListener = this)
        simpleOnlyEtDialog.show(mTradeRecordInfoActivity)
    }


    /**创建新类别*/
    private fun createNewCategory(categoryName: String) {
        val newCategory = TradeCategory(categoryName, System.currentTimeMillis(),
                mTradeInfoModel.getRootCategory()?.baseObjId
                        ?: NumberConstants.number_long_one_Negative)
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<Boolean> { t ->
            t?.onNext(newCategory.save())
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) {
                        mTradeRecordInfoActivity.addCategory(newCategory)
                    } else {
                        WkToast.showToast("失败")
                    }
                }
        )
    }

    override fun ok(bundle: Bundle?): Boolean {
        val textString = bundle?.getString("textString")
        if (textString.isNullOrBlank()) {
            WkToast.showToast("不能为空")
            return true
        }
        createNewCategory(textString)
        return false
    }

    override fun cancel(bundle: Bundle?): Boolean {
        return false
    }

    /**保存 \ 更新*/
    fun saveTradeRecode(bundle: Bundle? = null) {
        val tradeNote = bundle?.getString(TradeRecode.TRADE_NOTE)
        val tradeAmount = bundle?.getDouble(TradeRecode.AMOUNT)
        mTradeInfoModel.setAmount(tradeAmount)
        mTradeInfoModel.setNote(tradeNote)
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<Boolean> { t ->
            t?.onNext(mTradeInfoModel.saveOrUpdate())
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mTradeRecordInfoActivity.saveResult(if (it) {
                        mTradeInfoModel.getBundle()
                    } else {
                        null
                    })

                }
        )
    }
}