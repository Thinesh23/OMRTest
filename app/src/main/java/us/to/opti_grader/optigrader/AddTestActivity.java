package us.to.opti_grader.optigrader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instacart.library.truetime.TrueTime;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;

import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Model.ExamList;
import us.to.opti_grader.optigrader.omrkey.OMRKeyActivity;
import us.to.opti_grader.optigrader.truetime.InitTrueTimeAsyncTask;

public class AddTestActivity extends AppCompatActivity {
    private String noOfAnswers, noOfChoices, type;
    private Button answer, scan;
    private MaterialSpinner examtype, totalquest, availableChoice;
    private boolean updateAnswer = false;
    private MaterialEditText studentNo;
    private String subjectId = "";
     TextView sname;
    FirebaseDatabase database;
    DatabaseReference examList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);


        answer = (Button) findViewById(R.id.btnAnswer);
        scan = (Button) findViewById(R.id.btnScan);
        totalquest = (MaterialSpinner) findViewById(R.id.exam_question);
        examtype = (MaterialSpinner) findViewById(R.id.exam_type);
        availableChoice = (MaterialSpinner) findViewById(R.id.exam_choices);
        studentNo = (MaterialEditText) findViewById(R.id.StudentNo);
        sname = (TextView) findViewById(R.id.SubjectName);

        sname.setText(Common.currentSubject.getName());

        if(getIntent()!=null){
            subjectId = getIntent().getStringExtra(Common.INTENT_SUBJECT_ID);
        }

        database = FirebaseDatabase.getInstance();
        examList = database.getReference("Subject").child(Common.currentSubject.getSbid());

        totalquest.setItems("Choose total question", "20", "50");
        totalquest.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                noOfAnswers = item.toString();
            }
        });
        examtype.setItems("Choose exam type", "FirstTerm", "MidTerm", "SecondTerm" ,"FinalTerm");
        examtype.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                type = item.toString();
            }
        });

        availableChoice.setItems("Available Choice", "A-D","A-E");
        availableChoice.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (item.toString().equals("A-D"))
                    noOfChoices = "4";
                else if (item.toString().equals("A-E"))
                    noOfChoices = "5";
            }
        });
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (availableChoice.getSelectedIndex() == 0 && totalquest.getSelectedIndex() == 0){
                    Toast.makeText(AddTestActivity.this, "Please select all the options", Toast.LENGTH_SHORT).show();
                } else {
                    Intent omrKeyActivity = new Intent(AddTestActivity.this, OMRKeyActivity.class);
                    omrKeyActivity.putExtra("noOfAnswers", noOfAnswers);
                    omrKeyActivity.putExtra("noOfChoices", noOfChoices);
                    startActivity(omrKeyActivity);
                }
                updateAnswer = true;

            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (availableChoice.getSelectedIndex() == 0 && totalquest.getSelectedIndex() == 0
                        && studentNo.getText().toString().trim().length() != 0
                        && examtype.getSelectedIndex() == 0){
                    Toast.makeText(AddTestActivity.this, "Please select all the options", Toast.LENGTH_SHORT).show();
                } else {
                    final ExamList el = new ExamList();
                    el.setExamType(type);
                    el.setStudentNo(studentNo.getText().toString());
                    el.setSubjectId(subjectId);
                    el.setSubjectName(Common.currentSubject.getName());
                    el.setTotalQues(noOfAnswers);
                    Common.currentExam = el;

                    examList.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            examList.child(Common.currentUser.getPhone()).child("examType").child(type).setValue(el);
                            Intent cameraIntent = new Intent(AddTestActivity.this, CameraActivity.class);
                            cameraIntent.putExtra("noOfAnswers", noOfAnswers);
                            cameraIntent.putExtra("noOfChoices", noOfChoices);
                            startActivity(cameraIntent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        displayValidityPeriodDialog();


    }

    public void displayValidityPeriodDialog(){
        if (!TrueTime.isInitialized())
            new InitTrueTimeAsyncTask(AddTestActivity.this).execute();
    }
}


