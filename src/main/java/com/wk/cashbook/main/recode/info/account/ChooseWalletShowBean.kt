package com.wk.cashbook.main.recode.info.account

import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/18
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class ChooseWalletShowBean(val walletId: Long,
                           val money: Pair<String, String> = Pair(WkStringConstants.STR_EMPTY,
                               NumberConstants.number_double_zero.toString()),
                           var name: String = WkStringConstants.STR_EMPTY)