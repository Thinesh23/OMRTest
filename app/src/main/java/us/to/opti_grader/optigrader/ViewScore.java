package us.to.opti_grader.optigrader;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Interface.ItemClickListener;
import us.to.opti_grader.optigrader.Model.ExamList;
import us.to.opti_grader.optigrader.Model.StudentScore;
import us.to.opti_grader.optigrader.ViewHolder.ExamListViewHolder;
import us.to.opti_grader.optigrader.ViewHolder.LeaderBoardViewHolder;

public class ViewScore extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference examList;

    SwipeRefreshLayout mSwipeRefreshLayout;

    FirebaseRecyclerAdapter<StudentScore,LeaderBoardViewHolder> adapter;

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

        setContentView(R.layout.activity_view_score);

        database = FirebaseDatabase.getInstance();
        examList = database.getReference("Subject").child(Common.currentSubject.getSbid())
                .child(Common.currentUser.getPhone()).child("examType").child(Common.currentExam.getExamType()).child("StudentScore");

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
                    Query query = examList.orderByChild("id").equalTo(true);

                    FirebaseRecyclerOptions<StudentScore> options = new FirebaseRecyclerOptions.Builder<StudentScore>()
                            .setQuery(query,StudentScore.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<StudentScore,LeaderBoardViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position, @NonNull StudentScore model) {
                            holder.id.setText(model.getId());
                            holder.score.setText(model.getScore());

                        }

                        @Override
                        public LeaderBoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_leaderboard,parent,false);
                            return new LeaderBoardViewHolder(view);
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
                    Query query = examList.orderByChild("id").equalTo(true);

                    FirebaseRecyclerOptions<StudentScore> options = new FirebaseRecyclerOptions.Builder<StudentScore>()
                            .setQuery(query,StudentScore.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<StudentScore,LeaderBoardViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position, @NonNull StudentScore model) {
                            holder.id.setText(model.getId());
                            holder.score.setText(model.getScore());

                        }

                        @Override
                        public LeaderBoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_leaderboard,parent,false);
                            return new LeaderBoardViewHolder(view);
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
