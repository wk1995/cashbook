package com.wk.cashbook.trade.info

import android.content.Intent
import android.os.Bundle
import com.wk.cashbook.CashBookConfig
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeCategory.Companion.INVALID_ID
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.cashbook.trade.data.TradeRecode.CREATOR.TRADE_RECODE_ID
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import com.wk.projects.common.log.WkLog

/**
 * @author      : wangkang
 * email        : 12264126603@qq.com
 * create date  : 2021/03/23
 * desc         :
 */


class TradeInfoModel(private val intent: Intent,
                     private val mTradeRecordInfoPresent: TradeRecordInfoPresent) {


    private val mCurrentTradeRecode by lazy {
        val id = intent.getLongExtra(TRADE_RECODE_ID, NumberConstants.number_long_zero)
        val mCurrentTradeRecode = intent.getParcelableExtra(TradeRecode.TAG)
                ?: TradeRecode(System.currentTimeMillis())
        mCurrentTradeRecode.assignBaseObjId(id)
        mCurrentTradeRecode
    }

    /**
     * 选择的根类别
     * */
    private var mSelectRootCategoryId=CashBookConfig.getDefaultCategoryId()

    // 根类别，出账账户，入账账户，金额

    var originRootCategoryId:Long=INVALID_ID

    var originAccountId:Long=TradeAccount.INVALID_ID

     var originReceiveId:Long=TradeAccount.INVALID_ID

    var originAmount:Double=NumberConstants.number_double_zero


    fun initData() {
        //没有类别，默认是支出
        val categoryId = mCurrentTradeRecode.categoryId
        WkLog.i("initData categoryId: $categoryId")
        if (categoryId == INVALID_ID) {
            mCurrentTradeRecode.categoryId = CashBookConfig.getDefaultCategoryId()
        }
        mCurrentTradeRecode.apply {
            originAmount=amount
            originReceiveId=receiveAccountId
            originAccountId=accountId
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

    fun setRootCategory(rootCategory: TradeCategory) {
        mSelectRootCategoryId = rootCategory.baseObjId
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

    fun setAccount(accountId:Long){
        mCurrentTradeRecode.accountId=accountId
    }

    fun setReceiveAccountId(accountId:Long){
        mCurrentTradeRecode.receiveAccountId=accountId
    }

    fun saveOrUpdate() = mCurrentTradeRecode.saveOrUpdate("id = ?",mCurrentTradeRecode.baseObjId.toString() )

    fun getAccountId()=mCurrentTradeRecode.accountId

    fun getReceiveAccountId()=mCurrentTradeRecode.receiveAccountId

    fun getMoney()=mCurrentTradeRecode.amount

    fun getBundle(): Bundle {
        val bundle = Bundle()
        bundle.putParcelable(TradeRecode.TAG, mCurrentTradeRecode)
        bundle.putLong(TRADE_RECODE_ID, mCurrentTradeRecode.baseObjId)
        bundle.putInt(WkStringConstants.STR_POSITION_LOW,
                intent.getIntExtra(WkStringConstants.STR_POSITION_LOW, -1))
        return bundle
    }

}