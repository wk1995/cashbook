package com.wk.cashbook.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wk.cashbook.R
import com.wk.cashbook.databinding.CashbookBillListActivityBinding
import com.wk.cashbook.CashBookListItemDecoration
import com.wk.cashbook.main.asset.MainAssetsInfoAdapter
import com.wk.cashbook.main.recode.CashListAdapter
import com.wk.cashbook.main.recode.DeleteCashBookDialog
import com.wk.cashbook.account.list.AccountListActivity
import com.wk.cashbook.initMoneyToString
import com.wk.cashbook.trade.data.TradeRecode
import com.wk.cashbook.main.recode.info.TradeRecordInfoActivity
import com.wk.cashbook.trade.record.*
import com.wk.cashbook.main.asset.AssetsInfoShowBean
import com.wk.cashbook.main.recode.ITradeRecodeShowBean
import com.wk.projects.common.BaseProjectsActivity
import com.wk.projects.common.BaseSimpleDialog
import com.wk.projects.common.communication.constant.BundleKey
import com.wk.projects.common.communication.constant.IFAFlag
import com.wk.projects.common.constant.WkStringConstants
import com.wk.projects.common.constant.WkStringConstants.STR_INT_ZERO
import com.wk.projects.common.helper.NumberUtil
import com.wk.projects.common.log.WkLog
import com.wk.projects.common.resource.WkContextCompat
import com.wk.projects.common.time.date.DateTime
import com.wk.projects.common.ui.recycler.IRvClickListener
import java.util.*
import kotlin.collections.ArrayList

/**
 * 首页
 *
 * 交易明细 ， 分类 ， 账户 tab
 *
 * */
class CashBookMainActivity : BaseProjectsActivity(), TabLayout.OnTabSelectedListener,
        IRvClickListener, BaseSimpleDialog.SimpleOnlyEtDialogListener {

    companion object {
        /**明细*/
        private const val LAYOUT_VIEWPAGER_DETAILED = 0

        /**类别*/
        private const val LAYOUT_VIEWPAGER_CATEGORY = 1

        /**账户*/
        private const val LAYOUT_VIEWPAGER_ACCOUNT = 2
    }

    class CardViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView)

    private val mBind by lazy {
        CashbookBillListActivityBinding.inflate(layoutInflater)
    }
    private val tabTitles by lazy {
        arrayOf(R.string.common_str_zh_detailed, R.string.common_str_zh_category, R.string.common_str_zh_account)
    }
    private val cashListAdapter by lazy {
        CashListAdapter(ArrayList(), this)
    }

    private val mAssetsInfoAdapter by lazy {
        val mAssetsInfoAdapter= MainAssetsInfoAdapter()
        mAssetsInfoAdapter.mIRvClickListener=this
        mAssetsInfoAdapter
    }

    private val pad by lazy {
        resources.getDimensionPixelOffset(R.dimen.d15dp)
    }

    /**时间*/
    private lateinit var llDate: ConstraintLayout

    /**年*/
    private lateinit var tvDateYear: TextView

    /**月*/
    private lateinit var tvDateMonth: TextView

    /**支出*/
    private lateinit var tvAllPay: TextView

    /**收入*/
    private lateinit var tvIncome: TextView

    /**返回*/
    private lateinit var ivTitleBack: ImageView

    /**
     * 账户
     * */
    private lateinit var ivAccounts: ImageView
    private lateinit var btnAddBill: Button

    private lateinit var vpCashbook: ViewPager2
    private lateinit var tlCashBook: TabLayout

    private var selectTime = System.currentTimeMillis()


    private val mCashBookMainPresent: CashBookMainPresent by lazy {
        CashBookMainPresent(this)
    }

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        supportActionBar?.hide()
    }

    override fun initResLayId() = mBind.root

    override fun bindView(savedInstanceState: Bundle?, mBaseProjectsActivity: BaseProjectsActivity) {
        initView()
        initListener()
        initTime(selectTime)
        mCashBookMainPresent.initSubscriptions()
        initData()
    }

    override fun onStart() {
        super.onStart()
        mCashBookMainPresent.initTotalData(System.currentTimeMillis())
    }

    override fun onDestroy() {
        super.onDestroy()
        mCashBookMainPresent.onDestroy()
    }

    private fun initView() {
        ivAccounts = mBind.ivAccounts
        llDate = mBind.llDate
        tvDateYear = mBind.tvDateYear
        tvDateMonth = mBind.tvDateMonth
        tvAllPay = mBind.tvAllPay
        tvAllPay.text = STR_INT_ZERO
        tvIncome = mBind.tvIncome
        tvIncome.text = STR_INT_ZERO
        tlCashBook = mBind.tlCashBook
        btnAddBill = mBind.btnAddBill
        ivTitleBack = mBind.ivTitleBack
        vpCashbook = mBind.vpCashbook
        vpCashbook.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
                val rootView = LayoutInflater.from(parent.context).inflate(
                        getLayoutId(viewType), parent, false)
                rootView.findViewById<RecyclerView>(R.id.rvCommon)?.apply {
                    val linearLayoutManager = LinearLayoutManager(context)
                    layoutManager = linearLayoutManager
                    addItemDecoration(CashBookListItemDecoration(this@CashBookMainActivity))
                    setBackgroundColor(WkContextCompat.getColor(R.color.common_white_FFFCFC))
                    setPadding(pad, 0, pad, 0)
                    addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
                    adapter =  when(viewType){
                        2->{
                            mAssetsInfoAdapter
                        }
                        else->{
                            cashListAdapter
                        }
                    }
                }

                return CardViewHolder(rootView)
            }

            override fun getItemCount() = tabTitles.size

            override fun getItemViewType(position: Int): Int {
                return position
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder.apply {
                    val tvCommon = itemView.findViewById<TextView>(R.id.tv)
                    tvCommon?.apply {
                        setText(tabTitles[position])
                        textSize = 100f
                        setTextColor(WkContextCompat.getColor( R.color.colorAccent))
                    }
                }
            }

            private fun getLayoutId(viewType: Int) =
                    when(viewType){
                        LAYOUT_VIEWPAGER_DETAILED, LAYOUT_VIEWPAGER_ACCOUNT ->{
                            R.layout.common_only_recycler
                        }
                        else->{
                            R.layout.common_default
                        }
                    }

        }
        TabLayoutMediator(tlCashBook, vpCashbook) { tab: TabLayout.Tab, position: Int ->
            tab.setText(tabTitles[position])
        }.attach()
    }

    private fun initTime(selectTime: Long) {
        val yearAndMonth = mCashBookMainPresent.getYearAndMonth(selectTime)
        tvDateYear.text = yearAndMonth.first.toString()
        tvDateMonth.text = yearAndMonth.second.toString()
    }

    private fun initListener() {
        ivAccounts.setOnClickListener(this)
        llDate.setOnClickListener(this)
        ivTitleBack.setOnClickListener(this)
        btnAddBill.setOnClickListener(this)
        tlCashBook.addOnTabSelectedListener(this)
        vpCashbook.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
    }

    fun initTotalData(totalData: Pair<Double, Double>) {
        tvAllPay.text = NumberUtil.initMoneyToString(totalData.second)
        tvIncome.text =  NumberUtil.initMoneyToString(totalData.first)
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.llDate -> {
                ChooseMonthDialog.create(simpleOnlyEtDialogListener = this).show(this)
            }
            R.id.btnAddBill -> {
                val intene = Intent(this, TradeRecordInfoActivity::class.java)
                startActivityForResult(intene, 1)
            }
            R.id.ivAccounts -> {
                val intent = Intent(this, AccountListActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun initData() {
        mCashBookMainPresent.initCashBookList(selectTime)
        mCashBookMainPresent.initAssetsInfo()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCashBookMainPresent.updateData(data)

    }

    fun replaceRecodeList(data: List<ITradeRecodeShowBean>) {
        cashListAdapter.replaceList(data)
    }

    fun replaceAssetsInfoData(data:List<AssetsInfoShowBean>){
        mAssetsInfoAdapter.updateData(data)
    }


    fun getData(position: Int) = cashListAdapter.getData(position)

    fun addData(tradeRecodeShowBean: ITradeRecodeShowBean) {
        cashListAdapter.addData(tradeRecodeShowBean)
    }

    fun replaceData(tradeRecodeShowBean: ITradeRecodeShowBean, position: Int, needSort: Boolean = false) {
        cashListAdapter.replaceData(tradeRecodeShowBean, position, needSort)
    }

    override fun onItemChildClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        when(adapter){
            is MainAssetsInfoAdapter ->{
                when(view?.id?:View.NO_ID){
                    //现金
                    R.id.tvAssetsInfoCash->{

                    }
                    //资产
                    R.id.tvAssetsInfoAssets->{

                    }
                    //负债
                    R.id.tvAssetsInfoLiabilities->{

                    }
                }
            }
        }
    }

    override fun onItemLongClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemLongClick(adapter, view, position)
        when(adapter){
            is CashListAdapter ->{
                val bundle = Bundle()
                val realPosition=adapter.getRealPosition(position)
                val item=adapter.getData(realPosition)
                bundle.putInt(WkStringConstants.STR_POSITION_LOW, realPosition)
                bundle.putLong(TradeRecode.TRADE_RECODE_ID, item.getTradeRecodeId())
                bundle.putString(BundleKey.LIST_ITEM_NAME, item.getShowText())
                DeleteCashBookDialog.create(bundle).show(supportFragmentManager)
            }
        }
    }

    override fun onItemChildLongClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemChildLongClick(adapter, view, position)
    }

    override fun onItemClick(adapter: RecyclerView.Adapter<*>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        when(adapter){
            is CashListAdapter ->{
                val intent = Intent(this, TradeRecordInfoActivity::class.java)
                val realPosition=adapter.getRealPosition(position)
                intent.putExtra(WkStringConstants.STR_POSITION_LOW, realPosition)
                val tradeRecodeId=adapter.getData(realPosition).getTradeRecodeId()
                intent.putExtra(TradeRecode.TRADE_RECODE_ID, tradeRecodeId)
                WkLog.d("click id: $tradeRecodeId")
                startActivityForResult(intent, 1)
            }
        }

    }

    override fun communication(flag: Int, bundle: Bundle?, any: Any?) {
        super.communication(flag, bundle, any)
        if (flag == IFAFlag.DELETE_ITEM_DIALOG) {
            mCashBookMainPresent.deleteItem(bundle)
        }
    }

    fun removeData(position: Int) {
        cashListAdapter.remove(position)
    }

    override fun ok(bundle: Bundle?): Boolean {
        val year = bundle?.getString("year") ?: return false
        val month = bundle.getString("month") ?: return false
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year.toInt())
        calendar.set(Calendar.MONTH, month.toInt() - 1)
        tvDateMonth.text = month
        tvDateYear.text = year
        WkLog.d("year: $year  month:  $month  time: ${DateTime.getDateString(calendar.timeInMillis)}")
        selectTime = calendar.timeInMillis
        initData()
        return false
    }

    override fun cancel(bundle: Bundle?) = false
}