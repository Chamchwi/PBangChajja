package com.dsm.spiralmoon.pbangchajja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> listViewItems = new ArrayList<ListViewItem>();

    public ReviewAdapter() {

    }

    @Override
    public int getCount() {
        return listViewItems.size();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.review_list, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView userid = (TextView) convertView.findViewById(R.id.review_list_userid) ;
        TextView title = (TextView) convertView.findViewById(R.id.review_list_title) ;
        TextView content = (TextView) convertView.findViewById(R.id.review_list_content) ;
        TextView date = (TextView) convertView.findViewById(R.id.review_list_date) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItems.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        userid.setText(listViewItem.getUserid());
        title.setText(listViewItem.getTitle());
        content.setText(listViewItem.getContent());
        date.setText(listViewItem.getDate());

        return convertView;
    }
    @Override // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    public long getItemId(int position) {
        return position ;
    }
    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItems.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String userid, String title, String content, String date) {

        ListViewItem item = new ListViewItem();

        item.setUserid(userid);
        item.setTitle(title);
        item.setContent(content);
        item.setDate(date);

        listViewItems.add(item);
    }
}
