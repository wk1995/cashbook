package com.wk.cashbook.account.info

import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import org.litepal.LitePal
import org.litepal.extension.runInTransaction

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/15
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class AccountInfoModel {

    var mCurrentTradeAccount:TradeAccount=TradeAccount()


    fun isValidAccount()=mCurrentTradeAccount.baseObjId > TradeAccount.INVALID_ID

    fun getAccountId() =mCurrentTradeAccount.baseObjId

    fun getTradeAccount(id:Long): TradeAccount? =
        LitePal.find(TradeAccount::class.java, id,true)

    fun setPicByteArray(image:ByteArray){
        mCurrentTradeAccount.accountPic=image
    }

    fun updateNameAndNote(name:String,note:String){
        mCurrentTradeAccount.accountName=name
        mCurrentTradeAccount.note=note
    }

    fun saveOrUpdate()=
        mCurrentTradeAccount.saveOrUpdate("id = ?",mCurrentTradeAccount.baseObjId.toString())

    fun addWallet(walletName:String= WkStringConstants.STR_EMPTY,
                  walletNote:String= WkStringConstants.STR_EMPTY,
                  walletTime:String= AccountWallet.CONVERT_CASH_TIME_AT_ONCE.toString(),
                  walletAmount:String= NumberConstants.number_double_zero.toString()):Boolean{
       return LitePal.runInTransaction {
           if(!isValidAccount()){
               return@runInTransaction false
           }
           val wallet=AccountWallet(walletName,amount = walletAmount.toDouble(),toCashTime =walletTime.toLong(),note = walletNote)
            if(!wallet.save()){
                return@runInTransaction false
            }
           return@runInTransaction mCurrentTradeAccount.addWallet(wallet)
        }
    }
}