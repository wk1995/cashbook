package com.wk.cashbook.trade.data

import androidx.annotation.WorkerThread
import com.wk.cashbook.CashBookConfig
import com.wk.projects.common.constant.DataBaseConstants
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import org.litepal.LitePal
import org.litepal.crud.LitePalSupport
import org.litepal.extension.findAsync

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/03/10
 * desc         :账单类别
 * @param categoryName  类别名称
 * @param createTime 创建时间
 * @param parentId 父类
 * @param note 备注
 */


data class TradeCategory(val categoryName: String, val createTime: Long = NumberConstants.number_long_zero,
                         var parentId: Long = NumberConstants.number_long_one_Negative,
                         var note: String = WkStringConstants.STR_EMPTY) : LitePalSupport() {

    companion object {
        const val CATEGORY_NAME = "categoryname"
        const val CREATE_TIME = "createtime"
        const val PARENT_ID = "parentid"
        const val NOTE = "note"

        const val INVALID_ID = NumberConstants.number_long_one_Negative


        /**
         * 获取最顶的类别
         * 支出，收入，内部转账
         * */
        @WorkerThread
        fun getRootCategory(): List<TradeCategory> {
            return getSubCategory()
        }

        /**
         * 获取parent 子类
         * */
        @WorkerThread
        fun getSubCategory(parent: TradeCategory? = null): List<TradeCategory> {
            val parentId = parent?.baseObjId ?: NumberConstants.number_long_one_Negative
            return LitePal.where(PARENT_ID + DataBaseConstants.STR_EQUAL_AND_QUESTION_MARK_EN,
                    parentId.toString())
                    .find(TradeCategory::class.java)

        }

        @WorkerThread
        fun getCategory(id: Long): TradeCategory? {
            return LitePal.find(TradeCategory::class.java, id)

        }

        fun initCategoryConfig() {
            LitePal.where("$CATEGORY_NAME=?", "支出")
                    .findAsync(TradeCategory::class.java).listen {
                        if (it.isEmpty()) {
                            initRootTradeCategory()
                        } else {
                            CashBookConfig.setDefaultCategoryId(it[0].baseObjId)
                        }
                    }
        }

        private fun initRootTradeCategory() {
            val initCategories = ArrayList<TradeCategory>()
            val defaultCategory = TradeCategory(categoryName = "支出")
            initCategories.add(defaultCategory)
            initCategories.add(TradeCategory(categoryName = "收入"))
            initCategories.add(TradeCategory(categoryName = "内部转账"))
            LitePal.saveAllAsync(initCategories).listen {
                CashBookConfig.setDefaultCategoryId(defaultCategory.baseObjId)
            }
        }


    }


    public override fun getBaseObjId(): Long {
        return super.getBaseObjId()
    }


}

