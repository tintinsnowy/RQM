package com.ibm.rqm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibm.rqm.Model.Workitem;
import com.ibm.rqm.Model.Workitem$Table;
import com.ibm.rqm.R;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.builder.Condition;

/**
 * Created by Jack on 2015/5/31 0031.
 */
public class WorkItemAdapter extends BaseAdapter {
    private FlowCursorList<Workitem> mFlowCursorList;
    private Context mCtx;

    public WorkItemAdapter(Context context) {

        mCtx = context;
        // retrieve and cache rows
        mFlowCursorList = new FlowCursorList<Workitem>(false, Workitem.class,
                Condition.column(Workitem$Table.ID).isNotNull());
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
    public Workitem getItem(int position) {
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

        Workitem workitem = mFlowCursorList.getItem(position);

        viewHolder.title.setText(workitem.getTitle());
        viewHolder.summary.setText(workitem.getSummary());
        viewHolder.description.setText(workitem.getUpdated());
        viewHolder.icon.setImageResource(R.drawable.abc_btn_check_material);
        return convertView;
    }

    public class ViewHolder{
        TextView title;
        TextView summary;
        TextView description;
        ImageView icon;
    }
}

