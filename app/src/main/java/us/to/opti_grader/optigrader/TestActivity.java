package us.to.opti_grader.optigrader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import us.to.opti_grader.optigrader.Model.Subject;
import us.to.opti_grader.optigrader.ViewHolder.ExamListViewHolder;

public class TestActivity extends AppCompatActivity{

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

        setContentView(R.layout.activity_test);

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

                            holder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position, boolean isLongClick) {
                                    //Intent scan = new Intent(TestActivity.this, CameraActivity.class);
                                    //startActivity(scan);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAddExamDialog();
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

                            holder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position, boolean isLongClick) {
                                    //Intent scan = new Intent(TestActivity.this, CameraActivity.class);
                                    //startActivity(scan);
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

    private void ShowAddExamDialog() {
        Intent addtest = new Intent(TestActivity.this, AddTestActivity.class);
        addtest.putExtra("subjectId", subjectId);
        startActivity(addtest);
 /*       AlertDialog.Builder alertDialog = new AlertDialog.Builder(TestActivity.this);
        alertDialog.setTitle("Add Exam");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_exam_layout, null);

        final TextView subjectName = (TextView) add_menu_layout.findViewById(R.id.SubjectName);
        final MaterialEditText studentNo = (MaterialEditText)add_menu_layout.findViewById(R.id.StudentNo);
        final MaterialEditText examYear = (MaterialEditText)add_menu_layout.findViewById(R.id.examYear);
        final MaterialEditText examtotalQuest = (MaterialEditText)add_menu_layout.findViewById(R.id.edtQuesNo);
        final MaterialEditText examAnswer = (MaterialEditText)add_menu_layout.findViewById(R.id.edtAnswer);
        final MaterialSpinner spinner = (MaterialSpinner) add_menu_layout.findViewById(R.id.exam_spinner);

        spinner.setItems(" ","First-Term", "Mid-Term", "Second-Term" ,"Final-Term");
        spinner.setSelectedIndex(0);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                examTyperesult = item.toString();
            }
        });
        //Set button
        subjectName.setText(Common.currentSubject.getName());
        alertDialog.setView(add_menu_layout);
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (subjectName.getText().toString().trim().length() != 0 &&
                        studentNo.getText().toString().trim().length() != 0 &&
                        examYear.getText().toString().trim().length() != 0 &&
                        examtotalQuest.getText().toString().trim().length() != 0 &&
                        examAnswer.getText().toString().trim().length() != 0) {


                    calcAnswer = examAnswer.getText().toString().trim().length();
                    compAnswer = Integer.parseInt(examtotalQuest.getText().toString());

                    if (compAnswer == calcAnswer){

                        examList.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(Common.currentUser.getPhone()).child(examTyperesult).exists()) {
                                    Toast.makeText(TestActivity.this, "Exam Type already exist", Toast.LENGTH_SHORT).show();
                                } else {
                                    //String examType, String studentNo, String subjectName, String subjectId, String examYear, String totalQues, String examAnswer
                                    ExamList el = new ExamList(
                                            examTyperesult,
                                            studentNo.getText().toString(),
                                            Common.currentSubject.getName(),
                                            subjectId,
                                            examYear.getText().toString(),
                                            examtotalQuest.getText().toString(),
                                            examAnswer.getText().toString()
                                    );
                                    examList.child(Common.currentUser.getPhone()).child(examTyperesult).setValue(el);
                                    Toast.makeText(TestActivity.this, "Exam Registered", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    } else {
                        Toast.makeText(TestActivity.this,"Total number of question not equal to answer",Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(TestActivity.this,"Please fill up all the details",Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();*/
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {

        //First, we need get all the subject in Category
        DatabaseReference subjectList = database.getReference("Subject").child(Common.currentSubject.getSbid()).child(Common.currentUser.getPhone());
        Query ExamInList = subjectList.orderByChild("examType").equalTo(key);
        ExamInList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    postSnapShot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        subjectList.child(key).removeValue();
        Toast.makeText(this, "Item Deleted !!", Toast.LENGTH_SHORT).show();
    }

    private void showUpdateDialog(final String key, final ExamList item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TestActivity.this);
        alertDialog.setTitle("Update Exam");
        alertDialog.setMessage("Please fill full information");

        final DatabaseReference subjectList = database.getReference("Subject").child(Common.currentSubject.getSbid()).child(Common.currentUser.getPhone());

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_subject_layout, null);

        final TextView subjectName = (TextView) add_menu_layout.findViewById(R.id.SubjectName);
        final MaterialEditText studentNo = (MaterialEditText)add_menu_layout.findViewById(R.id.StudentNo);
        final MaterialEditText examYear = (MaterialEditText)add_menu_layout.findViewById(R.id.examYear);
        final MaterialEditText examtotalQuest = (MaterialEditText)add_menu_layout.findViewById(R.id.edtQuesNo);
        final MaterialEditText examAnswer = (MaterialEditText)add_menu_layout.findViewById(R.id.edtAnswer);
        final MaterialSpinner spinner = (MaterialSpinner) add_menu_layout.findViewById(R.id.exam_spinner);


        spinner.setItems(" ","First-Term", "Mid-Term", "Second-Term" ,"Final-Term");

        //Set Default name
        subjectName.setText(item.getSubjectName());
        studentNo.setText(item.getStudentNo());
        examtotalQuest.setText(item.getTotalQues());
        if (item.getExamType().equals("First-term"))
            spinner.setSelectedIndex(1);
        else if (item.getExamType().equals("Mid-Term"))
            spinner.setSelectedIndex(2);
        else if (item.getExamType().equals("Second-Term"))
            spinner.setSelectedIndex(3);
        else if (item.getExamType().equals("Final-Term"))
            spinner.setSelectedIndex(4);

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                spinnerresult = item.toString();
            }
        });
        item.setExamType(spinnerresult);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (item != null){
                    subjectList.child(spinnerresult).setValue(item);
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void loadExamList(String subjectId){
        adapter.startListening();

        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private String convertCodeToString(String examType) {
        if(examType.equals("0"))
            return "First-Term";
        else if (examType.equals("1"))
            return "Mid-Term";
        else if (examType.equals("2"))
            return "Second-Term";
        else
            return "Final-Term";
    }
}
