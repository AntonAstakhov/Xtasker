package io.excitinglab.xtasker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.excitinglab.xtasker.TodayFragment;

import static java.sql.Types.NULL;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }


//    Todo: FIGURE OUT WHETHER TO DELETE DB-INSTANCE OR NOT

//    public void finalize() throws Throwable {
//        if(null != sInstance)
//            sInstance.close();
//        super.finalize();
//    }

    private static final String LOG = DatabaseHelper.class.getName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TaskManager.db";

    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_LISTS = "lists";
    private static final String TABLE_SUBTASKS = "subtasks";

    private static final String LIST_ID = "id";
    private static final String LIST_NAME = "name";
    private static final String LIST_SORT = "my_sort";

    private static final String TASK_ID = "id";
    private static final String TASK_CREATION = "creation";
    private static final String TASK_NAME = "name";
    private static final String TASK_STATUS = "status";
    private static final String TASK_DEADLINE = "deadline";
    private static final String TASK_REMINDER = "reminder";
    private static final String TASK_NOTE = "note";
    private static final String TASK_P_ID = "p_id";

    private static final String SUBTASK_ID = "id";
    private static final String SUBTASK_NAME = "name";
    private static final String SUBTASK_STATUS = "status";
    private static final String SUBTASK_P_ID = "p_id";

    private static final String CREATE_TABLE_TASKS = "CREATE TABLE "
            + TABLE_TASKS + "(" + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK_CREATION + " LONG, "
            + TASK_NAME + " TEXT, " + TASK_STATUS + " INTEGER," + TASK_DEADLINE
            + " LONG, " + TASK_REMINDER + " LONG, "
            + TASK_NOTE + " TEXT, " + TASK_P_ID + " INTEGER);";

    private static final String CREATE_TABLE_LISTS = "CREATE TABLE " + TABLE_LISTS
            + "(" + LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + LIST_NAME + " TEXT, " +
            LIST_SORT + " INTEGER)";

    private static final String CREATE_TABLE_SUBTASKS = "CREATE TABLE "
            + TABLE_SUBTASKS + "(" + SUBTASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SUBTASK_STATUS + " INTEGER, " + SUBTASK_NAME + " TEXT, "
            + SUBTASK_P_ID + " INTEGER)";

    private Context context;
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASKS);
        db.execSQL(CREATE_TABLE_LISTS);
        db.execSQL(CREATE_TABLE_SUBTASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBTASKS);

        onCreate(db);
    }


    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


    // ------------------------ "lists" table methods ----------------//

    public int createList(Lists list) {
        SQLiteDatabase db = this.getWritableDatabase();

        String name = list.getName();
        String selectQuery = "SELECT  * FROM " + TABLE_LISTS + " WHERE " + LIST_NAME + " = " + "\"" + name + "\"";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) list.setName(changeList(list));
        else list.setName(list.getName());

        ContentValues values = new ContentValues();
        values.put(LIST_NAME, list.getName());
        values.put(LIST_SORT, list.getSort());
        long list_id = db.insert(TABLE_LISTS, null, values);

        if (list_id == -1) {
            return -1;
        } else {

        }

//        Log.e("SORT: ", String.valueOf(list.getSort()));

        c.close();

        db.close();

        return getList(list.getName()).getId();
    }

    private String changeList(Lists list) {
        SQLiteDatabase db = this.getWritableDatabase();

        String name = list.getName();
        String tempName = name;

        for (int i = 1; i < 1000000000; i++) {
            String selectQuery = "SELECT  * FROM " + TABLE_LISTS + " WHERE " + LIST_NAME + " = " + "\"" + tempName + "\"";
            Cursor c = db.rawQuery(selectQuery, null);
//            Log.e("CHANGE LIST", selectQuery);

            if (c.moveToFirst()) {
                Lists t = new Lists();
                t.setId(c.getInt((c.getColumnIndex(LIST_ID))));
                t.setName(c.getString(c.getColumnIndex(LIST_NAME)));
                t.setSort(c.getInt(c.getColumnIndex(LIST_SORT)));
                tempName = name + " " + i;
            }
            else {
                i = i-1;
                return name + " " + i;
            }
            c.close();
        }

        db.close();

        return name;
    }

    public Lists getList(String name) {
        String selectQuery = "SELECT  * FROM " + TABLE_LISTS +
                " WHERE " + LIST_NAME + " = " + "\"" + name + "\"";

//        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Lists t = new Lists();
        if (c.moveToFirst()) {
            t.setId(c.getInt((c.getColumnIndex(LIST_ID))));
            t.setName(c.getString(c.getColumnIndex(LIST_NAME)));
            t.setSort(c.getInt(c.getColumnIndex(LIST_SORT)));
        }
        c.close();
        db.close();

        return t;
    }

    public Lists getListByID(int id) {
        String selectQuery = "SELECT  * FROM " + TABLE_LISTS +
                " WHERE " + LIST_ID + " = " + id;

//        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Lists t = new Lists();
        if (c.moveToFirst()) {
            t.setId(c.getInt((c.getColumnIndex(LIST_ID))));
            t.setName(c.getString(c.getColumnIndex(LIST_NAME)));
            t.setSort(c.getInt(c.getColumnIndex(LIST_SORT)));
        }

        c.close();
        db.close();

        return t;
    }

    public List<Lists> getAllLists() {
        List<Lists> lists = new ArrayList<Lists>();
        String selectQuery = "SELECT  * FROM " + TABLE_LISTS;

//        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Lists t = new Lists();
                t.setId(c.getInt((c.getColumnIndex(LIST_ID))));
                t.setName(c.getString(c.getColumnIndex(LIST_NAME)));
                t.setSort(c.getInt(c.getColumnIndex(LIST_SORT)));

//                Log.e("ALL_LISTS: ", t.getId() + "  " + t.getName());

                lists.add(t);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return lists;
    }

    public boolean updateList(Lists list) {
        SQLiteDatabase db = this.getWritableDatabase();

        String name = list.getName();
        String selectQuery = "SELECT  * FROM " + TABLE_LISTS + " WHERE " + LIST_NAME + " = " + "\"" + name + "\"";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) list.setName(changeList(list));
        else list.setName(list.getName());

        ContentValues values = new ContentValues();
        values.put(LIST_NAME, list.getName());
        values.put(LIST_SORT, list.getSort());

        long i = db.update(TABLE_LISTS, values, LIST_ID + " = ?",
                new String[]{String.valueOf(list.getId())});

        c.close();
        db.close();

        if (i == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean updateListSort(Lists list) {
        SQLiteDatabase db = this.getWritableDatabase();

        String name = list.getName();
        String selectQuery = "SELECT  * FROM " + TABLE_LISTS + " WHERE " + LIST_NAME + " = " + "\"" + name + "\"";
        Cursor c = db.rawQuery(selectQuery, null);
//        if (c.moveToFirst()) list.setName(changeList(list));
//        else list.setName(list.getName());

        ContentValues values = new ContentValues();
        values.put(LIST_NAME, list.getName());
        values.put(LIST_SORT, list.getSort());

        long i = db.update(TABLE_LISTS, values, LIST_ID + " = ?",
                new String[]{String.valueOf(list.getId())});

        c.close();
        db.close();

        if (i == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public void deleteList(Lists list) {
        List<Task> tasks = getAllTasks(list);

        for (Task t : tasks) {
            deleteTask(t.getId());
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTS, LIST_ID + " = ?",
                new String[]{String.valueOf(list.getId())});

        db.close();
    }



    // ------------------------ "tasks" table methods ----------------//

//    Todo: ADD METHODS TO GET ALL TASKS SORTED


    public long createTask(Task task, int list_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASK_CREATION, new java.util.Date().getTime());
        values.put(TASK_NAME, task.getName());
        values.put(TASK_STATUS, 0);
        values.put(TASK_DEADLINE, task.getDeadline());
        values.put(TASK_REMINDER, task.getReminder());
        values.put(TASK_NOTE, task.getNote());
        values.put(TASK_P_ID, list_id);

        long task_id = db.insert(TABLE_TASKS, null, values);

        db.close();

        return task_id;
    }

    public Task getTask(int task_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_TASKS + " WHERE "
                + TASK_ID + " = " + task_id;

//        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();


        Task t = new Task();
        t.setId(c.getInt(c.getColumnIndex(TASK_ID)));
        t.setCreate(c.getLong(c.getColumnIndex(TASK_CREATION)));
        t.setName(c.getString(c.getColumnIndex(TASK_NAME)));
        t.setStatus(c.getInt(c.getColumnIndex(TASK_STATUS)));
        t.setDeadline(c.getLong(c.getColumnIndex(TASK_DEADLINE)));
        t.setReminder(c.getLong(c.getColumnIndex(TASK_REMINDER)));
        t.setNote(c.getString(c.getColumnIndex(TASK_NOTE)));
        t.setP_id(c.getInt(c.getColumnIndex(TASK_P_ID)));

        c.close();
        db.close();

        return t;
    }

    public List<Task> getAllTasks(Lists list) {
        List<Task> tasks = new ArrayList<Task>();

        String selectQuery = "SELECT " +
                TABLE_TASKS + "." + TASK_ID + ", " +
                TABLE_TASKS + "." + TASK_CREATION + ", " +
                TABLE_TASKS + "." + TASK_NAME + ", " +
                TABLE_TASKS + "." + TASK_STATUS + ", " +
                TABLE_TASKS + "." + TASK_DEADLINE + ", " +
                TABLE_TASKS + "." + TASK_REMINDER + ", " +
                TABLE_TASKS + "." + TASK_NOTE + ", " +
                TABLE_TASKS + "." + TASK_P_ID +
                " FROM " + TABLE_TASKS + " JOIN " + TABLE_LISTS + " ON " +
                TABLE_TASKS + "." + TASK_P_ID + " = " + TABLE_LISTS + "." + LIST_ID +
                " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = " + list.getId();

//        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setId(c.getInt(c.getColumnIndex(TASK_ID)));
                t.setName(c.getString(c.getColumnIndex(TASK_NAME)));
                t.setCreate(c.getLong(c.getColumnIndex(TASK_CREATION)));
                t.setStatus(c.getInt(c.getColumnIndex(TASK_STATUS)));
                t.setDeadline(c.getLong(c.getColumnIndex(TASK_DEADLINE)));
                t.setReminder(c.getLong(c.getColumnIndex(TASK_REMINDER)));
                t.setNote(c.getString(c.getColumnIndex(TASK_NOTE)));
                t.setP_id(c.getInt(c.getColumnIndex(TASK_P_ID)));


//                Log.d("SPECIFIC_LIST_TASKS: ", t.getName() + "; p_id:" + t.getP_id() + "; list_id:" + list.getId());

                tasks.add(t);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return tasks;
    }


    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<Task>();

        String selectQuery = "SELECT * FROM " + TABLE_TASKS;

//        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setId(c.getInt(c.getColumnIndex(TASK_ID)));
                t.setName(c.getString(c.getColumnIndex(TASK_NAME)));
                t.setCreate(c.getLong(c.getColumnIndex(TASK_CREATION)));
                t.setStatus(c.getInt(c.getColumnIndex(TASK_STATUS)));
                t.setDeadline(c.getLong(c.getColumnIndex(TASK_DEADLINE)));
                t.setReminder(c.getLong(c.getColumnIndex(TASK_REMINDER)));
                t.setNote(c.getString(c.getColumnIndex(TASK_NOTE)));
                t.setP_id(c.getInt(c.getColumnIndex(TASK_P_ID)));


//                Log.d("SPECIFIC_LIST_TASKS: ", t.getName() + "; p_id:" + t.getP_id() + "; list_id:" + list.getId());

                tasks.add(t);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return tasks;
    }

    public List<Task> getActiveTasks(Lists list) {
        List<Task> tasks = new ArrayList<Task>();

        String selectQuery = "SELECT " +
                TABLE_TASKS + "." + TASK_ID + ", " +
                TABLE_TASKS + "." + TASK_CREATION + ", " +
                TABLE_TASKS + "." + TASK_NAME + ", " +
                TABLE_TASKS + "." + TASK_STATUS + ", " +
                TABLE_TASKS + "." + TASK_DEADLINE + ", " +
                TABLE_TASKS + "." + TASK_REMINDER + ", " +
                TABLE_TASKS + "." + TASK_NOTE + ", " +
                TABLE_TASKS + "." + TASK_P_ID +
                " FROM " + TABLE_TASKS + " JOIN " + TABLE_LISTS + " ON " +
                TABLE_TASKS + "." + TASK_P_ID + " = " + TABLE_LISTS + "." + LIST_ID +
                " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = " + list.getId() +
                " AND " + TABLE_TASKS + "." + TASK_STATUS + " = 0";

//        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setId(c.getInt(c.getColumnIndex(TASK_ID)));
                t.setName(c.getString(c.getColumnIndex(TASK_NAME)));
                t.setCreate(c.getLong(c.getColumnIndex(TASK_CREATION)));
                t.setStatus(c.getInt(c.getColumnIndex(TASK_STATUS)));
                t.setDeadline(c.getLong(c.getColumnIndex(TASK_DEADLINE)));
                t.setReminder(c.getLong(c.getColumnIndex(TASK_REMINDER)));
                t.setNote(c.getString(c.getColumnIndex(TASK_NOTE)));
                t.setP_id(c.getInt(c.getColumnIndex(TASK_P_ID)));

//                Log.d("SPECIFIC_LIST_TASKS: ", t.getName() + "; p_id:" + t.getP_id() + "; list_id:" + list.getId());

                tasks.add(t);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return tasks;
    }

    public List<Task> getActiveTasksNoSort(Lists list, int sort) {

        SQLiteDatabase db = this.getReadableDatabase();

        List<Task> tasks = new ArrayList<>();
        String selectQuery = "";
        String ascdesc = "";
        int listSort = 0;

        if (sort == 0 || sort == 1) {
            if (list.getSort() == 0) ascdesc = " ASC";
            else ascdesc = " DESC";

            selectQuery = "SELECT " +
                    TABLE_TASKS + "." + TASK_ID + ", " +
                    TABLE_TASKS + "." + TASK_CREATION + ", " +
                    TABLE_TASKS + "." + TASK_NAME + ", " +
                    TABLE_TASKS + "." + TASK_STATUS + ", " +
                    TABLE_TASKS + "." + TASK_DEADLINE + ", " +
                    TABLE_TASKS + "." + TASK_REMINDER + ", " +
                    TABLE_TASKS + "." + TASK_NOTE + ", " +
                    TABLE_TASKS + "." + TASK_P_ID +
                    " FROM " + TABLE_TASKS + " JOIN " + TABLE_LISTS + " ON " +
                    TABLE_TASKS + "." + TASK_P_ID + " = " + TABLE_LISTS + "." + LIST_ID +
                    " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = " + list.getId() +
                    " AND " + TABLE_TASKS + "." + TASK_STATUS + " = 0" +
                    " ORDER BY " + TABLE_TASKS + "." + TASK_CREATION + ascdesc;
        }

        else if (sort == 2 || sort == 3) {
            if (list.getSort() == 2) ascdesc = " ASC";
            else ascdesc = " DESC";

            selectQuery = "SELECT " +
                    TABLE_TASKS + "." + TASK_ID + ", " +
                    TABLE_TASKS + "." + TASK_CREATION + ", " +
                    TABLE_TASKS + "." + TASK_NAME + ", " +
                    TABLE_TASKS + "." + TASK_STATUS + ", " +
                    TABLE_TASKS + "." + TASK_DEADLINE + ", " +
                    TABLE_TASKS + "." + TASK_REMINDER + ", " +
                    TABLE_TASKS + "." + TASK_NOTE + ", " +
                    TABLE_TASKS + "." + TASK_P_ID +
                    " FROM " + TABLE_TASKS + " JOIN " + TABLE_LISTS + " ON " +
                    TABLE_TASKS + "." + TASK_P_ID + " = " + TABLE_LISTS + "." + LIST_ID +
                    " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = " + list.getId() +
                    " AND " + TABLE_TASKS + "." + TASK_STATUS + " = 0" +
                    " ORDER BY " + TABLE_TASKS + "." + TASK_DEADLINE + ascdesc;
        }

        else if (sort == 4 || sort == 5) {
            if (list.getSort() == 4) ascdesc = " ASC";
            else ascdesc = " DESC";

            selectQuery = "SELECT " +
                    TABLE_TASKS + "." + TASK_ID + ", " +
                    TABLE_TASKS + "." + TASK_CREATION + ", " +
                    TABLE_TASKS + "." + TASK_NAME + ", " +
                    TABLE_TASKS + "." + TASK_STATUS + ", " +
                    TABLE_TASKS + "." + TASK_DEADLINE + ", " +
                    TABLE_TASKS + "." + TASK_REMINDER + ", " +
                    TABLE_TASKS + "." + TASK_NOTE + ", " +
                    TABLE_TASKS + "." + TASK_P_ID +
                    " FROM " + TABLE_TASKS + " JOIN " + TABLE_LISTS + " ON " +
                    TABLE_TASKS + "." + TASK_P_ID + " = " + TABLE_LISTS + "." + LIST_ID +
                    " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = " + list.getId() +
                    " AND " + TABLE_TASKS + "." + TASK_STATUS + " = 0" +
                    " ORDER BY " + TABLE_TASKS + "." + TASK_NAME + ascdesc;
        }

//        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setId(c.getInt(c.getColumnIndex(TASK_ID)));
                t.setName(c.getString(c.getColumnIndex(TASK_NAME)));
                t.setCreate(c.getLong(c.getColumnIndex(TASK_CREATION)));
                t.setStatus(c.getInt(c.getColumnIndex(TASK_STATUS)));
                t.setDeadline(c.getLong(c.getColumnIndex(TASK_DEADLINE)));
                t.setReminder(c.getLong(c.getColumnIndex(TASK_REMINDER)));
                t.setNote(c.getString(c.getColumnIndex(TASK_NOTE)));
                t.setP_id(c.getInt(c.getColumnIndex(TASK_P_ID)));

//                Log.d("SPECIFIC_LIST_TASKS: ", t.getName() + "; p_id:" + t.getP_id() + "; list_id:" + list.getId());

                tasks.add(t);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return tasks;
    }


    public List<Task> getActiveTasks(Lists list, int sort) {

        //    0 - TASK_CREATION
        //    1 - TASK_CREATION r
        //    2 - TASK_DEADLINE
        //    3 - TASK_DEADLINE r
        //    4 - TASK_NAME
        //    5 - TASK_NAME r

        SQLiteDatabase db = this.getReadableDatabase();

        List<Task> tasks = new ArrayList<>();
        String selectQuery = "";
        String ascdesc = "";
        int listSort = 0;

//        Log.e("LIST_SORT: ", String.valueOf(list.getSort()));

        if (sort == 0 || sort == 1) {
            if (list.getSort() == 0) ascdesc = " DESC";
            else ascdesc = " ASC";

            selectQuery = "SELECT " +
                    TABLE_TASKS + "." + TASK_ID + ", " +
                    TABLE_TASKS + "." + TASK_CREATION + ", " +
                    TABLE_TASKS + "." + TASK_NAME + ", " +
                    TABLE_TASKS + "." + TASK_STATUS + ", " +
                    TABLE_TASKS + "." + TASK_DEADLINE + ", " +
                    TABLE_TASKS + "." + TASK_REMINDER + ", " +
                    TABLE_TASKS + "." + TASK_NOTE + ", " +
                    TABLE_TASKS + "." + TASK_P_ID +
                    " FROM " + TABLE_TASKS + " JOIN " + TABLE_LISTS + " ON " +
                    TABLE_TASKS + "." + TASK_P_ID + " = " + TABLE_LISTS + "." + LIST_ID +
                    " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = " + list.getId() +
                    " AND " + TABLE_TASKS + "." + TASK_STATUS + " = 0" +
                    " ORDER BY " + TABLE_TASKS + "." + TASK_CREATION + ascdesc;
        }

        else if (sort == 2 || sort == 3) {
            if (list.getSort() == 2) ascdesc = " DESC";
            else ascdesc = " ASC";

            selectQuery = "SELECT " +
                    TABLE_TASKS + "." + TASK_ID + ", " +
                    TABLE_TASKS + "." + TASK_CREATION + ", " +
                    TABLE_TASKS + "." + TASK_NAME + ", " +
                    TABLE_TASKS + "." + TASK_STATUS + ", " +
                    TABLE_TASKS + "." + TASK_DEADLINE + ", " +
                    TABLE_TASKS + "." + TASK_REMINDER + ", " +
                    TABLE_TASKS + "." + TASK_NOTE + ", " +
                    TABLE_TASKS + "." + TASK_P_ID +
                    " FROM " + TABLE_TASKS + " JOIN " + TABLE_LISTS + " ON " +
                    TABLE_TASKS + "." + TASK_P_ID + " = " + TABLE_LISTS + "." + LIST_ID +
                    " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = " + list.getId() +
                    " AND " + TABLE_TASKS + "." + TASK_STATUS + " = 0" +
                    " ORDER BY " + TABLE_TASKS + "." + TASK_DEADLINE + ascdesc;
        }

        else if (sort == 4 || sort == 5) {
            if (list.getSort() == 4) ascdesc = " DESC";
            else ascdesc = " ASC";

            selectQuery = "SELECT " +
                    TABLE_TASKS + "." + TASK_ID + ", " +
                    TABLE_TASKS + "." + TASK_CREATION + ", " +
                    TABLE_TASKS + "." + TASK_NAME + ", " +
                    TABLE_TASKS + "." + TASK_STATUS + ", " +
                    TABLE_TASKS + "." + TASK_DEADLINE + ", " +
                    TABLE_TASKS + "." + TASK_REMINDER + ", " +
                    TABLE_TASKS + "." + TASK_NOTE + ", " +
                    TABLE_TASKS + "." + TASK_P_ID +
                    " FROM " + TABLE_TASKS + " JOIN " + TABLE_LISTS + " ON " +
                    TABLE_TASKS + "." + TASK_P_ID + " = " + TABLE_LISTS + "." + LIST_ID +
                    " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = " + list.getId() +
                    " AND " + TABLE_TASKS + "." + TASK_STATUS + " = 0" +
                    " ORDER BY " + TABLE_TASKS + "." + TASK_NAME + ascdesc;
        }



//        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setId(c.getInt(c.getColumnIndex(TASK_ID)));
                t.setName(c.getString(c.getColumnIndex(TASK_NAME)));
                t.setCreate(c.getLong(c.getColumnIndex(TASK_CREATION)));
                t.setStatus(c.getInt(c.getColumnIndex(TASK_STATUS)));
                t.setDeadline(c.getLong(c.getColumnIndex(TASK_DEADLINE)));
                t.setReminder(c.getLong(c.getColumnIndex(TASK_REMINDER)));
                t.setNote(c.getString(c.getColumnIndex(TASK_NOTE)));
                t.setP_id(c.getInt(c.getColumnIndex(TASK_P_ID)));

//                Log.d("SPECIFIC_LIST_TASKS: ", t.getName() + "; p_id:" + t.getP_id() + "; list_id:" + list.getId());

                tasks.add(t);
            } while (c.moveToNext());
        }

        c.close();

        if (ascdesc.equals(" DESC")) sort++;
        list.setSort(sort);
        updateListSort(list);

        db.close();

        return tasks;
    }


    public List<Task> getUnactiveTasks(Lists list) {
        List<Task> tasks = new ArrayList<Task>();

        String selectQuery = "SELECT " +
                TABLE_TASKS + "." + TASK_ID + ", " +
                TABLE_TASKS + "." + TASK_CREATION + ", " +
                TABLE_TASKS + "." + TASK_NAME + ", " +
                TABLE_TASKS + "." + TASK_STATUS + ", " +
                TABLE_TASKS + "." + TASK_DEADLINE + ", " +
                TABLE_TASKS + "." + TASK_REMINDER + ", " +
                TABLE_TASKS + "." + TASK_NOTE + ", " +
                TABLE_TASKS + "." + TASK_P_ID +
                " FROM " + TABLE_TASKS + " JOIN " + TABLE_LISTS + " ON " +
                TABLE_TASKS + "." + TASK_P_ID + " = " + TABLE_LISTS + "." + LIST_ID +
                " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = " + list.getId() +
                " AND " + TABLE_TASKS + "." + TASK_STATUS + " = 1";

//        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setId(c.getInt(c.getColumnIndex(TASK_ID)));
                t.setName(c.getString(c.getColumnIndex(TASK_NAME)));
                t.setCreate(c.getLong(c.getColumnIndex(TASK_CREATION)));
                t.setStatus(c.getInt(c.getColumnIndex(TASK_STATUS)));
                t.setDeadline(c.getLong(c.getColumnIndex(TASK_DEADLINE)));
                t.setReminder(c.getLong(c.getColumnIndex(TASK_REMINDER)));
                t.setNote(c.getString(c.getColumnIndex(TASK_NOTE)));
                t.setP_id(c.getInt(c.getColumnIndex(TASK_P_ID)));

//                Log.d("SPECIFIC_LIST_TASKS: ", t.getName() + "; p_id:" + t.getP_id() + "; list_id:" + list.getId());

                tasks.add(t);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return tasks;
    }



    public List<Task> getAllTasksFromInbox() {
        List<Task> tasks = new ArrayList<Task>();

        String selectQuery = "SELECT " +
                TABLE_TASKS + "." + TASK_ID + ", " +
                TABLE_TASKS + "." + TASK_CREATION + ", " +
                TABLE_TASKS + "." + TASK_NAME + ", " +
                TABLE_TASKS + "." + TASK_STATUS + ", " +
                TABLE_TASKS + "." + TASK_DEADLINE + ", " +
                TABLE_TASKS + "." + TASK_REMINDER + ", " +
                TABLE_TASKS + "." + TASK_NOTE + ", " +
                TABLE_TASKS + "." + TASK_P_ID +
                " FROM " + TABLE_TASKS +
                " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = 0";

//        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setId(c.getInt(c.getColumnIndex(TASK_ID)));
                t.setCreate(c.getLong(c.getColumnIndex(TASK_CREATION)));
                t.setName(c.getString(c.getColumnIndex(TASK_NAME)));
                t.setStatus(c.getInt(c.getColumnIndex(TASK_STATUS)));
                t.setDeadline(c.getLong(c.getColumnIndex(TASK_DEADLINE)));
                t.setReminder(c.getLong(c.getColumnIndex(TASK_REMINDER)));
                t.setNote(c.getString(c.getColumnIndex(TASK_NOTE)));
                t.setP_id(0);

                tasks.add(t);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return tasks;
    }


    public List<Task> getActiveTasksFromInbox() {
        List<Task> tasks = new ArrayList<Task>();

        String selectQuery = "SELECT " +
                TABLE_TASKS + "." + TASK_ID + ", " +
                TABLE_TASKS + "." + TASK_CREATION + ", " +
                TABLE_TASKS + "." + TASK_NAME + ", " +
                TABLE_TASKS + "." + TASK_STATUS + ", " +
                TABLE_TASKS + "." + TASK_DEADLINE + ", " +
                TABLE_TASKS + "." + TASK_REMINDER + ", " +
                TABLE_TASKS + "." + TASK_NOTE + ", " +
                TABLE_TASKS + "." + TASK_P_ID +
                " FROM " + TABLE_TASKS +
                " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = 0" +
                " AND " + TABLE_TASKS + "." + TASK_STATUS + " = 0";

//        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setId(c.getInt(c.getColumnIndex(TASK_ID)));
                t.setCreate(c.getLong(c.getColumnIndex(TASK_CREATION)));
                t.setName(c.getString(c.getColumnIndex(TASK_NAME)));
                t.setStatus(c.getInt(c.getColumnIndex(TASK_STATUS)));
                t.setDeadline(c.getLong(c.getColumnIndex(TASK_DEADLINE)));
                t.setReminder(c.getLong(c.getColumnIndex(TASK_REMINDER)));
                t.setNote(c.getString(c.getColumnIndex(TASK_NOTE)));
                t.setP_id(0);

                tasks.add(t);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return tasks;
    }





    public List<Task> getAllTasksForToday() {
        List<Task> tasks = new ArrayList<>();

//        Calendar now = Calendar.getInstance();
//        long time = new java.util.Date().getTime();


        String selectQuery = "SELECT * FROM " + TABLE_TASKS +
                " WHERE " + TASK_STATUS + " = 0" +
                " ORDER BY " + TASK_DEADLINE + " ASC";

//        String selectQuery = "SELECT " +
//                TABLE_TASKS + "." + TASK_ID + ", " +
//                TABLE_TASKS + "." + TASK_CREATION + ", " +
//                TABLE_TASKS + "." + TASK_NAME + ", " +
//                TABLE_TASKS + "." + TASK_STATUS + ", " +
//                TABLE_TASKS + "." + TASK_DEADLINE + ", " +
//                TABLE_TASKS + "." + TASK_REMINDER + ", " +
//                TABLE_TASKS + "." + TASK_NOTE + ", " +
//                TABLE_TASKS + "." + TASK_P_ID +
//                " FROM " + TABLE_TASKS + " JOIN " + TABLE_LISTS + " ON " +
//                TABLE_TASKS + "." + TASK_P_ID + " = " + TABLE_LISTS + "." + LIST_ID +
//                " WHERE " + TABLE_TASKS + "." + TASK_P_ID + " = " + list.getId() +
//                " AND " + TABLE_TASKS + "." + TASK_STATUS + " = 0" +
//                " AND " + TABLE_TASKS + "." + TASK_DEADLINE + " <= " + now.get(Calendar.DATE);

//        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setId(c.getInt(c.getColumnIndex(TASK_ID)));
                t.setName(c.getString(c.getColumnIndex(TASK_NAME)));
                t.setCreate(c.getLong(c.getColumnIndex(TASK_CREATION)));
                t.setStatus(c.getInt(c.getColumnIndex(TASK_STATUS)));
                t.setDeadline(c.getLong(c.getColumnIndex(TASK_DEADLINE)));
                t.setReminder(c.getLong(c.getColumnIndex(TASK_REMINDER)));
                t.setNote(c.getString(c.getColumnIndex(TASK_NOTE)));
                t.setP_id(c.getInt(c.getColumnIndex(TASK_P_ID)));


//                Log.d("SPECIFIC_LIST_TASKS: ", t.getName() + "; p_id:" + t.getP_id() + "; list_id:" + list.getId());

                tasks.add(t);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return tasks;
    }


    public List<Task> getAllTasksForWeek() {
        List<Task> tasks = new ArrayList<>();


        String selectQuery = "SELECT * FROM " + TABLE_TASKS +
                " WHERE " + TASK_STATUS + " = 0" +
                " ORDER BY " + TASK_DEADLINE + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setId(c.getInt(c.getColumnIndex(TASK_ID)));
                t.setName(c.getString(c.getColumnIndex(TASK_NAME)));
                t.setCreate(c.getLong(c.getColumnIndex(TASK_CREATION)));
                t.setStatus(c.getInt(c.getColumnIndex(TASK_STATUS)));
                t.setDeadline(c.getLong(c.getColumnIndex(TASK_DEADLINE)));
                t.setReminder(c.getLong(c.getColumnIndex(TASK_REMINDER)));
                t.setNote(c.getString(c.getColumnIndex(TASK_NOTE)));
                t.setP_id(c.getInt(c.getColumnIndex(TASK_P_ID)));


//                Log.d("SPECIFIC_LIST_TASKS: ", t.getName() + "; p_id:" + t.getP_id() + "; list_id:" + list.getId());

                tasks.add(t);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return tasks;
    }


    public int updateTask(Task task) {



//        Log.e ("VERY NEW: ", String.valueOf(task.getId()));


        long reminderOld = getTask((int) task.getId()).getReminder();
        long reminderNew = task.getReminder();

//        Log.e("BEFORE REM: ", String.valueOf(reminderOld));
//        Log.e("AFTER REM: ", String.valueOf(reminderNew));

        if (reminderOld == reminderNew) {
            // do nothing
        }
        else if (reminderOld != 0) {
            // cancel old alarm
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent stopIntent = new Intent(MainActivity.getAppContext(), Alarm.class);
            PendingIntent stopPI = PendingIntent.getBroadcast(MainActivity.getAppContext(), (int) task.getId(), stopIntent, 0);
            mgr.cancel(stopPI);
        }

        if (reminderNew != 0) {
            // add new alarm
            Intent intent2 = new Intent(context, Alarm.class);
            intent2.putExtra("Title", task.getName());
            intent2.putExtra("List", task.getP_id());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) task.getId(), intent2, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT < 19) {
                am.set(AlarmManager.RTC_WAKEUP, reminderNew, pendingIntent);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, reminderNew, pendingIntent);
            }
        }


//        if (task.getReminder() != 0) {
////            Log.e ("REMINDER: ", String.valueOf(task.getReminder()));
//            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            Intent stopIntent = new Intent(MainActivity.getAppContext(), Alarm.class);
//            PendingIntent stopPI = PendingIntent.getBroadcast(MainActivity.getAppContext(), (int) task.getId(), stopIntent, 0);
//            mgr.cancel(stopPI);
//        }


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASK_ID, task.getId());
        values.put(TASK_CREATION, task.getCreate());
        values.put(TASK_NAME, task.getName());
        values.put(TASK_STATUS, task.getStatus());
        values.put(TASK_DEADLINE, task.getDeadline());
        values.put(TASK_REMINDER, task.getReminder());
        values.put(TASK_NOTE, task.getNote());
        values.put(TASK_P_ID, task.getP_id());

//        Log.e("OLD: ", String.valueOf(getTask((int) task.getId())));
//        Log.e("NEW: ", String.valueOf(task.getReminder()));


        int i = db.update(TABLE_TASKS, values, TASK_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });

        db.close();

        return i;
    }

//    private void deleteReminder(Task task) {
//        if (task.getReminder() != 0) {
////            Log.e ("REMINDER: ", String.valueOf(task.getReminder()));
//            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            Intent stopIntent = new Intent(MainActivity.getAppContext(), Alarm.class);
//            PendingIntent stopPI = PendingIntent.getBroadcast(MainActivity.getAppContext(), (int) task.getId(), stopIntent, 0);
//            mgr.cancel(stopPI);
//        }
//    }

    public int completeTask(Task task) {

        if (task.getReminder() != 0) {
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent stopIntent = new Intent(MainActivity.getAppContext(), Alarm.class);
            PendingIntent stopPI = PendingIntent.getBroadcast(MainActivity.getAppContext(), (int) task.getId(), stopIntent, 0);
            mgr.cancel(stopPI);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASK_ID, task.getId());
        values.put(TASK_CREATION, task.getCreate());
        values.put(TASK_NAME, task.getName());
        values.put(TASK_STATUS, 1);
        values.put(TASK_DEADLINE, task.getDeadline());
        values.put(TASK_REMINDER, 0);
        values.put(TASK_NOTE, task.getNote());
        values.put(TASK_P_ID, task.getP_id());


        int i = db.update(TABLE_TASKS, values, TASK_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });


        db.close();

        return i;
    }

    public int returnTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASK_ID, task.getId());
        values.put(TASK_CREATION, task.getCreate());
        values.put(TASK_NAME, task.getName());
        values.put(TASK_STATUS, 0);
        values.put(TASK_DEADLINE, task.getDeadline());
        values.put(TASK_REMINDER, 0);
        values.put(TASK_NOTE, task.getNote());
        values.put(TASK_P_ID, task.getP_id());


        int i = db.update(TABLE_TASKS, values, TASK_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });


        db.close();

        return i;
    }

    public void deleteTask(long task_id) {

        if (getTask((int) task_id).getReminder() != 0) {
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent stopIntent = new Intent(MainActivity.getAppContext(), Alarm.class);
            PendingIntent stopPI = PendingIntent.getBroadcast(MainActivity.getAppContext(), (int) task_id, stopIntent, 0);
            mgr.cancel(stopPI);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, TASK_ID + " = ?",
                new String[] { String.valueOf(task_id) });

        db.close();
    }








    // ------------------------ "subtasks" table methods ----------------//



}

