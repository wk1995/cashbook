package com.wk.cashbook.trade.data

import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import org.litepal.crud.LitePalSupport

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/03/10
 * desc         : 钱包
 * @param walletName 名称 如花呗，余额宝
 * @param createTime 创建时间
 * @param amount 余额
 * @param note 备注
 * @param unit 单位，人民币="CNY",美元USD，日元 JPY，港元HKD see[CurrencyType]
 * @param toCashTime 转成现金所需要的时间 [CONVERT_CASH_TIME_AT_ONCE]表示能实时到账 ,[CONVERT_CASH_TIME_IMPOSSIBLE]表示不能转为现金 单位ms
 */


data class AccountWallet(var accountName: String=WkStringConstants.STR_EMPTY,
                         val createTime: Long = System.currentTimeMillis(),
                         var amount: Double = NumberConstants.number_double_zero,
                         var note: String = WkStringConstants.STR_EMPTY,
                         var unit: String = CurrencyType.RenMinBi.mCurrencyCode,
                         var toCashTime:Long=CONVERT_CASH_TIME_AT_ONCE)
    : LitePalSupport() {

    companion object {
        const val WALLET_NAME = "accountname"
        const val CREATE_TIME = "createtime"
        const val AMOUNT = "amount"
        const val UNIT = "unit"
        const val NOTE = "note"
        const val ACCOUNT_WALLET_ID = "id"
        const val ACCOUNT_ID = "tradeaccount_id"

        const val INVALID_ID = 0L

        /**0立刻能转换为现金*/
        const val CONVERT_CASH_TIME_AT_ONCE = NumberConstants.number_long_zero

        /**
         * -1不可转换为现金
         * */
        const val CONVERT_CASH_TIME_IMPOSSIBLE = NumberConstants.number_long_one_Negative
    }

    public override fun getBaseObjId(): Long {
        return super.getBaseObjId()
    }
}