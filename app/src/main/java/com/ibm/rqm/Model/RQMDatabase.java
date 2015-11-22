package com.ibm.rqm.Model;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by JACK on 2015/5/14.
 */

@Database(name = RQMDatabase.NAME, version = RQMDatabase.VERSION)

public class RQMDatabase {
    public static final String NAME = "RQMDatabase";
    public static final int VERSION = 1;
}
