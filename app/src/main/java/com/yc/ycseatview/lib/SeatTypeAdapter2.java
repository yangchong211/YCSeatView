package com.yc.ycseatview.lib;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.yc.ycseatview.R;

import java.util.ArrayList;
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
@Deprecated
public class SeatTypeAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private OnItemClickListener listener;
    private List<SeatBean> data;
    /**
     * 正常
     */
    private static final int TYPE_NORMAL = 1;
    /**
     * 过道
     */
    private static final int TYPE_CORRIDOR = 2;

    public SeatTypeAdapter2(Context context, ArrayList<SeatBean> list) {
        this.mContext = context;
        this.data = list;
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        //type 的值为TYPE_HEADER，TYPE_FOOTER，TYPE_AD，等其中一个
        int type = data.get(position).getType();
        if (type == SeatConstant.SeatType.TYPE_3){
            return TYPE_CORRIDOR;
        } else {
            return TYPE_NORMAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        switch (viewType){
            //过道
            case TYPE_CORRIDOR:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_seat_corridor_view, parent, false);
                return new MyCorridorViewHolder(view);
            //正常布局
            case TYPE_NORMAL:
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_seat_view, parent, false);
                return new MyViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType){
            case TYPE_NORMAL:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                if (data!=null && data.size()>0){
                    SeatBean seatBean = data.get(position);
                    int type = seatBean.getType();
                    switch (type){
                        //正常座位
                        case SeatConstant.SeatType.TYPE_1:
                            if (seatBean.getName()!=null){
                                myViewHolder.tvName.setText(seatBean.getName()+"\n"+ seatBean.getColumn()+ "列" +"/"+seatBean.getLine()+"行");
                                //holder.tvName.setText(seatBean.getName());
                            } else {
                                myViewHolder.tvName.setText("");
                            }
                            break;
                        //调课位
                        case SeatConstant.SeatType.TYPE_2:
                            myViewHolder.tvName.setText("");
                            break;
                        //不可坐
                        case SeatConstant.SeatType.TYPE_4:
                            myViewHolder.tvName.setText("不可坐");
                            break;
                    }
                    ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    layoutParams.width = 250;
                    holder.itemView.setLayoutParams(layoutParams);
                }
                break;
            case TYPE_CORRIDOR:
                MyCorridorViewHolder myCorridorViewHolder = (MyCorridorViewHolder) holder;
                myCorridorViewHolder.tvName.setText("过道");
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.width = 80;
                holder.itemView.setLayoutParams(layoutParams);
                break;
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;

        MyViewHolder(final View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_student_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(itemView,getAdapterPosition());
                    }
                }
            });
        }
    }

    class MyCorridorViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;

        MyCorridorViewHolder(final View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_student_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(itemView,getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


}
