package io.excitinglab.lifemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Send feedback");

        final EditText textIssue = (EditText) findViewById(R.id.textIssue);
        final EditText textNotes = (EditText) findViewById(R.id.textNotes);
        Button btnSend = (Button) findViewById(R.id.buttonSendFeedback);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toemailAddress = "excitinglab.io@gmail.com";
                String msubject = textIssue.getText().toString();
                String mmessage = textNotes.getText().toString();

                Intent emailApp = new Intent(Intent.ACTION_SEND);
                emailApp.putExtra(Intent.EXTRA_EMAIL, new String[]{toemailAddress});
                emailApp.putExtra(Intent.EXTRA_SUBJECT, msubject);
                emailApp.putExtra(Intent.EXTRA_TEXT, mmessage);
                emailApp.setType("message/rfc822");
                startActivity(Intent.createChooser(emailApp, "Send Email Via"));

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
