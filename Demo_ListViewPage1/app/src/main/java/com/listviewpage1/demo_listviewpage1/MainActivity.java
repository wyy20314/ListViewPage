package com.listviewpage1.demo_listviewpage1;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Vector;

import java.util.logging.LogRecord;

/*
 *分页刷新
 */
public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener{

    private ListView listView;
    //容器
    private Vector<news> news = new Vector<>();
    private MyAdapter myAdapter;
    //数据更新完的标记
    private  static final int DATA_UPDATE = 0X1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listview);
        //注册事件
        listView.setOnScrollListener(this);
        View footerView = getLayoutInflater().inflate(R.layout.loading,null);
        //新添加一个视图
        listView.addFooterView(footerView);
        //初始化数据
        initData();
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
    }

    private  int index = 1;
    /*
     *初始化数据
     */
    private void initData(){
        for(int i = 0 ;i<20;i++){
            news n = new news();
            n.title = "title--"+index;
            n.content = "content--"+index;
            index++;
            news.add(n);
        }
    }

    private int visibleLastIndex;//用来可显示的最后一条数据的索引值
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //总数是最后一条数据,并且是停止状态
        if(myAdapter.getCount() ==visibleLastIndex && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
           //启动事件
            new LoadDataThread().start();
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
       visibleLastIndex = firstVisibleItem + visibleItemCount-1;

    }
    /*
     *线程之间通讯的机制
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case DATA_UPDATE:
                    myAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    /*
     *线程类(模拟加载数据的线程)
     */
    class LoadDataThread extends Thread{
      @Override
        public void run(){
            initData();
            try{
              Thread.sleep(2000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
          //
          //通过handler给主线程发送一个消息标记
          handler.sendEmptyMessage(DATA_UPDATE);
        }
    }

    /*
     *自定义适配器
     */
     class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return news.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if(convertView == null){
               convertView = getLayoutInflater().inflate(R.layout.list_item,null);
                vh = new ViewHolder();
                vh.tv_title = (TextView)convertView.findViewById(R.id.textView_title);
                vh.tv_content = (TextView)convertView.findViewById(R.id.textView_content);
                convertView.setTag(vh);
            }else {
                vh = (ViewHolder)convertView.getTag();
            }
            news n = news.get(position);
            vh.tv_title.setText(n.title);
            vh.tv_content.setText(n.content);
            return convertView;
        }

         class ViewHolder{
            TextView tv_title;
            TextView tv_content;
        }
    }
}
