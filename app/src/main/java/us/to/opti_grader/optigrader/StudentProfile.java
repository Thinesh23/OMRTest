package us.to.opti_grader.optigrader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Interface.ItemClickListener;
import us.to.opti_grader.optigrader.Model.ExamScore;
import us.to.opti_grader.optigrader.Model.Subject;
import us.to.opti_grader.optigrader.Model.SubjectScore;
import us.to.opti_grader.optigrader.ViewHolder.SubjectScoreViewHolder;

public class StudentProfile extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<SubjectScore,SubjectScoreViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference scores;

    SwipeRefreshLayout mSwipeRefreshLayout;

    int sum = 0;
    String examtype[] = {"First-Term", "Mid-Term", "Second-Term", "Final-Term"};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        database = FirebaseDatabase.getInstance();
        scores = database.getReference("Score");

        recyclerView = (RecyclerView)findViewById(R.id.studentProfile);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(Common.currentUser !=null){
                    loadProfile();
                }
            }
        });

        //Thread to load comment on first launch
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                if(Common.currentUser !=null){
                    loadProfile();
                }
            }
        });

    }

    private void loadProfile(){
        Query getOrderByUser = scores.child(Common.currentUser.getPhone()).orderByChild("studentId").equalTo(Common.currentUser.getPhone());
        FirebaseRecyclerOptions<SubjectScore> orderOptions = new FirebaseRecyclerOptions.Builder<SubjectScore>()
                .setQuery(getOrderByUser,SubjectScore.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<SubjectScore,SubjectScoreViewHolder>(orderOptions) {
            @Override
            protected void onBindViewHolder(@NonNull SubjectScoreViewHolder viewHolder, final int position, @NonNull final SubjectScore model) {
                viewHolder.subjectName.setText(model.getSubjectName());
                viewHolder.averageScore.setText(model.getAverageScore());

                final SubjectScore local = model;
                updateAverageScore(local);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent examlist = new Intent(StudentProfile.this, StudentExamList.class);
                        examlist.putExtra("subjectName", adapter.getRef(position).getKey());
                        Common.currentSubjectScore = local;
                        startActivity(examlist);
                    }
                });
            }

            @NonNull
            @Override
            public SubjectScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.show_student_layout,parent,false);
                return new SubjectScoreViewHolder(itemView);
            }
        };

        loadComment();
    }

    private void updateAverageScore (SubjectScore model){

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
