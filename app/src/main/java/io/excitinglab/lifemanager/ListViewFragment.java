package io.excitinglab.lifemanager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.ArrayList;

public class ListViewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

//    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;

    public ListViewFragment() {

    }

    public static ListViewFragment newInstance(String param1, String param2) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_PARAM2, param2);
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static int selectedID;
    private boolean SHOW_COMPLETED;
    private int SORTLIST = -1;

    DatabaseHelper mDatabaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    MyAdapter adapter;
    SwipeActionAdapter adapter1;

    int s;

    @Override
    public void onStart(){
        super.onStart();

        ListView mListView;

        mDatabaseHelper = DatabaseHelper.getInstance(getActivity());
        mListView = (ListView) getActivity().findViewById(R.id.listView);

        Lists list = mDatabaseHelper.getListByID(selectedID);
        final ArrayList<Task> tasks = new ArrayList<>();


        if (SORTLIST == -1) {
            SORTLIST = list.getSort();
            tasks.addAll(mDatabaseHelper.getActiveTasksNoSort(list, SORTLIST));
        }
        else {
            tasks.addAll(mDatabaseHelper.getActiveTasks(list, SORTLIST));
        }

        SORTLIST = -1;

        s = tasks.size();

        if (SHOW_COMPLETED) {
            tasks.addAll(mDatabaseHelper.getUnactiveTasks(list));
        }

        adapter = new MyAdapter(getActivity(),tasks);
        mListView.setAdapter(adapter);

        adapter1 = new SwipeActionAdapter(adapter);
        adapter1.setListView(mListView);
        mListView.setAdapter(adapter1);

        adapter1.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
                .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.row_bg_right_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right);


        adapter1.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener(){
            @Override
            public boolean hasActions(int position, SwipeDirection direction){
                if(direction.isLeft()) return true; // Change this to false to disable left swipes
                if(direction.isRight()) return true;
                return false;
            }

            @Override
            public boolean shouldDismiss(int position, SwipeDirection direction){

                return direction == SwipeDirection.DIRECTION_FAR_LEFT || direction == SwipeDirection.DIRECTION_FAR_RIGHT;
            }

            @Override
            public void onSwipe(int[] positionList, SwipeDirection[] directionList){
                for(int i=0;i<positionList.length;i++) {
                    SwipeDirection direction = directionList[i];
                    final int position = positionList[i];

                    if (direction == SwipeDirection.DIRECTION_FAR_LEFT) {
                        mDatabaseHelper.deleteTask(tasks.get(position).getId());
                        tasks.remove(position);
                        if (position < s) s--;

                    }

                    if (direction == SwipeDirection.DIRECTION_FAR_RIGHT) {
                        if (SHOW_COMPLETED) {
                            if (position >= s) {
                                mDatabaseHelper.returnTask(tasks.get(position));
                                onStart();
                            }
                            else {
                                mDatabaseHelper.completeTask(tasks.get(position));
                                tasks.remove(position);
                                onStart();
                            }
                        }
                        else {
                            mDatabaseHelper.completeTask(tasks.get(position));
                            tasks.remove(position);
                            s--;
                        }
                    }
                    adapter1.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


//    private void toastMessage(String message){
//        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int myValue = this.getArguments().getInt("id");
        boolean myValue2 = this.getArguments().getBoolean("show_completed");
        int sort = this.getArguments().getInt("sort");
        SHOW_COMPLETED = myValue2;
        selectedID = myValue;
        SORTLIST = sort;
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
