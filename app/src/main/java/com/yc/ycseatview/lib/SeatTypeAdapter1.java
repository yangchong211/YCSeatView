package com.yc.ycseatview.lib;


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
public class SeatTypeAdapter1 extends RecyclerView.Adapter<SeatTypeAdapter1.MyViewHolder> {

    private Context mContext;
    private OnItemClickListener listener;
    private List<SeatBean> data;

    public SeatTypeAdapter1(Context context, ArrayList<SeatBean> list) {
        this.mContext = context;
        this.data = list;
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_seat_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (data!=null && data.size()>0){
            SeatBean seatBean = data.get(position);
            if (seatBean.getName()!=null){
                holder.tvName.setText(seatBean.getName());
            } else {
                holder.tvName.setText("");
            }
            if (seatBean.isCorridor()){
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                holder.itemView.setLayoutParams(layoutParams);
            } else {
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                holder.itemView.setLayoutParams(layoutParams);
            }
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
                        listener.onItemClick(v,getAdapterPosition());
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
