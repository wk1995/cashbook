package com.wk.cashbook.trade.data

import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import org.litepal.crud.LitePalSupport

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/03/10
 * desc         : 交易账号
 * @param accountName 账户名
 * @param createTime 创建时间
 * @param amount 余额
 * @param note 备注
 * @param unit 单位，人民币="CNY"
 * @param parentId 父账号
 * @param belong 账号属于谁
 */


data class TradeAccount(var accountName: String, val createTime: Long = NumberConstants.number_long_zero,
                        var amount: Double = NumberConstants.number_double_zero, var note: String = WkStringConstants.STR_EMPTY,
                        var unit: String = "CNY", var parentId: Long = NumberConstants.number_long_one_Negative,
                        var belong:String="wk")
    : LitePalSupport() {

    companion object{
        const val ACCOUNT_NAME="accountname"
        const val CREATE_TIME="createtime"
        const val AMOUNT="amount"
        const val UNIT="unit"
        const val NOTE="note"
        const val PARENT_ID="parentid"
        const val BELONG="parentId"
    }



    public override fun getBaseObjId(): Long {
        return super.getBaseObjId()
    }
}