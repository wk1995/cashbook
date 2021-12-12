package com.wk.cashbook

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.trade.record.CashListAdapter
import com.wk.projects.common.resource.WkContextCompat


/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/12/8
 *      desc   :
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * */
class CashBookListItemDecoration(context: Context): RecyclerView.ItemDecoration() {
    private val dividerPaint by lazy {
       val dividerPaint= Paint()
        dividerPaint.color = WkContextCompat.getColor(context, R.color.color_grey_807E797B)
        dividerPaint
    }

    private val dividerHeight by lazy {
        context.resources.getDimensionPixelSize(R.dimen.d1dp)
    }



    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val adapter=parent.adapter
        val childCount = adapter?.itemCount?:return
        for (i in 0 until childCount) {
            if(adapter is CashListAdapter && adapter.isTitle(i)){
                continue
            }
            val view = parent.getChildAt(i)
            val top = view.bottom.toFloat()
            val bottom = (view.bottom + dividerHeight).toFloat()
            c.drawRect(left.toFloat(), top, right.toFloat(), bottom, dividerPaint)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = dividerHeight
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }
}