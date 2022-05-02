package com.wk.cashbook

import com.wk.projects.common.configuration.WkConfiguration
import com.wk.projects.common.configuration.WkProjects
import com.wk.projects.common.constant.NumberConstants

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/3
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
object CashBookConfig {

    private const val CASHBOOK_DEFAULT_CATEGORY_ID = "cashbook_default_category_id"

    @Deprecated("")
    fun getDefaultCategoryId() =
            WkProjects.getConfiguration<Long>(CASHBOOK_DEFAULT_CATEGORY_ID)
                    ?: NumberConstants.number_long_one_Negative

    @Deprecated("")
    fun setDefaultCategoryId(categoryId: Long) {
        WkProjects.setConfiguration(CASHBOOK_DEFAULT_CATEGORY_ID, categoryId)
    }
}