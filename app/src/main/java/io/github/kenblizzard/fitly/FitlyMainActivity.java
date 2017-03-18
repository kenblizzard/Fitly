package io.github.kenblizzard.fitly;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class FitlyMainActivity extends AppCompatActivity implements RoutineListFragment.OnRoutineLisFragmentInteractionListener {

    private TimerFragment timerFragment;
    private RoutineListFragment routineListFragment;
    private TabHost tabHost;

    private AdView mAdView;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataHandler.createRoutines();
        setContentView(R.layout.activity_fitly_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tabHost = (TabHost) findViewById(R.id.tabhost);

        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.ic_playlist_play_black_24dp));

        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.ic_alarm_black_24dp));

        tabHost.addTab(spec);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        routineListFragment = RoutineListFragment.newInstance("asdas", "asdas");
        fragmentTransaction.replace(R.id.listRoutines, routineListFragment);

        timerFragment = TimerFragment.newInstance("", "");
        fragmentTransaction.replace(R.id.tab2, timerFragment);
        fragmentTransaction.commit();


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Fitly");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "On create");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Fitly");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "On create");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


    @Override
    public void onRoutineListItemClick(Routine rtn) {
        timerFragment.populateTimer(rtn);
        tabHost.setCurrentTab(1);
    }

    @Override
    public void onRoutineListItemLongPressed(final Routine rtn, final int position) {
        final View view = getLayoutInflater().inflate(R.layout.menu_longclick_item, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("Select action for: \n" + rtn.getLabel()).setView(view).show();

        Button btnRemoveItem = (Button) view.findViewById(R.id.btnRemoveItem);
        Button btnEditItem = (Button) view.findViewById(R.id.btnEditItem);

        btnRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String removedRoutine = DataHandler.removeRoutine(position);
                routineListFragment.reloadList();
                alertDialog.cancel();
                Toast.makeText(getApplicationContext(), "Successfully removed " + removedRoutine, Toast.LENGTH_LONG).show();
            }
        });


        btnEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View editView = getLayoutInflater().inflate(R.layout.fragment_add_routine, null);

                final EditText tvLabel = (EditText) editView.findViewById(R.id.editLabel);
                final EditText tvWork = (EditText) editView.findViewById(R.id.editWork);
                final EditText tvRest = (EditText) editView.findViewById(R.id.editRest);
                final EditText tvReps = (EditText) editView.findViewById(R.id.editReps);

                Log.d("kenneth",rtn.getLabel());
                tvLabel.setText(rtn.getLabel());
                tvWork.setText(rtn.duration + "");
                tvRest.setText(rtn.rest + "");
                tvReps.setText(rtn.reps + "");


                new AlertDialog.Builder(alertDialog.getContext())
                        .setTitle("Edit routine")
                        .setView(editView)
                        .setPositiveButton("Update",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {


                                        Routine toEdit = new Routine();
                                        toEdit.createRoutine(tvLabel.getText() + "", "",
                                                Integer.parseInt(tvWork.getText() + ""),
                                                Integer.parseInt(tvReps.getText() + ""),
                                                Integer.parseInt(tvRest.getText() + "")
                                        );

                                        DataHandler.editRoutine(toEdit, position);
                                        routineListFragment.reloadList();
                                        Toast.makeText(getApplicationContext(), "Updated: " + toEdit.getLabel(), Toast.LENGTH_SHORT).show();
                                        alertDialog.cancel();
                                    }
                                })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add_routine) {
            this.add();
        }

        return super.onOptionsItemSelected(item);
    }

    private void add() {
        final View addView = getLayoutInflater().inflate(R.layout.fragment_add_routine, null);


        new AlertDialog.Builder(this)
                .setTitle("New routine")
                .setView(addView)
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                                final EditText tvLabel = (EditText) addView.findViewById(R.id.editLabel);
                                final EditText tvWork = (EditText) addView.findViewById(R.id.editWork);
                                final EditText tvRest = (EditText) addView.findViewById(R.id.editRest);
                                final EditText tvReps = (EditText) addView.findViewById(R.id.editReps);

                                Routine rtn = new Routine();
                                rtn.createRoutine(tvLabel.getText() + "", "",
                                        Integer.parseInt(tvWork.getText() + ""),
                                        Integer.parseInt(tvReps.getText() + ""),
                                        Integer.parseInt(tvRest.getText() + "")
                                );

                                DataHandler.addRoutine(rtn);
                                routineListFragment.reloadList();
                                Toast.makeText(getApplicationContext(), "Added: " + rtn.getLabel(), Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();

    }
}
