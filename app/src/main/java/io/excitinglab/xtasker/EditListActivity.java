package io.excitinglab.xtasker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditListActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    private Button btnSave;
    private EditText editText;
    int selectedID;
    Lists list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        Intent intent = getIntent();
        selectedID = intent.getIntExtra("id",-1);

        editText = (EditText) findViewById(R.id.editText);
//        btnSave = (Button) findViewById(R.id.btnSave);
//        btnDel= (Button) findViewById(R.id.btnDel);
        mDatabaseHelper = mDatabaseHelper.getInstance(this);

        getSupportActionBar().setTitle("Edit list");

        list = new Lists();
        list = mDatabaseHelper.getListByID(selectedID);
        editText.setText(list.getName());

        editText.setSelection(editText.getText().length());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String item = editText.getText().toString();
//
//                if (item.equals(mDatabaseHelper.getListByID(selectedID).getName())) {
//                    finish();
//                }
//
//                else if (!item.equals("")){
//                    Lists list;
//                    list = mDatabaseHelper.getListByID(selectedID);
//                    list.setName(item);
//                    mDatabaseHelper.updateList(list);
//                    toastMessage(mDatabaseHelper.getListByID(selectedID).getName());
//                    finish();
//                } else {
//                    toastMessage("You must enter a name");
//                }
//            }
//        });
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_rename, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            String i = editText.getText().toString();

            if (i.equals(mDatabaseHelper.getListByID(selectedID).getName())) {
                finish();
            }

            else if (!i.equals("")){
                Lists list;
                list = mDatabaseHelper.getListByID(selectedID);
                list.setName(i);
                mDatabaseHelper.updateList(list);
                toastMessage(mDatabaseHelper.getListByID(selectedID).getName());
                finish();
            } else {
                toastMessage("You must enter a name");
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
