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
public final class Project$Adapter extends ModelAdapter<Project> {

  @Override
  public Class<Project> getModelClass() {
    return Project.class;
  }

  @Override
  public String getTableName() {
    return com.ibm.rqm.Model.Project$Table.TABLE_NAME;
  }

  @Override
  protected final String getInsertStatementQuery() {
    return "INSERT INTO `Project` (`IDENTIFIER`, `TITLE`, `DESCRIPTION`, `ALIAS`, `UUID`) VALUES (?, ?, ?, ?, ?)";
  }

  @Override
  public void bindToStatement(android.database.sqlite.SQLiteStatement statement, Project model) {
    // Column Boxed Type:java.lang.String
    if (((java.lang.String)model.getIdentifier()) != null)  {
      statement.bindString(1,((java.lang.String)model.getIdentifier()));
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
    if (((java.lang.String)model.getDescription()) != null)  {
      statement.bindString(3,((java.lang.String)model.getDescription()));
    } else {
      statement.bindNull(3);
    }
    // Column Boxed Type:java.lang.String
    if (((java.lang.String)model.getAlias()) != null)  {
      statement.bindString(4,((java.lang.String)model.getAlias()));
    } else {
      statement.bindNull(4);
    }
    // Column Boxed Type:java.lang.String
    if (((java.lang.String)model.getUUID()) != null)  {
      statement.bindString(5,((java.lang.String)model.getUUID()));
    } else {
      statement.bindNull(5);
    }

  }

  @Override
  public void bindToContentValues(ContentValues contentValues, Project model) {
    if (((java.lang.String)model.getIdentifier()) != null)  {
      contentValues.put("identifier",((java.lang.String)model.getIdentifier()));
    } else {
      contentValues.putNull("identifier");
    }
    if (((java.lang.String)model.getTitle()) != null)  {
      contentValues.put("title",((java.lang.String)model.getTitle()));
    } else {
      contentValues.putNull("title");
    }
    if (((java.lang.String)model.getDescription()) != null)  {
      contentValues.put("description",((java.lang.String)model.getDescription()));
    } else {
      contentValues.putNull("description");
    }
    if (((java.lang.String)model.getAlias()) != null)  {
      contentValues.put("alias",((java.lang.String)model.getAlias()));
    } else {
      contentValues.putNull("alias");
    }
    if (((java.lang.String)model.getUUID()) != null)  {
      contentValues.put("UUID",((java.lang.String)model.getUUID()));
    } else {
      contentValues.putNull("UUID");
    }

  }

  @Override
  public void bindToInsertValues(ContentValues contentValues, Project model) {
    if (((java.lang.String)model.getIdentifier()) != null)  {
      contentValues.put("identifier",((java.lang.String)model.getIdentifier()));
    } else {
      contentValues.putNull("identifier");
    }
    if (((java.lang.String)model.getTitle()) != null)  {
      contentValues.put("title",((java.lang.String)model.getTitle()));
    } else {
      contentValues.putNull("title");
    }
    if (((java.lang.String)model.getDescription()) != null)  {
      contentValues.put("description",((java.lang.String)model.getDescription()));
    } else {
      contentValues.putNull("description");
    }
    if (((java.lang.String)model.getAlias()) != null)  {
      contentValues.put("alias",((java.lang.String)model.getAlias()));
    } else {
      contentValues.putNull("alias");
    }
    if (((java.lang.String)model.getUUID()) != null)  {
      contentValues.put("UUID",((java.lang.String)model.getUUID()));
    } else {
      contentValues.putNull("UUID");
    }

  }

  @Override
  public boolean exists(Project model) {
    return new Select().from(Project.class).where(getPrimaryModelWhere(model)).hasData();
  }

  @Override
  public void loadFromCursor(Cursor cursor, Project model) {
    int indexidentifier = cursor.getColumnIndex("identifier");
    if (indexidentifier != -1)  {
      if (cursor.isNull(indexidentifier)) {
        model.setIdentifier(null);
      } else {
        model.setIdentifier(cursor.getString(indexidentifier));
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
    int indexdescription = cursor.getColumnIndex("description");
    if (indexdescription != -1)  {
      if (cursor.isNull(indexdescription)) {
        model.setDescription(null);
      } else {
        model.setDescription(cursor.getString(indexdescription));
      }
    }
    int indexalias = cursor.getColumnIndex("alias");
    if (indexalias != -1)  {
      if (cursor.isNull(indexalias)) {
        model.setAlias(null);
      } else {
        model.setAlias(cursor.getString(indexalias));
      }
    }
    int indexUUID = cursor.getColumnIndex("UUID");
    if (indexUUID != -1)  {
      if (cursor.isNull(indexUUID)) {
        model.setUUID(null);
      } else {
        model.setUUID(cursor.getString(indexUUID));
      }
    }
  }

  @Override
  public Object getCachingId(Project model) {
    return model.getIdentifier();
  }

  @Override
  public String getCachingColumnName() {
    return Project$Table.IDENTIFIER;
  }

  @Override
  public Object getCachingIdFromCursorIndex(Cursor cursor, int indexgetIdentifier) {
    return cursor.getString(indexgetIdentifier);
  }

  @Override
  public ConditionQueryBuilder<Project> getPrimaryModelWhere(Project model) {
    return new ConditionQueryBuilder<Project>(Project.class, Condition.column(Project$Table.IDENTIFIER).is((model.getIdentifier())));
  }

  @Override
  public ConditionQueryBuilder<Project> createPrimaryModelWhere() {
    return new ConditionQueryBuilder<Project>(Project.class, Condition.column(Project$Table.IDENTIFIER).is("?"));
  }

  @Override
  public String getCreationQuery() {
    return "CREATE TABLE IF NOT EXISTS `Project`(`identifier` TEXT, `title` TEXT, `description` TEXT, `alias` TEXT, `UUID` TEXT, PRIMARY KEY(`identifier`));";
  }

  @Override
  public final Project newInstance() {
    return new com.ibm.rqm.Model.Project();
  }
}
