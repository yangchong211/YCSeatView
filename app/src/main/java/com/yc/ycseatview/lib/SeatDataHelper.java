package com.yc.ycseatview.lib;

import java.util.ArrayList;
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
public class SeatDataHelper {

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
     * @param mList                     集合数据
     * @param line                      行
     */
    public static void notifyListAndSet(ArrayList<SeatBean> mList, int line) {
        if (mList==null || mList.size()==0 || line==0){
            return;
        }
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
     * 获取座位排好的map数据
     * @param total                         总共数量
     * @return
     */
    public static LinkedHashMap<Integer, ArrayList<SeatBean>> getSeatMap(int total , int line) {
        LinkedHashMap<Integer , ArrayList<SeatBean>> mSeatMap = new LinkedHashMap<>();
        int xColumn = 1;
        ArrayList<SeatBean> newList = new ArrayList<>();
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
            if (xColumn==beanColumn){
                newList.add(seatBean);
                //最后一列
                if (i+1 == total){
                    mSeatMap.put(xColumn,newList);
                }
                SeatLogUtils.i("SeatRecyclerView------initRecyclerView---第" +xColumn+ "列数据"  +newList.size());
            } else {
                mSeatMap.put(xColumn,newList);
                SeatLogUtils.i("SeatRecyclerView------initRecyclerView-插入--第" +xColumn+ "列数据"  +newList.size());
                xColumn++;
                newList = new ArrayList<>();
                newList.add(seatBean);
            }
        }
        SeatLogUtils.i("SeatRecyclerView------initRecyclerView--1--初始化总学生座位数-" + mSeatMap.size());
        return mSeatMap;
    }

    /**
     * 将map数据添加到list集合中
     * @param mSeatMap                      map集合
     * @return
     */
    public static ArrayList<SeatBean> getListToMap(LinkedHashMap<Integer , ArrayList<SeatBean>> mSeatMap){
        ArrayList<SeatBean> mList = new ArrayList<>();
        Set<Integer> integers = mSeatMap.keySet();
        Iterator<Integer> iterator = integers.iterator();
        while (iterator.hasNext()){
            Integer next = iterator.next();
            ArrayList<SeatBean> seatBeans = mSeatMap.get(next);
            if (seatBeans!=null){
                mList.addAll(seatBeans);
            }
        }
        return mList;
    }

}
