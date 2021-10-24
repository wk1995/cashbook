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
 * @param unit 单位，人民币="CNY",美元USD，日元 JPY，港元HKD
 * @param category 账号类别
 * @param belong 账号属于谁：同归属账号之间交易属于内部转账
 */


data class TradeAccount(var accountName: String, val createTime: Long = NumberConstants.number_long_zero,
                        var amount: Double = NumberConstants.number_double_zero, var note: String = WkStringConstants.STR_EMPTY,
                        var unit: String = "CNY", var category: AccountCategory?=null,
                        var belong:String="wk")
    : LitePalSupport() {

    companion object{
        const val ACCOUNT_NAME="accountname"
        const val CREATE_TIME="createtime"
        const val AMOUNT="amount"
        const val UNIT="unit"
        const val NOTE="note"
        const val CATEGORY="category"
        const val BELONG="belong"
        const val ACCOUNT_ID="account_id"
        const val INVALID_ID=0L
    }



    public override fun getBaseObjId(): Long {
        return super.getBaseObjId()
    }
}