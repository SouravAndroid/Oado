package com.oado.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.oado.R;
import com.oado.fragments.DraftMessages;
import com.oado.fragments.FavouriteMessages;
import com.oado.fragments.InboxMessages;
import com.oado.fragments.SentMessages;
import com.oado.fragments.TrashMessages;

import java.util.ArrayList;
import java.util.List;

public class MessagesScreen extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab_sent_message;



    private int[] tabIcons = {
            R.mipmap.inbox_white,
            R.mipmap.sent_white
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_screen);
        initViews();


    }

    private void initViews(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        fab_sent_message = (FloatingActionButton) findViewById(R.id.fab_sent_message);
        fab_sent_message.setVisibility(View.GONE);

        setupTabIcons();


        fab_sent_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_compose = new Intent(MessagesScreen.this, ComposeMessage.class);
                startActivity(intent_compose);

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
       // tabLayout.getTabAt(2).setIcon(tabIcons[2]);
       // tabLayout.getTabAt(3).setIcon(tabIcons[3]);
       // tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    private void setupTabIcons1() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_single_tab, null);
        tabOne.setText("INBOX");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[0], 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_single_tab, null);
        tabTwo.setText("SENT");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[1], 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_single_tab, null);
        tabThree.setText("DRAFT");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[2], 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_single_tab, null);
        tabFour.setText("STARRED");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[3], 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new InboxMessages(), "INBOX");
        adapter.addFragment(new SentMessages(), "SENT");
       // attendanceListAdapter.addFragment(new DraftMessages(), "DRAFT");
      //  attendanceListAdapter.addFragment(new FavouriteMessages(), "FAVOURITE");
      //  attendanceListAdapter.addFragment(new TrashMessages(), "TRASH");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
