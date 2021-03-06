package com.wk.cashbook.main.recode.info

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.WorkerThread
import com.wk.cashbook.R
import com.wk.cashbook.initMoneyToString
import com.wk.cashbook.main.recode.info.account.ChooseAccountShowBean
import com.wk.cashbook.main.recode.info.account.ChooseWalletShowBean
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.cashbook.trade.data.TradeCategory
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.projects.common.BaseSimpleDialog
import com.wk.projects.common.SimpleOnlyEtDialog
import com.wk.projects.common.configuration.WkProjects
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import com.wk.projects.common.helper.NumberUtil
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
        TradeInfoModel(this)
    }

    fun initData() {
        val id = intent.getLongExtra(TradeRecode.TRADE_RECODE_ID, NumberConstants.number_long_zero)
        if (id <= TradeRecode.INIT_ID) {
            initData(TradeRecode(DateTime.getDayStart(System.currentTimeMillis())))
        } else {
            mSubscriptions.add(Observable.create(Observable.OnSubscribe<TradeRecode> {
                it.onNext(LitePal.find(TradeRecode::class.java, id))
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        initData(it)
                    })
        }
    }

    @WorkerThread
    private fun initData(tradeRecode: TradeRecode) {
        mTradeInfoModel.mCurrentTradeRecode = tradeRecode
        mTradeInfoModel.initData()
        initRootCategoriesAsync()
    }


    fun onDestroy() {
        mSubscriptions.clear()
    }

    /**????????????*/
    fun showNote(note: String) {
        mTradeRecordInfoActivity.showNote(note)
    }

    /**????????????*/
    fun showAmount(amount: String) {
        mTradeRecordInfoActivity.showAmount(amount)
    }

    /**??????????????????*/
    fun showTradeTime(time: Long) {
        mTradeInfoModel.setTradeTime(time)
        mTradeRecordInfoActivity.showTradeTime(
                DateTime.getDateString(time, SimpleDateFormat("MM-dd", Locale.getDefault()))
        )
    }


    /**????????????*/
    fun showTradeFlag() {
        mTradeRecordInfoActivity.showTradeFlag()
    }

    /**
     * ?????????????????????
     * */
    fun showTradeCategory(categoryId: Long) {
        mTradeRecordInfoActivity.setTradeCategory(categoryId)
    }

    /**
     * ???????????????
     * */
    fun initRootCategory(categoryId: Long) {
        WkLog.i("showRootCategory")
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<TradeCategory> {
            it.onNext(TradeRecode.getRootTradeCategory(categoryId))
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it == null) {
                        mTradeRecordInfoActivity.finish()
                    } else {
                        WkLog.i("initRootCategory:  ${it.categoryName}")
                        mTradeInfoModel.originRootCategoryId = it.baseObjId
                        setSelectRootCategory(it)

                    }
                }
        )

    }


    /**
     * ????????????
     *
     *
     * */
    fun showTradeAccount(accountId: Long, accountType: Int = 0) {
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<AccountWallet> { t ->
            t?.onNext(
                    LitePal.find(AccountWallet::class.java, accountId)
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
        val accountId = bundle?.getLong(AccountWallet.ACCOUNT_WALLET_ID) ?: return
        showTradeAccount(accountId, accountType)
    }


    /**
     * ?????????????????????
     * ??????????????????????????????
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
     * ?????????????????????
     * @param parent ???????????????
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
     * ???????????????????????????
     * */
    fun setCategory(category: TradeCategory?) {
        val selectCategoryId = category?.baseObjId ?: return
        mTradeInfoModel.setCategoryId(selectCategoryId)
        mTradeRecordInfoActivity.setTradeCategory(selectCategoryId)
    }

    fun setSelectRootCategory(category: TradeCategory?,isSelect:Boolean=false) {
        if (category?.isRootCategory() != true) {
            return
        }
        mTradeInfoModel.mSelectRootCategoryName=category.categoryName
        val isInternalTransfer = isInternalTrans(category)
        if (isInternalTransfer) {
            showTradeAccount(mTradeInfoModel.getReceiveAccountId(), 1)
        }
        mTradeRecordInfoActivity.initInternalTransferView(isInternalTransfer)
        mTradeInfoModel.setRootCategory(category,isSelect)
        mTradeRecordInfoActivity.setRootCategory(category)
    }

    private fun isInternalTrans(category: TradeCategory?) =
            category?.parentId == TradeCategory.INVALID_ID && category.categoryName == "????????????"

    /**?????????????????????*/
    fun showAddCategoryDialog() {
        val simpleOnlyEtDialog = SimpleOnlyEtDialog.create(simpleOnlyEtDialogListener = this)
        simpleOnlyEtDialog.show(mTradeRecordInfoActivity)
    }


    /**???????????????*/
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
                        WkToast.showToast("??????")
                    }
                }
        )
    }

    override fun ok(bundle: Bundle?): Boolean {
        val textString = bundle?.getString("textString")
        if (textString.isNullOrBlank()) {
            WkToast.showToast("????????????")
            return true
        }
        createNewCategory(textString)
        return false
    }

    override fun cancel(bundle: Bundle?): Boolean {
        return false
    }

    /*  /**
       * @param originAccountId ?????????id
       * @param accountId ?????????????????????id
       * @param isPayToComeIn  originAccountId ????????????????????????????????????????????????
       *
       * */
      @WorkerThread
      fun saveAccount(originAccountId: Long, accountId: Long, isPayToComeIn: Boolean): Boolean {

          // ?????????????????????????????????
          if (originAccountId <= TradeAccount.INVALID_ID) {
              return true
          }
          //?????????????????????
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
      }*/

    /**
     * ????????????????????????????????????1???2??????????????????????????????
     * ????????????????????????????????????????????????????????????????????????????????????
     * */
    private fun checkAccount(needCheckAccountWallet: AccountWallet, vararg accountIds: Long): AccountWallet {
        val needCheckAccountId = needCheckAccountWallet.baseObjId
        if (accountIds.contains(needCheckAccountId)) {
            return LitePal.find(AccountWallet::class.java, needCheckAccountId)
        }
        return needCheckAccountWallet
    }


    /**
     * ?????? \ ??????
     *
     * */
    fun saveTradeRecode(bundle: Bundle? = null) {
        val tradeNote = bundle?.getString(TradeRecode.TRADE_NOTE)
        val tradeAmount = bundle?.getDouble(TradeRecode.AMOUNT)
        mTradeInfoModel.setAmount(tradeAmount)
        mTradeInfoModel.setNote(tradeNote)

        mSubscriptions.add(Observable.create(Observable.OnSubscribe<Boolean> { t ->
            t?.onNext(
                    //?????????????????????
                    LitePal.runInTransaction {
                        val rootCategoryId = mTradeInfoModel.getRootCategoryId()
                        val rootCategory = LitePal.find(TradeCategory::class.java, rootCategoryId)
                        val accountId = mTradeInfoModel.getAccountId()

                        if (accountId <= AccountWallet.INVALID_ID) {
                            WkLog.i("accountId is invalid")
                        }
                        var account = LitePal.find(AccountWallet::class.java, accountId)
                        if (account == null) {
                            WkLog.i("TradeAccount is null")
                            return@runInTransaction false
                        }
                        if (rootCategory == null) {
                            WkLog.i("rootCategory is null")
                            return@runInTransaction false
                        }
                        val originRootCategoryId = mTradeInfoModel.originRootCategoryId
                        val originAccountId = mTradeInfoModel.originAccountId
                        val origin = mTradeInfoModel.originAmount
                        val amount = mTradeInfoModel.getMoney()
                        if (rootCategoryId == originRootCategoryId) {
                            WkLog.i("????????????????????????")
                            if (accountId != originAccountId) {
                                if (originAccountId > AccountWallet.INVALID_ID) {
                                    WkLog.i("??????????????????")
                                    val originAccount = LitePal.find(AccountWallet::class.java, originAccountId)
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
                                        WkLog.e("????????????????????????")
                                        return@runInTransaction false
                                    }
                                } else {
                                    WkLog.i("???????????????")
                                    if (rootCategory.isPay() || rootCategory.isInternalTransfer()) {
                                        account.amount -= amount
                                    }
                                    if (rootCategory.isComeIn()) {
                                        account.amount += amount
                                    }

                                    if (!account.save()) {
                                        WkLog.e("????????????????????????")
                                        return@runInTransaction false
                                    }
                                }
                            } else {
                                WkLog.i("????????????????????????")
                                if (origin != amount) {
                                    WkLog.i("??????????????????")
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

                            //?????????????????????
                            if (rootCategory.isInternalTransfer()) {
                                WkLog.i("????????????????????????")
                                if (!internalTransferReceiveAccount()) {
                                    return@runInTransaction false
                                }
                            }
                        } else {
                            WkLog.i("????????????????????????")
                            //???????????????????????????
                            // ?????????????????????????????????
                            if (originRootCategoryId > TradeCategory.INVALID_ID) {
                                WkLog.i("??????????????????")
                                val originRootCategory = LitePal.find(TradeCategory::class.java, originRootCategoryId)
                                if (originRootCategory == null) {
                                    WkLog.i("originRootCategory is null")
                                    return@runInTransaction false
                                }
                                when {
                                    rootCategory.isComeIn() -> {
                                        WkLog.i("????????????????????????")
                                        if (originRootCategory.isPay()) {
                                            WkLog.i("?????????????????????")
                                            //????????????????????????
                                            if (accountId == originAccountId) {
                                                WkLog.i("??????1 ?????????")
                                                account.amount += origin
                                                account.amount += amount
                                                if (!account.save()) {
                                                    return@runInTransaction false
                                                }
                                            } else {
                                                //???????????????
                                                if (originAccountId > AccountWallet.INVALID_ID) {
                                                    WkLog.i("?????????1 ??????")
                                                    val originAccount = LitePal.find(AccountWallet::class.java, originAccountId)
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
                                                    WkLog.i("??????1 ????????????")
                                                    return@runInTransaction false
                                                }
                                            }

                                        }
                                        if (originRootCategory.isInternalTransfer()) {
                                            WkLog.i("???????????????????????????")
                                            mTradeInfoModel.setReceiveAccountId(AccountWallet.INVALID_ID)
                                            val originReceiveAccountId = mTradeInfoModel.originReceiveId
                                            if (originReceiveAccountId > AccountWallet.INVALID_ID) {
                                                WkLog.i("??? ??????2 ??????")
                                                val originReceiveAccount = LitePal.find(AccountWallet::class.java, originReceiveAccountId)
                                                if (originReceiveAccount == null) {
                                                    WkLog.i("originReceiveAccount is null")
                                                    return@runInTransaction false
                                                }
                                                originReceiveAccount.amount -= origin
                                                if (!originReceiveAccount.save()) {
                                                    WkLog.i("??? ??????2 ????????????")
                                                    return@runInTransaction false
                                                }
                                            }
                                            if (originAccountId != accountId) {
                                                WkLog.i("??????1 ????????????")
                                                if (originAccountId > AccountWallet.INVALID_ID) {
                                                    WkLog.i("??? ??????1 ??????")
                                                    val originAccount = LitePal.find(AccountWallet::class.java, originAccountId)
                                                    if (originAccount == null) {
                                                        WkLog.i("originAccount is null")
                                                        return@runInTransaction false
                                                    }
                                                    originAccount.amount += origin
                                                    if (!originAccount.save()) {
                                                        WkLog.i("??? ??????1 ????????????")
                                                        return@runInTransaction false
                                                    }
                                                }
                                                account = checkAccount(account, originReceiveAccountId)
                                                account.amount += amount
                                            } else {
                                                WkLog.i("??????1 ??????????????????")
                                                account.amount += origin
                                                account.amount += amount
                                            }
                                            if (!account.save()) {
                                                WkLog.i("??????1 ????????????")
                                                return@runInTransaction false
                                            }

                                        }
                                    }
                                    rootCategory.isPay() -> {
                                        WkLog.i("????????????????????????")
                                        when {
                                            originRootCategory.isInternalTransfer() -> {
                                                WkLog.i("????????????->??????")
                                                mTradeInfoModel.setReceiveAccountId(AccountWallet.INVALID_ID)
                                                val originReceiveAccountId = mTradeInfoModel.originReceiveId
                                                val originReceiveAccount = LitePal.find(AccountWallet::class.java, originReceiveAccountId)
                                                if (originReceiveAccount == null) {
                                                    WkLog.i("originReceiveAccount is null originReceiveAccountId is $originReceiveAccountId")
                                                    return@runInTransaction false
                                                }
                                                originReceiveAccount.amount -= origin
                                                if (!originReceiveAccount.save()) {
                                                    WkLog.i("?????????2 ????????????")
                                                    return@runInTransaction false
                                                }
                                                if (originAccountId != accountId) {
                                                    WkLog.i("??????1 ???????????????")
                                                    if (originAccountId > AccountWallet.INVALID_ID) {
                                                        WkLog.i("?????????1 ??????")
                                                        val originAccount = LitePal.find(AccountWallet::class.java, originAccountId)
                                                        if (originAccount == null) {
                                                            WkLog.i("originAccount is null")
                                                            return@runInTransaction false
                                                        }
                                                        originAccount.amount += origin
                                                        if (!originAccount.save()) {
                                                            return@runInTransaction false
                                                        }

                                                    }
                                                    account = checkAccount(account, originReceiveAccountId)
                                                    account.amount -= amount
                                                    if (!account.save()) {
                                                        WkLog.e("account save fail")
                                                        return@runInTransaction false
                                                    }
                                                } else {
                                                    WkLog.i("??????1 ??????????????????")
                                                    if (amount != origin) {
                                                        WkLog.i("??????????????????")
                                                        account.amount += origin
                                                        account.amount -= amount
                                                        if (!account.save()) {
                                                            WkLog.e("account save fail")
                                                            return@runInTransaction false
                                                        }
                                                    }
                                                }

                                            }
                                            //??????->??????
                                            originRootCategory.isComeIn() -> {
                                                WkLog.i("??????->??????")
                                                if (originAccountId != accountId) {
                                                    WkLog.i("??????1 ???????????????")
                                                    if (originAccountId > AccountWallet.INVALID_ID) {
                                                        WkLog.i("?????????1 ??????")
                                                        val originAccount = LitePal.find(AccountWallet::class.java, originAccountId)
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

                                                } else {
                                                    WkLog.i("??????1 ??????????????????")
                                                    account.amount -= origin
                                                    account.amount -= amount
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
                                            //??????->????????????
                                            originRootCategory.isPay() -> {
                                                WkLog.i("??????->????????????")

                                                if (accountId != originAccountId) {
                                                    WkLog.i("accountId != originAccountId")
                                                    if (originAccountId > AccountWallet.INVALID_ID) {
                                                        WkLog.i(" originAccountId is valid")
                                                        val originAccount = LitePal.find(AccountWallet::class.java, originAccountId)
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
                                                } else {
                                                    WkLog.i("accountId == originAccountId")
                                                    if (origin != amount) {
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
                                                if (receiveId <= AccountWallet.INVALID_ID) {
                                                    WkLog.i("receiveId is invalid")
                                                    return@runInTransaction false
                                                }
                                                val receiveAccount = LitePal.find(AccountWallet::class.java, receiveId)
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
                                            //??????->????????????
                                            originRootCategory.isComeIn() -> {
                                                if (accountId != originAccountId) {
                                                    WkLog.i("accountId != originAccountId")
                                                    if (originAccountId > AccountWallet.INVALID_ID) {
                                                        WkLog.i(" originAccountId is valid")
                                                        val originAccount = LitePal.find(AccountWallet::class.java, originAccountId)
                                                        if (originAccount == null) {
                                                            WkLog.i("originAccount is null")
                                                            return@runInTransaction false
                                                        }
                                                        originAccount.amount -= origin
                                                        if (!originAccount.save()) {
                                                            return@runInTransaction false
                                                        }
                                                    }
                                                    account.amount -= amount
                                                    if (!account.save()) {
                                                        WkLog.i("account.save fail")
                                                        return@runInTransaction false
                                                    }
                                                } else {
                                                    WkLog.i("accountId == originAccountId")
                                                    account.amount -= origin
                                                    account.amount -= amount
                                                    if (!account.save()) {
                                                        WkLog.i("account.save fail")
                                                        return@runInTransaction false
                                                    }
                                                }
                                                val receiveId = mTradeInfoModel.getReceiveAccountId()
                                                if (receiveId <= AccountWallet.INVALID_ID) {
                                                    WkLog.i("receiveId is invalid")
                                                    return@runInTransaction false
                                                }
                                                val receiveAccount = LitePal.find(AccountWallet::class.java, receiveId)
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


                        mTradeInfoModel.saveOrUpdate()

                    }
            )
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mTradeRecordInfoActivity.goToCashBookList(if (it) {
                        val result = mTradeInfoModel.getTradeShowBeanInfo()
                        result.putInt(WkStringConstants.STR_POSITION_LOW,
                                intent.getIntExtra(WkStringConstants.STR_POSITION_LOW, -1))
                        result
                    } else {
                        null
                    })

                }
        )
    }

    /**
     * ???????????? ???????????????????????????
     * */
    private fun internalTransferReceiveAccount(): Boolean {
        val receiveAccountId = mTradeInfoModel.getReceiveAccountId()
        val originReceiveId = mTradeInfoModel.originReceiveId
        WkLog.i("receiveAccountId is $receiveAccountId  originReceiveId:  $originReceiveId")
        //?????????????????????????????????,???????????????????????????????????????
        val amount = mTradeInfoModel.getMoney()
        val origin = mTradeInfoModel.originAmount
        val receiveAccount = LitePal.find(AccountWallet::class.java, mTradeInfoModel.getReceiveAccountId())
        if (receiveAccount == null) {
            WkLog.i("receiveAccount is null")
            return false
        }

        if (receiveAccountId != originReceiveId) {
            WkLog.i("??????2????????????")
            if (originReceiveId > AccountWallet.INVALID_ID) {
                WkLog.i("?????????2??????")
                val originReceiveAccount = LitePal.find(AccountWallet::class.java, mTradeInfoModel.originReceiveId)
                if (originReceiveAccount == null) {
                    WkLog.i("originAccount is null")
                    return false
                }

                receiveAccount.amount += amount
                originReceiveAccount.amount -= origin
                if (!receiveAccount.save() || !originReceiveAccount.save()) {
                    WkLog.e("??????2????????????")
                    return false
                }
            } else {
                WkLog.i("?????????2??????")
                if (amount != origin) {
                    receiveAccount.amount += amount
                    receiveAccount.amount -= origin
                    if (!receiveAccount.save()) {
                        WkLog.e("receiveAccount ????????????")
                        return false
                    }
                }
            }
        } else {
            WkLog.i("??????2??????????????????")
            receiveAccount.amount += amount
            receiveAccount.amount -= origin
            if (!receiveAccount.save()) {
                WkLog.e("??????2????????????")
                return false
            }
        }
        return true
    }


    fun initAccountList(){
        mSubscriptions.add(Observable.create(Observable.OnSubscribe<List<TradeAccount>> {
            it.onNext(LitePal.findAll(TradeAccount::class.java, true))
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { tradeAccounts ->
                val data = ArrayList<ChooseAccountShowBean>()
                tradeAccounts.forEach { tradeAccount ->
                    val accountWallets = tradeAccount.accountWallets
                    if (accountWallets.isNullOrEmpty()) {
                        return@forEach
                    }
                    val chooseAccountShowBean =
                        ChooseAccountShowBean(tradeAccount.accountName)
                    accountWallets.forEach { tradeWallet ->
                        val map = HashMap<String, Double>()
                        map[tradeWallet.unit] = tradeWallet.amount
                        chooseAccountShowBean.addChooseWalletShowBean(
                            ChooseWalletShowBean(
                                tradeWallet.baseObjId,
                                Pair(tradeWallet.unit, NumberUtil.initMoneyToString(tradeWallet.amount)),
                                tradeWallet.accountName
                            )
                        )
                    }
                    data.add(chooseAccountShowBean)
                }
                mTradeRecordInfoActivity.updateAccountData(data)
            }
        )
    }

}