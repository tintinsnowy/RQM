package com.ibm.rqm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ibm.rqm.Model.Project;
import com.ibm.rqm.Model.Project$Table;
import com.ibm.rqm.R;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.builder.Condition;

/**
 * Created by Jack on 2015/5/24 0024.
 * ProjectAdapter//������ProjectListfragment��չʾproject�б�
 */

public class ProjectAdapter extends BaseAdapter {
    private FlowCursorList<Project> mFlowCursorList;
    private Context mCtx;

    public ProjectAdapter(Context context) {

        mCtx = context;
        // retrieve and cache rows
        mFlowCursorList = new FlowCursorList<Project>(false, Project.class,
                Condition.column(Project$Table.IDENTIFIER).isNotNull());
    }

    @Override
    public int getCount() {
        return mFlowCursorList.getCount();
    }

    @Override
    public Project getItem(int position) {
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
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.project_list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView)convertView.findViewById(R.id.project_title);
            //viewHolder.description = (TextView)convertView.findViewById(R.id.project_description);
           // viewHolder.alias = (TextView)convertView.findViewById(R.id.project_alias);
            viewHolder.id = (TextView)convertView.findViewById(R.id.project_id);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Project project = mFlowCursorList.getItem(position);
        viewHolder.title.setText((position + 1) + ". " + project.getTitle());
      //  viewHolder.alias.setText("Alias: " + project.getAlias());
       // viewHolder.description.setText("Description: " + project.getDescription());
        viewHolder.id.setText("ID: " + project.getIdentifier());

        return convertView;
    }

    public class ViewHolder{
        TextView id;
        TextView title;
       // TextView alias;
        //TextView description;
    }
}
