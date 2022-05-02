package com.wk.cashbook.module

import androidx.annotation.WorkerThread
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.util.TradeCategoryUtils
import com.wk.projects.common.BaseApplication
import com.wk.projects.common.configuration.WkProjects
import org.litepal.LitePal
import org.litepal.tablemanager.callback.DatabaseListener

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/01/25
 * desc         :
 */


class WkCashbookApp : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        WkProjects.init(this)
                .withModuleName(getString(R.string.module_name))
                .configure()
        Thread {
            initTradeAccount()
            initTradeCategory()
        }.start()
        LitePal.registerDatabaseListener(object :DatabaseListener{
            override fun onCreate() {

            }

            override fun onUpgrade(oldVersion: Int, newVersion: Int) {

            }
        })
    }

    companion object {

        @WorkerThread
        fun initTradeAccount() {

        }

        @WorkerThread
        fun initTradeCategory() {
            TradeCategoryUtils.initCategoryConfig()
        }

    }


}
