package com.wk.cashbook.trade.account.info

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.AccountWallet
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.projects.common.BaseProjectsActivity
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

   /* private lateinit var etAccountInfoNote: EditText
    private lateinit var etAccountInfoName: EditText

    private lateinit var etAccountWalletNote: EditText
    private lateinit var etAccountWalletName: EditText
    private lateinit var etAccountWalletTime: EditText
    private lateinit var etAccountWalletAmount: EditText*/


    private val adapter by lazy {
       val mAccountWalletAdapter= AccountWalletAdapter()
        mAccountWalletAdapter.mIRvClickListener=this
        mAccountWalletAdapter
    }

    private val mAccountInfoPresent by lazy{
        AccountInfoPresent(this)
    }

    override fun initResLayId() = R.layout.cashbook_account_info_activity

    override fun bindView(savedInstanceState: Bundle?, mBaseProjectsActivity: BaseProjectsActivity) {
        initView()
        initListener()
        initData()
    }

    private fun initView() {
        btnAddWallet = findViewById(R.id.btnCreate)
        wbAccountInfoTitle = findViewById(R.id.wbAccountInfoTitle)
        wbAccountInfoTitle.setRightViewText(R.string.cashbook_add_wallet)
   /*     etAccountInfoName = findViewById(R.id.etAccountInfoName)
        etAccountInfoNote = findViewById(R.id.etAccountInfoNote)
        etAccountWalletNote = findViewById(R.id.etAccountWalletNote)
        etAccountWalletAmount = findViewById(R.id.etAccountWalletAmount)
        etAccountWalletName = findViewById(R.id.etAccountWalletName)
        etAccountWalletTime = findViewById(R.id.etAccountWalletTime)*/
        ivAccountInfoPic = findViewById(R.id.ivAccountInfoPic)
        tvAccountInfoName = findViewById(R.id.tvAccountInfoName)
        tvAccountInfoNote = findViewById(R.id.tvAccountInfoNote)
        rvAccountWalletList = findViewById(R.id.rvAccountWalletList)
        rvAccountWalletList.layoutManager=LinearLayoutManager(this)
        rvAccountWalletList.adapter=adapter
    }

    private fun initListener() {
        btnAddWallet.setOnClickListener(this)
        tvAccountInfoName.setOnClickListener(this)
        tvAccountInfoNote.setOnClickListener(this)
        ivAccountInfoPic.setOnClickListener(this)
        wbAccountInfoTitle.setRightViewClickListener(this)
    }

    fun setAccountPic(bitmap:Bitmap){
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

    fun showAddAccount(needShow:Boolean){
        val show=if(needShow){
            View.VISIBLE
        }else{
            View.GONE
        }
      /*  etAccountInfoName.visibility=show
        etAccountInfoNote.visibility=show*/
    }

    fun clearAddData(){
       /* etAccountInfoName.setText(WkStringConstants.STR_EMPTY)
        etAccountInfoNote.setText(WkStringConstants.STR_EMPTY)
        etAccountWalletName.setText(WkStringConstants.STR_EMPTY)
        etAccountWalletTime.setText(WkStringConstants.STR_EMPTY)
        etAccountWalletAmount.setText(WkStringConstants.STR_EMPTY)
        etAccountWalletNote.setText(WkStringConstants.STR_EMPTY)*/
    }

    override fun onItemChildClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        val id=adapter?.getItemId(position)
        mAccountInfoPresent.gotoUpdateWallet(id?:AccountWallet.INVALID_ID)
    }

    override fun onItemClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            wbAccountInfoTitle.getRightViewId()->{
                mAccountInfoPresent.gotoCreateWallet()
            }
            R.id.btnCreate -> {
              /*  val accountName=etAccountInfoName.text.toString()
                val accountNote=etAccountInfoNote.text.toString()
                val walletName=etAccountWalletName.text.toString()
                val walletNote=etAccountWalletNote.text.toString()
                val walletTime=etAccountWalletTime.text.toString()
                val walletAmount=etAccountWalletAmount.text.toString()
                mAccountInfoPresent.saveOrUpdateAccount(accountName,accountNote,walletName,
                        walletNote,walletTime,walletAmount)*/

            }
            R.id.ivAccountInfoPic -> {

            }
            R.id.tvAccountInfoNote -> {

            }
            R.id.tvAccountInfoName -> {




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

    override fun onDestroy() {
        super.onDestroy()

    }
}