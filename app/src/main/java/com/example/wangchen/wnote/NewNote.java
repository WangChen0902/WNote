package com.example.wangchen.wnote;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
            urls = data.getPicture();
            File file = new File(data.getPicture());
            if (file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(data.getPicture());
                //将图片显示到ImageView中
                imageView.setImageBitmap(bm);
            }
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
            data=new Datas(title, content, times, picture);
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

//        //获得图片
//        Bitmap bitmap = null;
//        ContentResolver resolver = getContentResolver();
        if (requestCode == REQUESTCODE) {
            Uri uri = data.getData();
            setPicture(uri);
//            Uri uri = data.getData();
//            myUri = uri;
//            urls = uri.getPath();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);//获得图片
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
//        imageView.setImageBitmap(bitmap);
    }

    private void setPicture(Uri uri){
        Bitmap bitmap = null;
        ContentResolver resolver = getContentResolver();
            urls = getRealPathFromUri(this, uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);//获得图片
            } catch (IOException e) {
                e.printStackTrace();
            }
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

//    作者：tianma
//    链接：https://www.jianshu.com/p/b168cbe50066
//    來源：简书
//    简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
}