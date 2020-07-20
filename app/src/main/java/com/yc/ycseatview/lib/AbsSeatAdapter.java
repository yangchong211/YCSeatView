package com.yc.ycseatview.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/15
 *     desc   : 自定义座位控件抽象适配器
 *     revise:
 * </pre>
 */
public abstract class AbsSeatAdapter<T> extends RecyclerView.Adapter<SeatViewHolder>  {

    private Context context;
    private int layoutId;
    private List<T> data;
    public MultiTypeSupport<T> multiTypeSupport;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    //默认可以回收
    private boolean isRecycle;
    private int position;
    private int line;

    private AbsSeatAdapter() {
        //默认可以回收
        this.isRecycle = true;
    }

    /**
     * 设置座位数据有几行
     * @param line
     */
    public void setDataLine(int line) {
        this.line = line;
    }

    /**
     * 获取座位数据有几行
     * @return
     */
    public int getLine() {
        return line;
    }

    /**
     * 构造方法
     * @param layoutId      布局
     * @param context       上下文
     */
    public AbsSeatAdapter(Context context , int layoutId) {
        this.layoutId = layoutId;
        this.context = context;
        data = new ArrayList<>();
        //默认可以回收
        this.isRecycle = true;
    }

    /**
     * 创建一个ViewHolder
     */
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (multiTypeSupport != null) {
            layoutId = viewType;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        SeatViewHolder viewHolder = new SeatViewHolder(view);
        if (!isRecycle) {
            viewHolder.setIsRecyclable(false);
        }
        setListener(viewHolder);
        return viewHolder;
    }

    /**
     * 绑定数据，创建抽象方法，让子类实现
     */
    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(data!=null && data.size()>0){
            this.position = position;
            bindData(holder, data.get(position));
        }
    }

    /**
     * 获取类型
     */
    @Override
    public int getItemViewType(int position) {
        if (multiTypeSupport != null) {
            return multiTypeSupport.getLayoutId(data, position);
        }
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return data==null ? 0 : data.size();
    }


    /**
     * 当子类adapter继承此AbsSeatAdapter时，需要子类实现的绑定数据方法
     */
    protected abstract void bindData(SeatViewHolder holder, T t);


    /*-------------------------------------操作方法-----------------------------------------------*/


    public int getViewPosition(){
        return position;
    }

    /**
     * 设置点击事件和长按事件
     * @param viewHolder        viewHolder
     */
    private void setListener(final SeatViewHolder viewHolder) {
        if (viewHolder.getItemView()==null){
            return;
        }
        viewHolder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    onItemClickListener.onItemClick(v,  position);
                }
            }
        });

        viewHolder.getItemView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return onItemLongClickListener.onItemLongClick(v, position);
                }
                return false;
            }
        });
    }


    /**
     * 设置数据，并且刷新页面
     */
    public void setData(List<T> list){
        data.clear();
        if(list==null || list.size()==0){
            return;
        }
        data.addAll(list);
        notifyItemRangeChanged(data.size()-list.size(),data.size());
        notifyDataSetChanged();
    }

    /**
     * 设置数据T，并且刷新页面
     */
    public boolean setData(T t) {
        if (t == null) {
            return false;
        }
        if (data.contains(t)) {
            return false;
        }
        boolean b = data.add(t);
        notifyItemInserted(data.size() - 1);
        return b;
    }

    /**
     * 在索引position处添加数据t并且刷新
     * @param position                  position
     * @param t                         t
     * @return
     */
    public boolean setData(int position, T t) {
        if (t == null){
            return false;
        }
        if (position < 0 || position > data.size()){
            return false;
        }
        if (data.contains(t)){
            return false;
        }
        data.add(position, t);
        notifyItemInserted(position);
        return true;
    }

    /**
     * 在索引position处添加数据list集合并且刷新
     * @param position                  position
     * @param list                      list
     * @return
     */
    public boolean setData(int position, List<T> list) {
        if (list == null) {
            return false;
        }
        if (data.contains(list)){
            return false;
        }
        data.addAll(position, list);
        notifyItemRangeInserted(position, list.size());
        return true;
    }


    /**
     * 获取数据
     */
    public List<T> getData(){
        return data;
    }

    /**
     * 清理所有数据，并且刷新页面
     */
    public void clearAll(){
        data.clear();
        notifyDataSetChanged();
    }


    /**
     * 移除数据
     */
    public void remove(T t){
        if(data.size()==0){
            return;
        }
        int index = data.indexOf(t);
        remove(index);
        notifyItemRemoved(index);
    }


    /**
     * 移除数据
     */
    public void remove(int position){
        if (position < 0 || position >= data.size()) {
            return ;
        }
        data.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 移除数据
     */
    public void remove(int start,int count){
        if(data.size()==0){
            return;
        }
        if((start +count) > data.size()){
            return;
        }
        data.subList(start,start+count).clear();
        notifyItemRangeRemoved(start,count);
    }


    /**
     * 更新数据
     * @param position           索引
     * @return
     */
    public boolean updateItem(int position) {
        if (position < 0 || position >= data.size()) {
            return false;
        }
        notifyItemChanged(position);
        return true;
    }

    /**
     * 更新数据
     * @param t                 t
     * @return
     */
    public boolean updateItem(T t) {
        if (t == null) {
            return false;
        }
        int index = data.indexOf(t);
        if (index >= 0) {
            data.set(index, t);
            notifyItemChanged(index);
            return true;
        }
        return false;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

}
