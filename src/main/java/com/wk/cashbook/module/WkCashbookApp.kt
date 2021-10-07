package com.wk.cashbook.module

import androidx.annotation.WorkerThread
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.projects.common.BaseApplication
import com.wk.projects.common.configuration.WkProjects
import org.litepal.LitePal

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

    }

    companion object {

        @WorkerThread
        fun initTradeAccount() {
            val accounts = LitePal.where(TradeAccount.ACCOUNT_NAME + "=?", "支付宝").find(TradeAccount::class.java)
            if (accounts.isEmpty()) {
                val initAccounts = ArrayList<TradeAccount>()
                initAccounts.add(TradeAccount(accountName = "支付宝"))
                initAccounts.add(TradeAccount(accountName = "微信"))
                initAccounts.add(TradeAccount(accountName = "银行卡"))
                LitePal.saveAll(initAccounts)
            }
        }

        @WorkerThread
        fun initTradeCategory() {
            TradeCategory.initCategoryConfig()
        }

    }


}
