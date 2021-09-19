package com.wk.cashbook.trade.record

import android.os.Parcel
import android.os.Parcelable
import com.wk.cashbook.trade.data.ITradeRecord

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/02/27
 * desc         :
 */


data class TradeRecordTotal(val date:Long,var allPayAmount:Double,var allIncomeAmount:Double): ITradeRecord {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readDouble(),
            parcel.readDouble()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(date)
        parcel.writeDouble(allPayAmount)
        parcel.writeDouble(allIncomeAmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TradeRecordTotal> {
        override fun createFromParcel(parcel: Parcel): TradeRecordTotal {
            return TradeRecordTotal(parcel)
        }

        override fun newArray(size: Int): Array<TradeRecordTotal?> {
            return arrayOfNulls(size)
        }
    }
}

