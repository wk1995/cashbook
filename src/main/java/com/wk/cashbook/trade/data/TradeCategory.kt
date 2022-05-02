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


data class TradeCategory(
    val categoryName: String, val createTime: Long = NumberConstants.number_long_zero,
    var parentId: Long = INVALID_ID,
    var note: String = WkStringConstants.STR_EMPTY
) : LitePalSupport() {

    companion object {
        const val CATEGORY_NAME = "categoryname"
        const val CREATE_TIME = "createtime"
        const val PARENT_ID = "parentid"
        const val NOTE = "note"

        /**无效值*/
        const val INVALID_ID = NumberConstants.number_long_one_Negative

        @Deprecated("")
        const val DEFAULT_ROOT_CATEGORY_PAY = "支出"
        @Deprecated("")
        const val DEFAULT_ROOT_CATEGORY_COME_IN = "收入"
        @Deprecated("")
        const val DEFAULT_ROOT_CATEGORY_INTERNAL_TRANSFER = "内部转账"

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
            return LitePal.where(
                PARENT_ID + DataBaseConstants.STR_EQUAL_AND_QUESTION_MARK_EN,
                parentId.toString()
            )
                .find(TradeCategory::class.java)

        }

        @WorkerThread
        fun getCategory(id: Long): TradeCategory? {
            return LitePal.find(TradeCategory::class.java, id)

        }

        @Deprecated(
            "not support use ", ReplaceWith(
                "isPay(tradeCategory)",
                "com.wk.cashbook.trade.data.util.TradeCategoryUtils"
            )
        )
        fun isPay(categoryName: String) = categoryName == DEFAULT_ROOT_CATEGORY_PAY

        @Deprecated(
            "not support use ", ReplaceWith(
                "isComeIn(tradeCategory)",
                "com.wk.cashbook.trade.data.util.TradeCategoryUtils"
            )
        )
        fun isComeIn(categoryName: String) = categoryName == DEFAULT_ROOT_CATEGORY_COME_IN

        @Deprecated(
            "not support use ", ReplaceWith(
                "isInternalTransfer(tradeCategory)",
                "com.wk.cashbook.trade.data.util.TradeCategoryUtils"
            )
        )
        fun isInternalTransfer(categoryName: String) =
            categoryName == DEFAULT_ROOT_CATEGORY_INTERNAL_TRANSFER


    }


    public override fun getBaseObjId(): Long {
        return super.getBaseObjId()
    }

    @Deprecated(
        "not support use ", ReplaceWith(
            "isPay(tradeCategory)",
            "com.wk.cashbook.trade.data.util.TradeCategoryUtils"
        )
    )
    fun isPay() = isPay(categoryName)

    @Deprecated(
        "not support use ", ReplaceWith(
            "isComeIn(tradeCategory)",
            "com.wk.cashbook.trade.data.util.TradeCategoryUtils"
        )
    )
    fun isComeIn() = isComeIn(categoryName)

    @Deprecated(
        "not support use ", ReplaceWith(
            "isInternalTransfer(tradeCategory)",
            "com.wk.cashbook.trade.data.util.TradeCategoryUtils"
        )
    )
    fun isInternalTransfer() = isInternalTransfer(categoryName)

    @Deprecated(
        "not support use ", ReplaceWith(
            "isRootCategory(tradeCategory)",
            "com.wk.cashbook.trade.data.util.TradeCategoryUtils"
        )
    )
    fun isRootCategory() = parentId == INVALID_ID

}

