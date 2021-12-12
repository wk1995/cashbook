package com.wk.cashbook.trade.data

import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import org.litepal.crud.LitePalSupport

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/12
 * desc         : 账号
 * @param accountName 账户名
 * @param createTime 创建时间
 * @param note 备注
 *      GitHub : https://github.com/wk1995
` *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
data class TradeAccount(var accountName: String= WkStringConstants.STR_EMPTY,
                        val createTime: Long = NumberConstants.number_long_zero,
                        var note: String=WkStringConstants.STR_EMPTY,
                        val accountWallets: MutableList<AccountWallet> = ArrayList())
    : LitePalSupport() {

    companion object {
        const val ACCOUNT_NAME = "accountname"
        const val CREATE_TIME = "createtime"
        const val NOTE = "note"
        const val ACCOUNT_ID="account_id"
        const val INVALID_ID = 0L
    }


    public override fun getBaseObjId(): Long {
        return super.getBaseObjId()
    }
}