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

public class InboxFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InboxFragment() {

    }

    public static InboxFragment newInstance(String param1, String param2) {
        InboxFragment fragment = new InboxFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    DatabaseHelper mDatabaseHelper;
    private ListView mListView;
    MyAdapter adapter;

    @Override
    public void onStart(){
        super.onStart();

        mDatabaseHelper = mDatabaseHelper.getInstance(getActivity());
        mListView = (ListView) getActivity().findViewById(R.id.listView);

        final ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(mDatabaseHelper.getActiveTasksFromInbox());

        adapter = new MyAdapter(getActivity(),tasks);
        mListView.setAdapter(adapter);



        final SwipeActionAdapter adapter1 = new SwipeActionAdapter(adapter);
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
                    int position = positionList[i];
                    String dir = "";

                    if (direction == SwipeDirection.DIRECTION_FAR_LEFT) {
                        mDatabaseHelper.deleteTask(tasks.get(position).getId());
                        tasks.remove(position);
                    }

                    if (direction == SwipeDirection.DIRECTION_FAR_RIGHT) {
                        mDatabaseHelper.completeTask(tasks.get(position));
                        tasks.remove(position);
                    }

                    adapter1.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }


    private void toastMessage(String message){
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
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
