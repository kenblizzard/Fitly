package io.github.kenblizzard.fitly;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRoutineLisFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoutineListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutineListFragment extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnRoutineLisFragmentInteractionListener mListener;
    private RoutineListAdapter routineListAdapter;

    private View view;

    public RoutineListFragment() {
    }

    public static RoutineListFragment newInstance(String param1, String param2) {
        RoutineListFragment fragment = new RoutineListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_routine_list, container, false);

        routineListAdapter = new RoutineListAdapter(getContext(), DataHandler.listRoutines, mListener);
        setListAdapter(routineListAdapter);


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        super.onActivityCreated(state);


        if (state != null) {
            int position = state.getInt("STATE_CHECKED", -1);
            if (position > -1) {
                getListView().setItemChecked(position, true);
            }
        }

    }

    public void reloadList() {
        routineListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRoutineLisFragmentInteractionListener) {
            mListener = (OnRoutineLisFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRoutineLisFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnRoutineLisFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRoutineListItemClick(Routine rtn);

        void onRoutineListItemLongPressed(Routine rtn, int position);

    }

    public class RoutineListAdapter extends ArrayAdapter<Routine> {
        private ArrayList<Routine> listRoutine;
        private Context context;
        private OnRoutineLisFragmentInteractionListener listener;

        public RoutineListAdapter(Context context, ArrayList<Routine> listRoutine, OnRoutineLisFragmentInteractionListener listener) {
            super(getActivity(), R.layout.fragment_item, listRoutine);
            this.listRoutine = listRoutine;
            this.context = context;
            this.listener = listener;
        }


        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final RoutineItemViewHolder routineItemViewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_item, parent, false);
                routineItemViewHolder = new RoutineItemViewHolder(convertView);
                convertView.setTag(routineItemViewHolder);
            } else {
                routineItemViewHolder = (RoutineItemViewHolder) convertView.getTag();
            }

            if (this.listRoutine.get(position).getLabel() == "$Add new routine") {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_button_add_routine, parent, false);
            } else {
                routineItemViewHolder.populateView(this.listRoutine.get(position));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRoutineListItemClick(listRoutine.get(position));
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onRoutineListItemLongPressed(listRoutine.get(position), position);
                    return true;
                }
            });


            return convertView;
        }
    }

    public class RoutineItemViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mContentRest;
        public final TextView mContentReps;

        public RoutineItemViewHolder(View view) {

            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.contentWork);
            mContentRest = (TextView) view.findViewById(R.id.contentRest);
            mContentReps = (TextView) view.findViewById(R.id.contentReps);


        }

        public void populateView(Routine mItem) {
            mIdView.setText(mItem.getLabel());
            mContentView.setText(mItem.duration + "s");
            mContentRest.setText(mItem.rest + "s");
            mContentReps.setText(mItem.reps + "X");
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
