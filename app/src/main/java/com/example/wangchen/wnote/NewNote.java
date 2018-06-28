package com.example.wangchen.wnote;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewNote extends AppCompatActivity {

    EditText ed1,ed2;
    ImageButton saveImage;
    ImageButton addPicture;
    ImageView imageView;
    MyDataBase myDatabase;
    Datas data;
    int ids;
    String urls;
    Uri myUri;

    private String INTENT_TYPE  = "image/*";
    private int REQUESTCODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        ed1=(EditText) findViewById(R.id.titleEdit);
        ed2=(EditText) findViewById(R.id.contentEdit);
        saveImage=(ImageButton) findViewById(R.id.saveButton);
        addPicture=(ImageButton) findViewById(R.id.addPic);
        imageView = (ImageView) findViewById(R.id.image_view);
        myDatabase=new MyDataBase(this);

        Intent intent=this.getIntent();
        ids=intent.getIntExtra("ids", 0);
        //默认为0，不为0,则为修改数据时跳转过来的
        if(ids!=0){
            data=myDatabase.getUpdate(ids);
            ed1.setText(data.getTitle());
            ed2.setText(data.getContent());
        }
        //保存按钮的点击事件，他和返回按钮是一样的功能，所以都调用isSave()方法；
        saveImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isSave();
            }
        });

        addPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addPic();
            }
        });
    }

    /*
     * 返回按钮调用的方法。
     */
    @Override
    public void onBackPressed() {
        isSave();
    }

    private void isSave(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String times = formatter.format(curDate);
        String title = ed1.getText().toString();
        String content = ed2.getText().toString();
        String picture = urls;
        //是要修改数据
        if(ids!=0){
            data=new Datas(ids, title, content, times, picture);
            myDatabase.toUpdate(data);
            Intent intent=new Intent(NewNote.this,MainActivity.class);
            startActivity(intent);
            NewNote.this.finish();
        }
        //新建便签
        else{
            data=new Datas(title,content,times, picture);
            myDatabase.toInsert(data);
            Intent intent=new Intent(NewNote.this,MainActivity.class);
            startActivity(intent);
            NewNote.this.finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        "标题："+ed1.getText().toString()+"    " +
                                "内容："+ed2.getText().toString());
                startActivity(intent);
                break;

            default:
                break;
        }
        return false;
    }

    private void addPic(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(INTENT_TYPE);
        startActivityForResult(intent,REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Log.e("TAG--->onresult", "ActivityResult resultCode error");
            return;
        }

        //获得图片
        Bitmap bitmap = null;
        ContentResolver resolver = getContentResolver();
        if (requestCode == REQUESTCODE) {
            Uri uri = data.getData();
            myUri = uri;
            urls = uri.getPath();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);//获得图片
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageView.setImageBitmap(bitmap);
    }

    private void setPicture(Uri uri){
        Bitmap bitmap = null;
        ContentResolver resolver = getContentResolver();
            urls = uri.getPath();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);//获得图片
            } catch (IOException e) {
                e.printStackTrace();
            }
        imageView.setImageBitmap(bitmap);
    }
}