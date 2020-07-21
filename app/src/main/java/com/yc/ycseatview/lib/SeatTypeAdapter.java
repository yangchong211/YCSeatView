package com.yc.ycseatview.lib;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yc.ycseatview.R;

import java.util.List;

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
                tvStudentName.setVisibility(View.GONE);
                ivClassIcon.setVisibility(View.VISIBLE);
                if (seatBean.isSelect()){
                    //不可坐
                    ivClassIcon.setImageResource(R.drawable.icon_not_set_seat);
                } else {
                    ivClassIcon.setImageResource(R.drawable.icon_normal_class_seat);
                }
            } else {
                tvStudentName.setVisibility(View.VISIBLE);
                ivClassIcon.setVisibility(View.GONE);
                int type = seatBean.getType();
                switch (type){
                    //正常座位
                    case SeatConstant.SeatType.TYPE_1:
                        if (seatBean.getName()!=null){
                            tvStudentName.setText(seatBean.getName()+"\n"+ seatBean.getColumn()+ "列" +"/"+seatBean.getLine()+"行");
                            //holder.tvName.setText(seatBean.getName());
                        } else {
                            tvStudentName.setText("");
                        }
                        break;
                    //调课位
                    case SeatConstant.SeatType.TYPE_2:
                        tvStudentName.setText("调课位"+seatBean.getIndex()+"\n"+ seatBean.getColumn()+ "列" +"/"+seatBean.getLine()+"行");
                        break;
                    //过道
                    case SeatConstant.SeatType.TYPE_3:
                        tvStudentName.setText("过"+"\n"+"道"+"\n"+seatBean.getIndex()+"\n"+seatBean.getColumn()+ "列" +"\n/"+seatBean.getLine()+"行");
                        break;
                    //不可坐
                    case SeatConstant.SeatType.TYPE_4:
                        tvStudentName.setText("不可坐"+seatBean.getIndex());
                        break;
                }
                if (seatBean.isSelect()){
                    //不可坐
                    tvStudentName.setText("不可坐"+seatBean.getIndex());
                }
            }
            if (seatBean.isSelect()){
                holder.itemView.setBackgroundResource(R.drawable.shape_seat_not_set_r6);
            } else {
                //其他
                int type = seatBean.getType();
                if (type == SeatConstant.SeatType.TYPE_2){
                    tvStudentName.setTextColor(Color.parseColor("#44A3F2"));
                    holder.itemView.setBackgroundResource(R.drawable.shape_seat_class_data_r6);
                } else {
                    tvStudentName.setTextColor(context.getResources().getColor(R.color.color_666666));
                    holder.itemView.setBackgroundResource(R.drawable.shape_seat_normal_data_r6);
                }
            }

        }
    }

    public void setClassTag(boolean isSetClassTag) {
        this.isSetClassTag = isSetClassTag;
    }
}
