package us.to.opti_grader.optigrader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Interface.ItemClickListener;
import us.to.opti_grader.optigrader.Model.Subject;
import us.to.opti_grader.optigrader.R;
import us.to.opti_grader.optigrader.ViewHolder.SubjectViewHolder;

public class Leaderboard extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference subjectList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String newSubjectId = "";

    MaterialEditText edtName;

    String subjectId = "";
    FirebaseRecyclerAdapter<Subject,SubjectViewHolder> adapter;
    FirebaseRecyclerAdapter<Subject,SubjectViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    SwipeRefreshLayout swipeRefreshLayout;
    Subject newSubject;
    Button btnUpload,btnSelect;

    Uri saveUri;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Note: add this code before setContentView method
/*        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/KGSkinnyLatte.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());*/
        setContentView(R.layout.activity_leaderboard);
        //Firebase
        database = FirebaseDatabase.getInstance();
        subjectList = database.getReference("Subject");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent() != null){
                    subjectId = getIntent().getStringExtra("SubjectId");
                }
                if (!subjectId.isEmpty() && subjectId != null){
                    if(Common.isConnectedToInternet(getBaseContext())){
                        loadListSubject(subjectId);
                    } else {
                        Toast.makeText(Leaderboard.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(getIntent() != null){
                    subjectId = getIntent().getStringExtra("SubjectId");
                }
                if (!subjectId.isEmpty() && subjectId != null){
                    if(Common.isConnectedToInternet(getBaseContext())){
                        loadListSubject(subjectId);
                    } else {
                        Toast.makeText(Leaderboard.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                //Search
                materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
                materialSearchBar.setHint("Enter your subject");
                loadSuggest();
                materialSearchBar.setCardViewElevation(10);
                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List<String> suggest = new ArrayList<String>();
                        for (String search:suggestList) {
                            if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggest.add(search);
                        }
                        materialSearchBar.setLastSuggestions(suggest);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        if (!enabled)
                            recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_subject);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    private void startSearch(CharSequence text) {
        //create query by name
        Query searchByName = subjectList.orderByChild("name").equalTo(text.toString());
        //Create options with query
        FirebaseRecyclerOptions<Subject> companyOptions = new FirebaseRecyclerOptions.Builder<Subject>()
                .setQuery(searchByName,Subject.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Subject, SubjectViewHolder>(companyOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final SubjectViewHolder viewHolder, final int position, @NonNull final Subject model) {
                viewHolder.subject_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.subject_image);

                newSubjectId = searchAdapter.getRef(position).getKey();

                final Subject local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent testscan = new Intent(Leaderboard.this, TestActivity.class);
                        testscan.putExtra("subjectId", searchAdapter.getRef(position).getKey());
                        Common.currentSubject = local;
                        startActivity(testscan);
                    }
                });
            }

            @NonNull
            @Override
            public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.subject_item,parent,false);
                return new SubjectViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest(){
        subjectList.orderByChild("menuId").equalTo(subjectId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Subject item = postSnapshot.getValue(Subject.class);
                    suggestList.add(item.getName());
                }
                materialSearchBar.setLastSuggestions(suggestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListSubject(String subjectId){

        //Create query by category Id
        Query searchByName = subjectList.orderByChild("menuId").equalTo(subjectId);
        //Create options with query
        FirebaseRecyclerOptions<Subject> companyOptions = new FirebaseRecyclerOptions.Builder<Subject>()
                .setQuery(searchByName,Subject.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Subject, SubjectViewHolder>(companyOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final SubjectViewHolder viewHolder, final int position, @NonNull final Subject model) {

                viewHolder.subject_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.subject_image);

                final Subject local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent testscan = new Intent(Leaderboard.this, TestActivity.class);
                        testscan.putExtra("subjectId", adapter.getRef(position).getKey());
                        Common.currentSubject = local;
                        startActivity(testscan);
                    }
                });
            }

            @NonNull
            @Override
            public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.subject_item,parent,false);
                return new SubjectViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if(searchAdapter != null) {
            searchAdapter.stopListening();
        }
    }
}
