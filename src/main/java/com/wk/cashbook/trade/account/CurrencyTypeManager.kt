package com.wk.cashbook.trade.account

import com.wk.cashbook.trade.data.CurrencyType
import java.util.*
import kotlin.collections.HashMap

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/10
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
object CurrencyTypeManager {

    private val currencyTypeCache by lazy {
        HashMap<String, CurrencyType>()
    }


    fun getCurrencyType(currencyCode: String): CurrencyType? {
        if (currencyTypeCache[currencyCode] == null) {
            EnumSet.allOf(CurrencyType::class.java).forEach {
                currencyTypeCache[it.mCurrencyCode] = it
            }
        }

        return currencyTypeCache[currencyCode]
    }

}