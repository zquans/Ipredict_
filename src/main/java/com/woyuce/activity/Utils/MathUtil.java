package com.woyuce.activity.Utils;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/12/12.
 * 用于解决商业计算，小数点保留两位的金额，Float/Double会有精度问题
 */
public class MathUtil {
    private static final int DEF_DIV_SCALE = 10;

    private MathUtil() {
    }

    public static double add(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.add(b2).doubleValue();

    }

    public static double sub(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.subtract(b2).doubleValue();

    }

    public static double mul(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.multiply(b2).doubleValue();

    }

    public static double div(double d1, double d2) {

        return div(d1, d2, DEF_DIV_SCALE);

    }

    public static double div(double d1, double d2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

    }
}