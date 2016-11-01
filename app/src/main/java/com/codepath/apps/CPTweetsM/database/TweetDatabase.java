package com.codepath.apps.CPTweetsM.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by chmanish on 10/29/16.
 */
@Database(name = TweetDatabase.NAME, version = TweetDatabase.VERSION)
public class TweetDatabase {

    public static final String NAME = "TweetDatabase";

    public static final int VERSION = 1;
}
