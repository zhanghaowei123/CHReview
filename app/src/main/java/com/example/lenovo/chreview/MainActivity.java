package com.example.lenovo.chreview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ListView bookListView;
    private BookListAdapter bookListAdapter;
    private List<Book> books = new ArrayList<>();
    private OkHttpClient okHttpClient;
    private SmartRefreshLayout refreshLayout;
    private int currentPage = 1;
    private int pageSize = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //将主线程注册成为订阅者
        EventBus.getDefault().register(this);
        okHttpClient = new OkHttpClient();
        initView();
        requestData(currentPage, pageSize);
    }

    private void initView() {
        bookListView = findViewById(R.id.lv_book);
        bookListAdapter = new BookListAdapter(MainActivity.this,
                R.layout.book_item, books);
        bookListView.setAdapter(bookListAdapter);

        refreshLayout = findViewById(R.id.refresh);
        //禁用下拉刷新
        refreshLayout.setEnableRefresh(false);
        //监听上拉加载更多的事件
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //请求服务器端操作
                requestData(++currentPage, pageSize);
            }
        });
    }

    private void requestData(int pageNum, int pageSize) {
        //GET请求 传参直接？传
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "BookListServlet?pageNum=" + pageNum + "&pageSize=" + pageSize)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bookListStr = response.body().string();
                //定义他的派生类调用getType，真是对象
                Type type = new TypeToken<List<Book>>() {
                }.getType();
                books.addAll((List<Book>) new Gson().fromJson(bookListStr, type));
                //在onResponse里面不能直接更新界面
                //接收到之后发送消息  通知给主线程
                EventBus.getDefault().post("图书列表");
            }
        });
    }

    //根据参数类型调用
    //消息的处理方法，形参类型同消息一致
    @Subscribe(threadMode = ThreadMode.MAIN)    //设置线程模式为主线程
    public void updateUI(String msg) {
        if (msg.equals("图书列表")) {
            //更新视图
            bookListAdapter.notifyDataSetChanged();
            refreshLayout.finishLoadMore();//结束加载动画  主线程调用
        }
//        } else if (msg.equals("删除成功")) {
//            //更新ListView  保证数据源正确且已修改
//            bookListAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
