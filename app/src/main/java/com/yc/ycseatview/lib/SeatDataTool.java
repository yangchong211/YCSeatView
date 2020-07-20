package com.yc.ycseatview.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/20
 *     desc   : 帮助类
 *     revise :
 * </pre>
 */
public class SeatDataTool {

    /**
     * 获取座位数据
     * @param total                     总共的
     * @param line                      行
     * @return
     */
    public static ArrayList<SeatBean> getListData(int total, int line) {
        ArrayList<SeatBean> list = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            SeatBean seatBean = new SeatBean();
            seatBean.setIndex(i);
            //设置第几行第几列中的 列
            int beanColumn = i / line +1;
            seatBean.setColumn(beanColumn);
            //设置第几行第几列中的 行
            int beanLine = i % line + 1;
            seatBean.setLine(beanLine);
            seatBean.setType(SeatConstant.SeatType.TYPE_1);
            seatBean.setName("学生" + i);
            list.add(seatBean);
        }
        return list;
    }

    /**
     * 重新设置座位数据
     * @param mList                             集合数据
     * @param line                              行
     */
    public static void notifyListAndSet(ArrayList<SeatBean> mList, int line) {
        if (mList==null || mList.size()==0 || line==0){
            return;
        }
        int mSeatColumn = 1;
        int mSeatLine = 1;
        for (int i=0 ; i<mList.size() ; i++){
            SeatBean bean = mList.get(i);
            int type = bean.getType();
            switch (type){
                //正常
                case SeatConstant.SeatType.TYPE_1:

                    break;
                //调课位
                case SeatConstant.SeatType.TYPE_2:

                    break;
                //过道
                case SeatConstant.SeatType.TYPE_3:

                    break;
                //不可做
                case SeatConstant.SeatType.TYPE_4:

                    break;
            }
        }
    }

    /**
     * 添加最后一行的调课位
     * @param mList
     * @param mLine
     * @return
     */
    public static ArrayList<SeatBean> addLastClass(ArrayList<SeatBean> mList, int mLine) {
        if (mList==null || mList.size()==0 || mLine==0){
            return null;
        }
        ArrayList<SeatBean> newList = new ArrayList<>();
        //获取过道的数量
        int corridorNum = getCorridorNum(mList);
        //添加最后一列
        for (int j=0 ; j<mList.size() ; j++){
            int index = mList.get(j).getIndex();
            if ((index+1) % mLine == 0){
                SeatBean seatBean = new SeatBean();
                seatBean.setType(SeatConstant.SeatType.TYPE_2);
                int addCount = index / mLine;
                int pos = index + addCount + 1;
                seatBean.setIndex(pos);
                newList.add(seatBean);
                SeatLogUtils.i("SeatRecyclerView------后方增加一列---添加排课位数据1--"+addCount+"----"+seatBean.getIndex());
            }
        }
        //修改原始数据
        for (int i=0 ; i<mList.size() ; i++){
            int index = mList.get(i).getIndex();
            //数据索引+
            int addCount = index / mLine;
            int pos = index + addCount;
            mList.get(i).setIndex(pos);
            SeatLogUtils.i("SeatRecyclerView------后方增加一列---修改正常数据索引2--"+mList.get(i).getIndex());
        }
        newList.addAll(mList);
        return newList;
    }

    /**
     * 获取过道的数量
     * @param mList                             集合
     * @return
     */
    public static int getCorridorNum(ArrayList<SeatBean> mList){
        if (mList==null || mList.size()==0){
            return 0;
        }
        int num = 0;
        for (int i=0 ; i<mList.size() ; i++){
            SeatBean bean = mList.get(i);
            int type = bean.getType();
            //过道
            if (type == SeatConstant.SeatType.TYPE_3) {
                num++;
            }
        }
        return num;
    }

    /**
     * 判断是否有调课位
     * @param mList                             集合
     * @return
     */
    public static boolean isHaveClass(ArrayList<SeatBean> mList){
        if (mList==null || mList.size()==0){
            return false;
        }
        for (int i=0 ; i<mList.size() ; i++){
            SeatBean bean = mList.get(i);
            int type = bean.getType();
            if (type == SeatConstant.SeatType.TYPE_2) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对集合数据进行排序
     * @param mList                             集合
     */
    public static void sortList(ArrayList<SeatBean> mList) {
        //重排位置，从小到大
        Collections.sort(mList, new Comparator<SeatBean>() {
            @Override
            public int compare(SeatBean o1, SeatBean o2) {
                long start1 = o1.getIndex();
                long start2 = o2.getIndex();
                if(start1 > start2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

}
