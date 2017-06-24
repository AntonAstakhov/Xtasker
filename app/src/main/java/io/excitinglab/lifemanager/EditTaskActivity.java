package io.excitinglab.lifemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    DatabaseHelper mDatabaseHelper;

    String selectedList;
    int listID;
    int taskID;

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

    Button btnDel;

    Intent newList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        mDatabaseHelper = DatabaseHelper.getInstance(this);

        newList = new Intent(this, AddListActivity.class);


        Intent intent = getIntent();
        taskID = intent.getIntExtra("id", -1);
        listID = mDatabaseHelper.getTask(taskID).getP_id();

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
        editNotes = (EditText) findViewById(R.id.editNotes);
        btnDel = (Button) findViewById(R.id.btnDel);
        deleteDue = (ImageButton) findViewById(R.id.deleteDue);
        deleteRem = (ImageButton) findViewById(R.id.deleteRem);

        dropdown = (Spinner) findViewById(R.id.spinner1);
        addItemsRunTime();

        setFields(taskID);

        editTaskName.setSelection(editTaskName.getText().length());

        reminderTime = mDatabaseHelper.getTask(taskID).getReminder();
        dueTime = mDatabaseHelper.getTask(taskID).getDeadline();

        btnDue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                btnID = 1;
                DatePickerDialog dpickerListner = new DatePickerDialog(EditTaskActivity.this, EditTaskActivity.this, year_y, month_y, day_y);
                dpickerListner.show();

            }
        });

        btnRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                btnID = 2;
                DatePickerDialog dtpickerListner = new DatePickerDialog(EditTaskActivity.this, EditTaskActivity.this, year_x, month_x, day_x);
                dtpickerListner.show();
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditTaskActivity.this)
                        .setTitle("Delete task")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabaseHelper.deleteTask(taskID);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .show();
            }
        });

        selectedList = "Inbox";
        if (listID > 0) {
            selectedList = mDatabaseHelper.getListByID(listID).getName();
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
                dueTime = 0;
                btnDue.setText("");
            }
        });

        deleteRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderTime = 0;
                btnRem.setText("");
            }
        });

    }



    private void setFields(int id) {
        String s = mDatabaseHelper.getTask(id).getName();
        editTaskName.setText(s);

        if (mDatabaseHelper.getTask(id).getDeadline() != 0) {
            Date datetime = new Date(mDatabaseHelper.getTask(id).getDeadline());
            SimpleDateFormat sdf1=new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String s1 = sdf1.format(datetime);
            btnDue.setText(s1);
        }

        if (mDatabaseHelper.getTask(id).getReminder() !=0 ) {
            Date datetime2=new Date(mDatabaseHelper.getTask(id).getReminder());
            SimpleDateFormat sdf2=new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            String s2 = sdf2.format(datetime2);
            btnRem.setText(s2);
        }

        s = mDatabaseHelper.getTask(id).getNote();
        editNotes.setText(s);
    }


    private void addItemsRunTime() {
        ArrayList<Lists> lists = new ArrayList<>();
        lists.addAll(mDatabaseHelper.getAllLists());

        ArrayList<String> items = new ArrayList<>();

        if (listID != 0) {
            String s = mDatabaseHelper.getListByID(listID).getName();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);
    }


    public void AddData() {
        Task task = new Task();
        task.setId(taskID);

        task.setCreate(mDatabaseHelper.getTask(taskID).getCreate());

        String s = editTaskName.getText().toString();
        if (editTaskName.length() != 0) {
            task.setName(s);
        }
        else {
            task.setName(mDatabaseHelper.getTask(taskID).getName());
        }

        long insertData;
        int i;

        if (selectedList.equals("Inbox")) {
            i = 0;
        } else {
            i = mDatabaseHelper.getList(selectedList).getId();
        }

        task.setStatus(mDatabaseHelper.getTask(taskID).getStatus());

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

        s = editNotes.getText().toString();
        task.setNote(s);
        task.setP_id(i);

        insertData = mDatabaseHelper.updateTask(task);

        if (insertData == -1) {
            toastMessage("Something went wrong");
        }
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

            today.set(year_y, month_y, day_y);
            dueTime = today.getTimeInMillis();

            Date datetime=new Date(dueTime);
            SimpleDateFormat sdf1=new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            dateString = sdf1.format(datetime);
            btnDue.setText(dateString);

        }
        if (btnID == 2) {
            year_x = year;
            month_x = month;
            day_x = dayOfMonth;

            TimePickerDialog tpickerListner = new TimePickerDialog(EditTaskActivity.this, EditTaskActivity.this, hour_x, minute_x, true);
            tpickerListner.show();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hour_x = hourOfDay;
        minute_x = minute;
        timeString = hour_x + ":" + minute_x;

        today.set(year_x, month_x, day_x, hour_x, minute_x);
        reminderTime = today.getTimeInMillis();


        Date datetime=new Date(reminderTime);
        SimpleDateFormat sdf1=new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        timeString = sdf1.format(datetime);
        btnRem.setText(timeString);


        Date nowDate = new Date (new java.util.Date().getTime());
        datetime = new Date (reminderTime);
        if (reminderTime != 0){
            if (datetime.before(nowDate)) {
                toastMessage("Reminder: wrong time!");
                reminderTime = 0;
                btnRem.setText("");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            AddData();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        AddData();

        finish();
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
                listID = mDatabaseHelper.getList(result).getId();
                selectedList = result;
            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//
//            }
        }
    }
}
