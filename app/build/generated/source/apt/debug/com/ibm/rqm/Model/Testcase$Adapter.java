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
public final class Testcase$Adapter extends ModelAdapter<Testcase> {

  @Override
  public Class<Testcase> getModelClass() {
    return Testcase.class;
  }

  @Override
  public String getTableName() {
    return com.ibm.rqm.Model.Testcase$Table.TABLE_NAME;
  }

  @Override
  protected final String getInsertStatementQuery() {
    return "INSERT INTO `Testcase` (`ID`, `TITLE`, `SUMMARY`, `UPDATED`) VALUES (?, ?, ?, ?)";
  }

  @Override
  public void bindToStatement(android.database.sqlite.SQLiteStatement statement, Testcase model) {
    // Column Boxed Type:java.lang.String
    if (((java.lang.String)model.getId()) != null)  {
      statement.bindString(1,((java.lang.String)model.getId()));
    } else {
      statement.bindNull(1);
    }
    // Column Boxed Type:java.lang.String
    if (((java.lang.String)model.getTitle()) != null)  {
      statement.bindString(2,((java.lang.String)model.getTitle()));
    } else {
      statement.bindNull(2);
    }
    // Column Boxed Type:java.lang.String
    if (((java.lang.String)model.getSummary()) != null)  {
      statement.bindString(3,((java.lang.String)model.getSummary()));
    } else {
      statement.bindNull(3);
    }
    // Column Boxed Type:java.lang.String
    if (((java.lang.String)model.getUpdated()) != null)  {
      statement.bindString(4,((java.lang.String)model.getUpdated()));
    } else {
      statement.bindNull(4);
    }

  }

  @Override
  public void bindToContentValues(ContentValues contentValues, Testcase model) {
    if (((java.lang.String)model.getId()) != null)  {
      contentValues.put("id",((java.lang.String)model.getId()));
    } else {
      contentValues.putNull("id");
    }
    if (((java.lang.String)model.getTitle()) != null)  {
      contentValues.put("title",((java.lang.String)model.getTitle()));
    } else {
      contentValues.putNull("title");
    }
    if (((java.lang.String)model.getSummary()) != null)  {
      contentValues.put("summary",((java.lang.String)model.getSummary()));
    } else {
      contentValues.putNull("summary");
    }
    if (((java.lang.String)model.getUpdated()) != null)  {
      contentValues.put("updated",((java.lang.String)model.getUpdated()));
    } else {
      contentValues.putNull("updated");
    }

  }

  @Override
  public void bindToInsertValues(ContentValues contentValues, Testcase model) {
    if (((java.lang.String)model.getId()) != null)  {
      contentValues.put("id",((java.lang.String)model.getId()));
    } else {
      contentValues.putNull("id");
    }
    if (((java.lang.String)model.getTitle()) != null)  {
      contentValues.put("title",((java.lang.String)model.getTitle()));
    } else {
      contentValues.putNull("title");
    }
    if (((java.lang.String)model.getSummary()) != null)  {
      contentValues.put("summary",((java.lang.String)model.getSummary()));
    } else {
      contentValues.putNull("summary");
    }
    if (((java.lang.String)model.getUpdated()) != null)  {
      contentValues.put("updated",((java.lang.String)model.getUpdated()));
    } else {
      contentValues.putNull("updated");
    }

  }

  @Override
  public boolean exists(Testcase model) {
    return new Select().from(Testcase.class).where(getPrimaryModelWhere(model)).hasData();
  }

  @Override
  public void loadFromCursor(Cursor cursor, Testcase model) {
    int indexid = cursor.getColumnIndex("id");
    if (indexid != -1)  {
      if (cursor.isNull(indexid)) {
        model.setId(null);
      } else {
        model.setId(cursor.getString(indexid));
      }
    }
    int indextitle = cursor.getColumnIndex("title");
    if (indextitle != -1)  {
      if (cursor.isNull(indextitle)) {
        model.setTitle(null);
      } else {
        model.setTitle(cursor.getString(indextitle));
      }
    }
    int indexsummary = cursor.getColumnIndex("summary");
    if (indexsummary != -1)  {
      if (cursor.isNull(indexsummary)) {
        model.setSummary(null);
      } else {
        model.setSummary(cursor.getString(indexsummary));
      }
    }
    int indexupdated = cursor.getColumnIndex("updated");
    if (indexupdated != -1)  {
      if (cursor.isNull(indexupdated)) {
        model.setUpdated(null);
      } else {
        model.setUpdated(cursor.getString(indexupdated));
      }
    }
  }

  @Override
  public Object getCachingId(Testcase model) {
    return model.getId();
  }

  @Override
  public String getCachingColumnName() {
    return Testcase$Table.ID;
  }

  @Override
  public Object getCachingIdFromCursorIndex(Cursor cursor, int indexgetId) {
    return cursor.getString(indexgetId);
  }

  @Override
  public ConditionQueryBuilder<Testcase> getPrimaryModelWhere(Testcase model) {
    return new ConditionQueryBuilder<Testcase>(Testcase.class, Condition.column(Testcase$Table.ID).is((model.getId())));
  }

  @Override
  public ConditionQueryBuilder<Testcase> createPrimaryModelWhere() {
    return new ConditionQueryBuilder<Testcase>(Testcase.class, Condition.column(Testcase$Table.ID).is("?"));
  }

  @Override
  public String getCreationQuery() {
    return "CREATE TABLE IF NOT EXISTS `Testcase`(`id` TEXT, `title` TEXT, `summary` TEXT, `updated` TEXT, PRIMARY KEY(`id`));";
  }

  @Override
  public final Testcase newInstance() {
    return new com.ibm.rqm.Model.Testcase();
  }
}
