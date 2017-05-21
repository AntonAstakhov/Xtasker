package io.excitinglab.xtasker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.sql.Types.NULL;


/**
 * Created by Alex on 14.05.2017.
 */

public class MyAdapter extends BaseAdapter {


    DatabaseHelper mDatabaseHelper;
//    CheckBox checkBox;


    Context context;
    ArrayList<Task> tasks;
    ArrayList<Boolean> checkedState;
    private static LayoutInflater inflater = null;

    public MyAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        checkedState=new ArrayList<>(tasks.size());
        for(int i=0; i<tasks.size();i++)
            checkedState.add(false);
    }



    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    Intent intent;
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, parent,false);


        mDatabaseHelper = mDatabaseHelper.getInstance(context);



//        checkBox = (CheckBox) vi.findViewById(R.id.img_choice);
//
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    checkedState.set(position,true);
//
//                   	mDatabaseHelper.completeTask(tasks.get(position));
//                    tasks.remove(position);
//                    checkedState.remove(position);
//                    notifyDataSetChanged();
//                }
//                else {
//                    checkedState.set(position,false);
//                }
//            }
//        });


//        final CheckBox checkBox = (CheckBox) vi.findViewById(R.id.check_box);
//        checkBox.setChecked(checkedState.get(position));


//        checkBox.setChecked(checkedState.get(position));//испольняется при каждом вызове notifyDataSetChanged

//        checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                CheckBox cb=(CheckBox) v.findViewById(R.id.check_box);
//
//                if (cb.isChecked()) {
//                    checkedState.set(position,true);
//                    checkBox.jumpDrawablesToCurrentState();
//                    notifyDataSetChanged();
//                    toastMessage("chB set to TRUE");
////                    try{
////                        Thread.sleep(500);
////                    }catch(Exception e){
////
////                    }
//
//
//
//
//                    tasks.remove(position);
//                    checkedState.remove(position);
//                    checkBox.jumpDrawablesToCurrentState();
//
//
//
//                } else {
//                    checkedState.set(position,false);
//                    toastMessage("chB is not checked");
//                }
//            }
//        });



        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, EditTaskActivity.class);
                int i = (int) tasks.get(position).getId();
                intent.putExtra("id", i);
                context.startActivity(intent);
            }
        });



//        checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                CheckBox cb=(CheckBox) v.findViewById(R.id.check_box);
//                if (cb.isChecked()) {
//
////                    cb.setChecked(true);
//                    checkedState.set(position,true);
//
//
//                    mDatabaseHelper.completeTask(tasks.get(position));
//                    tasks.remove(position);
//                    checkedState.remove(position);
//
//
//                } else {
////                    cb.setChecked(false);
//                    checkedState.set(position,false);
//                }
//                notifyDataSetChanged();
//
//            }
//        });


//        Todo: Add method like ' onItemClickListener '
//
//        intent = new Intent(context, EditTaskActivity.class);
//        int i = (int) tasks.get(position).getId();
//        intent.putExtra("id", i);
//        context.startActivity(intent);
//

//        checkBox.setChecked(checkedState.get(position));


        TextView text1 = (TextView) vi.findViewById(R.id.tv_header);
        TextView text2 = (TextView) vi.findViewById(R.id.tv_list);
        TextView text3 = (TextView) vi.findViewById(R.id.tv_date);
        TextView text4 = (TextView) vi.findViewById(R.id.tv_notif);
        ImageView img1=(ImageView) vi.findViewById(R.id.img_date);
        ImageView img2=(ImageView) vi.findViewById(R.id.img_notif);



        text1.setText(tasks.get(position).getName());

        if (tasks.get(position).getStatus() == 1) {
            text1.setPaintFlags(text1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            text1.setPaintFlags(0);
        }



//        if (tasks.get(position).getP_id() == 0) {
//            text2.setText("Inbox");
//        }
//        else {
//            text2.setText(tasks.get(position).getName());
//        }

//        text2.setText(mDatabaseHelper.getListByID(tasks.get(position).getP_id()).getName());

        text2.setText("");


        String dateString="";
        String timeString="";
        if (tasks.get(position).getDeadline()!=NULL) {
            Date datetime=new Date(tasks.get(position).getDeadline());
            SimpleDateFormat sdf1=new SimpleDateFormat("EEE, d MMM");
            dateString=sdf1.format(datetime);

            img1.setVisibility(View.VISIBLE);
        }
        else {
            img1.setVisibility(View.INVISIBLE);
        }

        if (tasks.get(position).getReminder()!=NULL) {
            Date datetime=new Date(tasks.get(position).getReminder());
//            SimpleDateFormat sdf2=new SimpleDateFormat("EEE, d MMM hh:mm aaa");
            SimpleDateFormat sdf2=new SimpleDateFormat("hh:mm a");
//            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy.MM.dd HH:mm");
            timeString=sdf2.format(datetime);

            img2.setVisibility(View.VISIBLE);
        }
        else {
            img2.setVisibility(View.INVISIBLE);
        }

        text3.setText(dateString);
        text4.setText(timeString);

        return vi;
    }


    private void toastMessage(String message){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

}

