package com.example.workmanagerdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final String MESSAGE_STATUS = "message_status";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Data data=new Data.Builder().putString(MESSAGE_STATUS,"the work data is sending").build();

        Constraints constraints=new Constraints.Builder().setRequiresCharging(true).build();

        PeriodicWorkRequest request=new PeriodicWorkRequest.Builder(MyWorker.class,15, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .addTag("First work")
                .setInputData(data)
                .build();

        /*OneTimeWorkRequest request=new OneTimeWorkRequest.Builder(MyWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();*/
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkManager workManager = WorkManager.getInstance(getApplicationContext());
                workManager.enqueueUniquePeriodicWork("First work", ExistingPeriodicWorkPolicy.REPLACE,request);
            }
        });
        final TextView textView=findViewById(R.id.textView);
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.getWorkInfoByIdLiveData(request.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {

                if(workInfo!=null)
                {
                    if(workInfo.getState().isFinished())
                    {
                        Data data=workInfo.getOutputData();
                        String output =data.getString(MyWorker.WORK_RESULT);
                        textView.append(output+"\n");
                    }
                }
                String status =workInfo.getState().name();
                textView.append(status +"\n");
            }
        });


    }



}