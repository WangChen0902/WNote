package com.example.wangchen.wnote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MyDataBase {

    Context context;
    MyOpenHelper myHelper;
    SQLiteDatabase myDatabase;
    /*
     * 别的类实例化这个类的同时，创建数据库
     */
    public MyDataBase(Context con){
        this.context=con;
        myHelper=new MyOpenHelper(context);
    }
    /*
     * 得到ListView的数据，从数据库里查找后解析
     */
    public ArrayList<Datas> getArray(){
        ArrayList<Datas> array = new ArrayList<Datas>();
        ArrayList<Datas> array1 = new ArrayList<Datas>();
        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor=myDatabase.rawQuery("select ids,title,times from myNote" , null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int id = cursor.getInt(cursor.getColumnIndex("ids"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String times = cursor.getString(cursor.getColumnIndex("times"));
            Datas cun = new Datas(id, title, times);
            array.add(cun);
            cursor.moveToNext();
        }
        myDatabase.close();
        for (int i = array.size(); i >0; i--) {
            array1.add(array.get(i-1));
        }
        return array1;
    }

    /*
     * 返回可能要修改的数据
     */
    public Datas getTiandCon(int id){
        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor=myDatabase.rawQuery("select title,content from myNote where ids='"+id+"'" , null);
        cursor.moveToFirst();
        String title=cursor.getString(cursor.getColumnIndex("title"));
        String content=cursor.getString(cursor.getColumnIndex("content"));
        Datas cun=new Datas(title,content);
        myDatabase.close();
        return cun;
    }

    /*
     * 用来修改日记
     */
    public void toUpdate(Datas cun){
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL(
                "update myNote set title='"+ cun.getTitle()+
                        "',times='"+cun.getTimes()+
                        "',content='"+cun.getContent() +
                        "' where ids='"+ cun.getIds()+"'");
        myDatabase.close();
    }

    /*
     * 用来增加新的日记
     */
    public void toInsert(Datas cun){
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL("insert into myNote(title,content,times)values('"
                + cun.getTitle()+"','"
                +cun.getContent()+"','"
                +cun.getTimes()
                +"')");
        myDatabase.close();
    }

    /*
     * 长按点击后选择删除日记
     */
    public void toDelete(int ids){
        myDatabase  = myHelper.getWritableDatabase();
        myDatabase.execSQL("delete from myNote where ids="+ids+"");
        myDatabase.close();
    }
}
