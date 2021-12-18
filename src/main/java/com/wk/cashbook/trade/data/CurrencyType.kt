package com.wk.cashbook.trade.data

/**
 *
 *      author : wk
 *      e-mail : 1226426603@qq.com
 *      time   : 2021/10/14
 *      desc   : 币种，货币类型
 *      see http://www.webmasterhome.cn/huilv/huobidaima.asp
 *      GitHub : https://github.com/wk1995
 *      CSDN   : http://blog.csdn.net/qq_33882671
 * @param mCurrencyCode 货币代码
 * @param chinese 中文
 * @param english 英文
 * */
@Suppress("unused")
enum class CurrencyType(val mCurrencyCode: String, private val chinese: String,
                        private val english: String, val mSymbol: String) {

    RenMinBi("CYN", "人民币", "RenMinBi", "￥"),
    Dollar("USD", "美元", "Dollar", "\$"),
    HongKongDollar("HKD", "港币", "Hong Kong Dollar", "HK\$"),
    NewTaiwanDollar("TWD", "新台币", "New Taiwan Dollar", "NT\$"),
    MacauPataca("MOP", "澳门元", "Macau Pataca", "MOP\$"),
    SouthKoreanWon("KRW", "韩元", "South Korean Won", "₩"),
    ThaiBaht("THB", "泰铢", "Thai Baht", "฿"),
    Euro("EUR", "欧元", "Euro", "€"),
    BritishPound("GBP", "英镑", "British Pound", "￡"),
    SingaporeDollar("SGD", "新加坡元", "Singapore Dollar", "S\$"),
    AustraliaDollar("AUD", "澳元 ", "Australia Dollar", "A\$"),
    CanadianDollar("CAD", "加元 ", "Canadian Dollar", "C\$"),
    JapaneseYen("JPY", "日元", "Japanese Yen", "JPY￥")


}