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
     * 座位类型
     */
    private int type;
    /**
     * 学生类型
     */
    private int studentType;
    /**
     * 位于第几列
     */
    private int column;
    /**
     * 位于第几行
     */
    private int line;
    /**
     * 位置【竖直方向】
     * 作用：demo计算使用
     * 这个是从上往下，然后从左往右的index
     */
    private int index;
    /**
     * 设置调课位类型
     */
    private int classType;
    /**
     * 长按选中。
     */
    private boolean longSelect;

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

    public int getStudentType() {
        return studentType;
    }

    public void setStudentType(int studentType) {
        this.studentType = studentType;
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

    public int getClassType() {
        return classType;
    }

    public void setClassType(int classType) {
        this.classType = classType;
    }

    public boolean isLongSelect() {
        return longSelect;
    }

    public void setLongSelect(boolean longSelect) {
        this.longSelect = longSelect;
    }

    @Override
    public String toString() {
        return "SeatBean{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", column=" + column +
                ", line=" + line +
                ", index=" + index +
                '}';
    }
}
