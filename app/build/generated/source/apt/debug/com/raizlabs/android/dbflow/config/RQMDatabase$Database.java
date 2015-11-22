package com.raizlabs.android.dbflow.config;

import java.util.ArrayList;
import java.util.List;
public final class RQMDatabase$Database extends BaseDatabaseDefinition {


  public RQMDatabase$Database(DatabaseHolder holder) {
    // Writing for: RQMDatabase
    holder.putDatabaseForTable(com.ibm.rqm.Model.Executionresult.class, this);
    holder.putDatabaseForTable(com.ibm.rqm.Model.Project.class, this);
    holder.putDatabaseForTable(com.ibm.rqm.Model.Testplan.class, this);
    holder.putDatabaseForTable(com.ibm.rqm.Model.Workitem.class, this);
    holder.putDatabaseForTable(com.ibm.rqm.Model.Testcase.class, this);
    holder.putDatabaseForTable(com.ibm.rqm.Model.Report.class, this);

    // Begin Migrations
    // End Migrations

    models.add(com.ibm.rqm.Model.Executionresult.class);
    modelTableNames.put("Executionresult", com.ibm.rqm.Model.Executionresult.class);
    modelAdapters.put(com.ibm.rqm.Model.Executionresult.class, new com.ibm.rqm.Model.Executionresult$Adapter());
    models.add(com.ibm.rqm.Model.Project.class);
    modelTableNames.put("Project", com.ibm.rqm.Model.Project.class);
    modelAdapters.put(com.ibm.rqm.Model.Project.class, new com.ibm.rqm.Model.Project$Adapter());
    models.add(com.ibm.rqm.Model.Testplan.class);
    modelTableNames.put("Testplan", com.ibm.rqm.Model.Testplan.class);
    modelAdapters.put(com.ibm.rqm.Model.Testplan.class, new com.ibm.rqm.Model.Testplan$Adapter());
    models.add(com.ibm.rqm.Model.Workitem.class);
    modelTableNames.put("Workitem", com.ibm.rqm.Model.Workitem.class);
    modelAdapters.put(com.ibm.rqm.Model.Workitem.class, new com.ibm.rqm.Model.Workitem$Adapter());
    models.add(com.ibm.rqm.Model.Testcase.class);
    modelTableNames.put("Testcase", com.ibm.rqm.Model.Testcase.class);
    modelAdapters.put(com.ibm.rqm.Model.Testcase.class, new com.ibm.rqm.Model.Testcase$Adapter());
    models.add(com.ibm.rqm.Model.Report.class);
    modelTableNames.put("Report", com.ibm.rqm.Model.Report.class);
    modelAdapters.put(com.ibm.rqm.Model.Report.class, new com.ibm.rqm.Model.Report$Adapter());
    // Writing Query Models
  }

  @Override
  public final boolean isForeignKeysSupported() {
    return false;
  }

  @Override
  public final boolean backupEnabled() {
    return false;
  }

  @Override
  public final boolean areConsistencyChecksEnabled() {
    return false;
  }

  @Override
  public final int getDatabaseVersion() {
    return 1;
  }

  @Override
  public final String getDatabaseName() {
    return "RQMDatabase";
  }
}
