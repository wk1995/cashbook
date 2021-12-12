package com.wk.cashbook.trade.data

import com.wk.projects.common.constant.NumberConstants
import org.litepal.crud.LitePalSupport

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/10
 *      desc   : 账号里面的钱
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * @param mUnit 单位 see[CurrencyType]
 * @param mAmount 金额
 * @param mToCashTime 转成现金所需要的时间 [CONVERT_CASH_TIME_AT_ONCE]表示能实时到账 ,[CONVERT_CASH_TIME_IMPOSSIBLE]表示不能转为现金
 * */
data class AccountMoney(val mAccountId:Long,
                        var mUnit:String,
                        var mAmount:Double,
                        var mToCashTime:Long): LitePalSupport() {

    companion object{
        /**立刻能转换为现金*/
        const val CONVERT_CASH_TIME_AT_ONCE=NumberConstants.number_long_zero
        /**
         * 不可能转换为现金
         * */
        const val CONVERT_CASH_TIME_IMPOSSIBLE=NumberConstants.number_long_one_Negative
    }


}