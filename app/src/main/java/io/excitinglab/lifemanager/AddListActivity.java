package io.excitinglab.lifemanager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import android.view.Menu;


public class AddListActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    private EditText editText;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        mDatabaseHelper = DatabaseHelper.getInstance(this);
        editText = (EditText) findViewById(R.id.editText);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Create new list");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void AddData(String newEntry) {
        Lists list = new Lists(newEntry);
        int insertData = mDatabaseHelper.createList(list);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",mDatabaseHelper.getListByID(insertData).getName());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_add, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            String newEntry = editText.getText().toString();
            if (editText.length() != 0) {
                AddData(newEntry);
                editText.setText("");
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

}
