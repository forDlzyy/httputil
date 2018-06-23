package com.cib.fintech.ufm.mock.utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

public class Utils {

    public static final DateFormat TIME_FORMAT_SHORT = new SimpleDateFormat(ServiceConst.DATETIME_FORMAT_SHORT);

    public static final DateFormat TIME_FORMAT_FULL = new SimpleDateFormat(ServiceConst.DATETIME_FORMAT_FULL);

    public static final DateFormat DATE_FORMAT_1 = new SimpleDateFormat(ServiceConst.DATETIME_FORMAT_DATE);

    private static final DecimalFormat AMONT_FOAMAT = new DecimalFormat("0.00");

    /**
     * 相同精度下比较浮点数大小<br/>
     * 精确到0.01
     * 
     * @param a
     * @param b
     * @return
     */
    public static boolean isEqual(double a, double b) {
        return formatAmt(a).equals(formatAmt(b));
    }

    public static String formatAmt(Double amt) {
        if (amt != null) {
            return AMONT_FOAMAT.format(amt);
        }
        return "0";
    }

    public static String formatAmt(BigDecimal amt) {
        if (amt != null) {
            return AMONT_FOAMAT.format(amt);
        }
        return "0";
    }

    /**
     * 格式化主键
     * 
     * @param value
     * @param length
     * @return
     */
    public static String formatPKey(long value, int length) {
        if (String.valueOf(value).length() > length) {

            return StringUtils.right(String.valueOf(value), length);
        } else {

            return StringUtils.leftPad(String.valueOf(value), length, '0');
        }
    }

    public static String getYesterDay(Date date) {
        Date yesterday = DateUtils.addDays(date, -1);
        return Utils.DATE_FORMAT_1.format(yesterday);
    }

    /**
     * 获取当天的前一天时间 比如今天是23号 那么前一天就是22号
     * 
     * @return
     */
    public static Date getYesterday() {
        Date date = new Date();// 取时间
        return DateUtils.addDays(date, -1);
    }

    private Utils() {
        // empty.
    }

}
