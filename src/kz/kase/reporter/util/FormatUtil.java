package kz.kase.reporter.util;

import java.math.RoundingMode;
import java.text.*;
import java.util.Date;
import java.util.Locale;

public class FormatUtil {

    //    public static final String NO_DATE_SET = "";
    public static final String NO_DATE_SET = "----;--;--";
    public static final int DEFAULT_PRECISION = 6;

    private static NumberFormat defaultFormat =
            NumberFormat.getNumberInstance(createDefaultLocale());

    private static final NumberFormat doubleFormat = new DecimalFormat("###,###,##0.00");


    private static final NumberFormat quoteFormat0 = new DecimalFormat("###,###,##0");
    private static final NumberFormat quoteFormat1 = new DecimalFormat("###,###,##0.0");
    private static final NumberFormat quoteFormat2 = new DecimalFormat("###,###,##0.00");
    private static final NumberFormat quoteFormat3 = new DecimalFormat("###,###,##0.000");
    private static final NumberFormat quoteFormat4 = new DecimalFormat("###,###,##0.0000");


    private static final DateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", new Locale("RU"));
    private static final DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", new Locale("RU"));


    private static final DecimalFormat decimalFormat2 = new DecimalFormat("#.00");
    private static final DecimalFormat decimalFormat4 = new DecimalFormat("###,###,##0.0000");

    static {
        decimalFormat4.setRoundingMode(RoundingMode.HALF_UP);
    }

//    public static Double formatWithPrecisionDouble(Number num, int precision) {
//        NumberFormat numb = NumberFormat.getNumberInstance(Locale.getDefault());
//        numb.setMaximumFractionDigits(precision);
//        return Double.parseDouble(numb.format(num));
//    }

    public static Format getFormat(int precision) {
        if (precision == 0) {
            return quoteFormat0;
        } else if (precision == 1) {
            return quoteFormat1;
        } else if (precision == 2) {
            return quoteFormat2;
        } else if (precision == 3) {
            return quoteFormat3;
        } else if (precision == 4) {
            return quoteFormat4;

        } else {
            return quoteFormat2;
        }
    }

    public static String format(Number num, int precision) {
        if (num == null) return "";
        if (precision == 0) {
            return quoteFormat0.format(num);
        } else if (precision == 1) {
            return quoteFormat1.format(num);
        } else if (precision == 2) {
            return quoteFormat2.format(num);
        } else if (precision == 3) {
            return quoteFormat3.format(num);
        } else if (precision == 4) {
            return quoteFormat4.format(num);

        } else {
            return quoteFormat2.format(num);
        }
    }

    public static String format(Number num) {
        if (num == null) return "N/A";
        if (num instanceof Float) {
            return doubleFormat.format(round((Float) num, DEFAULT_PRECISION));
//            return doubleFormat.format(num);
        } else if (num instanceof Double) {
            return doubleFormat.format(round((Double) num, DEFAULT_PRECISION));
//            return doubleFormat.format(num);
        } else if (num instanceof SignedDouble) {
            return format((SignedDouble) num);
        } else {
            return defaultFormat.format(num);
        }
    }

    public static String format4(double value) {
        return decimalFormat4.format(value);
    }

    public static String format(Date date) {
        if (date == null) return NO_DATE_SET;
        return dateFormatter.format(date);
    }

    public static String format(DateTS dateTS) {
        if (dateTS == null) return NO_DATE_SET;
        return format(dateTS.getDate());
    }

//    public static String format(TimeTS timeTS) {
//        if (timeTS == null) return NO_DATE_SET;
//        return timeFormatter.format(timeTS.getDate());
//    }

    public static String format(SignedDouble signedDouble) {
        double val = signedDouble.doubleValue();
        String formatted = format(val);
        return val > 0 ? "+" + formatted : formatted;
    }

    public static String format2(double value) {
        return decimalFormat2.format(value);
    }


    public static Number parse(String val, Number defValue) {
        if (val == null) return defValue;
        val = val.trim();
        try {
            return defaultFormat.parse(val);
        } catch (ParseException e) {
            return defValue;
        }
    }

    public static Integer parseInt(String val, Integer defValue) {
        return parse(val, defValue).intValue();
    }

    public static Double parseDouble(String val, Double defValue) {
        return parse(val, defValue).doubleValue();
    }

//    public static Double pareDouble(double number,int pricision){
//          if(pricision == 2){
//              doubleFormat.parse
//          }
//    }

    public static Long parseLong(String val, Long defValue) {
        return parse(val, defValue).longValue();
    }

    public static Integer parseInt(String val) {
        return parseInt(val, 0);
    }

    public static Double parseDouble(String val) {
        return parseDouble(val, 0.0);
    }

    public static Long parseLong(String val) {
        return parseLong(val, 0L);
    }

//    public static String getDirectionTitle(Markets.OrderDir dir) {
//        int a = 0;
//        switch (dir) {
//            case BUY:
//                return Lang.getStr("a_buy");
//            case SELL:
//                return Lang.getStr("a_sell");
//        }
//        return null;
//    }

    public static double round(double val, int zeros) {
        int order = (int) Math.pow(10, zeros);
//        double ceil = Math.round(val);
//        return ceil + (double) Math.round((val - ceil) * order) / order;
        return (double) Math.round(val * order) / order;
    }

    public static final double[] PRECISIONS = {0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001};

    public static boolean compare(Double d1, Double d2, int prec) {
        double d = Math.abs(d1 - d2);
        return !(prec < 0 || prec >= PRECISIONS.length) && Math.abs(d1 - d2) < PRECISIONS[prec + 1];
    }


    public static class SignedDouble extends Number {

        private double doubleVal = 0;

        public SignedDouble(double doubleVal) {
            this.doubleVal = doubleVal;
        }

        @Override
        public int intValue() {
            return (int) doubleVal;
        }

        @Override
        public long longValue() {
            return (long) doubleVal;
        }

        @Override
        public float floatValue() {
            return (float) doubleVal;
        }

        @Override
        public double doubleValue() {
            return doubleVal;
        }
    }

    private static Locale createDefaultLocale() {
        for (Locale locale : NumberFormat.getAvailableLocales()) {
            if ("ru".equalsIgnoreCase(locale.getLanguage())) {
                return locale;
            }
        }
        return null;
    }

}
