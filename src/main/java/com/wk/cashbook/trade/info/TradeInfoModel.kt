package com.wk.cashbook.trade.info

import android.os.Bundle
import com.wk.cashbook.CashBookConfig
import com.wk.cashbook.CashBookConstants
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeCategory.Companion.INVALID_ID
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.cashbook.trade.record.bean.TradeRecodeShowBean
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.log.WkLog

/**
 * @author      : wangkang
 * email        : 12264126603@qq.com
 * create date  : 2021/03/23
 * desc         :
 */


class TradeInfoModel(private val mTradeRecordInfoPresent: TradeRecordInfoPresent) {
    var mCurrentTradeRecode: TradeRecode = TradeRecode()

    /**
     * 选择的根类别
     * */
    private var mSelectRootCategoryId = CashBookConfig.getDefaultCategoryId()

    var mSelectRootCategoryName: String = ""

    // 根类别，出账账户，入账账户，金额

    var originRootCategoryId: Long = INVALID_ID

    var originAccountId: Long = AccountWallet.INVALID_ID

    var originReceiveId: Long = AccountWallet.INVALID_ID

    var originAmount: Double = NumberConstants.number_double_zero


    fun initData() {
        //没有类别，默认是支出
        val categoryId = mCurrentTradeRecode.categoryId
        WkLog.i("initData categoryId: $categoryId")
        if (categoryId == INVALID_ID) {
            mCurrentTradeRecode.categoryId = CashBookConfig.getDefaultCategoryId()
            mSelectRootCategoryName = TradeCategory.DEFAULT_ROOT_CATEGORY_PAY
        }
        mCurrentTradeRecode.apply {
            originAmount = amount
            originReceiveId = receiveAccountId
            originAccountId = accountId
            mTradeRecordInfoPresent.showAmount(amount.toString())
            mTradeRecordInfoPresent.showNote(tradeNote)
            mTradeRecordInfoPresent.showTradeFlag()
            mTradeRecordInfoPresent.showTradeTime(tradeTime)
            mTradeRecordInfoPresent.showTradeAccount(accountId)
        }
    }

    fun setCategoryId(categoryId: Long) {
        mCurrentTradeRecode.categoryId = categoryId
    }

    fun setTradeTime(tradeTime:Long){
        mCurrentTradeRecode.tradeTime=tradeTime
    }

    fun setRootCategory(rootCategory: TradeCategory,isSelect:Boolean=false) {
        val rootCategoryId = rootCategory.baseObjId
        if (isSelect && mSelectRootCategoryId != rootCategoryId) {
            mCurrentTradeRecode.categoryId = rootCategoryId
        }
        mSelectRootCategoryId = rootCategoryId
        mSelectRootCategoryName = rootCategory.categoryName

    }

    fun getRootCategoryId() = mSelectRootCategoryId

    fun showTradeCategory(categoryId: Long = mCurrentTradeRecode.categoryId) {
        WkLog.i("showTradeCategory:  $categoryId")
        mTradeRecordInfoPresent.showTradeCategory(categoryId)
    }

    fun initRootCategory(categoryId: Long = mCurrentTradeRecode.categoryId) {
        mTradeRecordInfoPresent.initRootCategory(categoryId)
    }


    fun setAmount(amount: Double?) {
        mCurrentTradeRecode.amount = amount ?: return
    }

    fun setNote(note: String?) {
        mCurrentTradeRecode.tradeNote = note ?: return
    }

    fun setAccount(accountId: Long) {
        mCurrentTradeRecode.accountId = accountId
    }

    fun setReceiveAccountId(accountId: Long) {
        mCurrentTradeRecode.receiveAccountId = accountId
    }

    fun saveOrUpdate() =
        mCurrentTradeRecode.saveOrUpdate("id = ?", mCurrentTradeRecode.baseObjId.toString())

    fun getAccountId() = mCurrentTradeRecode.accountId

    fun getReceiveAccountId() = mCurrentTradeRecode.receiveAccountId

    fun getMoney() = mCurrentTradeRecode.amount

    fun getTradeShowBeanInfo(): Bundle {
        val bundle = Bundle()
        bundle.putLong(TradeRecodeShowBean.TRADE_RECODE_ID, mCurrentTradeRecode.baseObjId)
        bundle.putLong(TradeRecodeShowBean.TRADE_TIME, mCurrentTradeRecode.tradeTime)
        bundle.putDouble(TradeRecodeShowBean.AMOUNT, mCurrentTradeRecode.amount)
        bundle.putString(TradeRecodeShowBean.SHOW_NAME, mCurrentTradeRecode.tradeNote)
        bundle.putInt(TradeRecodeShowBean.TRADE_TYPE, when {
            TradeCategory.isPay(mSelectRootCategoryName) -> {
                CashBookConstants.TYPE_ROOT_CATEGORY_PAY
            }
            TradeCategory.isComeIn(mSelectRootCategoryName) -> {
                CashBookConstants.TYPE_ROOT_CATEGORY_INCOME
            }
            TradeCategory.isInternalTransfer(mSelectRootCategoryName) -> {
                CashBookConstants.TYPE_ROOT_CATEGORY_INTERNAL_TRANSFER
            }
            else -> {
                CashBookConstants.TYPE_ROOT_CATEGORY_UNKNOWN
            }
        })
        return bundle
    }

}