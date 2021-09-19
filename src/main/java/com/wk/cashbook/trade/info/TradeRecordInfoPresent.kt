package com.wk.cashbook.trade.info

import android.os.Bundle
import android.widget.Button
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.projects.common.BaseSimpleDialog
import com.wk.projects.common.SimpleOnlyEtDialog
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.ui.WkToast
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/03/16
 * desc         :
 * @param currentTradeRecode  当前的交易记录
 */

class TradeRecordInfoPresent(private val mTradeRecordInfoActivity: TradeRecordInfoActivity, private val currentTradeRecode: TradeRecode,val id:Long)
    : BaseSimpleDialog.SimpleOnlyEtDialogListener {

    var isUpdate=false

    /**当前的根类别*/
    var currentRootCategory: TradeCategory? = null
        set(value) {
            field = value
            initCategoryAsync(field)
        }


    init {
        currentTradeRecode.apply {
            mTradeRecordInfoActivity.setAmount(amount.toString())
            mTradeRecordInfoActivity.setNote(tradeNote)
            mTradeRecordInfoActivity.setTradeFlag()
            mTradeRecordInfoActivity.setTradeTime(tradeTime)
            mTradeRecordInfoActivity.setTradeAccount(accountId)
            mTradeRecordInfoActivity.setTradeCategory(categoryId)
        }

    }


    fun setTradeTime(time: Long) {
        WkLog.d("修改之前： $currentTradeRecode")
        currentTradeRecode.tradeTime = time
        WkLog.d("修改之后： $currentTradeRecode")
        mTradeRecordInfoActivity.setTradeTime(time)
    }

    /**
     * 获取最顶的类别
     * 支出，收入，内部转账
     * */

    fun initRootCategoryAsync() {
        Observable.create(Observable.OnSubscribe<List<TradeCategory>> { t ->
            t?.onNext(TradeCategory.getRootCategory())
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    WkLog.d(it.toString())
                    mTradeRecordInfoActivity.setRootCategory(it)
                }
    }


    private fun initCategoryAsync(parent: TradeCategory?) {
        Observable.create(Observable.OnSubscribe<List<TradeCategory>> { t ->
            t?.onNext(TradeCategory.getSubCategory(parent))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    WkLog.d(it.toString())
                    mTradeRecordInfoActivity.setCategories(it)
                }
    }

    fun setCategory(category: TradeCategory) {
        currentTradeRecode.categoryId = category.baseObjId
    }

    /**添加类别的弹窗*/
    fun showAddCategoryDialog() {
        val simpleOnlyEtDialog = SimpleOnlyEtDialog.create(simpleOnlyEtDialogListener = this)
        simpleOnlyEtDialog.show(mTradeRecordInfoActivity)
    }


    /**保存新类别*/
    private fun saveNewCategory(categoryName: String) {
        val newCategory = TradeCategory(categoryName, System.currentTimeMillis(),
                currentRootCategory?.baseObjId ?: NumberConstants.number_long_one_Negative)
        Observable.create(Observable.OnSubscribe<Boolean> { t ->
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
    }

    override fun ok(bundle: Bundle?): Boolean {
        val textString = bundle?.getString("textString")
        if (textString.isNullOrBlank()) {
            WkToast.showToast("不能为空")
            return true
        }
        saveNewCategory(textString)
        return false
    }

    override fun cancel(bundle: Bundle?): Boolean {
        return false
    }

    /**保存 \ 更新*/
    fun saveTradeRecode(bundle: Bundle? = null) {
        Observable.create(Observable.OnSubscribe<Boolean> { t ->
            currentTradeRecode.apply {
                val originTradeNote = currentTradeRecode.tradeNote
                val tradeNote = bundle?.getString(TradeRecode.TRADE_NOTE,
                        originTradeNote) ?: originTradeNote
                this.tradeNote = tradeNote
                val originAmount = currentTradeRecode.amount
                val tradeAmount = bundle?.getDouble(TradeRecode.AMOUNT,
                        originAmount) ?: originAmount
                this.amount = tradeAmount
            }
//            WkLog.d("保存的： $currentTradeRecode")
            val result =if(id==0L){
                currentTradeRecode.save()
            }else{
                currentTradeRecode.update(id)>0
            }
            t?.onNext(result)
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mTradeRecordInfoActivity.saveResult(if (it) {
                        currentTradeRecode
                    } else {
                        null
                    })
                }

    }
}