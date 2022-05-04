package com.wk.cashbook.module

import androidx.annotation.WorkerThread
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.util.TradeCategoryUtils
import com.wk.projects.common.BaseApplication
import com.wk.projects.common.configuration.WkProjects
import com.wk.projects.common.helper.file.FileHelper
import com.wk.projects.common.helper.file.path.FilePathManager
import com.wk.projects.common.log.WkLogUtil
import com.wk.projects.common.ui.WkToast
import org.litepal.LitePal
import org.litepal.tablemanager.callback.DatabaseListener
import java.io.File
import java.io.FileOutputStream

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
        LitePal.registerDatabaseListener(object : DatabaseListener {
            override fun onCreate() {

            }

            override fun onUpgrade(oldVersion: Int, newVersion: Int) {

            }
        })
    }

    override fun beforeInitOtherSdk() {
        super.beforeInitOtherSdk()
      /*  val externalRootFile = FilePathManager.getExternalRootFile()
        val child = externalRootFile.listFiles()
        if (child == null) {
            WkLogUtil.d(TAG,getString(R.string.cashbook_database_sql_storage_no_permission))
            return
        }
        val databaseParentPath =
            getDatabasePath("ignored").parentFile?.path ?: return
        val databasePath = "$databaseParentPath/cashbookDb.db"

        val databaseFile = File(databasePath)
        if (databaseFile.exists() && !databaseFile.delete()) {
            WkLogUtil.d(TAG,getString(R.string.cashbook_database_sql_delete_fail))
            return
        }
        val read = assets.open("cashbookDb220501.db")
        FileHelper.getInstance().readAndWriteOtherFile(
            read,
            FileOutputStream(databaseFile)
        )*/
    }

    companion object {
        private const val TAG="WkCashbookApp"
        @WorkerThread
        fun initTradeAccount() {

        }

        @WorkerThread
        fun initTradeCategory() {
            TradeCategoryUtils.initCategoryConfig()
        }

    }


}
