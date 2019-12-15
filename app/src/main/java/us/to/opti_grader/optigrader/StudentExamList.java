package us.to.opti_grader.optigrader;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Interface.ItemClickListener;
import us.to.opti_grader.optigrader.Model.ExamScore;
import us.to.opti_grader.optigrader.Model.SubjectScore;
import us.to.opti_grader.optigrader.ViewHolder.ExamScoreViewHolder;
import us.to.opti_grader.optigrader.ViewHolder.SubjectScoreViewHolder;

public class StudentExamList extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<ExamScore,ExamScoreViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference scores;

    SwipeRefreshLayout mSwipeRefreshLayout;

    int sum = 0;
    String subjectName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_exam_list);

        database = FirebaseDatabase.getInstance();
        scores = database.getReference("Score");

        recyclerView = (RecyclerView)findViewById(R.id.studentExamList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        subjectName = getIntent().getStringExtra("subjectName");
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(Common.currentUser !=null && subjectName != null && !subjectName.isEmpty()){
                    loadProfile();
                }
            }
        });

        //Thread to load comment on first launch
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                if(Common.currentUser !=null && subjectName != null && !subjectName.isEmpty()){
                    loadProfile();
                }
            }
        });

    }

    private void loadProfile(){
        Query getOrderByUser = scores.child(Common.currentUser.getPhone()).child(subjectName).child("examType").orderByKey();
        FirebaseRecyclerOptions<ExamScore> orderOptions = new FirebaseRecyclerOptions.Builder<ExamScore>()
                .setQuery(getOrderByUser,ExamScore.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ExamScore,ExamScoreViewHolder>(orderOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ExamScoreViewHolder viewHolder, final int position, @NonNull final ExamScore model) {
                viewHolder.totalQues.setText(model.getTotalQues());
                viewHolder.examType.setText(model.getExamType());
                viewHolder.score.setText(model.getScore());
            }

            @NonNull
            @Override
            public ExamScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.show_student_exam_list,parent,false);
                return new ExamScoreViewHolder(itemView);
            }
        };

        loadComment();
    }

    private void loadComment(){
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
