package com.wk.cashbook.main.recode.info

import org.junit.Test
import rx.Observable
import rx.Subscriber

/**
 * @author :wangkang_shenlong
 * email        :shenlong.wang@tuya.com
 * create date  : 2021/03/19
 * desc         :
 */


class TradeRecordInfoPresentTest {

    @Test
    fun getRootCategoryAsyn() {
        Observable.create(object :Observable.OnSubscribe<Int> {
            override fun call(t: Subscriber<in Int>?) {
                t?.onNext(addNumber())
            }
        }).subscribe {
           print(it)
        }
    }
    
    private fun addNumber()=2
}