package com.wk.cashbook.trade.data

import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import org.litepal.crud.LitePalSupport

/**
 * @author      :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/03/10
 * desc         : 标签
 * @param name 标签名
 * @param createTime 创建时间
 * @param note 备注
 */


data class TradeFlag(val name: String, val createTime: Long = NumberConstants.number_long_zero,
                     var note: String = WkStringConstants.STR_EMPTY)
    : LitePalSupport() {


    public override fun getBaseObjId(): Long {
        return super.getBaseObjId()
    }
}