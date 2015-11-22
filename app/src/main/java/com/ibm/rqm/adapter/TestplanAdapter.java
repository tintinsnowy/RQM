package com.ibm.rqm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibm.rqm.Model.Testplan;
import com.ibm.rqm.Model.Testplan$Table;
import com.ibm.rqm.R;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.builder.Condition;

/**
 * Created by Jack on 2015/5/24 0024.
 */

public class TestplanAdapter extends BaseAdapter {
    private FlowCursorList<Testplan> mFlowCursorList;
    private Context mCtx;

    public TestplanAdapter(Context context) {

        mCtx = context;
        // retrieve and cache rows
        mFlowCursorList = new FlowCursorList<Testplan>(false, Testplan.class,
                Condition.column(Testplan$Table.ID).isNotNull());
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return mFlowCursorList.getCount();
    }

    @Override
    public Testplan getItem(int position) {
        return mFlowCursorList.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.base_item, null);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView)convertView.findViewById(R.id.item_title);
            viewHolder.description = (TextView)convertView.findViewById(R.id.item_discription);
            viewHolder.summary = (TextView)convertView.findViewById(R.id.item_summary);
            viewHolder.icon = (ImageView)convertView.findViewById(R.id.item_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Testplan testplan = mFlowCursorList.getItem(position);
        viewHolder.title.setText(testplan.getTitle());
        viewHolder.summary.setText(testplan.getSummary());
        //这里的描述采用的是更新时间
        viewHolder.description.setText(testplan.getUpdated());
        viewHolder.icon.setImageResource(R.drawable.icon1);
        return convertView;
    }

    public class ViewHolder{
        TextView title;
        TextView summary;
        TextView description;
        ImageView icon;
    }
}
