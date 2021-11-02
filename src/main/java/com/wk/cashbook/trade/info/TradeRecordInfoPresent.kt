package com.wk.cashbook.trade.info

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.WorkerThread
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.projects.common.BaseSimpleDialog
import com.wk.projects.common.SimpleOnlyEtDialog
import com.wk.projects.common.configuration.WkProjects
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.time.date.DateTime
import com.wk.projects.common.ui.WkToast
import org.litepal.LitePal
import org.litepal.extension.runInTransaction
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author      :wangkang
 *
 * email        :shenlong.wang@tuya.com
 *
 * create date  : 2021/03/16
 *
 * desc         :
 */

class TradeRecordInfoPresent(private val mTradeRecordInfoActivity: TradeRecordInfoActivity,
                             private val intent: Intent)
    : BaseSimpleDialog.SimpleOnlyEtDialogListener {
    private val mSubscriptions by lazy { CompositeSubscription() }

    private val mTradeInfoModel by lazy {
        TradeInfoModel(intent, this)
    }

    fun initData() {
        mTradeInfoModel.initData()
        initRootCategoriesAsync()
    }

    fun onDestroy() {
        mSubscriptions.clear()
    }

    /**显示备注*/
    fun showNote(note: String) {
        mTradeRecordInfoActivity.showNote(note)
    }

    /**显示金额*/
    fun showAmount(amount: String) {
        mTradeRecordInfoActivity.showAmount(amount)
    }

    /**显示交易时间*/
    fun showTradeTime(time: Long) {
        mTradeRecordInfoActivity.showTradeTime(
                DateTime.getDateString(time, SimpleDateFormat("MM-dd", Locale.getDefault()))
        )
    }


    /**显示标签*/
    fun showTradeFlag() {
        mTradeRecordInfoActivity.showTradeFlag()
    }

    /**
     * 显示当前的类别
     * */
    fun showTradeCategory(categoryId: Long) {
        mTradeRecordInfoActivity.setTradeCategory(categoryId)
    }

    /**
     * 显示根类别
     * */
    fun initRootCategory(categoryId: Long) {
        WkLog.i("showRootCategory")
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<TradeCategory> {
            it.onNext(TradeRecode.getRootTradeCategory(categoryId))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    WkLog.i("initRootCategory:  ${it.categoryName}")
                    mTradeInfoModel.originRootCategoryId = it.baseObjId
                    setSelectRootCategory(it)
                }
        )

    }


    /**
     * 设置账号
     * */
    fun showTradeAccount(accountId: Long, accountType: Int = 0) {
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<TradeAccount> { t ->
            t?.onNext(
                    LitePal.find(TradeAccount::class.java, accountId)
            )
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val accountName = it?.accountName
                    if (accountType == 1) {
                        mTradeInfoModel.setReceiveAccountId(accountId)
                        mTradeRecordInfoActivity.showTradeToAccount(
                                if (TextUtils.isEmpty(accountName)) {
                                    WkProjects.getApplication().getString(R.string.cashbook_no_select_account)
                                } else {
                                    accountName
                                })
                    } else {
                        mTradeInfoModel.setAccount(accountId)
                        mTradeRecordInfoActivity.showTradeAccount(
                                if (TextUtils.isEmpty(accountName)) {
                                    WkProjects.getApplication().getString(R.string.cashbook_no_select_account)
                                } else {
                                    accountName
                                })
                    }


                }
        )
    }

    fun showTradeAccount(bundle: Bundle?) {
        val accountType = bundle?.getInt("accountType") ?: 0
        val accountId = bundle?.getLong(TradeAccount.ACCOUNT_ID) ?: return
        showTradeAccount(accountId, accountType)
    }


    /**
     * 获取最顶的类别
     * 支出，收入，内部转账
     * */
    private fun initRootCategoriesAsync() {
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<List<TradeCategory>> { t ->
            t?.onNext(TradeCategory.getRootCategory())
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mTradeRecordInfoActivity.setRootCategories(it)
                    mTradeInfoModel.initRootCategory()
                }
        )
    }


    /**
     * 显示当前的类别
     * @param parent 当前根类别
     * */
    fun initCategoryAsync(parent: TradeCategory?) {
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<List<TradeCategory>> { t ->
            t?.onNext(TradeCategory.getSubCategory(parent))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    WkLog.d(it.toString())
                    mTradeRecordInfoActivity.setCategories(it)
                    mTradeInfoModel.showTradeCategory()
                }
        )
    }

    /**
     * 设置当前交易的类别
     * */
    fun setCategory(category: TradeCategory?) {
        val selectCategoryId = category?.baseObjId ?: return
        mTradeInfoModel.setCategoryId(selectCategoryId)
        mTradeRecordInfoActivity.setTradeCategory(selectCategoryId)
    }

    fun setSelectRootCategory(category: TradeCategory?) {
        if (category?.isRootCategory() != true) {
            return
        }
        val isInternalTransfer = isInternalTrans(category)
        if (isInternalTransfer) {
            showTradeAccount(mTradeInfoModel.getReceiveAccountId(), 1)
        }
        mTradeRecordInfoActivity.initInternalTransferView(isInternalTransfer)
        mTradeRecordInfoActivity.setRootCategory(category)
        mTradeInfoModel.setRootCategory(category)
    }

    private fun isInternalTrans(category: TradeCategory?) =
            category?.parentId == TradeAccount.INVALID_ID && category.categoryName == "内部转账"

    /**添加类别的弹窗*/
    fun showAddCategoryDialog() {
        val simpleOnlyEtDialog = SimpleOnlyEtDialog.create(simpleOnlyEtDialogListener = this)
        simpleOnlyEtDialog.show(mTradeRecordInfoActivity)
    }


    /**创建新类别*/
    private fun createNewCategory(categoryName: String) {
        val newCategory = TradeCategory(categoryName, System.currentTimeMillis(),
                mTradeInfoModel.getRootCategoryId())
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<Boolean> { t ->
            t?.onNext(newCategory.save())
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) {
                        mTradeRecordInfoActivity.addCategory(newCategory)
                    } else {
                        WkToast.showToast("失败")
                    }
                }
        )
    }

    override fun ok(bundle: Bundle?): Boolean {
        val textString = bundle?.getString("textString")
        if (textString.isNullOrBlank()) {
            WkToast.showToast("不能为空")
            return true
        }
        createNewCategory(textString)
        return false
    }

    override fun cancel(bundle: Bundle?): Boolean {
        return false
    }

    /**
     * @param originAccountId 原账户id
     * @param accountId 最终选择的账户id
     * @param isPayToComeIn  originAccountId 该账户是否相对的从收入变为了支出
     *
     * */
    @WorkerThread
    fun saveAccount(originAccountId: Long, accountId: Long, isPayToComeIn: Boolean): Boolean {

        // 说明是更新，而不是保存
        if (originAccountId <= TradeAccount.INVALID_ID) {
            return true
        }
        //说明未发生改变
        if (accountId == originAccountId) {
            return true
        }
        val account = LitePal.find(TradeAccount::class.java, accountId)
        if (account == null) {
            WkLog.i("TradeAccount is null")
            return false
        }

        val originAccount = LitePal.find(TradeAccount::class.java, originAccountId)
        if (originAccount == null) {
            WkLog.i("originAccount is null")
            return false
        }
        val amount = mTradeInfoModel.getMoney()
        val origin = mTradeInfoModel.originAmount
        account.amount -= amount
        originAccount.amount += origin
        return account.save() && originAccount.save()
    }


    /**
     * 保存 \ 更新
     *
     *
     *
     *
     * */
    fun saveTradeRecode(bundle: Bundle? = null) {
        val tradeNote = bundle?.getString(TradeRecode.TRADE_NOTE)
        val tradeAmount = bundle?.getDouble(TradeRecode.AMOUNT)
        mTradeInfoModel.setAmount(tradeAmount)
        mTradeInfoModel.setNote(tradeNote)

        mSubscriptions.add(Observable.create(Observable.OnSubscribe<Boolean> { t ->
            t?.onNext(
                    //账户金额的变化
                    LitePal.runInTransaction {
                        val accountId = mTradeInfoModel.getAccountId()
                        //说明有使用账户
                        if (accountId > TradeAccount.INVALID_ID) {
                            val account = LitePal.find(TradeAccount::class.java, accountId)
                            if (account == null) {
                                WkLog.i("TradeAccount is null")
                                return@runInTransaction false
                            }
                            val rootCategoryId = mTradeInfoModel.getRootCategoryId()
                            val rootCategory = LitePal.find(TradeCategory::class.java, rootCategoryId)
                            if (rootCategory == null) {
                                WkLog.i("rootCategory is null")
                                return@runInTransaction false
                            }
                            val originRootCategoryId = mTradeInfoModel.originRootCategoryId
                            val originAccountId = mTradeInfoModel.originAccountId
                            val origin = mTradeInfoModel.originAmount
                            val amount = mTradeInfoModel.getMoney()
                            if (rootCategoryId == originRootCategoryId) {
                                WkLog.i("根类别未发生变化")
                                if (accountId != originAccountId) {
                                    if (originAccountId > TradeAccount.INVALID_ID) {
                                        WkLog.i("账户发生变化")
                                        val originAccount = LitePal.find(TradeAccount::class.java, originAccountId)
                                        if (originAccount == null) {
                                            WkLog.i("originAccount is null")
                                            return@runInTransaction false
                                        }

                                        if (rootCategory.isPay() || rootCategory.isInternalTransfer()) {
                                            account.amount -= amount
                                            originAccount.amount += origin
                                        }
                                        if (rootCategory.isComeIn()) {
                                            account.amount += amount
                                            originAccount.amount -= origin
                                        }

                                        if (!account.save() || !originAccount.save()) {
                                            WkLog.e("账户数据更新失败")
                                            return@runInTransaction false
                                        }
                                    } else {
                                        WkLog.i("新增了账户")
                                        if (rootCategory.isPay() || rootCategory.isInternalTransfer()) {
                                            account.amount -= amount
                                        }
                                        if (rootCategory.isComeIn()) {
                                            account.amount += amount
                                        }

                                        if (!account.save()) {
                                            WkLog.e("账户数据更新失败")
                                            return@runInTransaction false
                                        }
                                    }
                                } else {
                                    WkLog.i("账户没有发生变化")
                                    if (origin != amount) {
                                        WkLog.i("金额发生变化")
                                        if (rootCategory.isPay() || rootCategory.isInternalTransfer()) {
                                            account.amount += origin
                                            account.amount -= amount
                                        }

                                        if (rootCategory.isComeIn()) {
                                            account.amount -= origin
                                            account.amount += amount
                                        }
                                        if (!account.save()) {
                                            return@runInTransaction false
                                        }
                                    }
                                }

                                //如果是内部转账
                                if (rootCategory.isInternalTransfer()) {
                                    WkLog.i("根类别是内部转账")
                                    if (!internalTransferReceiveAccount()) {
                                        return@runInTransaction false
                                    }
                                }
                            } else {
                                WkLog.i("根类别发生了变化")
                                //说明根类别发生变化
                                // 说明是更新，而不是保存
                                if (originRootCategoryId > TradeCategory.INVALID_ID) {
                                    WkLog.i("原根类别有效")
                                    val originRootCategory = LitePal.find(TradeCategory::class.java, originRootCategoryId)
                                    if (originRootCategory == null) {
                                        WkLog.i("originRootCategory is null")
                                        return@runInTransaction false
                                    }
                                    when {
                                        rootCategory.isComeIn() -> {
                                            WkLog.i("当前类别属于收入")
                                            if (originRootCategory.isPay()) {
                                                WkLog.i("由支出变为收入")
                                                //账户是否发生变化
                                                if (accountId == originAccountId) {
                                                    WkLog.i("账户1 未改变")
                                                    if (origin != amount) {
                                                        account.amount += origin
                                                        account.amount += amount
                                                        if (!account.save()) {
                                                            return@runInTransaction false
                                                        }
                                                    }

                                                } else {
                                                    //以前有账户
                                                    if (originAccountId > TradeAccount.INVALID_ID) {
                                                        WkLog.i("原账户1 有效")
                                                        val originAccount = LitePal.find(TradeAccount::class.java, originAccountId)
                                                        if (originAccount == null) {
                                                            WkLog.i("originAccount is null")
                                                            return@runInTransaction false
                                                        }
                                                        originAccount.amount += origin
                                                        if (!originAccount.save()) {
                                                            return@runInTransaction false
                                                        }
                                                    }
                                                    account.amount += amount
                                                    if (!account.save()) {
                                                        WkLog.i("账户1 保存失败")
                                                        return@runInTransaction false
                                                    }
                                                }


                                            }
                                            if (originRootCategory.isInternalTransfer()) {
                                                WkLog.i("由内部转账变为收入")
                                                mTradeInfoModel.setReceiveAccountId(TradeAccount.INVALID_ID)
                                                val originReceiveAccountId = mTradeInfoModel.originReceiveId
                                                if (originReceiveAccountId > TradeAccount.INVALID_ID) {
                                                    WkLog.i("原 账户2 有效")
                                                    val originReceiveAccount = LitePal.find(TradeAccount::class.java, originReceiveAccountId)
                                                    if (originReceiveAccount == null) {
                                                        WkLog.i("originReceiveAccount is null")
                                                        return@runInTransaction false
                                                    }
                                                    originReceiveAccount.amount -= origin
                                                    if (!originReceiveAccount.save()) {
                                                        WkLog.i("原 账户2 保存失败")
                                                        return@runInTransaction false
                                                    }
                                                }
                                                if (originAccountId != accountId) {
                                                    WkLog.i("账户1 发生改变")
                                                    if (originAccountId > TradeAccount.INVALID_ID) {
                                                        WkLog.i("原 账户1 有效")
                                                        val originAccount = LitePal.find(TradeAccount::class.java, originAccountId)
                                                        if (originAccount == null) {
                                                            WkLog.i("originAccount is null")
                                                            return@runInTransaction false
                                                        }
                                                        originAccount.amount += origin
                                                        if (!originAccount.save()) {
                                                            WkLog.i("原 账户1 保存失败")
                                                            return@runInTransaction false
                                                        }
                                                    }
                                                    account.amount += amount
                                                }else{
                                                    WkLog.i("账户1 没有发生改变")
                                                    account.amount += origin
                                                    account.amount += amount
                                                }
                                                if (!account.save()) {
                                                    WkLog.i("账户1 保存失败")
                                                    return@runInTransaction false
                                                }

                                            }
                                        }
                                        rootCategory.isPay() -> {
                                            WkLog.i("当前类别属于支出")
                                            when {
                                                originRootCategory.isInternalTransfer() -> {
                                                    WkLog.i("内部转账->支出")
                                                    mTradeInfoModel.setReceiveAccountId(TradeAccount.INVALID_ID)
                                                    val originReceiveAccountId = mTradeInfoModel.originReceiveId
                                                    val originReceiveAccount = LitePal.find(TradeAccount::class.java, originReceiveAccountId)
                                                    if (originReceiveAccount == null) {
                                                        WkLog.i("originReceiveAccount is null originReceiveAccountId is $originReceiveAccountId")
                                                        return@runInTransaction false
                                                    }
                                                    originReceiveAccount.amount -= origin
                                                    if (!originReceiveAccount.save()) {
                                                        WkLog.i("原账户2 保存失败")
                                                        return@runInTransaction false
                                                    }
                                                    if(originAccountId!=accountId){
                                                        WkLog.i("账户1 发生了改变")
                                                        if(originAccountId>TradeAccount.INVALID_ID){
                                                            WkLog.i("原账户1 有效")
                                                            val originAccount = LitePal.find(TradeAccount::class.java, originAccountId)
                                                            if (originAccount == null) {
                                                                WkLog.i("originAccount is null")
                                                                return@runInTransaction false
                                                            }
                                                            originAccount.amount += origin
                                                            if (!originAccount.save()) {
                                                                return@runInTransaction false
                                                            }

                                                        }
                                                        account.amount -= amount
                                                        if (!account.save()) {
                                                            return@runInTransaction false
                                                        }
                                                    }else{
                                                        WkLog.i("账户1 没有发生改变")
                                                        if(amount!=origin){
                                                            WkLog.i("金额发生改变")
                                                            account.amount += origin
                                                            account.amount -= amount
                                                        }
                                                    }

                                                }
                                                //收入->支出
                                                originRootCategory.isComeIn() -> {
                                                    WkLog.i("收入->支出")
                                                    if(originAccountId!=accountId) {
                                                        WkLog.i("账户1 发生了改变")
                                                        if(originAccountId>TradeAccount.INVALID_ID) {
                                                            WkLog.i("原账户1 有效")
                                                            val originAccount = LitePal.find(TradeAccount::class.java, originAccountId)
                                                            if (originAccount == null) {
                                                                WkLog.i("originAccount is null")
                                                                return@runInTransaction false
                                                            }
                                                            originAccount.amount -= origin
                                                            if (!originAccount.save()) {
                                                                WkLog.i("originAccount save fail")
                                                                return@runInTransaction false
                                                            }
                                                        }
                                                        account.amount -= amount
                                                        if (!account.save()) {
                                                            WkLog.i("account save fail")
                                                            return@runInTransaction false
                                                        }

                                                    }else{
                                                        WkLog.i("账户1 没有发生改变")
                                                        if(amount!=origin){
                                                            WkLog.i("金额发生改变")
                                                            account.amount -= origin
                                                            account.amount -= amount
                                                        }
                                                        if (!account.save()) {
                                                            WkLog.i("account save fail")
                                                            return@runInTransaction false
                                                        }
                                                    }

                                                }
                                                else -> {
                                                    WkLog.i("originRootCategory is invalid")
                                                    return@runInTransaction false
                                                }
                                            }

                                        }

                                        rootCategory.isInternalTransfer() -> {
                                            WkLog.i("rootCategory.isInternalTransfer")
                                            when {
                                                //支出->内部转账
                                                originRootCategory.isPay() -> {
                                                    WkLog.i("支出->内部转账")
                                                    if (accountId != originAccountId) {
                                                        WkLog.i("accountId != originAccountId")
                                                        if(originAccountId>TradeAccount.INVALID_ID){
                                                            WkLog.i(" originAccountId is valid")
                                                            val originAccount = LitePal.find(TradeAccount::class.java, originAccountId)
                                                            if (originAccount == null) {
                                                                WkLog.i("originAccount is null")
                                                                return@runInTransaction false
                                                            }
                                                            originAccount.amount += origin
                                                            if (!originAccount.save()) {
                                                                WkLog.i("originAccount.save fail")
                                                                return@runInTransaction false
                                                            }
                                                        }
                                                        account.amount -= amount
                                                        if (!account.save()) {
                                                            WkLog.i("account.save fail")
                                                            return@runInTransaction false
                                                        }
                                                    }else{
                                                        WkLog.i("accountId == originAccountId")
                                                        if(origin!=amount){
                                                            WkLog.i("origin!=amount")
                                                            account.amount += origin
                                                            account.amount -= amount
                                                            if (!account.save()) {
                                                                WkLog.i("account.save fail")
                                                                return@runInTransaction false
                                                            }
                                                        }
                                                    }


                                                    val receiveId = mTradeInfoModel.getReceiveAccountId()
                                                    if (receiveId <= TradeAccount.INVALID_ID) {
                                                        WkLog.i("receiveId is invalid")
                                                        return@runInTransaction false
                                                    }
                                                    val receiveAccount = LitePal.find(TradeAccount::class.java, receiveId)
                                                    if (receiveAccount == null) {
                                                        WkLog.i("receiveAccount == null")
                                                        return@runInTransaction false
                                                    }
                                                    receiveAccount.amount += amount
                                                    if (!receiveAccount.save()) {
                                                        WkLog.i("receiveAccount.save fail")
                                                        return@runInTransaction false
                                                    }
                                                }
                                                //收入->内部转账
                                                originRootCategory.isComeIn() -> {
                                                    if (accountId != originAccountId) {
                                                        WkLog.i("accountId != originAccountId")
                                                        if(originAccountId>TradeAccount.INVALID_ID){
                                                            WkLog.i(" originAccountId is valid")
                                                            val originAccount = LitePal.find(TradeAccount::class.java, originAccountId)
                                                            if (originAccount == null) {
                                                                WkLog.i("originAccount is null")
                                                                return@runInTransaction false
                                                            }
                                                            originAccount.amount -= origin
                                                            if (!originAccount.save()) {
                                                                return@runInTransaction false
                                                            }
                                                        }
                                                        account.amount -= origin
                                                        if (!account.save()) {
                                                            return@runInTransaction false
                                                        }
                                                    } else {
                                                        WkLog.i("accountId == originAccountId")
                                                        if(origin!=amount){
                                                            WkLog.i("origin!=amount")
                                                            account.amount -= origin
                                                            account.amount -= amount
                                                            if (!account.save()) {
                                                                WkLog.i("account.save fail")
                                                                return@runInTransaction false
                                                            }
                                                        }
                                                    }
                                                    val receiveId = mTradeInfoModel.getReceiveAccountId()
                                                    if (receiveId <= TradeAccount.INVALID_ID) {
                                                        WkLog.i("receiveId is invalid")
                                                        return@runInTransaction false
                                                    }
                                                    val receiveAccount = LitePal.find(TradeAccount::class.java, receiveId)
                                                    if (receiveAccount == null) {
                                                        WkLog.i("receiveAccount is null")
                                                        return@runInTransaction false
                                                    }
                                                    receiveAccount.amount += amount
                                                    if (!receiveAccount.save()) {
                                                        WkLog.i("receiveAccount.save fail")
                                                        return@runInTransaction false
                                                    }
                                                }
                                                else -> {
                                                    WkLog.i("originRootCategory is invalid")
                                                    return@runInTransaction false
                                                }
                                            }
                                        }
                                        else -> {
                                            WkLog.i("rootCategory is invalid")
                                            return@runInTransaction false
                                        }
                                    }
                                } else {
                                    WkLog.e("originRootCategoryId: <=  TradeCategory.INVALID_ID")
                                    return@runInTransaction false
                                }
                            }
                        }

                        mTradeInfoModel.saveOrUpdate()

                    }
            )
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mTradeRecordInfoActivity.saveResult(if (it) {
                        mTradeInfoModel.getBundle()
                    } else {
                        null
                    })

                }
        )
    }

    /**
     * 内部转账 收钱账户的账户逻辑
     * */
    private fun internalTransferReceiveAccount(): Boolean {
        val receiveAccountId = mTradeInfoModel.getReceiveAccountId()
        val originReceiveId = mTradeInfoModel.originReceiveId

        if (receiveAccountId != originReceiveId) {
            WkLog.i("账户2发生改变")
            val receiveAccount = LitePal.find(TradeAccount::class.java, mTradeInfoModel.getReceiveAccountId())
            if (receiveAccount == null) {
                WkLog.i("receiveAccount is null")
                return false
            }
            //如果消费金额也发生变化,原接受账号要减掉原来的金额
            val amount = mTradeInfoModel.getMoney()
            val origin = mTradeInfoModel.originAmount

            if (originReceiveId > TradeAccount.INVALID_ID) {
                WkLog.i("原账户2有效")
                val originReceiveAccount = LitePal.find(TradeAccount::class.java, mTradeInfoModel.originReceiveId)
                if (originReceiveAccount == null) {
                    WkLog.i("originAccount is null")
                    return false
                }

                receiveAccount.amount -= amount
                originReceiveAccount.amount += origin
                if (!receiveAccount.save() || !originReceiveAccount.save()) {
                    WkLog.e("账户2保存失败")
                    return false
                }
            } else {
                WkLog.i("原账户2无效")
                if (amount != origin) {
                    receiveAccount.amount -= amount
                    receiveAccount.amount += origin
                    if (!receiveAccount.save()) {
                        WkLog.e("receiveAccount 保存失败")
                        return false
                    }
                }
            }
        }
        return true
    }

}