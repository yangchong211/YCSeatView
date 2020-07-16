package com.yc.ycseatview.lib;


import java.io.Serializable;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2018/9/18
 *     desc  : 这个类是用来记录分组列表中组的结构的
 * </pre>
 */
public class GroupStructure implements Serializable {

    /**
     * 是否有过道
     */
    private boolean hasAisle;
    /**
     * 孩子数量
     */
    private int childrenCount;

    public boolean isHasAisle() {
        return hasAisle;
    }

    public void setHasAisle(boolean hasAisle) {
        this.hasAisle = hasAisle;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }
}
