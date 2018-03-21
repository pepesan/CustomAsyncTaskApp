package com.cursosdedesarrollo.customasynctaskapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static class DoBackgroundTask extends CustomAsyncTask<Void, Integer, Void> {
        private static final String TAG = "DoBackgroundTask";

        private ProgressDialog mProgress;
        private int mCurrProgress;

        public DoBackgroundTask(MainActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected void onActivityDetached() {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }

        @Override
        protected void onActivityAttached() {
            showProgressDialog();
        }

        private void showProgressDialog() {
            mProgress = new ProgressDialog(mActivity);
            mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgress.setMessage("Doing stuff...");
            mProgress.setCancelable(true);
            mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });

            mProgress.show();
            mProgress.setProgress(mCurrProgress);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 0; i < 100; i+=10) {
                    Thread.sleep(1000);
                    this.publishProgress(i);
                }

            }
            catch (InterruptedException e) {
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            mCurrProgress = progress[0];
            if (mActivity != null) {
                mProgress.setProgress(mCurrProgress);
            }
            else {
                Log.d(TAG, "Progress updated while no Activity was attached.");
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (mActivity != null) {
                mProgress.dismiss();
                Toast.makeText(mActivity, "AsyncTask finished", Toast.LENGTH_LONG).show();
            }
            else {
                Log.d(TAG, "AsyncTask finished while no Activity was attached.");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ((CustomApplication) getApplication()).detach(this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ((CustomApplication) getApplication()).attach(this);
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
        if (id == R.id.action_launch_asynctask) {
            new MainActivity.DoBackgroundTask(MainActivity.this).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
