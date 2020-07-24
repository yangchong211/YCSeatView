package com.yc.ycseatview.lib;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yc.ycseatview.R;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/15
 *     desc   : 自定义座位控件适配器adapter
 *     revise:
 * </pre>
 */
public class SeatTypeAdapter extends AbsSeatAdapter<SeatBean> {

    private boolean isSetClassTag;

    public SeatTypeAdapter(Context context){
        super(context,R.layout.item_seat_view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindData(SeatViewHolder holder, SeatBean seatBean) {
        TextView tvStudentName = holder.getView(R.id.tv_student_name);
        ImageView ivClassIcon = holder.getView(R.id.iv_class_icon);
        if (seatBean!=null){
            if (seatBean.getType() == SeatConstant.SeatType.TYPE_3){
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.width = SeatPictureUtils.dip2px(context,50);
                holder.itemView.setLayoutParams(layoutParams);
            } else {
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = SeatPictureUtils.dip2px(context,34);
                layoutParams.width = SeatPictureUtils.dip2px(context,112);
                holder.itemView.setLayoutParams(layoutParams);
            }

            if (isSetClassTag){
                //标记课程
                setClassTagTypeData(holder,seatBean);
            } else {
                //设置座位
                setClassNormalTypeData(holder,seatBean);
            }

            if (seatBean.isLongSelect()){
                //长按
                tvStudentName.setTextColor(Color.parseColor("#FFAB57"));
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_drag_r6);
            }
        }
    }

    /**
     * 设置座位
     * @param holder                                holder
     * @param seatBean                              bean
     */
    @SuppressLint("SetTextI18n")
    private void setClassNormalTypeData(SeatViewHolder holder, SeatBean seatBean) {
        TextView tvStudentName = holder.getView(R.id.tv_student_name);
        ImageView ivClassIcon = holder.getView(R.id.iv_class_icon);
        tvStudentName.setVisibility(View.VISIBLE);
        ivClassIcon.setVisibility(View.GONE);
        int type = seatBean.getType();
        int studentType = seatBean.getStudentType();
        SeatLogUtils.i("SeatTypeAdapter--------"+seatBean.getIndex()+"------座位类型座位类型-------"+type);
        SeatLogUtils.i("SeatTypeAdapter--------"+seatBean.getIndex()+"------学生类型-------"+studentType);
        //座位类型
        switch (type){
            //正常座位
            case SeatConstant.SeatType.TYPE_1:
                if (seatBean.getName()!=null){
                    tvStudentName.setText(seatBean.getName()+"\n"+ seatBean.getColumn()+ "列" +"/"+seatBean.getLine()+"行");
                    //holder.tvName.setText(seatBean.getName());
                } else {
                    tvStudentName.setText("");
                }
                tvStudentName.setTextColor(context.getResources().getColor(R.color.color_666666));
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_normal_r6);
                setStudentType(holder,seatBean);
                break;
            //调课位
            case SeatConstant.SeatType.TYPE_2:
                tvStudentName.setText("调课位"+seatBean.getIndex()+"\n"+ seatBean.getColumn()+ "列" +"/"+seatBean.getLine()+"行");
                tvStudentName.setTextColor(context.getResources().getColor(R.color.color_44a3f2));
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_class_r6);
                setStudentType(holder,seatBean);
                break;
            //过道
            case SeatConstant.SeatType.TYPE_3:
                tvStudentName.setText("过"+"\n"+"道"+"\n"+seatBean.getIndex()+"\n"+seatBean.getColumn()+ "列" +"\n/"+seatBean.getLine()+"行");
                tvStudentName.setTextColor(context.getResources().getColor(R.color.color_666666));
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_corridor_r6);
                break;
            //不可坐
            case SeatConstant.SeatType.TYPE_4:
                tvStudentName.setText("不可坐"+seatBean.getIndex());
                tvStudentName.setTextColor(context.getResources().getColor(R.color.color_999999));
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_corridor_r6);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setStudentType(SeatViewHolder holder, SeatBean seatBean) {
        TextView tvStudentName = holder.getView(R.id.tv_student_name);
        ImageView ivClassIcon = holder.getView(R.id.iv_class_icon);
        int studentType = seatBean.getStudentType();
        //学生类型
        switch (studentType){
            //未知状态
            case SeatConstant.StudentType.STUDENT_0:
                tvStudentName.setText("未知"+seatBean.getIndex());
                tvStudentName.setTextColor(context.getResources().getColor(R.color.color_666666));
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_normal_r6);
                break;
            //请假。学生请假
            case SeatConstant.StudentType.STUDENT_1:
                tvStudentName.setText(seatBean.getName()+"\n"+ "请假" + seatBean.getIndex());
                tvStudentName.setTextColor(context.getResources().getColor(R.color.color_f00101));
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_normal_r6);
                break;
            //调课位学生。指的是在调课位位置的学生
            case SeatConstant.StudentType.STUDENT_2:
                tvStudentName.setText("调课学生"+seatBean.getIndex());
                tvStudentName.setTextColor(context.getResources().getColor(R.color.color_666666));
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_normal_r6);
                break;
            //调期学员。添加新的学员，类似插班生
            case SeatConstant.StudentType.STUDENT_3:
                tvStudentName.setText("调期学员"+seatBean.getIndex());
                tvStudentName.setTextColor(context.getResources().getColor(R.color.color_666666));
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_normal_r6);
                break;
            //空座位。指的是没有学生坐的座位，场景是删除学生后只是用于UI显示
            case SeatConstant.StudentType.STUDENT_4:
                tvStudentName.setText("空座位"+seatBean.getIndex());
                tvStudentName.setTextColor(context.getResources().getColor(R.color.color_999999));
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_empty_r6);
                break;
            //正常学生
            case SeatConstant.StudentType.STUDENT_5:
                if (seatBean.getName()!=null){
                    tvStudentName.setText(seatBean.getName()+"\n"+ seatBean.getColumn()+ "列" +"/"+seatBean.getLine()+"行");
                    //holder.tvName.setText(seatBean.getName());
                } else {
                    tvStudentName.setText("");
                }
                tvStudentName.setTextColor(context.getResources().getColor(R.color.color_666666));
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_normal_r6);
                break;
        }
    }

    /**
     * 标记课程
     * @param holder                                holder
     * @param seatBean                              bean
     */
    @SuppressLint("SetTextI18n")
    private void setClassTagTypeData(SeatViewHolder holder, SeatBean seatBean) {
        TextView tvStudentName = holder.getView(R.id.tv_student_name);
        ImageView ivClassIcon = holder.getView(R.id.iv_class_icon);
        tvStudentName.setVisibility(View.GONE);
        ivClassIcon.setVisibility(View.VISIBLE);
        if (seatBean.getType() == SeatConstant.SeatType.TYPE_4){
            //不可坐
            ivClassIcon.setImageResource(R.drawable.icon_not_set_seat);
            holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_empty_r6);
        } else {
            //可坐
            ivClassIcon.setImageResource(R.drawable.icon_normal_class_seat);
            holder.itemView.setBackgroundResource(R.drawable.shape_seat_type_normal_r6);
        }
    }

    public void setClassTag(boolean isSetClassTag) {
        this.isSetClassTag = isSetClassTag;
    }
}
