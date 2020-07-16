package com.yc.ycseatview.utils;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time  : 2020/6/12
 *     desc  : 转化工具类
 *     revise:
 * </pre>
 */
public final class NumberUtils {

    public static int parse(String string){
        if (string==null || string.length()==0){
            return 0;
        }
        if (isInteger(string)){
            return Integer.parseInt(string);
        } else if (isFloat(string)){
            return (int) Float.parseFloat(string);
        } else if (isDouble(string)){
            return (int) Double.parseDouble(string);
        }
        return 0;
    }

    public static long parseLong(String string){
        if (string==null || string.length()==0){
            return 0;
        }
        if (isInteger(string)){
            return Integer.parseInt(string);
        } else if (isFloat(string)){
            return (long) Float.parseFloat(string);
        } else if (isDouble(string)){
            return (long) Double.parseDouble(string);
        }
        return 0;
    }

    public static double parseDouble(String string){
        if (string==null || string.length()==0){
            return 0;
        }
        if (isInteger(string)){
            return Integer.parseInt(string);
        } else if (isFloat(string)){
            return Float.parseFloat(string);
        } else if (isDouble(string)){
            return Double.parseDouble(string);
        }
        return 0;
    }

    private static boolean isInteger(String string){
        boolean isInteger;
        try {
            Integer.parseInt(string);
            isInteger = true;
        } catch (NumberFormatException e){
            e.printStackTrace();
            isInteger = false;
        }
        return isInteger;
    }

    private static boolean isFloat(String string){
        boolean isFloat;
        try {
            Float.parseFloat(string);
            isFloat = true;
        } catch (NumberFormatException e){
            e.printStackTrace();
            isFloat = false;
        }
        return isFloat;
    }

    private static boolean isDouble(String string){
        boolean isDouble;
        try {
            Double.parseDouble(string);
            isDouble = true;
        } catch (NumberFormatException e){
            e.printStackTrace();
            isDouble = false;
        }
        return isDouble;
    }

}
