package com.wk.cashbook.trade.data

import org.litepal.crud.LitePalSupport

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/12
 *      desc   : 账号类别
 *
银行卡，微信，支付宝 属于账号类别
工商1，招商2，微信1，属于账号

 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
data class AccountCategory(var name: String, var createTime: Long, var image: ByteArray) : LitePalSupport() {

    public override fun getBaseObjId(): Long {
        return super.getBaseObjId()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountCategory

        if (name != other.name) return false
        if (createTime != other.createTime) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + createTime.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }

}