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


    /*----------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------*/


    /**
     * 获取座位排好的map数据
     * @param total                             总共数量
     * @return
     */
    public static LinkedHashMap<Integer, ArrayList<SeatBean>> getSeatMap(int total , int line) {
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = new LinkedHashMap<>();
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
                    map.put(xColumn-1,newList);
                }
                SeatLogUtils.i("SeatRecyclerView------initRecyclerView---第" +xColumn+ "列数据"  +newList.size());
            } else {
                map.put(xColumn-1,newList);
                SeatLogUtils.i("SeatRecyclerView------initRecyclerView-插入--第" +xColumn+ "列数据"  +newList.size());
                xColumn++;
                newList = new ArrayList<>();
                newList.add(seatBean);
            }
        }
        SeatLogUtils.i("SeatRecyclerView------initRecyclerView--1--初始化总学生座位数-" + map.size());
        return map;
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
        //设置坐标
        int seatColumn = 1;
        int seatLine = 1;
        while (iterator.hasNext()){
            Integer next = iterator.next();
            ArrayList<SeatBean> seatBeans = mSeatMap.get(next);
            if (seatBeans!=null && seatBeans.size()>0){
                for (int i=0 ; i<seatBeans.size() ; i++){
                    SeatBean bean = seatBeans.get(i);
                    //设置列
                    bean.setColumn(seatColumn);
                    //设置行
                    bean.setLine(i+seatLine);
                }
                mList.addAll(seatBeans);
            }
            seatColumn++;
        }
        //给数据标记索引，很重要
        for (int i=0 ; i<mList.size() ; i++){
            mList.get(i).setIndex(i);
        }
        return mList;
    }

    /**
     * 添加过道
     * @param mSeatMap
     * @return
     */
    public static LinkedHashMap<Integer, ArrayList<SeatBean>> addCorridor(LinkedHashMap<Integer, ArrayList<SeatBean>> mSeatMap) {
        int size = mSeatMap.size();
        ArrayList<SeatBean> list = new ArrayList<>();
        SeatBean seatBean = new SeatBean();
        seatBean.setType(SeatConstant.SeatType.TYPE_3);
        list.add(seatBean);
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = new LinkedHashMap<>();
        int corridor = 1;
        for (int i=0 ; i<size+1 ; i++){
            if (i < corridor){
                map.put(i,mSeatMap.get(i));
            } else if (i == corridor){
                //插入过道
                map.put(i,list);
                //插入正常数据
                map.put(i+1,mSeatMap.get(i));
            } else {
                //插入正常数据
                map.put(i+1,mSeatMap.get(i));
            }
        }
        return map;
    }

    /**
     * 在最后一行添加调课位
     * @param mSeatMap                                      map数据
     * @return
     */
    public static LinkedHashMap<Integer, ArrayList<SeatBean>> addLastDataClass(
            LinkedHashMap<Integer, ArrayList<SeatBean>> mSeatMap) {
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = new LinkedHashMap<>();
        Set<Integer> integers = mSeatMap.keySet();
        Iterator<Integer> iterator = integers.iterator();
        while (iterator.hasNext()){
            Integer next = iterator.next();
            ArrayList<SeatBean> seatBeans = mSeatMap.get(next);
            if (seatBeans==null){
                continue;
            }
            if (seatBeans.size() != 1) {
                //不是过道。则添加调课位
                SeatBean seatBean = new SeatBean();
                seatBean.setType(SeatConstant.SeatType.TYPE_2);
                seatBeans.add(seatBean);
            }
            map.put(next,seatBeans);
            SeatLogUtils.i("SeatRecyclerView------后方增加一列----" + next + "------" + seatBeans.size());
        }
        return map;
    }

    /**
     * 在左右添加调课位
     * @param mSeatMap                                      map数据
     * @param mLine                                         多少行
     * @param isLeft                                        是否是左边
     * @return
     */
    public static LinkedHashMap<Integer, ArrayList<SeatBean>> addRightOrLeftDataClass(
            LinkedHashMap<Integer, ArrayList<SeatBean>> mSeatMap , int mLine , boolean isLeft) {
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = new LinkedHashMap<>();
        ArrayList<SeatBean> mList = new ArrayList<>();
        for (int i=0 ; i<mLine ; i++){
            SeatBean seatBean = new SeatBean();
            seatBean.setType(SeatConstant.SeatType.TYPE_2);
            mList.add(seatBean);
        }
        if (isLeft){
            //在左侧加一列
            map.put(0,mList);
            //后面依次往后挪动
            Set<Integer> integers = mSeatMap.keySet();
            Iterator<Integer> iterator = integers.iterator();
            while (iterator.hasNext()){
                Integer next = iterator.next();
                map.put(next+1,mSeatMap.get(next));
            }
        } else {
            //在右侧加一列
            map.putAll(mSeatMap);
            map.put(mSeatMap.size()+1,mList);
        }
        return map;
    }
}
