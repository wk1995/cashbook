package com.wk.cashbook.trade.data


/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2022/5/2
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
private const val DEFAULT_ROOT_CATEGORY_PAY = "支出"
private const val DEFAULT_ROOT_CATEGORY_COME_IN = "收入"
private const val DEFAULT_ROOT_CATEGORY_INTERNAL_TRANSFER = "内部转账"

enum class TradeRootCategory(val chineseName: String) {
    PAY(DEFAULT_ROOT_CATEGORY_PAY),
    COME_IN(DEFAULT_ROOT_CATEGORY_COME_IN),
    INTERNAL_TRANSFER(DEFAULT_ROOT_CATEGORY_INTERNAL_TRANSFER)
}