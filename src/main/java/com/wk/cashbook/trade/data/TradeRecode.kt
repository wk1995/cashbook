package com.wk.cashbook.trade.data

import androidx.annotation.WorkerThread
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import org.litepal.LitePal
import org.litepal.crud.LitePalSupport

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/03/10
 * desc         : 交易记录
 * @param tradeTime 交易时间
 * @param accountId 账户 [AccountWallet]
 * @param categoryId 类别  [TradeCategory]
 * @param flagIds 标签 [TradeFlag]
 * @param amount 金额
 * @param tradeNote 备注
 * @param receiveAccountId 收款账户 [AccountWallet]
 * @param relationTradeId 关联交易 比如还款
 */


data class TradeRecode(var tradeTime: Long = NumberConstants.number_long_zero,
                       var accountId: Long = NumberConstants.number_long_one_Negative,
                       var categoryId: Long = NumberConstants.number_long_one_Negative,
                       val flagIds: ArrayList<Long> = ArrayList(),
                       var amount: Double = NumberConstants.number_double_zero,
                       var tradeNote: String = WkStringConstants.STR_EMPTY,
                       var receiveAccountId: Long = NumberConstants.number_long_one_Negative,
                       var relationTradeId: Long = NumberConstants.number_long_one_Negative
) : LitePalSupport(), ITradeRecord {

    public override fun getBaseObjId(): Long {
        return super.getBaseObjId()
    }

    override fun toString(): String {
        return "TradeRecode(baseObjId=${baseObjId}, tradeTime=$tradeTime, accountId=$accountId, categoryId=$categoryId, flagIds=$flagIds, amount=$amount, tradeNote='$tradeNote', receiveAccountId=$receiveAccountId, relationTradeId=$relationTradeId)"
    }

    companion object{

        @WorkerThread
        fun getRootTradeCategory(categoryId: Long):TradeCategory?{
            val tradeCategory=LitePal.find(TradeCategory::class.java,categoryId)
            return if(tradeCategory==null){
                null
            }else {
                val parentId = tradeCategory.parentId
                if (parentId == TradeCategory.INVALID_ID) {
                    tradeCategory
                } else {
                    getRootTradeCategory(parentId)
                }
            }

        }

        const val TRADE_RECODE_ID="trade_recode_id"
        const val TRADE_TIME="tradetime"
        const val ACCOUNT_ID="accountid"
        const val CATEGORY_ID="categoryid"
        const val FLAG_IDS="flagids"
        const val AMOUNT="amount"
        const val TRADE_NOTE="tradenote"
        const val RECEIVE_ACCOUNT_ID="receiveaccountid"
        const val RELATION_TRADE_ID="relationtradeid"
        const val INVALID_ID = NumberConstants.number_long_one_Negative
        /**初始id，新建对象，id都为这个0*/
        const val INIT_ID=NumberConstants.number_long_zero
    }

}