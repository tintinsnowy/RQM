package com.ibm.rqm.Model;

import android.content.ContentValues;
import android.database.Cursor;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.builder.ConditionQueryBuilder;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
// This table belongs to the RQMDatabase database
public final class Report$Adapter extends ModelAdapter<Report> {

  @Override
  public Class<Report> getModelClass() {
    return Report.class;
  }

  @Override
  public String getTableName() {
    return com.ibm.rqm.Model.Report$Table.TABLE_NAME;
  }

  @Override
  protected final String getInsertStatementQuery() {
    return "INSERT INTO `Report` (`QUERYUUID`, `NAME`, `DESCRIPTION`, `REPORTUUID`) VALUES (?, ?, ?, ?)";
  }

  @Override
  public void bindToStatement(android.database.sqlite.SQLiteStatement statement, Report model) {
    // Column Boxed Type:java.lang.String
    if (((java.lang.String)model.getQueryUUID()) != null)  {
      statement.bindString(1,((java.lang.String)model.getQueryUUID()));
    } else {
      statement.bindNull(1);
    }
    // Column Boxed Type:java.lang.String
    if (((java.lang.String)model.getName()) != null)  {
      statement.bindString(2,((java.lang.String)model.getName()));
    } else {
      statement.bindNull(2);
    }
    // Column Boxed Type:java.lang.String
    if (((java.lang.String)model.getDescription()) != null)  {
      statement.bindString(3,((java.lang.String)model.getDescription()));
    } else {
      statement.bindNull(3);
    }
    // Column Boxed Type:java.lang.String
    if (((java.lang.String)model.getReportUUID()) != null)  {
      statement.bindString(4,((java.lang.String)model.getReportUUID()));
    } else {
      statement.bindNull(4);
    }

  }

  @Override
  public void bindToContentValues(ContentValues contentValues, Report model) {
    if (((java.lang.String)model.getQueryUUID()) != null)  {
      contentValues.put("queryUUID",((java.lang.String)model.getQueryUUID()));
    } else {
      contentValues.putNull("queryUUID");
    }
    if (((java.lang.String)model.getName()) != null)  {
      contentValues.put("name",((java.lang.String)model.getName()));
    } else {
      contentValues.putNull("name");
    }
    if (((java.lang.String)model.getDescription()) != null)  {
      contentValues.put("description",((java.lang.String)model.getDescription()));
    } else {
      contentValues.putNull("description");
    }
    if (((java.lang.String)model.getReportUUID()) != null)  {
      contentValues.put("reportUUID",((java.lang.String)model.getReportUUID()));
    } else {
      contentValues.putNull("reportUUID");
    }

  }

  @Override
  public void bindToInsertValues(ContentValues contentValues, Report model) {
    if (((java.lang.String)model.getQueryUUID()) != null)  {
      contentValues.put("queryUUID",((java.lang.String)model.getQueryUUID()));
    } else {
      contentValues.putNull("queryUUID");
    }
    if (((java.lang.String)model.getName()) != null)  {
      contentValues.put("name",((java.lang.String)model.getName()));
    } else {
      contentValues.putNull("name");
    }
    if (((java.lang.String)model.getDescription()) != null)  {
      contentValues.put("description",((java.lang.String)model.getDescription()));
    } else {
      contentValues.putNull("description");
    }
    if (((java.lang.String)model.getReportUUID()) != null)  {
      contentValues.put("reportUUID",((java.lang.String)model.getReportUUID()));
    } else {
      contentValues.putNull("reportUUID");
    }

  }

  @Override
  public boolean exists(Report model) {
    return new Select().from(Report.class).where(getPrimaryModelWhere(model)).hasData();
  }

  @Override
  public void loadFromCursor(Cursor cursor, Report model) {
    int indexqueryUUID = cursor.getColumnIndex("queryUUID");
    if (indexqueryUUID != -1)  {
      if (cursor.isNull(indexqueryUUID)) {
        model.setQueryUUID(null);
      } else {
        model.setQueryUUID(cursor.getString(indexqueryUUID));
      }
    }
    int indexname = cursor.getColumnIndex("name");
    if (indexname != -1)  {
      if (cursor.isNull(indexname)) {
        model.setName(null);
      } else {
        model.setName(cursor.getString(indexname));
      }
    }
    int indexdescription = cursor.getColumnIndex("description");
    if (indexdescription != -1)  {
      if (cursor.isNull(indexdescription)) {
        model.setDescription(null);
      } else {
        model.setDescription(cursor.getString(indexdescription));
      }
    }
    int indexreportUUID = cursor.getColumnIndex("reportUUID");
    if (indexreportUUID != -1)  {
      if (cursor.isNull(indexreportUUID)) {
        model.setReportUUID(null);
      } else {
        model.setReportUUID(cursor.getString(indexreportUUID));
      }
    }
  }

  @Override
  public Object getCachingId(Report model) {
    return model.getQueryUUID();
  }

  @Override
  public String getCachingColumnName() {
    return Report$Table.QUERYUUID;
  }

  @Override
  public Object getCachingIdFromCursorIndex(Cursor cursor, int indexgetQueryUUID) {
    return cursor.getString(indexgetQueryUUID);
  }

  @Override
  public ConditionQueryBuilder<Report> getPrimaryModelWhere(Report model) {
    return new ConditionQueryBuilder<Report>(Report.class, Condition.column(Report$Table.QUERYUUID).is((model.getQueryUUID())));
  }

  @Override
  public ConditionQueryBuilder<Report> createPrimaryModelWhere() {
    return new ConditionQueryBuilder<Report>(Report.class, Condition.column(Report$Table.QUERYUUID).is("?"));
  }

  @Override
  public String getCreationQuery() {
    return "CREATE TABLE IF NOT EXISTS `Report`(`queryUUID` TEXT, `name` TEXT, `description` TEXT, `reportUUID` TEXT, PRIMARY KEY(`queryUUID`));";
  }

  @Override
  public final Report newInstance() {
    return new com.ibm.rqm.Model.Report();
  }
}
