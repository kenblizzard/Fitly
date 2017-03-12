package io.github.kenblizzard.fitly;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Ken on 3/4/2017.
 */

public class TimerFragment extends Fragment {

    private Button btnStart;
    private Chronometer chrono;
    private MediaPlayer mp;
    private TextView tvRest;
    private LinearLayout layoutMain;
    private ViewPager mViewPager;
    private View view;
    private TextView tv;
    private TextView timerWork;
    private TextView timerRest;
    private TextView timerReps;

    private boolean isStart = true;
    private Routine routine;
    private TimeSet ts;


    private static final String ARG_LABEL = "column-label";
    private static final String ARG_ACTION = "column-action";


    private int index = 0;


    public static TimerFragment newInstance(String label, String action) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LABEL, label);
        args.putString(ARG_ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);


        String arg_action = getArguments().getString("column-action");
        String arg_label = getArguments().getString("column-label");


        switch (arg_action) {
            case "Start":
                routine = DataHandler.getRoutine(arg_label);
                break;
            default:
                if (DataHandler.getListRoutineSize() > 0) {
                    routine = DataHandler.getRoutine(0);
                } else {
                    return view;
                }

        }

        tv = (TextView) view.findViewById(R.id.tvLabel);
        tvRest = (TextView) view.findViewById(R.id.tvTimerStatusMessage);
        timerReps = (TextView) view.findViewById(R.id.timerReps);
        timerWork = (TextView) view.findViewById(R.id.timerWork);
        timerRest = (TextView) view.findViewById(R.id.timerRest);
        layoutMain = (LinearLayout) view.findViewById(R.id.layoutMain);


        btnStart = (Button) view.findViewById(R.id.button);
        chrono = (Chronometer) view.findViewById(R.id.chronometer2);
        mp = MediaPlayer.create(getContext(), R.raw.beep);

        btnStart.setOnClickListener(clickStart);

        chrono.setOnChronometerTickListener(onTimerTick);

        this.populateTimer(routine);

        return view;
    }

    public void populateTimer(Routine rtn) {
        this.routine = rtn;

        tv.setText(this.routine.getLabel());
        timerReps.setText(" " + this.routine.reps + "");
        timerWork.setText(" " + this.routine.duration + "s");
        timerRest.setText(" " + this.routine.rest + "s");
        stopTimer();

    }

    private void stopTimer() {
        isStart = true;
        index = 0;
        chrono.stop();
        chrono.setBase(SystemClock.elapsedRealtime());
        btnStart.setText(R.string.btn_start);
        tvRest.setText("Press 'START!' when you are ready");
        layoutMain.setBackgroundColor(0);
    }

    private Chronometer.OnChronometerTickListener onTimerTick = new Chronometer.OnChronometerTickListener() {

        @Override
        public void onChronometerTick(Chronometer chronometer) {
            long elapsedMillis = SystemClock.elapsedRealtime() - chrono.getBase();

            int hours = (int) (elapsedMillis / 3600000);
            int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
            int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;

            int totalElapsedSeconds = (hours * 60 * 60)
                    + (minutes * 60)
                    + seconds;

            // routine.getListTimeSet();
            ts = routine.getListTimeSet().get(index);


            if (((totalElapsedSeconds == ts.getDuration() + 1 && ts.getIsRest()) ||
                    (totalElapsedSeconds == ts.getRest() + 1 && !ts.getIsRest()))
                    && !isStart) {
                mp.start();

                chrono.stop();

                chrono.setBase(SystemClock.elapsedRealtime());
                chrono.start();

                index++;
            }

            if (ts.getIsRest()) {
                tvRest.setText(R.string.txt_work);
                layoutMain.setBackgroundColor(getResources().getColor(R.color.colorWork));

            } else {
                tvRest.setText(R.string.txt_rest);
                layoutMain.setBackgroundColor(getResources().getColor(R.color.colorRest));
            }

            if (index == routine.getListTimeSet().size() - 1) {
                stopTimer();

            }
        }
    };

    private View.OnClickListener clickStart = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isStart) {
                btnStart.setText("STOP!");
                chrono.setBase(SystemClock.elapsedRealtime());
                chrono.start();

            } else {
                btnStart.setText("START!");
                stopTimer();
            }
            isStart = !isStart;

        }

    };

}
