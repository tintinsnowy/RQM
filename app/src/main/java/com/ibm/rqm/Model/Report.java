package com.ibm.rqm.Model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Jack on 2015/5/19 0019.
 */

@Table(databaseName = RQMDatabase.NAME)
public class Report extends BaseModel{

    @Column
    @PrimaryKey
    private String queryUUID;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String reportUUID;

    public String getQueryUUID() {
        return queryUUID;
    }

    public void setQueryUUID(String queryUUID) {
        this.queryUUID = queryUUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReportUUID() {
        return reportUUID;
    }

    public void setReportUUID(String reportUUID) {
        this.reportUUID = reportUUID;
    }
}
