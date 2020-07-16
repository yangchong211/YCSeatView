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

    /**
     * 添加调课位座位类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        //左侧增加一列
        int TYPE_1 = 1;
        //右侧增加一列
        int TYPE_2 = 2;
        //后方增加一列
        int TYPE_3 = 3;
    }

    /**
     * 座位类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface SeatType {
        //正常
        int TYPE_1 = 1;
        //调课位
        int TYPE_2 = 2;
        //过道
        int TYPE_3 = 3;
        //不可坐
        int TYPE_4 = 4;
    }

}
