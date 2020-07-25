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
public final class SeatDataHelper {

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
                num = num +1;
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
     * 判断是否有请假的学生
     * @param mList                             集合
     * @return
     */
    public static boolean isHaveLeave(ArrayList<SeatBean> mList) {
        if (mList==null || mList.size()==0){
            return false;
        }
        for (int i=0 ; i<mList.size() ; i++){
            SeatBean bean = mList.get(i);
            //int type = bean.getType();
            int studentType = bean.getStudentType();
            if (studentType == SeatConstant.StudentType.STUDENT_1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否有调期学员。添加新的学员，类似插班生
     * @param mList                             集合
     * @return
     */
    public static boolean isHaveNewStudent(ArrayList<SeatBean> mList) {
        if (mList==null || mList.size()==0){
            return false;
        }
        for (int i=0 ; i<mList.size() ; i++){
            SeatBean bean = mList.get(i);
            //int type = bean.getType();
            int studentType = bean.getStudentType();
            if (studentType == SeatConstant.StudentType.STUDENT_3) {
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
     * @param column                                    列
     * @param line                                      行
     * @return
     */
    public static LinkedHashMap<Integer, ArrayList<SeatBean>> getInitSeatMap(int column , int line , int totalNum) {
        int totalNormal = column * line;
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = new LinkedHashMap<>();
        int xColumn = 1;
        ArrayList<SeatBean> newList = new ArrayList<>();
        for (int i = 0; i < totalNormal; i++) {
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

            if (i >= totalNum){
                //设置空座位
                seatBean.setStudentType(SeatConstant.StudentType.STUDENT_4);
            } else {
                //设置正常学生
                seatBean.setStudentType(SeatConstant.StudentType.STUDENT_5);
            }

            //set数据到map集合，key是列，value是所在列的list集合
            if (xColumn==beanColumn){
                newList.add(seatBean);
                //最后一列
                if (i+1 == totalNormal){
                    map.put(xColumn,newList);
                }
                SeatLogUtils.i("SeatRecyclerView------initRecyclerView---第" +xColumn+ "列数据"  +newList.size());
            } else {
                map.put(xColumn,newList);
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
     * 设置坐标【列/行】
     * 给数据标记索引
     * @param mSeatMap                      map集合
     * @return
     */
    public static ArrayList<SeatBean> getListToMap(LinkedHashMap<Integer , ArrayList<SeatBean>> mSeatMap){
        ArrayList<SeatBean> mList = new ArrayList<>();
        LinkedHashMap<Integer, ArrayList<SeatBean>> newMap = setMap(mSeatMap);
        Set<Integer> integers = newMap.keySet();
        Iterator<Integer> iterator = integers.iterator();
        //设置坐标
        int seatColumn = 1;
        int seatLine = 1;
        while (iterator.hasNext()){
            Integer next = iterator.next();
            ArrayList<SeatBean> seatBeans = newMap.get(next);
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
        ArrayList<SeatBean> list = new ArrayList<>();
        SeatBean seatBean = new SeatBean();
        seatBean.setType(SeatConstant.SeatType.TYPE_3);
        list.add(seatBean);
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = new LinkedHashMap<>();
        //在左侧加一列
        map.put(1,list);
        //后面依次往后挪动
        Set<Integer> integers = mSeatMap.keySet();
        Iterator<Integer> iterator = integers.iterator();
        while (iterator.hasNext()){
            Integer next = iterator.next();
            map.put(next+1,mSeatMap.get(next));
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
        Iterator<Integer> iterator = mSeatMap.keySet().iterator();
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
                seatBean.setClassType(SeatConstant.Type.TYPE_LAST);
                seatBeans.add(seatBean);
            }
            map.put(next,seatBeans);
            SeatLogUtils.i("SeatRecyclerView------后方增加一列----" + next + "------" + seatBeans.size());
        }
        return setMap(map);
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
            if (isLeft){
                seatBean.setClassType(SeatConstant.Type.TYPE_LEFT);
            } else {
                seatBean.setClassType(SeatConstant.Type.TYPE_RIGHT);
            }
            mList.add(seatBean);
        }
        if (isLeft){
            //在左侧加一列
            map.put(1,mList);
            //后面依次往后挪动
            Iterator<Integer> iterator = mSeatMap.keySet().iterator();
            while (iterator.hasNext()){
                Integer next = iterator.next();
                map.put(next+1,mSeatMap.get(next));
            }
        } else {
            //在右侧加一列
            map.putAll(mSeatMap);
            map.put(mSeatMap.size()+3,mList);
        }
        return setMap(map);
    }

    public static LinkedHashMap<Integer, ArrayList<SeatBean>> setMap(LinkedHashMap<Integer, ArrayList<SeatBean>> map){
        LinkedHashMap<Integer, ArrayList<SeatBean>> newMap = new LinkedHashMap<>();
        Set<Integer> integers = map.keySet();
        Iterator<Integer> iterator = integers.iterator();
        int index = 1;
        while (iterator.hasNext()){
            Integer next = iterator.next();
            newMap.put(index,map.get(next));
            index++;
        }
        return newMap;
    }

    /**
     * 判断调课位中是否有学生
     *
     * @param mList
     * @param mSeatMap                                      map集合
     * @return
     */
    public static boolean isHaveStudentClass(ArrayList<SeatBean> mList, LinkedHashMap<Integer, ArrayList<SeatBean>> mSeatMap) {
        Set<Integer> integers = mSeatMap.keySet();
        Iterator<Integer> iterator = integers.iterator();
        int dataNum = 1;
        //记录最后一排调课位的数量
        int classNum = 0;
        int studentNum = 0;
        while (iterator.hasNext()){
            Integer next = iterator.next();
            ArrayList<SeatBean> seatBeans = mSeatMap.get(next);
            //直接看集合中的最后一个数据，是否是调课位类型
            if (seatBeans!=null && seatBeans.size()>1){
                if (seatBeans.get(seatBeans.size()-1).getType()==SeatConstant.SeatType.TYPE_2){
                    //有调课位数据
                    SeatBean bean = seatBeans.get(seatBeans.size() - 1);
                    int classType = bean.getClassType();
                    if (classType == SeatConstant.Type.TYPE_LEFT || classType == SeatConstant.Type.TYPE_RIGHT){
                        //左边一列是调课位
                        //右边一列是调课位
                        return haveStudent(seatBeans);
                    } else {
                        //最后一排是调课位
                        classNum = classNum+1;
                    }
                } else if (seatBeans.get(seatBeans.size()-1).getType()==SeatConstant.SeatType.TYPE_1){
                    //有学生
                    studentNum = studentNum+1;
                }
                //所有遍历完成
                if (dataNum == mSeatMap.size() && classNum>0){
                    if (studentNum==0){
                        //没有学生
                        SeatLogUtils.i("-----isHaveStudentClass----最后一排是调课位------没有学生--");
                        return false;
                    } else {
                        //有学生
                        SeatLogUtils.i("-----isHaveStudentClass----最后一排是调课位------有学生--");
                        return true;
                    }
                }
            }
            dataNum++;
        }
        return false;
    }

    /**
     * 判断左边一列，右边一列调课位是否有学生
     * @param seatBeans                                     list集合
     * @return
     */
    private static boolean haveStudent(ArrayList<SeatBean> seatBeans){
        for (int i=0 ; i<seatBeans.size() ; i++){
            if (seatBeans.get(i).getType() == SeatConstant.SeatType.TYPE_1){
                SeatLogUtils.i("-----isHaveStudentClass----左边/右边一列是调课位------有学生--");
                return true;
            }
        }
        SeatLogUtils.i("-----isHaveStudentClass----左边/右边一列是调课位------没有有学生--");
        return false;
    }

}
