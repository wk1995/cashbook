package com.wk.cashbook.cache

import com.wk.cashbook.CashbookLog
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.util.TradeCategoryUtils

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2022/5/2
 *      desc   : 账单的缓存
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
object CashBookCacheManager {

    private const val TAG="CashBookCacheManager"

    private val mRootTradeCategoriesList = ArrayList<TradeCategory>()

    fun getRootTradeCategoriesList() = mRootTradeCategoriesList

    fun addRootTradeCategory(rootTradeCategory: TradeCategory) {
        if (mRootTradeCategoriesList.contains(rootTradeCategory)) {
            return
        }
        if (!TradeCategoryUtils.isRootTradeCategory(rootTradeCategory)) {
            return
        }
        CashbookLog.d(TAG,"addRootTradeCategory ${rootTradeCategory.baseObjId}")
        mRootTradeCategoriesList.add(rootTradeCategory)
    }

    fun clearRootTradeCategories() {
        mRootTradeCategoriesList.clear()
    }

    fun updateRootTradeCategories(rootTradeCategoriesList: List<TradeCategory>) {
        clearRootTradeCategories()
        rootTradeCategoriesList.forEach {
            addRootTradeCategory(it)
        }

    }
}