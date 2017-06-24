package io.excitinglab.lifemanager;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView appVersion, aboutBody;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String version = "";
        if (pInfo != null) {
            version = "Version: " + pInfo.versionName;
        }

        appVersion = (TextView) findViewById(R.id.appVersion);
        appVersion.setText(version);

        aboutBody = (TextView) findViewById(R.id.aboutBody);
        aboutBody.setText(R.string.aboutText);
    }
}
