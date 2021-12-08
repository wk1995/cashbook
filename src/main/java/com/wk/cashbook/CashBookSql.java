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
    public static final String SQL_QUERY_SUM_AMOUNT_CATEGORY="select sum(a.amount),b.categoryname from traderecode a " +
            "join (select a.id,a.topid,b.categoryname from (select id,(CASE  WHEN parentid<=-1 THEN  id ELSE parentid END) AS topid from tradecategory) a " +
            "join tradecategory b on a.topid=b.id) b on a.categoryid=b.id " +
            "where a.tradetime>? and a.tradetime<? " +
            "group by b.topid ;";

    /**
     * 首页数据
     * 需要分组，每个标题需要统计收入支出数据
     *
     * 最后结果是交易记录列表，每行数据增加一列，根类别名
     *
     * id,tradetime,amount,tradenote,categoryname
     * */
    public static final String SQL_QUERY_TRADE_RECODE_GROUP_DATE=" select a.id,a.tradetime,a.amount,a.tradenote,c.categoryname from traderecode a \n" +
            " join (select id,(CASE  WHEN parentid<=-1 THEN  id ELSE parentid END) AS topid from tradecategory) b \n" +
            " join tradecategory c\n" +
            " on b.topid=c.id and a.categoryid=b.id\n" +
            " where a.tradetime>? and a.tradetime<? " +
            " order by a.tradetime desc;";
}
