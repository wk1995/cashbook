package com.wk.cashbook.main.recode.info.account

/**
 * 
 *      author : wk 
 *      e-mail : 1226426603@qq.com
 *      time   : 2022/1/9
 *      desc   :   
 *      GitHub : https://github.com/wk1995 
 *      CSDN   : http://blog.csdn.net/qq_33882671 
 * */
class ChooseAccountShowBean(val accountName:String,
                            val chooseWalletShowBeans: MutableList<ChooseWalletShowBean> = ArrayList()) {

    fun addChooseWalletShowBean(chooseWalletShowBean:ChooseWalletShowBean){
        chooseWalletShowBeans.add(chooseWalletShowBean)
    }
}