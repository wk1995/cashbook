package com.wk.cashbook.trade.data

import android.os.Parcel
import android.os.Parcelable
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
 * @param accountId 账户 [TradeAccount]
 * @param categoryId 类别  [TradeCategory]
 * @param flagIds 标签 [TradeFlag]
 * @param amount 金额
 * @param tradeNote 备注
 * @param receiveAccountId 收款账户 [TradeAccount]
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


    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readArrayList(Long::class.java.classLoader) as ArrayList<Long>,
            parcel.readDouble(),
            parcel.readString() ?: WkStringConstants.STR_EMPTY,
            parcel.readLong(),
            parcel.readLong()) {
    }

    public override fun getBaseObjId(): Long {
        return super.getBaseObjId()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(tradeTime)
        parcel.writeLong(accountId)
        parcel.writeLong(categoryId)
        parcel.writeList(flagIds)
        parcel.writeDouble(amount)
        parcel.writeString(tradeNote)
        parcel.writeLong(receiveAccountId)
        parcel.writeLong(relationTradeId)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "TradeRecode(baseObjId=${baseObjId}, tradeTime=$tradeTime, accountId=$accountId, categoryId=$categoryId, flagIds=$flagIds, amount=$amount, tradeNote='$tradeNote', receiveAccountId=$receiveAccountId, relationTradeId=$relationTradeId)"
    }

    companion object CREATOR : Parcelable.Creator<TradeRecode> {
        override fun createFromParcel(parcel: Parcel): TradeRecode {
            return TradeRecode(parcel)
        }

        override fun newArray(size: Int): Array<TradeRecode?> {
            return arrayOfNulls(size)
        }

        val TAG: String = TradeRecode::class.java.simpleName

        fun getTradeRecodes(vararg conditions: String?): MutableList<TradeRecode> =
            LitePal.where(*conditions).find(TradeRecode::class.java)

        @WorkerThread
        fun getRootTradeCategory(categoryId: Long):TradeCategory{
            val tradeCategory=LitePal.find(TradeCategory::class.java,categoryId)
            val parentId=tradeCategory.parentId
            return if(parentId==TradeCategory.INVALID_ID){
                tradeCategory
            }else{
                getRootTradeCategory(parentId)
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
    }


}