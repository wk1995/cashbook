package com.wk.cashbook.trade.account.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wk.cashbook.CashBookActivityRequestCode.REQUEST_CODE_ACCOUNT_LIST_ACTIVITY
import com.wk.cashbook.CashBookActivityResultCode.RESULT_CODE_ACCOUNT_INFO_ACTIVITY
import com.wk.cashbook.R
import com.wk.cashbook.trade.data.TradeAccount
import com.wk.cashbook.trade.data.TradeAccount.Companion.INVALID_ID
import com.wk.cashbook.trade.record.DeleteCashBookDialog
import com.wk.projects.common.BaseProjectsActivity
import com.wk.projects.common.communication.IFragmentToActivity
import com.wk.projects.common.communication.constant.IFAFlag
import com.wk.projects.common.constant.NumberConstants
import com.wk.projects.common.constant.WkStringConstants
import com.wk.projects.common.ui.recycler.IRvClickListener

/**
 * 资产管理
 * 账户列表
 * */
class AccountListActivity : BaseProjectsActivity(), IRvClickListener, IFragmentToActivity {

    /**返回键*/
    private lateinit var ivAccountListBack:ImageView
    /**总资产*/
    private lateinit var ivAllAmount:TextView

    /**账号列表*/
    private lateinit var rvAccountList:RecyclerView

    /**添加账号*/
    private lateinit var btnAddAccount:Button

    private val mAccountListAdapter by lazy {
        AccountListAdapter(mIRvClickListener=this)
    }

    private val mAccountListPresent by lazy {
        AccountListPresent(this)
    }

    override fun initResLayId()=R.layout.cashbook_account_list_activity

    override fun bindView(savedInstanceState: Bundle?, mBaseProjectsActivity: BaseProjectsActivity) {
        mAccountListPresent.onCreate()
        initView()
        initListener()
        mAccountListPresent.initData()
    }

    override fun onStart() {
        super.onStart()
        mAccountListPresent.onStart()
    }

    override fun onStop() {
        super.onStop()
        mAccountListPresent.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAccountListPresent.onDestroy()
    }

    private fun initView(){
        ivAccountListBack=findViewById(R.id.ivAccountListBack)
        ivAllAmount=findViewById(R.id.ivAllAmount)
        btnAddAccount=findViewById(R.id.btnAddAccount)
        initAccountList()
    }

    private fun initListener(){
        ivAccountListBack.setOnClickListener(this)
        btnAddAccount.setOnClickListener(this)
    }

    private fun initAccountList(){
        rvAccountList=findViewById(R.id.rvAccountList)
        rvAccountList.layoutManager=LinearLayoutManager(this)
        rvAccountList.adapter=mAccountListAdapter
    }
    override fun onClick(v: View?) {
        super.onClick(v)
        when(v?.id){
            R.id.ivAccountListBack->{
                mAccountListPresent.back()
            }
            R.id.btnAddAccount->{
                mAccountListPresent.goToInfoActivity()
            }
        }
    }

    override fun communication(flag:Int,bundle: Bundle?,any: Any?){
        if(flag== IFAFlag.DELETE_ITEM_DIALOG){
            val itemId=bundle?.getLong(TradeAccount.ACCOUNT_ID)
            val position=bundle?.getInt(WkStringConstants.STR_POSITION_LOW)?:return
            mAccountListPresent.deleteData(itemId,position)
        }
    }

    override fun onItemLongClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemLongClick(adapter, view, position)
        if(adapter==mAccountListAdapter){
            val bundle=Bundle()
            bundle.putLong(TradeAccount.ACCOUNT_ID,mAccountListAdapter.getItemId(position))
            bundle.putInt(WkStringConstants.STR_POSITION_LOW,position)
            DeleteCashBookDialog.create(bundle).show(supportFragmentManager)
        }
    }

    override fun onItemClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        if(adapter==mAccountListAdapter){
            val itemId=mAccountListAdapter.getItemId(position)
            mAccountListPresent.goToInfoActivity(itemId,position)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_CODE_ACCOUNT_LIST_ACTIVITY
                && resultCode==RESULT_CODE_ACCOUNT_INFO_ACTIVITY){
            val id=data?.getLongExtra(TradeAccount.ACCOUNT_ID,INVALID_ID)?:return
            if(id<=INVALID_ID){
                return
            }
            val note=data.getStringExtra(TradeAccount.NOTE)?:return
            val name=data.getStringExtra(TradeAccount.ACCOUNT_NAME)?:return
            val unit=data.getStringExtra(TradeAccount.UNIT)?:return
            val amount=data.getDoubleExtra(TradeAccount.AMOUNT,NumberConstants.number_double_zero)
            val position=data.getIntExtra(WkStringConstants.STR_POSITION_LOW,NumberConstants.number_int_one_Negative)
            val money=HashMap<String,Double>()
            money[unit]=amount
            val showBean=AccountListShowBean(money,name,note,id)
            if(position==NumberConstants.number_int_one_Negative) {
                mAccountListAdapter.addData(showBean)
            }else{
                mAccountListAdapter.updateData(showBean,position)
            }
        }
    }

    fun updateData(accounts:List<AccountListShowBean>){
        mAccountListAdapter.updateData(accounts)
    }

    fun removeData(position: Int){
        mAccountListAdapter.removeData(position)
    }
}