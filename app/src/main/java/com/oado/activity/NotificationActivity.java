package com.oado.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.oado.R;
import com.oado.adapters.NotificationsListAdapter;
import com.oado.database.DatabaseHelper;
import com.oado.database.MsgData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_notifications);
        ButterKnife.bind(this);
        initViews();



    }

    public void initViews(){

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());


        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        ArrayList<MsgData> msgDataArrayList = databaseHelper.getAllMsg();


        NotificationsListAdapter adapter = new NotificationsListAdapter(NotificationActivity.this,
                msgDataArrayList);
        recycler_view.setAdapter(adapter);

        if (msgDataArrayList.size() == 0){
            Toasty.info(NotificationActivity.this,
                    "No notification here",
                    Toast.LENGTH_SHORT, true).show();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                Intent intent = new Intent(NotificationActivity.this, Splash.class);
                startActivity(intent);
                finish();
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(NotificationActivity.this, Splash.class);
        startActivity(intent);
        finish();

    }
}
