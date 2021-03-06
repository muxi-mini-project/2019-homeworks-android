package com.mini.homeworks.MainActivity;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mini.homeworks.R;
import com.mini.homeworks.Utils.GetDate;
import com.mini.homeworks.net.bean.TasksBean;

import java.time.Instant;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<TasksBean.AssignListBean> mDatas;
    private OnItemClickListener onRecyclerViewItemClickListener;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnRecyclerViewItemClickListener(OnItemClickListener onItemClickListener) {
        this.onRecyclerViewItemClickListener = onItemClickListener;
    }

    public TaskAdapter(List<TasksBean.AssignListBean> mDatas){
        this.mDatas = mDatas;
    }

    public void update(List<TasksBean.AssignListBean> aNew) {
        this.mDatas.clear();
        this.mDatas.addAll(aNew);
    }

    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = View.inflate(parent.getContext(), R.layout.tasks_item, null);
        return new TaskAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (TaskAdapter.MyViewHolder holder, final int position){
        TasksBean.AssignListBean dataBean = mDatas.get(position);
        String begintime = GetDate.TimeStampToDate(""+dataBean.getBeginTime(), "yyyy-MM-dd HH:mm:ss").substring(0,10);
        String endtime = GetDate.TimeStampToDate(""+dataBean.getEndTime(), "yyyy-MM-dd HH:mm:ss").substring(0,10);
        holder.tv_begin.setText("开始时间："+GetDate.DateToWeek(begintime)+" "+begintime);
        holder.tv_ddl.setText("截止时间："+GetDate.DateToWeek(endtime)+" "+endtime);
        holder.tv_assignName.setText(dataBean.getAssignName());
        long now = Instant.now().getEpochSecond()*1000;
        if ( now > dataBean.getEndTime() && ( dataBean.getStatus() == 0 || dataBean.getStatus() == 2 ) ) {
            holder.aitem.setBackgroundResource(R.drawable.rounded_rectangle_bcbcbc);
            holder.iv_status.setImageResource(R.drawable.cross);
            holder.tv_status.setText("已逾期");
            holder.tv_begin.setTextColor(Color.parseColor("#BCBCBC"));
            holder.tv_ddl.setTextColor(Color.parseColor("#BCBCBC"));
        } else if ( dataBean.getStatus() == 1 || dataBean.getStatus() == 3 ) {
            holder.aitem.setBackgroundResource(R.drawable.rounded_rectangle_3f51b5);
            holder.iv_status.setImageResource(R.drawable.tick);
            holder.tv_status.setText("已完成");
            holder.tv_ddl.setTextColor(Color.parseColor("#3F51B5"));
            holder.tv_begin.setTextColor(Color.parseColor("#3F51B5"));
        } else if ( now < dataBean.getEndTime() && ( dataBean.getStatus() == 0 || dataBean.getStatus() == 2 ) ) {
            holder.aitem.setBackgroundResource(R.drawable.rounded_rectangle_039be5);
            holder.iv_status.setImageResource(R.drawable.circle);
            holder.tv_status.setText("进行中");
            holder.tv_begin.setTextColor(Color.parseColor("#039BE5"));
            holder.tv_ddl.setTextColor(Color.parseColor("#039BE5"));
        }
        if (onRecyclerViewItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRecyclerViewItemClickListener.onClick(position);
                }
            });
        }
    }
    @Override
    public int getItemCount(){
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ddl;
        TextView tv_begin;
        TextView tv_assignName;
        ImageView iv_status;
        TextView tv_status;
        LinearLayout itemLayout;
        LinearLayout aitem;

        public MyViewHolder(View view){
            super (view);
            iv_status = view.findViewById(R.id.iv_courseassign_stutas);
            tv_status = view.findViewById(R.id.tv_courseassign_status);
            tv_ddl = view.findViewById(R.id.tv_ddl);
            tv_assignName = view.findViewById(R.id.tv_assignName);
            tv_begin = view.findViewById(R.id.tv_begin);
            aitem = view.findViewById(R.id.tasks_aitem);
            itemLayout = view.findViewById(R.id.tasks_item);
        }
    }
}
