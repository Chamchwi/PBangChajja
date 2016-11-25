package com.dsm.spiralmoon.pbangchajja;

public class ListViewItem {

    private String userid;
    private String title;
    private String content;
    private String date;

    public void setUserid(String s) {
        userid = s;
    }
    public void setTitle(String s) {
        title = s;
    }
    public void setContent(String s) {
        content = s;
    }
    public void setDate(String s) {
        date = s;
    }

    public String getUserid() {
        return userid;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getDate() {
        return date;
    }
}
