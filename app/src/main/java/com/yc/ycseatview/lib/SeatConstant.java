package com.yc.ycseatview.lib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/16
 *     desc   : 自定义座位常量
 *     revise:
 * </pre>
 */
public final class SeatConstant {

    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        //左侧增加一列
        int TYPE_1 = 1;
        //右侧增加一列
        int TYPE_2 = 2;
        //后方增加一列
        int TYPE_3 = 3;
    }

}
