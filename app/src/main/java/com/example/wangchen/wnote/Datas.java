package com.example.wangchen.wnote;

public class Datas {

    private int ids;        //编号
    private String title;   //标题
    private String content; //内容
    private String times;   //时间
    private String picture; //图片路径

    public Datas(int ids, String title, String content ,String times, String picture){
        this.ids=ids;
        this.picture=picture;
        this.title=title;
        this.content=content;
        this.times=times;
    }

    public Datas(String title, String content, String picture){
        this.title = title;
        this.content = content;
        this.picture = picture;
    }

    public Datas(int ids, String title, String content, String times){
        this.ids=ids;
        this.title=title;
        this.content=content;
        this.times=times;
    }

    public Datas(String title,String content,String times,String picture){
        this.title=title;
        this.content=content;
        this.times=times;
        this.picture=picture;
    }

    public Datas(int ids,String title,String times){
        this.ids=ids;
        this.title=title;
        this.times=times;
    }

    public Datas(String title,String content){
        this.title=title;
        this.content=content;
    }

    public int getIds() {
        return ids;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTimes() {
        return times;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

}
