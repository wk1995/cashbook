package com.wk.cashbook.trade.data.util

import com.wk.cashbook.CashBookConfig
import com.wk.cashbook.cache.CashBookCacheManager
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeRootCategory
import org.litepal.LitePal

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2022/5/2
 *      desc   : TradeCategory工具类
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
object TradeCategoryUtils {

    /**是根类别*/
    fun isRootTradeCategory(tradeCategory: TradeCategory) =
        tradeCategory.parentId == TradeCategory.INVALID_ID


    /**是否是属于支出*/
    fun isPay(tradeCategory: TradeCategory) =
        tradeCategory.categoryName == TradeRootCategory.PAY.chineseName

    /**是否是属于收入*/
    fun isComeIn(tradeCategory: TradeCategory) =
        tradeCategory.categoryName == TradeRootCategory.COME_IN.chineseName

    /**是否是属于内部交易*/
    fun isInternalTransfer(tradeCategory: TradeCategory) =
        tradeCategory.categoryName == TradeRootCategory.INTERNAL_TRANSFER.chineseName


    fun initCategoryConfig() {
        LitePal.where("${TradeCategory.PARENT_ID}=?", TradeCategory.INVALID_ID.toString())
            .findAsync(TradeCategory::class.java).listen {
                if (it.size < TradeRootCategory.values().size) {
                    initRootTradeCategory()
                } else {
                    CashBookCacheManager.updateRootTradeCategories(it)
                    CashBookConfig.setDefaultCategoryId(it[0].baseObjId)
                }
            }
    }

    private fun initRootTradeCategory() {
        val initCategories = ArrayList<TradeCategory>()
        val defaultCategory = TradeCategory(
            categoryName = TradeRootCategory.PAY.chineseName
        )
        initCategories.add(defaultCategory)
        initCategories.add(
            TradeCategory(
                categoryName = TradeRootCategory.COME_IN.chineseName
            )
        )
        initCategories.add(
            TradeCategory(
                categoryName = TradeRootCategory.INTERNAL_TRANSFER.chineseName
            )
        )
        LitePal.saveAllAsync(initCategories).listen {
            CashBookConfig.setDefaultCategoryId(defaultCategory.baseObjId)
            CashBookCacheManager.updateRootTradeCategories(initCategories)
        }
    }

}