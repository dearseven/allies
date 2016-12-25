package cc.m2u.allisee.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.m2u.allisee.R;
import cc.m2u.allisee.beans.FileDes;
import cc.m2u.allisee.utils.BitmapHelper;
import cc.m2u.allisee.utils.FileCatcher;
import cc.m2u.allisee.utils.LRUImageCache;
import cc.m2u.allisee.views.WaterfallRecyclerView;
import cc.m2u.allisee.views.animator.MyItemAnimator;
import cc.m2u.allisee.views.dividers.DividerGridItemDecoration;

public class MainActivity extends AppCompatActivity {
    private FileCatcher fileCatcher = new FileCatcher();
    private WaterfallRecyclerView recyclerView;
    private WaterfallAdapter adapter;
    /**
     * 0显示除了qq和微信聊天缓存的图片 <br/>1qq聊天图片<br/>2微信图片
     */
    private int showStauts = 0;

    private LRUImageCache cache=new LRUImageCache();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //加入返回按钮
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("视图看看");
        //
        new FileSearcher().execute(true, true, false, false);
        //
        recyclerView = (WaterfallRecyclerView) findViewById(R.id.main_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        MyItemAnimator animator = new MyItemAnimator();
        animator.setAddDuration(500);
        animator.setRemoveDuration(500);
        animator.setContext(this);
        recyclerView.setItemAnimator(animator);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());


        //adapter
        adapter = new WaterfallAdapter();
        recyclerView.setAdapter(adapter);
        //设置item之间的间隔
        DividerGridItemDecoration decoration = new DividerGridItemDecoration(this);
        recyclerView.addItemDecoration(decoration);
    }

    private class WaterfallAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.waterfall_item_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            FileDes fd = fileCatcher.files.get(position);
            if (fd.fileType == FileDes.FILE_TYPE.sys_img || fd.fileType == FileDes.FILE_TYPE.qq_img
                    || fd.fileType == FileDes.FILE_TYPE.wx_img) {
                if(cache.getBitmapFromMemCache(fd.filePath)==null){
                    cache.addBitmapToMemoryCache(fd.filePath,BitmapHelper.loadBitmap(fd.filePath));
                }
                holder.imageView.setImageBitmap(cache.getBitmapFromMemCache(fd.filePath));
            } else if (fd.fileType == FileDes.FILE_TYPE.sys_video) {
                holder.imageView.setImageBitmap(fd.thumb);
            } else if (fd.fileType == FileDes.FILE_TYPE.wx_img) {
                if(cache.getBitmapFromMemCache(fd.filePath)==null){
                    cache.addBitmapToMemoryCache(fd.filePath,BitmapHelper.loadBitmap(fd.filePath));
                }
                holder.imageView.setImageBitmap(cache.getBitmapFromMemCache(fd.filePath));
            }

            holder.imageView.setTag(R.id.tagId1, fd.filePath);
            holder.imageView.setOnClickListener(new ItemImageViewClicker());
            holder.imageView.setOnLongClickListener(new ItemImageViewLongClicker());
            // holder.textView.setText(products.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return fileCatcher.files.size();
        }
    }

    private class ItemImageViewLongClicker implements View.OnLongClickListener {


        @Override
        public boolean onLongClick(View view) {
            String filePath = (String) view.getTag(R.id.tagId1);
            int index = -1;
            for (int i = 0; i < fileCatcher.files.size(); i++) {
                if (fileCatcher.files.get(i).filePath.equals(filePath)) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                return true;
            }
            FileDes fd = fileCatcher.files.get(index);
            if (fd.fileType == FileDes.FILE_TYPE.sys_img || fd.fileType == FileDes.FILE_TYPE.qq_img
                    || fd.fileType == FileDes.FILE_TYPE.wx_img) {
                //new File(fd.filePath).delete();//不真的删除
                fileCatcher.files.remove(index);
                adapter.notifyItemRemoved(index);
            }
            return true;
        }
    }

    private class ItemImageViewClicker implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String filePath = (String) view.getTag(R.id.tagId1);
            int index = -1;
            for (int i = 0; i < fileCatcher.files.size(); i++) {
                if (fileCatcher.files.get(i).filePath.equals(filePath)) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                return;
            }
            FileDes fd = fileCatcher.files.get(index);
            if (fd.fileType == FileDes.FILE_TYPE.sys_img || fd.fileType == FileDes.FILE_TYPE.qq_img
                    || fd.fileType == FileDes.FILE_TYPE.wx_img) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String type = "image/*";
                Uri uri = Uri.fromFile(new File(fd.filePath));
                intent.setDataAndType(uri, type);
                startActivity(intent);
                Toast.makeText(MainActivity.this, fd.filePath, Toast.LENGTH_SHORT).show();
            } else if (fd.fileType == FileDes.FILE_TYPE.sys_video) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String type = "video/*";
                Uri uri = Uri.fromFile(new File(fd.filePath));
                intent.setDataAndType(uri, type);
                startActivity(intent);
                Toast.makeText(MainActivity.this, fd.filePath, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 视图
     */
    private class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_view_image);
            textView = (TextView) itemView.findViewById(R.id.item_view_play);
        }

    }

    /**
     * 查找文件的异步任务
     */
    private class FileSearcher extends AsyncTask<Boolean, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //do nothing
        }


        @Override
        protected Boolean doInBackground(Boolean... bls) {
            fileCatcher.getAllFiles(MainActivity.this, bls[0], bls[1], bls[2], bls[3]);
            //其实我就是无聊，好久没用个这个东西了
            publishProgress(fileCatcher.files.size());

            return fileCatcher.files.size() > 0;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int count = values[0];
            //Toast.makeText(MainActivity.this, "找到" + count + "个文件", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            initPullDisplay(aBoolean);
        }
    }

    List<FileDes> l=null;
    /**
     * 查询好所有文件，然后显示
     */
    private void initPullDisplay(boolean suc) {
        if (suc == false)
            return;
        Toast.makeText(MainActivity.this, "找到" + fileCatcher.files.size() + "个文件,即将显示!", Toast.LENGTH_SHORT).show();
        Collections.reverse(fileCatcher.files);

       l = new ArrayList<FileDes>();
        for (FileDes fd : fileCatcher.files) {
            l.add(fd);
        }
        fileCatcher.files.clear();
        //fileCatcher.files.add(l.get(0));
        //l.remove(0);
        adapter.notifyDataSetChanged();
        h.sendEmptyMessageDelayed(0,100);
    }

    private Handler h=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){
                for (int i = 0; i < l.size(); i++) {
                    fileCatcher.files.add(l.get(i));
                }
                adapter.notifyItemRangeInserted(1,fileCatcher.files.size()-1);
            }
            //
        }
    };

    //------------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sys_img) {
            fileCatcher.files.clear();
            new FileSearcher().execute(true, true, false, false);
            return true;
        }
        if (id == R.id.wx_img) {
            fileCatcher.files.clear();
            new FileSearcher().execute(false, false, false, true);
            return true;
        }
        if (id == R.id.qq_img) {
            Toast.makeText(this, "还没找到qq的存放目录", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == android.R.id.home) {
            Toast.makeText(this, "see you~", Toast.LENGTH_SHORT).show();
            this.finish(); // back button
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
