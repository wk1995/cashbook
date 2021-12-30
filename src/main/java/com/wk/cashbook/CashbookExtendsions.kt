package com.wk.cashbook

import com.wk.projects.common.helper.NumberUtil
import java.text.DecimalFormat

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/30
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
fun NumberUtil.initMoneyToString(double: Double):String{
    val df = DecimalFormat("#.00")
    return df.format(double)
}

