package io.excitinglab.xtasker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    ListView listView;
//    private ArrayAdapter<String> mAdapter;
//    private ImageView imgNavHeaderBg, imgProfile;
//    private TextView txtName, txtWebsite;
//    private Toolbar toolbar;

    private DrawerLayout drawer;
    private View navHeader;

    private FloatingActionButton fab;

    public static int navItemIndex = 0;

    private static final String TAG_TODAY = "home";
    private static final String TAG_WEEK = "photos";
    private static final String TAG_INBOX = "movies";
    private static final String TAG_LIST = "list";

    private static int current_list_id;
    int selectedMenuItemId = 0;

    public static String CURRENT_TAG = TAG_TODAY;

    private String[] activityTitles;

    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    Intent intent, intent2;
    private NavigationView navigationView;
    DatabaseHelper mDatabaseHelper;

//    protected ActionBarDrawerToggle mDrawerToggle;
//    private DrawerLayout mDrawerLayout;

    private static boolean SHOW_COMPLETED = false;

    private static int SORTLIST = -1;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MainActivity.context = getApplicationContext();

        SHOW_COMPLETED = false;

        SORTLIST = -1;

        mDatabaseHelper = mDatabaseHelper.getInstance(this);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        navHeader = navigationView.getHeaderView(0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Today");
        setSupportActionBar(toolbar);

        addItemsRunTime(navigationView);

        intent2 = new Intent(this, AddTaskActivity.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = 0;
                if (navItemIndex == 3) {
                    i = current_list_id;
                }
                else {
                    i = 0;
                }

                intent2.putExtra("id", i);
                startActivityForResult(intent2, 2);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        navigationView.getMenu().getItem(0).setChecked(true);

//        loadNavHeader();

//        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_TODAY;
            loadHomeFragment();
        }

    }

    public static Context getAppContext() {
        return MainActivity.context;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_add) {
            item.setCheckable(false);
            intent = new Intent(this, AddListActivity.class);
            startActivityForResult(intent, 1);

        } else if (id == R.id.nav_inbox) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_INBOX;
        } else if (id == R.id.nav_today) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_TODAY;
        } else if (id == R.id.nav_week) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_WEEK;
        } else if (id == R.id.nav_setting) {
            item.setCheckable(false);
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rate) {

//            Todo: CHANGE URL

            item.setCheckable(false);
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
//            Uri uri = Uri.parse("market://details?id=com.wunderkinder.wunderlistandroid");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
//                        Uri.parse("http://play.google.com/store/apps/details?id=com.wunderkinder.wunderlistandroid")));
            }
//        } else if (id == R.id.nav_share) {
//            item.setCheckable(false);
//            intent = new Intent(this, ShareActivity.class);
//            startActivity(intent);
        } else if (id == R.id.nav_feedback) {
            item.setCheckable(false);
            intent = new Intent(this, FeedbackActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_help) {
            item.setCheckable(false);
            intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }
        else {

            navItemIndex = 3;
            CURRENT_TAG = TAG_LIST;

            String s = item.getTitle().toString();

            Lists list = mDatabaseHelper.getList(s);
            current_list_id = list.getId();

            final Menu menu = navigationView.getMenu();
            for (int i = 4; i < menu.size()-1; i++) {
                if(menu.getItem(i).getTitle().equals(item.getTitle())) {
                    selectedMenuItemId = i;
                    break;
                }
            }
        }

        loadHomeFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void addItemsRunTime(NavigationView navigationView) {

        ArrayList<Lists> lists = new ArrayList<>();
        lists.addAll(mDatabaseHelper.getAllLists());

        final Menu menu = navigationView.getMenu();

        for (int i = 0; i < lists.size(); i++) {
            String s = lists.get(i).getName();

//            if (s.length() > 24) {
//                s = s.substring(0, 22);
//                s = s + "...";
//            }

            menu.add(R.id.group2,Menu.NONE,4,s).setIcon(R.drawable.ic_list_black_24dp);
        }

//        for (int i = 0, count = navigationView.getChildCount(); i < count; i++) {
//            final View child = navigationView.getChildAt(i);
//            if (child != null && child instanceof ListView) {
//                final ListView menuView = (ListView) child;
//                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
//                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
//                wrapped.notifyDataSetChanged();
//            }
//        }

    }

//    private void toastMessage(String message){
//        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
//    }

    private void loadHomeFragment() {

        selectNavMenu();

        setToolbarTitle();

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {

                Fragment fragment = getHomeFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("id", current_list_id);
                bundle.putBoolean("show_completed", SHOW_COMPLETED);
                bundle.putInt("sort", SORTLIST);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


//                 Todo: REMEMBER THIS FUCKING LINE OF CODE FOR THE REST OF YOUR LIFE !!!!!
//                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);


                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();

                SORTLIST = -1;
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                TodayFragment todayFragment = new TodayFragment();
                return todayFragment;
            case 1:
                WeekFragment weekFragment = new WeekFragment();
                return weekFragment;
            case 2:
                InboxFragment inboxFragment = new InboxFragment();
                return inboxFragment;
            case 3:
                ListViewFragment listViewFragment = new ListViewFragment();
                return listViewFragment;
            default:
                return new TodayFragment();
        }

    }


    private void setToolbarTitle() {
//        getSupportActionBar().setTitle(activityTitles[navItemIndex]);

        if (CURRENT_TAG.equals(TAG_TODAY) || CURRENT_TAG.equals(TAG_WEEK) || CURRENT_TAG.equals(TAG_INBOX)) {
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        }
        else if (CURRENT_TAG.equals(TAG_LIST)){
            getSupportActionBar().setTitle(mDatabaseHelper.getListByID(current_list_id).getName());
        }
    }


    private void selectNavMenu() {
        if (navItemIndex == 3) navigationView.getMenu().getItem(selectedMenuItemId).setCheckable(true);
        else navigationView.getMenu().getItem(navItemIndex).setCheckable(true);
    }


    private void toggleFab() {
        if (navItemIndex==0 || navItemIndex==1 || navItemIndex==2 || navItemIndex==3)
            fab.show();
        else
            fab.hide();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (CURRENT_TAG.equals(TAG_LIST)) {
            if (SHOW_COMPLETED) {
                getMenuInflater().inflate(R.menu.main3, menu);
            }
            else {
                getMenuInflater().inflate(R.menu.main, menu);
            }
        }
        else if (CURRENT_TAG.equals(TAG_TODAY) || CURRENT_TAG.equals(TAG_WEEK) || CURRENT_TAG.equals(TAG_INBOX))
            getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_rename) {
            Intent editScreenIntent = new Intent(MainActivity.this, EditListActivity.class);
            editScreenIntent.putExtra("id", current_list_id);
            startActivity(editScreenIntent);
            return true;
        }

        else if (id == R.id.action_delete) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete list")
                    .setMessage("Are you sure you want to delete this list?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabaseHelper.deleteList(mDatabaseHelper.getListByID(current_list_id));
//                            toastMessage("removed from database");
                            CURRENT_TAG = TAG_TODAY;
//                            current_list_id = 0;
                            navItemIndex = 0;

                            final Menu menu = navigationView.getMenu();
                            menu.removeGroup(R.id.group2);

                            navigationView.getMenu().getItem(0).setChecked(true);

                            addItemsRunTime(navigationView);
                            loadHomeFragment();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

//        else if (id == R.id.action_edit) {
//
//        }

        else if (id == R.id.action_sort) {

//            Log.e("SORT_BEFORE:", String.valueOf(SORTLIST));

            //    0 - TASK_CREATION
            //    1 - TASK_CREATION r
            //    2 - TASK_DEADLINE
            //    3 - TASK_DEADLINE r
            //    4 - TASK_NAME
            //    5 - TASK_NAME r

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
            builderSingle.setIcon(R.drawable.ic_sort_black_24dp);
            builderSingle.setTitle("Select sort method");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Creation date");
//            arrayAdapter.add("Alphabetically Z-A");
            arrayAdapter.add("Due date");
//            arrayAdapter.add("Due date 31-1");
            arrayAdapter.add("Alphabetically");
//            arrayAdapter.add("Creation date reverse");
//        arrayAdapter.add("Completed");
//            arrayAdapter.add("Completed reverse");


            builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String strName = arrayAdapter.getItem(which);


                    if (arrayAdapter.getItem(which).equals("Creation date")) SORTLIST = 0;
                    else if (arrayAdapter.getItem(which).equals("Due date")) SORTLIST = 2;
                    else if (arrayAdapter.getItem(which).equals("Alphabetically")) SORTLIST = 4;

//                    Log.e("SORT_FUNC:", String.valueOf(SORTLIST));

                    loadHomeFragment();


//                AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
//                builderInner.setMessage(strName);
//                builderInner.setTitle("Your Selected Item is");
//                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog,int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builderInner.show();
                }
            });

            builderSingle.show();

//            Log.e("FUNC_END: ", String.valueOf(SORTLIST))


//            sortList();




//            Log.e("SORT_AFTER_FUNC", String.valueOf(SORTLIST));
        }

        else if (id == R.id.action_show_completed) {
            SHOW_COMPLETED = true;
            loadHomeFragment();
        }

        else if (id == R.id.action_hide_completed) {
            SHOW_COMPLETED = false;
            loadHomeFragment();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                final Menu menu = navigationView.getMenu();
                String result = data.getStringExtra("result");
                CURRENT_TAG = TAG_LIST;
                navItemIndex = 3;
                current_list_id = mDatabaseHelper.getList(result).getId();

                selectedMenuItemId = menu.size()-4;

            }
            if (resultCode == Activity.RESULT_CANCELED) {
//                onResume();
            }
        }

        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                int i = 2;
                int result = data.getIntExtra("result2", i);

                if (result == 0) {
                    current_list_id = 0;
                    CURRENT_TAG = TAG_INBOX;
                    navItemIndex = 2;
                }
                else {

                    final Menu menu = navigationView.getMenu();
                    menu.removeGroup(R.id.group2);
                    addItemsRunTime(navigationView);

                    boolean z = false;
                    for (int j = 0; j < navigationView.getMenu().size(); j++) {
                        if (navigationView.getMenu().getItem(j).getTitle().toString().equals(mDatabaseHelper.getListByID(result).getName())) {
                            selectedMenuItemId = j;
                            z = true;
                            break;
                        }
                    }
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_LIST;
                    current_list_id = result;
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
//                onResume();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        final Menu menu = navigationView.getMenu();
        menu.removeGroup(R.id.group2);

        addItemsRunTime(navigationView);

        if (navItemIndex == 3) navigationView.getMenu().getItem(selectedMenuItemId).setChecked(true);
        else navigationView.getMenu().getItem(navItemIndex).setChecked(true);

        loadHomeFragment();
    }

}
