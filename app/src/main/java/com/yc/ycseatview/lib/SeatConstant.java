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
        int TYPE_LEFT = 1;
        //右侧增加一列
        int TYPE_RIGHT = 2;
        //后方增加一列
        int TYPE_LAST = 3;
    }

    /**
     * 座位类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface SeatType {
        //普通座位
        int TYPE_1 = 1;
        //调课位
        int TYPE_2 = 2;
        //过道
        int TYPE_3 = 3;
        //不可坐。不能拖动交换
        int TYPE_4 = 4;
    }

    /**
     * 学生类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface StudentType {
        //请假。学生请假
        int STUDENT_1 = 5;
        //调课位学生。指的是在调课位位置的学生
        int STUDENT_2 = 6;
        //调期学员。添加新的学员，类似插班生
        int STUDENT_3 = 7;
        //空座位。指的是没有学生坐的座位
        int STUDENT_4 = 8;
        //正常学生。
        int STUDENT_5 = 9;
        //未知状态。删除学生后的空数据
        int STUDENT_0 = 10;
    }

    /**
     * 视图类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewType {
        //删除学员视图
        int TYPE_1 = 1;
        //调课位，添加调课位学员和标记不可坐视图
        int TYPE_2 = 2;
        //标记请假视图
        int TYPE_3 = 3;
        //删除过道视图
        int TYPE_4 = 4;
        //回到原视图
        int TYPE_5 = 5;
        //添加调期学员，类似插班生【调课学员UI也是这个】
        int TYPE_6 = 6;
        //取消请假视图
        int TYPE_7 = 7;
    }

}
