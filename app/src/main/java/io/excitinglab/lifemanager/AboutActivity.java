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
        String version = pInfo.versionName;

        appVersion = (TextView) findViewById(R.id.appVersion);
        appVersion.setText("Version: "+version);

        aboutBody = (TextView) findViewById(R.id.aboutBody);
        aboutBody.setText("LifeManager is a simple yet effective tool for your task management. There is no bloat or clutter – just what you need to get things done.\n" +
                "LifeManager is currently in alpha testing. Please let us know if you encounter any issues.\n" +
                "excitinglab.io@gmail.com\n" +
                "SwipeActionAdapter\n" +
                "Copyright (c) 2014 Wouter Dullaert\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "http://www.apache.org/licenses/LICENSE-2.0\n" +
                "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and limitations under the License.\n");
    }
}
