package com.yc.ycseatview.lib;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;
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

    public SeatTypeAdapter(Context context){
        super(context,R.layout.item_seat_view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindData(SeatViewHolder holder, SeatBean seatBean) {
        TextView tvStudentName = holder.getView(R.id.tv_student_name);
        if (seatBean!=null){
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
                    tvStudentName.setText("");
                    break;
                //过道
                case SeatConstant.SeatType.TYPE_3:
                    tvStudentName.setText("过"+"\n"+"道"+holder.getDataPosition());
                    break;
                //不可坐
                case SeatConstant.SeatType.TYPE_4:
                    tvStudentName.setText("不可坐"+holder.getDataPosition());
                    break;
            }
            if (type == SeatConstant.SeatType.TYPE_3){
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.width = 100;
                //holder.itemView.setBackgroundResource(R.color.colorAccent);
                holder.itemView.setLayoutParams(layoutParams);
            } else {
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.width = 250;
                //holder.itemView.setBackgroundResource(R.drawable.shape_seat_view_null_r5);
                holder.itemView.setLayoutParams(layoutParams);
            }
            holder.itemView.setBackgroundResource(R.drawable.shape_seat_view_null_r5);
            seatBean.setIndex(holder.getDataPosition());
        }
    }

}
