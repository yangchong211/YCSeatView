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
public class SeatTypeAdapter extends RecyclerView.Adapter<SeatTypeAdapter.MyViewHolder> {

    private Context mContext;
    private OnItemClickListener listener;
    private List<SeatBean> data;

    public SeatTypeAdapter(Context context, ArrayList<SeatBean> list) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (data!=null && data.size()>0){
            SeatBean seatBean = data.get(position);
            if (seatBean.getName()!=null){
                //holder.tvName.setText(seatBean.getName()+"/n"+ seatBean.getColumn()+"/"+seatBean.getLine());
                holder.tvName.setText(seatBean.getName());
            } else {
                holder.tvName.setText("");
            }
            if (seatBean.isCorridor()){
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.width = 80;
                holder.itemView.setLayoutParams(layoutParams);
            } else {
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.width = 250;
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
                        listener.onItemClick(getAdapterPosition());
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
