package us.to.opti_grader.optigrader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;

import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Interface.ItemClickListener;
import us.to.opti_grader.optigrader.Model.ExamList;
import us.to.opti_grader.optigrader.ViewHolder.ExamListViewHolder;

public class LeaderboardExamList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference examList;

    SwipeRefreshLayout mSwipeRefreshLayout;

    FirebaseRecyclerAdapter<ExamList,ExamListViewHolder> adapter;

    String subjectId="";
    String examTyperesult="";
    String subjectName="";
    String spinnerresult = "";
    int calcAnswer=0;
    int compAnswer=0;

    FloatingActionButton fab;


    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaderboard_exam_list);

        database = FirebaseDatabase.getInstance();
        examList = database.getReference("Subject").child(Common.currentSubject.getSbid()).child(Common.currentUser.getPhone()).child("examType");

        fab = (FloatingActionButton)findViewById(R.id.fab);

        if (getIntent() != null){
            subjectName = getIntent().getStringExtra(Common.INTENT_SUBJECT_NAME);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout2);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent()!=null){
                    subjectId = getIntent().getStringExtra(Common.INTENT_SUBJECT_ID);
                }
                if(!subjectId.isEmpty() && subjectId !=null){
                    //examList.child(Common.currentUser.getPhone()).child(examTyperesult).setValue(el)
                    Query query = examList.orderByChild("subjectId").equalTo(Common.currentSubject.getSbid());

                    FirebaseRecyclerOptions<ExamList> options = new FirebaseRecyclerOptions.Builder<ExamList>()
                            .setQuery(query,ExamList.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<ExamList,ExamListViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ExamListViewHolder holder, int position, @NonNull ExamList model) {
                            holder.txtSubjectName.setText(model.getSubjectName());
                            holder.txtExamType.setText(model.getExamType());
                            holder.txtStudentNo.setText(model.getStudentNo());
                            holder.txtTotalQues.setText(model.getTotalQues());

                            ExamList local = model;
                            Common.currentExam = local;
                            holder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position, boolean isLongClick) {
                                    Intent scan = new Intent(LeaderboardExamList.this, ViewScore.class);
                                    scan.putExtra("type", adapter.getRef(position).getKey());
                                    startActivity(scan);
                                }
                            });

                        }

                        @Override
                        public ExamListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_subject_test_layout,parent,false);
                            return new ExamListViewHolder(view);
                        }
                    };

                    loadExamList(subjectId);
                }
            }
        });

        //Thread to load comment on first launch
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                if(getIntent()!=null){
                    subjectId = getIntent().getStringExtra(Common.INTENT_SUBJECT_ID);
                }
                if(!subjectId.isEmpty() && subjectId !=null){
                    Query query = examList.orderByChild("subjectId").equalTo(Common.currentSubject.getSbid());
                    FirebaseRecyclerOptions<ExamList> options = new FirebaseRecyclerOptions.Builder<ExamList>()
                            .setQuery(query,ExamList.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<ExamList,ExamListViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ExamListViewHolder holder, int position, @NonNull ExamList model) {
                            holder.txtSubjectName.setText(model.getSubjectName());
                            holder.txtExamType.setText(model.getExamType());
                            holder.txtStudentNo.setText(model.getStudentNo());
                            holder.txtTotalQues.setText(model.getTotalQues());

                            ExamList local = model;
                            Common.currentExam = local;
                            holder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position, boolean isLongClick) {
                                    Intent scan = new Intent(LeaderboardExamList.this, ViewScore.class);
                                    scan.putExtra("type", adapter.getRef(position).getKey());
                                    startActivity(scan);
                                }
                            });

                        }

                        @Override
                        public ExamListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_subject_test_layout,parent,false);
                            return new ExamListViewHolder(view);
                        }
                    };

                    loadExamList(subjectId);
                }
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recyclerSubject);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void loadExamList(String subjectId){
        adapter.startListening();

        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
