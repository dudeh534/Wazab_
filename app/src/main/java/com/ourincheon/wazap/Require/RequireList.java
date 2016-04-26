package com.ourincheon.wazap.Require;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ourincheon.wazap.R;
import com.ourincheon.wazap.Retrofit.ContestData;
import com.ourincheon.wazap.Retrofit.Contests;

import java.util.ArrayList;

/**
 * Created by Youngdo on 2016-02-02.
 */
public class RequireList extends AppCompatActivity{
    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.require_activity);
        ListAdapter listAdapter = new com.ourincheon.wazap.Require.ListAdapter(this, R.layout.require_ltem);
        not_ListAdapter not_listAdapter = new not_ListAdapter(this, R.layout.not_require_item);
        ListView listView = (ListView) findViewById(R.id.listView);
        ListView listView1 = (ListView) findViewById(R.id.listView1);
        scrollView = (ScrollView) findViewById(R.id.scrollView1);
        listView.setAdapter(listAdapter);
        listView1.setAdapter(not_listAdapter);

        setListViewHeightBasedOnChildren(listView);
        setListViewHeightBasedOnChildren(listView1);

    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public class not_ListAdapter extends ArrayAdapter<String> {
        private final Activity context;
        TextView dday, title, cate, man;
        ImageView small;
        Button bt;
        Contests contest;
        ArrayList<ContestData> cont_list;
        int count;

        public not_ListAdapter(Activity context, int resource) {
            super(context, resource);
            this.context = context;
            //list = new ArrayList<Contests>();
            SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
            String access_token = pref.getString("access_token", "");
            //loadPage(access_token);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.not_require_item, null, true);
            dday = (TextView) rowView.findViewById(R.id.dday);
            title = (TextView) rowView.findViewById(R.id.title);
            cate = (TextView) rowView.findViewById(R.id.cate);
            man = (TextView) rowView.findViewById(R.id.man);
            bt = (Button) rowView.findViewById(R.id.require);

            title.setText("[서울] 한화생명 보험 아이디어 공모전");
            cate.setText("영상/ucc/사진");
            man.setText("모집인원 5명");
            return rowView;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
