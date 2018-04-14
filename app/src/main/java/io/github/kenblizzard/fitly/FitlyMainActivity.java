package io.github.kenblizzard.fitly;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class FitlyMainActivity extends AppCompatActivity implements RoutineListFragment.OnRoutineLisFragmentInteractionListener {

    private TimerFragment timerFragment;
    private RoutineListFragment routineListFragment;
    private TabHost tabHost;

    private AdView mAdView;

    public static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataHandler.createRoutines(getCacheDir());
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


        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int error) {
                mAdView.setVisibility(View.GONE);
            }

        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mAdView.loadAd(adRequest);
            }
        }, 3000);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Fitly");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "On create");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
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
                DataHandler.commit();
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

                Log.d("kenneth", rtn.getLabel());
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

                                        int x, y, z;
                                        String label = tvLabel.getText().length() > 0 ? tvLabel.getText() + "" : "Untitled";
                                        x = tvWork.getText().length() > 0 ? Integer.parseInt(tvWork.getText() + "") : 0;
                                        y = tvReps.getText().length() > 0 ? Integer.parseInt(tvReps.getText() + "") : 0;
                                        z = tvRest.getText().length() > 0 ? Integer.parseInt(tvRest.getText() + "") : 0;

                                        Routine toEdit = new Routine();
                                        toEdit.createRoutine(label, "",
                                                x,
                                                y,
                                                z
                                        );

                                        DataHandler.editRoutine(toEdit, position);
                                        routineListFragment.reloadList();
                                        Toast.makeText(getApplicationContext(), "Updated: " + toEdit.getLabel(), Toast.LENGTH_SHORT).show();
                                        DataHandler.commit();
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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

                                int x, y, z;
                                String label = tvLabel.getText().length() > 0 ? tvLabel.getText() + "" : "Untitled";
                                x = tvWork.getText().length() > 0 ? Integer.parseInt(tvWork.getText() + "") : 0;
                                y = tvReps.getText().length() > 0 ? Integer.parseInt(tvReps.getText() + "") : 0;
                                z = tvRest.getText().length() > 0 ? Integer.parseInt(tvRest.getText() + "") : 0;

                                Routine rtn = new Routine();
                                rtn.createRoutine(label, "",
                                        x,
                                        y,
                                        z
                                );

                                DataHandler.addRoutine(rtn);
                                routineListFragment.reloadList();
                                DataHandler.commit();

                                Toast.makeText(getApplicationContext(), "Added: " + rtn.getLabel(), Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();

    }
}
