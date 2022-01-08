package com.wk.cashbook.account.info

import com.wk.projects.common.constant.NumberConstants

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/20
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class AccountInfoWalletShowBean(val walletId: Long, val walletName: String,
                                val totalPay: Double = NumberConstants.number_double_zero,
                                val totalInCome: Double = NumberConstants.number_double_zero,
                                val balance: Double = NumberConstants.number_double_zero) {
}