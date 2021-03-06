package com.mini.homeworks.MainActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.mini.homeworks.AssignDetail.DetailActivity;
import com.mini.homeworks.R;
import com.mini.homeworks.net.RetrofitWrapper;
import com.mini.homeworks.net.Service.TasksService;
import com.mini.homeworks.net.bean.TasksBean;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class TaskFragment extends Fragment implements View.OnClickListener{

    private View view;
    private RecyclerView rv_task;
    private Spinner spinner_collation;
    private Button btn_all, btn_completed, btn_processing, btn_overdue;
    private String cookie;
    private String token;
    int i = 0;

    List<TasksBean.AssignListBean> tasklist = new ArrayList<>();
    List<TasksBean.AssignListBean> tmptasklist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.task_view,container,false);
        initTask();
        request_task();
        return view;
    }

    private void request_task() {
        GetCookieAndToken();
        TasksService tasksService = RetrofitWrapper.getInstance().create(TasksService.class);
        Call<TasksBean> call = tasksService.getTaskBean(cookie,token);
        call.enqueue(new Callback<TasksBean>() {
            @Override
            public void onResponse(Call<TasksBean> call, Response<TasksBean> response) {
                if (response.isSuccessful()) {
                    tasklist.addAll(response.body().getAssignList());
                    if(rv_task.getAdapter() != null) {
                        tmptasklist.clear();
                        tmptasklist.addAll(tasklist);
                        rv_task.getAdapter().notifyDataSetChanged();
                    }
                    cookie = response.body().getCookie();
                    SaveCookie(cookie);
                } else
                    Toast.makeText(getContext(), "加载失败，请重试", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<TasksBean> call, Throwable t) {
                Toast.makeText(getContext(),"请检查网络连接",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initTask() {
        rv_task = view.findViewById(R.id.rv_task);
        btn_overdue = view.findViewById(R.id.btn_overdue);
        btn_processing = view.findViewById(R.id.btn_processing);
        btn_all = view.findViewById(R.id.btn_all);
        btn_completed = view.findViewById(R.id.btn_completed);
        spinner_collation = view.findViewById(R.id.spinner_collation);

        btn_all.setOnClickListener(this);
        btn_overdue.setOnClickListener(this);
        btn_processing.setOnClickListener(this);
        btn_completed.setOnClickListener(this);

        btn_all.setTextColor(Color.parseColor("#FFFFFF"));
        btn_all.setBackgroundColor(Color.parseColor("#4DB6AC"));
        btn_overdue.setTextColor(Color.parseColor("#42000000"));
        btn_overdue.setBackgroundColor(Color.parseColor("#1F000000"));
        btn_processing.setTextColor(Color.parseColor("#42000000"));
        btn_processing.setBackgroundColor(Color.parseColor("#1F000000"));
        btn_completed.setTextColor(Color.parseColor("#42000000"));
        btn_completed.setBackgroundColor(Color.parseColor("#1F000000"));
        tmptasklist.clear();
        tmptasklist.addAll(tasklist);
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.array_collation, R.layout.task_view_spinner_text_item);
        spinnerAdapter.setDropDownViewResource(R.layout.task_view_spinner_dropdown_item);
        spinner_collation.setAdapter(spinnerAdapter);
        spinner_collation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    Log.e("msg........................................BEGIN",""+(i++));
                    beginorder();
                }
                else {
                    Log.e("msg.........................................END",""+(i++));
                    endorder();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                beginorder();
            }
        });
        spinner_collation.setSelection(0, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_task.setLayoutManager(layoutManager);
        TaskAdapter taskAdapter = new TaskAdapter(tmptasklist);
        rv_task.setAdapter(taskAdapter);
        taskAdapter.setOnRecyclerViewItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("siteId", tmptasklist.get(position).getSiteId());
                intent.putExtra("assignId", tmptasklist.get(position).getAssignId());
                startActivity(intent);
            }
        });
        spinner_collation.setSelection(0,true);
        spinner_collation.setSelection(0);
        Log.e("what-------",spinner_collation.getSelectedItemId()+"");
    }

    private void beginorder() {
        Collections.sort(tmptasklist, new TaskBeginComparator());
        rv_task.getAdapter().notifyDataSetChanged();
    }

    private void endorder() {
        Collections.sort(tmptasklist, new TaskEndComparator());
        rv_task.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_all: {
                btn_all.setTextColor(Color.parseColor("#FFFFFF"));
                btn_all.setBackgroundColor(Color.parseColor("#4DB6AC"));
                btn_overdue.setTextColor(Color.parseColor("#42000000"));
                btn_overdue.setBackgroundColor(Color.parseColor("#1F000000"));
                btn_processing.setTextColor(Color.parseColor("#42000000"));
                btn_processing.setBackgroundColor(Color.parseColor("#1F000000"));
                btn_completed.setTextColor(Color.parseColor("#42000000"));
                btn_completed.setBackgroundColor(Color.parseColor("#1F000000"));
                tmptasklist.clear();
                tmptasklist.addAll(tasklist);
                spinner_collation.setSelection(0,true);
                spinner_collation.setSelection(0);
                Log.e("what-------",spinner_collation.getSelectedItemId()+"");
                rv_task.getAdapter().notifyDataSetChanged();
                break;
            }
            case R.id.btn_processing: {
                btn_processing.setTextColor(Color.parseColor("#FFFFFF"));
                btn_processing.setBackgroundColor(Color.parseColor("#4DB6AC"));
                btn_overdue.setTextColor(Color.parseColor("#42000000"));
                btn_overdue.setBackgroundColor(Color.parseColor("#1F000000"));
                btn_all.setTextColor(Color.parseColor("#42000000"));
                btn_all.setBackgroundColor(Color.parseColor("#1F000000"));
                btn_completed.setTextColor(Color.parseColor("#42000000"));
                btn_completed.setBackgroundColor(Color.parseColor("#1F000000"));
                selectprocessing();
                break;
            }
            case R.id.btn_completed: {
                btn_completed.setTextColor(Color.parseColor("#FFFFFF"));
                btn_completed.setBackgroundColor(Color.parseColor("#4DB6AC"));
                btn_overdue.setTextColor(Color.parseColor("#42000000"));
                btn_overdue.setBackgroundColor(Color.parseColor("#1F000000"));
                btn_all.setTextColor(Color.parseColor("#42000000"));
                btn_all.setBackgroundColor(Color.parseColor("#1F000000"));
                btn_processing.setTextColor(Color.parseColor("#42000000"));
                btn_processing.setBackgroundColor(Color.parseColor("#1F000000"));
                selectcompeleted();
                break;
            }
            case R.id.btn_overdue: {
                btn_overdue.setTextColor(Color.parseColor("#FFFFFF"));
                btn_overdue.setBackgroundColor(Color.parseColor("#4DB6AC"));
                btn_processing.setTextColor(Color.parseColor("#42000000"));
                btn_processing.setBackgroundColor(Color.parseColor("#1F000000"));
                btn_all.setTextColor(Color.parseColor("#42000000"));
                btn_all.setBackgroundColor(Color.parseColor("#1F000000"));
                btn_completed.setTextColor(Color.parseColor("#42000000"));
                btn_completed.setBackgroundColor(Color.parseColor("#1F000000"));
                selectoverdue();
                break;
            }
        }
    }

    private void selectprocessing() {
        long now = Instant.now().getEpochSecond()*1000;
        tmptasklist.clear();
        int l = tasklist.size();
        for (int i = 0;  i < l ; i++ ) {
            if (tasklist.get(i).getEndTime() <= now ||
                    tasklist.get(i).getBeginTime() >= now ||
                    (tasklist.get(i).getStatus() != 0 && tasklist.get(i).getStatus() != 2))
                continue;
            else tmptasklist.add(tasklist.get(i));
        }
        spinner_collation.setSelection(0, true);
        spinner_collation.setSelection(0);
        Log.e("what-------",spinner_collation.getSelectedItemId()+"");
        rv_task.getAdapter().notifyDataSetChanged();
    }

    private void selectcompeleted() {
        tmptasklist.clear();
        int l = tasklist.size();
        for (int i = 0;  i < l ; i++ ) {
            if ( tasklist.get(i).getStatus() != 1 && tasklist.get(i).getStatus() != 3 )
                continue;
            else tmptasklist.add(tasklist.get(i));
        }
        spinner_collation.setSelection(0, true);
        spinner_collation.setSelection(0);
        Log.e("what-------",spinner_collation.getSelectedItemId()+"");
        rv_task.getAdapter().notifyDataSetChanged();
    }

    private void selectoverdue() {
        long now = Instant.now().getEpochSecond()*1000;
        tmptasklist.clear();
        int l = tasklist.size();
        for (int i = 0;  i < l ; i++ ) {
            if ( tasklist.get(i).getEndTime() <= now || ( tasklist.get(i).getStatus() != 0 && tasklist.get(i).getStatus() != 2) )
                continue;
            else tmptasklist.add(tasklist.get(i));
        }
        spinner_collation.setSelection(0, true);
        spinner_collation.setSelection(0);
        Log.e("what-------",spinner_collation.getSelectedItemId()+"");
        rv_task.getAdapter().notifyDataSetChanged();
    }

    private void SaveCookie (String cookie) {
        SharedPreferences data = getContext().getSharedPreferences("CandT",MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString("cookie",cookie);
        editor.apply();
    }

    private void GetCookieAndToken () {
        SharedPreferences data = getContext().getSharedPreferences("CandT",MODE_PRIVATE);
        cookie = data.getString("cookie",null);
        token = data.getString("token", null);
    }

}