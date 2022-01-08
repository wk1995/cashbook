package com.wk.cashbook.main.recode

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.wk.cashbook.R
import com.wk.projects.common.BaseSimpleDialog
import com.wk.projects.common.communication.constant.BundleKey
import com.wk.projects.common.communication.constant.IFAFlag

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/9
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class DeleteCashBookDialog : BaseSimpleDialog() {
    companion object {
        fun create(bundle: Bundle? = null): DeleteCashBookDialog {
            val mUpdateOrDeleteDialog = DeleteCashBookDialog()
            mUpdateOrDeleteDialog.arguments = bundle
            return mUpdateOrDeleteDialog
        }
    }

    private lateinit var tvCommon: TextView

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnComSimpleDialogOk ->
                iFa.communication(IFAFlag.DELETE_ITEM_DIALOG, arguments)

        }
        super.onClick(v)
    }

    override fun initVSView(vsView: View) {
        tvCommon = vsView.findViewById(R.id.tvCommon)
        tvCommon.setText(R.string.cashbook_delete_item)
        tvCommon.gravity= Gravity.CENTER
        view?.findViewById<TextView>(R.id.tvComSimpleDialogTheme)?.text = (arguments?.getString(BundleKey.LIST_ITEM_NAME))
    }

    override fun initViewSubLayout() = R.layout.common_only_text

}