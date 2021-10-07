package com.wk.cashbook.trade.info

import android.content.Intent
import android.os.Bundle
import com.wk.cashbook.CashBookConfig
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeCategory.Companion.INVALID_ID
import com.wk.cashbook.trade.data.TradeRecode
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
        intent.getParcelableExtra(TradeRecode.TAG) ?: TradeRecode(System.currentTimeMillis())
    }

    /**
     * 选择的根类别
     * */
    var mSelectRootCategory: TradeCategory? = null


    /**当前的交易类型*/
    var currentTradeCategory: TradeCategory? = null


    fun initData() {
        //没有类别，默认是支出
        val categoryId=mCurrentTradeRecode.categoryId
        WkLog.i("initData categoryId: $categoryId")
        if(categoryId==INVALID_ID){
            mCurrentTradeRecode.categoryId=CashBookConfig.getDefaultCategoryId()
        }
        mCurrentTradeRecode.apply {
            mTradeRecordInfoPresent.showAmount(amount.toString())
            mTradeRecordInfoPresent.showNote(tradeNote)
            mTradeRecordInfoPresent.showTradeFlag()
            mTradeRecordInfoPresent.showTradeTime(tradeTime)
            mTradeRecordInfoPresent.showTradeAccount(accountId)
        }
    }

    fun setCategoryId(categoryId:Long){
        mCurrentTradeRecode.categoryId=categoryId
    }

    fun setRootCategory(rootCategory:TradeCategory){
        mSelectRootCategory=rootCategory
    }

    fun getRootCategory()=mSelectRootCategory

    fun showTradeCategory(categoryId:Long=mCurrentTradeRecode.categoryId){
        WkLog.i("showTradeCategory:  $categoryId")
        mTradeRecordInfoPresent.showTradeCategory(categoryId)
    }

    fun showRootCategory(categoryId:Long=mCurrentTradeRecode.categoryId){
        mTradeRecordInfoPresent.showRootCategory(categoryId)
    }


    fun setAmount(amount:Double?){
        mCurrentTradeRecode.amount=amount?:return
    }

    fun setNote(note:String?){
        mCurrentTradeRecode.tradeNote=note?:return
    }

    fun saveOrUpdate()=mCurrentTradeRecode.saveOrUpdate()


    fun getBundle():Bundle{
        val bundle= Bundle()
        bundle.putParcelable(TradeRecode.TAG, mCurrentTradeRecode)
        return bundle
    }

}