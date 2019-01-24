package com.camp.bit.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    private TodoContract() {
    }

    public static class TodoEntry implements BaseColumns{
        public static String TABLE_NAME = "note";
        public static String COLUMN_NAME_MESSAGE = "message";
        public static String COLUMN_NAME_TIME  = "time";
        public static String COLUMN_NAME_STATE = "state";
    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "  + TodoEntry.TABLE_NAME + "("
            + TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + TodoEntry.COLUMN_NAME_MESSAGE + " TEXT , " + TodoEntry.COLUMN_NAME_TIME + " INTEGER , "
            +TodoEntry.COLUMN_NAME_STATE + " INTEGER " + ")";

    public static final String SQL_DELEYE_ENTRIES = "DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME;
}
