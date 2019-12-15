package us.to.opti_grader.optigrader.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import us.to.opti_grader.optigrader.Adapter.UserAdapter;
import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Model.User;
import us.to.opti_grader.optigrader.R;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> users;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        users = new ArrayList<>();

        loadUsers();

        return view;
    }

    private void loadUsers(){

        DatabaseReference table_user = FirebaseDatabase.getInstance().getReference("User");

        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;

                    if(!user.getPhone().equals(Common.currentUser.getPhone())){
                        users.add(user);
                    }
                }
                userAdapter = new UserAdapter(getContext(), users,false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
