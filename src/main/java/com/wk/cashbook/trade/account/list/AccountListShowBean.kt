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
                               val accountId: Long)