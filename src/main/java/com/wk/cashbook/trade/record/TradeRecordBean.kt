package com.wk.cashbook.trade.record

import android.os.Parcel
import android.os.Parcelable
import com.wk.cashbook.trade.data.ITradeRecord
import com.wk.projects.common.constant.WkStringConstants

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/02/27
 * desc         :
 */


data class TradeRecordBean(val date:Long, val note:String,
                           val amount:Double,
                           val type:String=WkStringConstants.COMMON_PUNCTUATION_SEMICOLON): ITradeRecord {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString()?:WkStringConstants.STR_EMPTY,
            parcel.readDouble(),
            parcel.readString()?:WkStringConstants.STR_EMPTY) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(date)
        parcel.writeString(note)
        parcel.writeDouble(amount)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TradeRecordBean> {
        override fun createFromParcel(parcel: Parcel): TradeRecordBean {
            return TradeRecordBean(parcel)
        }

        override fun newArray(size: Int): Array<TradeRecordBean?> {
            return arrayOfNulls(size)
        }
    }
}

