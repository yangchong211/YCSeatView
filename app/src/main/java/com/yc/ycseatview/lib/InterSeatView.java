package com.yc.ycseatview.lib;


/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/15
 *     desc   : 自定义座位控件接口
 *     revise :
 * </pre>
 */
public interface InterSeatView {

    /**
     * 添加过道
     */
    void addCorridor();

    /**
     * 恢复自动排座
     */
    void restoreSeat();

    /**
     * 更改座位布局
     */
    void changeSeat();

    /**
     * 添加调课位
     * @param type                          类型
     */
    void addTypeClass(@SeatConstant.SeatType int type);
}
