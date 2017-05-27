package io.excitinglab.xtasker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AddTaskActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    DatabaseHelper mDatabaseHelper;


    //    private EditText editText;
    private TextView textView;
    private static String selectedList;

    private int selectedID;

    static final int DUE_DIALOG_ID = 0;
    static final int REM_DIALOG_ID = 1;
    TextView editDueDate, editReminder;

    TextView btnDue, btnRem;
    String dateString, timeString;
    EditText editNotes;
    EditText editTaskName;
    int btnID;

    int hour_x, minute_x, day_x, month_x, year_x; // reminder
    int day_y, month_y, year_y; // due date

    Spinner dropdown;

    long reminderTime = 0;
    long dueTime = 0;

    Calendar today;

    ImageButton deleteDue, deleteRem;

    private static String TaskName;

    private static int ID;

    Intent newList;

    Button btnAdd;

    private static Context mContext;
    public static Context getActivityOneContext() {
        return AddTaskActivity.mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mContext = getApplicationContext();

        mDatabaseHelper = mDatabaseHelper.getInstance(this);

        TaskName = "";

        ID = 0;

        newList = new Intent(this, AddListActivity.class);

        Intent intent = getIntent();
        selectedID = intent.getIntExtra("id", -1);

        today = Calendar.getInstance();
        day_x = today.get(Calendar.DAY_OF_MONTH);
        month_x = today.get(Calendar.MONTH);
        year_x = today.get(Calendar.YEAR);
        hour_x = today.get(Calendar.HOUR_OF_DAY);
        minute_x = today.get(Calendar.MINUTE);
        day_y = today.get(Calendar.DAY_OF_MONTH);
        month_y = today.get(Calendar.MONTH);
        year_y = today.get(Calendar.YEAR);

        editTaskName = (EditText) findViewById(R.id.editTaskName);
        btnDue = (TextView) findViewById(R.id.editDueDate);
        btnRem = (TextView) findViewById(R.id.editReminder);
        btnAdd = (Button) findViewById(R.id.btnAdd);
//        editNotes = (EditText) findViewById(R.id.editNotes);

        deleteDue = (ImageButton) findViewById(R.id.deleteDue);
        deleteRem = (ImageButton) findViewById(R.id.deleteRem);

        dropdown = (Spinner) findViewById(R.id.spinner1);
        addItemsRunTime();

        btnDue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                btnID = 1;
                DatePickerDialog dpickerListner = new DatePickerDialog(AddTaskActivity.this, AddTaskActivity.this, year_y, month_y, day_y);
                dpickerListner.show();

            }
        });

        btnRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                btnID = 2;
                DatePickerDialog dtpickerListner = new DatePickerDialog(AddTaskActivity.this, AddTaskActivity.this, year_x, month_x, day_x);
                dtpickerListner.show();
            }
        });

        selectedList = "Inbox";
        if (selectedID > 0) {
            selectedList = mDatabaseHelper.getListByID(selectedID).getName();
        }

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (dropdown.getSelectedItem().toString().equals("Add new list...")) {
                    startActivityForResult(newList, 3);
                }
                else {
                    selectedList = dropdown.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        deleteDue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                year_y = 0;
//                month_y = 0;
//                day_y = 0;

                dueTime = 0;
                btnDue.setText("");
            }
        });

        deleteRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                year_x = 0;
//                month_x = 0;
//                day_x = 0;
//                hour_x = 0;
//                minute_x = 0;

                reminderTime = 0;
                btnRem.setText("");
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = editTaskName.getText().toString();
                if (editTaskName.length() != 0) {
                    AddData(newEntry);
                } else {
                    toastMessage("You must put something in the text field!");
                }
            }
        });


//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    private void addItemsRunTime() {
        ArrayList<Lists> lists = new ArrayList<>();
        lists.addAll(mDatabaseHelper.getAllLists());

        ArrayList<String> items = new ArrayList<>();

        if (selectedID != 0) {
//            Log.e("HEY: ", "ID != 0 !!!!!");
            String s = mDatabaseHelper.getListByID(selectedID).getName();
            for (int i = 0; i < lists.size(); i++) {
                if (lists.get(i).getName().equals(s)) {
                    items.add(s);
                    lists.remove(i);
                    break;
                }
            }
        }
        items.add("Inbox");

        for (int i = 0; i < lists.size(); i++) {
            items.add(lists.get(i).getName());
        }

        items.add(items.size(), "Add new list...");

        ArrayAdapter<String> adapter = new ArrayAdapter(this,R.layout.spinner_item, items);

        dropdown.setAdapter(adapter);
    }


    public void AddData(String newEntry) {
        Task task = new Task(newEntry);
        long insertData = -1;
        int i = 0;

        if (selectedList.equals("Inbox")) {
            i = 0;
        } else {
            i = mDatabaseHelper.getList(selectedList).getId();
        }


        if (dueTime != 0) task.setDeadline(dueTime);

        Date nowDate = new Date (new java.util.Date().getTime());
        Date datetime = new Date (reminderTime);
        if (reminderTime != 0){
            if (datetime.before(nowDate)) {
                toastMessage("Reminder: wrong time!");
                reminderTime = 0;
            }
            task.setReminder(reminderTime);
        }

        insertData = mDatabaseHelper.createTask(task, i);


        TaskName = newEntry;
        ID = (int) insertData;

        if (reminderTime != 0) {
            Intent intent2 = new Intent(this, Alarm.class);
            intent2.putExtra("Title", editTaskName.getText().toString());
            intent2.putExtra("List", selectedList);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ID, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT < 19) {
                am.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
            }


        }






//        if (reminderTime != 0) {
//            NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(this)
//                            .setSmallIcon(R.drawable.ic_alarm_black_18dp)
//                            .setContentTitle(editTaskName.getText().toString())
//                            .setContentText(selectedList);
//            Intent resultIntent = new Intent(this, MainActivity.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//            stackBuilder.addParentStack(MainActivity.class);
//            stackBuilder.addNextIntent(resultIntent);
//            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            mBuilder.setContentIntent(resultPendingIntent);
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
//        }



        if (insertData == -1) {
            toastMessage("Something went wrong");
        } else {
            toastMessage("Data Successfully Inserted!");
        }


//        Date datetime=new Date(task.getReminder());
//        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy.MM.dd HH:mm");
//        String temp = sdf1.format(datetime);
//
//        Date datetime2=new Date(task.getDeadline());
//        SimpleDateFormat sdf2=new SimpleDateFormat("yyyy.MM.dd");
//        String temp2 = sdf2.format(datetime2);
//
//        Log.e("TASK: ", task.getName() + ", " + temp);


        Intent returnIntent = new Intent();
        returnIntent.putExtra("result2", i);
        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (btnID == 1) {
            year_y = year;
            month_y = month;
            day_y = dayOfMonth;
//            int temp_month = month_y + 1;
//            dateString = day_y + "." + temp_month + "." + year_y;
//            btnDue.setText(dateString);

            today.set(year_y, month_y, day_y);
            dueTime = today.getTimeInMillis();

            Date datetime=new Date(dueTime);
            SimpleDateFormat sdf1=new SimpleDateFormat("dd.MM.yyyy");
            dateString = sdf1.format(datetime);
            btnDue.setText(dateString);

//            Date datetime2=new Date(task.getDeadline());
//            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy.MM.dd");
//            String temp2 = sdf2.format(datetime2);
//
//            Log.e("TASK: ", task.getName() + ", " + temp);

        }
        if (btnID == 2) {
            year_x = year;
            month_x = month;
            day_x = dayOfMonth;
//            int temp_month = month_y + 1;
//            dateString = day_x + "." + temp_month + "." + year_x;

            TimePickerDialog tpickerListner = new TimePickerDialog(AddTaskActivity.this, AddTaskActivity.this, hour_x, minute_x, true);
            tpickerListner.show();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hour_x = hourOfDay;
        minute_x = minute;
        timeString = hour_x + ":" + minute_x;
//        btnRem.setText(timeString + " " + dateString);

        today.set(year_x, month_x, day_x, hour_x, minute_x);
        reminderTime = today.getTimeInMillis();


        Date datetime=new Date(reminderTime);
        SimpleDateFormat sdf1=new SimpleDateFormat("dd.MM.yyyy HH:mm");
        timeString = sdf1.format(datetime);
        btnRem.setText(timeString);



//        handleNotification(reminderTime);


//        Intent notifyIntent = new Intent(this,Alarm.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//
//        alarmManager.set(AlarmManager.RTC_WAKEUP,  reminderTime, pendingIntent);










//        final AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        final Intent intentAlarm = new Intent(AddTaskActivity.this, Alarm.class);
//        if (alarmManager!= null) {
//            alarmManager.cancel(PendingIntent.getBroadcast(AddTaskActivity.this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
//        }
//        alarmManager.set(AlarmManager.RTC_WAKEUP,reminderTime, PendingIntent.getBroadcast(AddTaskActivity.this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));





//                    Date datetime2=new Date(task.getDeadline());
//            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy.MM.dd");
//            String temp2 = sdf2.format(datetime2);
    }


//    public String getTaskName() {
//        return TaskName;
//    }
//
//    public String getListName() {
//        Log.e("Task List: ", selectedList);
//        return selectedList;
//    }
//
//    public int getId() {
//        return ID;
//    }






//    private void handleNotification(long time) {
//        Intent alarmIntent = new Intent(this, Alarm.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 5000, pendingIntent);
//    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.action_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            String newEntry = editTaskName.getText().toString();
            if (editTaskName.length() != 0) {
                AddData(newEntry);
            } else {
                toastMessage("You must put something in the text field!");
            }
            return true;
        }

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        addItemsRunTime();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 3) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                selectedID = mDatabaseHelper.getList(result).getId();
                selectedList = result;
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }
}

