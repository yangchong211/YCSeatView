package com.yc.ycseatview.lib;

import java.io.Serializable;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/15
 *     desc   : 自定义座位控件实体类
 *     revise:
 * </pre>
 */
public class SeatBean implements Serializable {

    /**
     * 名字
     */
    private String name;
    /**
     * 类型
     */
    private int type;
    /**
     * 位于第几列
     */
    private int column;
    /**
     * 位于第几行
     */
    private int line;
    /**
     * 位置
     */
    private int index;
    /**
     * 是否选中
     */
    private boolean select;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
