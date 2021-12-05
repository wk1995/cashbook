package com.wk.cashbook;

/**
 * author : wk
 * e-mail : 1226426603@qq.com
 * time   : 2021/11/28
 * desc   :
 * GitHub : https://github.com/wk1995
 * CSDN   : http://blog.csdn.net/qq_33882671
 */
public class CashBookSql {

    /**
     * 查询总类别汇 总数据
     * */
    public static final String SQL_QUERY_SUM_AMOUNT_CATEGORY="select sum(a.amount),b.categoryname " +
            "from traderecode a join (select a.id,a.topid,b.categoryname from (select id,(CASE  WHEN parentid<=-1 THEN  id ELSE parentid END) AS topid from tradecategory) a join tradecategory b on a.topid=b.id) b on a.categoryid=b.id  where a.tradetime>? and a.tradetime<? group\n" +
            " by b.topid \n" +
            " ;";

}
