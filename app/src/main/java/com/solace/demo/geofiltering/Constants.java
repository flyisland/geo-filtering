package com.solace.demo.geofiltering;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;

public class Constants {
    static final int POSITIVE = 1;
    static final int NEGATIVE = -1;
    static final BigDecimal TEN = new BigDecimal(10);
    // (https://sites.google.com/site/trescopter/Home/concepts/required-precision-for-gps-calculations)
    static final BigDecimal minimumUnit = new BigDecimal("0.00001");
    static final HashMap<DIMS, DecimalFormat> df = new HashMap<>();

    public enum DIMS {
        X, Y
    }

    static {
        var scale = minimumUnit.scale();
        df.put(DIMS.X, new DecimalFormat("000."+"0".repeat(scale))); // -180~180
        df.put(DIMS.Y, new DecimalFormat("00."+"0".repeat(scale))); // -90~90
    }
}
