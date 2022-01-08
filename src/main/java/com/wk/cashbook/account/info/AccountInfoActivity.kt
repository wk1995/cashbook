package com.wk.cashbook.account.info

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.CashBookListItemDecoration
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.projects.common.BaseProjectsActivity
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.resource.WkContextCompat
import com.wk.projects.common.ui.WkCommonActionBar
import com.wk.projects.common.ui.recycler.IRvClickListener

/**
 * 资金渠道详情
 * */
class AccountInfoActivity : BaseProjectsActivity(), IRvClickListener {
    private lateinit var btnAddWallet: Button
    private lateinit var wbAccountInfoTitle: WkCommonActionBar
    private lateinit var ivAccountInfoPic: ImageView
    private lateinit var tvAccountInfoName: TextView
    private lateinit var tvAccountInfoNote: TextView
    private lateinit var rvAccountWalletList: RecyclerView

    private val adapter by lazy {
        val mAccountWalletAdapter = AccountWalletAdapter()
        mAccountWalletAdapter.mIRvClickListener = this
        mAccountWalletAdapter
    }

    private val mAccountInfoPresent by lazy {
        AccountInfoPresent(this)
    }

    override fun initResLayId() = R.layout.cashbook_account_info_activity

    override fun bindView(savedInstanceState: Bundle?, mBaseProjectsActivity: BaseProjectsActivity) {
        initView()
        initListener()
        initData()
    }

    private fun initView() {
        btnAddWallet = findViewById(R.id.btnAddWattle)
        wbAccountInfoTitle = findViewById(R.id.wbAccountInfoTitle)
        initTitleView()
        ivAccountInfoPic = findViewById(R.id.ivAccountInfoPic)
        tvAccountInfoName = findViewById(R.id.tvAccountInfoName)
        tvAccountInfoNote = findViewById(R.id.tvAccountInfoNote)
        rvAccountWalletList = findViewById(R.id.rvAccountWalletList)
        rvAccountWalletList.layoutManager = GridLayoutManager(this,2)
        rvAccountWalletList.addItemDecoration(CashBookListItemDecoration(this))
        rvAccountWalletList.adapter = adapter
    }

    private fun initTitleView() {
        wbAccountInfoTitle.setRightViewText(R.string.cashbook_add_wallet)
        wbAccountInfoTitle.setMiddleViewText(R.string.cashbook_title_account_info)
        wbAccountInfoTitle.setMiddleViewTextColor(WkContextCompat.getColor(R.color.common_black_2B2A2A))
        wbAccountInfoTitle.setMiddleViewGravity(Gravity.START or Gravity.CENTER_VERTICAL)
    }

    private fun initListener() {
        btnAddWallet.setOnClickListener(this)
        tvAccountInfoName.setOnClickListener(this)
        tvAccountInfoNote.setOnClickListener(this)
        ivAccountInfoPic.setOnClickListener(this)
        wbAccountInfoTitle.setRightViewClickListener(this)
    }

    fun setAccountPic(bitmap: Bitmap) {
        ivAccountInfoPic.setImageBitmap(bitmap)
    }

    fun setAccountName(name: String) {
        tvAccountInfoName.text = name
    }

    fun setNote(note: String) {
        tvAccountInfoNote.text = note
    }

    fun setWallet(wallets: List<AccountWallet>) {
        adapter.updateData(wallets)
    }

    private fun initData() {
        val id = intent.getLongExtra(TradeAccount.ACCOUNT_ID, TradeAccount.INVALID_ID)
        mAccountInfoPresent.initData(id)
    }

    fun showAddAccount(needShow: Boolean) {
        val show = if (needShow) {
            View.VISIBLE
        } else {
            View.GONE
        }
        /*  etAccountInfoName.visibility=show
          etAccountInfoNote.visibility=show*/
    }

    fun clearAddData() {

    }

    override fun onItemChildClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        val id = adapter?.getItemId(position)
        mAccountInfoPresent.gotoUpdateWallet(id ?: AccountWallet.INVALID_ID)
    }

    override fun onItemClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val id = adapter?.getItemId(position)?:AccountWallet.INVALID_ID
        if(id==AccountWallet.INVALID_ID){
            WkLog.w("id is  invalid")
            return
        }
        mAccountInfoPresent.gotoWalletRecodeInfo(id)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            wbAccountInfoTitle.getRightViewId(), R.id.btnAddWattle -> {
                mAccountInfoPresent.gotoCreateWallet()
            }

            R.id.ivAccountInfoPic -> {
                mAccountInfoPresent.gotoUpdateAccount()
            }
            R.id.tvAccountInfoNote -> {
                mAccountInfoPresent.gotoUpdateAccount()
            }
            R.id.tvAccountInfoName -> {
                mAccountInfoPresent.gotoUpdateAccount()

                /*  mCurrentAccountWallet?.apply {
                      accountName = etAccountName.text.toString()
                      amount = try {
                          etAccountMoney.text.toString().toDouble()
                      } catch (e: NumberFormatException) {
                          NumberConstants.number_double_zero
                      }
                      mSubscriptions?.add(
                              Observable.create(Observable.OnSubscribe<Boolean> {
                                  it.onNext(saveOrUpdate("id = ?",baseObjId.toString()))
                              }).subscribeOn(Schedulers.newThread())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe {
                                          if (it) {
                                              WkToast.showToast("保存成功")
                                              val resultIntent = Intent()
                                              resultIntent.putExtra(AccountWallet.ACCOUNT_MONEY_ID, baseObjId)
                                              resultIntent.putExtra(AccountWallet.ACCOUNT_NAME, accountName)
                                              resultIntent.putExtra(AccountWallet.NOTE, note)
                                              resultIntent.putExtra(AccountWallet.UNIT, unit)
                                              resultIntent.putExtra(AccountWallet.AMOUNT, amount)
                                              resultIntent.putExtra(STR_POSITION_LOW,
                                                      intent.getIntExtra(STR_POSITION_LOW, NumberConstants.number_int_one_Negative))
                                              setResult(RESULT_CODE_ACCOUNT_INFO_ACTIVITY, resultIntent)
                                              finish()
                                          } else {
                                              WkToast.showToast("保存失败")
                                          }
                                      }
                      )
                  }*/
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        initData()
    }


    override fun onDestroy() {
        super.onDestroy()

    }
}