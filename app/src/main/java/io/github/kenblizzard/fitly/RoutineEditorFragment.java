package io.github.kenblizzard.fitly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Ken on 3/5/2017.
 */

public class RoutineEditorFragment extends Fragment {

    private View view;

    private EditText editLabel;
    private EditText editWork;
    private EditText editRest;
    private EditText editSet;


    public static RoutineEditorFragment newInstance() {
        RoutineEditorFragment fragment = new RoutineEditorFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_routine, container, false);

//        Button btnSaveRoutine = (Button) view.findViewById(R.id.btnSaveRoutine);
        editLabel = (EditText) view.findViewById(R.id.editLabel);
        editWork = (EditText) view.findViewById(R.id.editWork);
        editRest = (EditText) view.findViewById(R.id.editRest);
        editSet = (EditText) view.findViewById(R.id.editReps);


//        btnSaveRoutine.setOnClickListener(onClickSaveRoutine);
        return view;
    }

    private View.OnClickListener onClickSaveRoutine = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Routine rtn = new Routine();

            rtn.createRoutine(editLabel.getText().toString(), editLabel.getText().toString(),
                    Integer.parseInt(editWork.getText().toString()),
                    Integer.parseInt(editRest.getText().toString()),
                    Integer.parseInt(editSet.getText().toString())
                    );

            Log.d("Kenneth before add: ",DataHandler.getListRoutineSize() + "");
            DataHandler.addRoutine(rtn);
            Log.d("Kenneth after add: ",DataHandler.getListRoutineSize() + "");

//            DataHandler.commit();
//            Toast.makeText(getContext(),"Save",Toast.LENGTH_SHORT).show();
        }
    };
}
