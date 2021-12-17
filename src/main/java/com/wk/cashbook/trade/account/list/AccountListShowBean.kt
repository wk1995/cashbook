package com.wk.cashbook.trade.account.list

import com.wk.projects.common.constant.WkStringConstants

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/13
 *      desc   : accountList 页面recycleView数据bean
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
data class AccountListShowBean(val money: Map<String, Double>,
                               var name: String = WkStringConstants.STR_EMPTY,
                               var note: String = WkStringConstants.STR_EMPTY,
                               var img:ByteArray = ByteArray(0),
                               val accountId: Long) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountListShowBean

        if (money != other.money) return false
        if (name != other.name) return false
        if (note != other.note) return false
        if (!img.contentEquals(other.img)) return false
        if (accountId != other.accountId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = money.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + img.contentHashCode()
        result = 31 * result + accountId.hashCode()
        return result
    }
}