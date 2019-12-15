package us.to.opti_grader.optigrader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Fragments.ChatFragment;
import us.to.opti_grader.optigrader.Fragments.UsersFragment;
import us.to.opti_grader.optigrader.Model.Chat;

public class ChattingMenu extends AppCompatActivity {


    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_menu);

        username = (TextView) findViewById(R.id.txtUserName);

        username.setText(Common.currentUser.getName());

        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);

        DatabaseReference table_chat = FirebaseDatabase.getInstance().getReference("Chats");
        table_chat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(Common.currentUser.getPhone()) && !chat.isSeen()){
                        unread++;
                    }
                }

                if(unread == 0){
                    viewPagerAdapter.addFragment(new ChatFragment(), "Chats");
                } else {
                    viewPagerAdapter.addFragment(new ChatFragment(), "("+unread+")Chats");
                }

                //viewPagerAdapter.addFragment(new UsersFragment(), "Users");

                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList <Fragment> fragments;
        private ArrayList <String>  titles;

        ViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position){
            return fragments.get(position);
        }

        @Override
        public int getCount(){
            return fragments.size();
        }

        public void addFragment (Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}