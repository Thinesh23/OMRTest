package us.to.opti_grader.optigrader;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import us.to.opti_grader.optigrader.omrkey.AppDatabase;
import us.to.opti_grader.optigrader.omrkey.OMRKey;
import us.to.opti_grader.optigrader.utils.AnswersUtils;

public class VerifyActivity extends AppCompatActivity implements RadioButton.OnCheckedChangeListener{

    private int circleIds[] = new int[]{R.mipmap.ic_omr_circle_a, R.mipmap.ic_omr_circle_b, R.mipmap.ic_omr_circle_c, R.mipmap.ic_omr_circle_d, R.mipmap.ic_omr_circle_e};

    private int[] correctAnswers;
    private int[] studentAnswers;

    private int noOfAnswers, noOfChoices;
    private ArrayList<String> answersList = new ArrayList<>();

    private Button Evaluate;
    String answernew = "";
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        Intent i = getIntent();
        if (i != null){
            noOfChoices = Integer.parseInt(i.getStringExtra("tempChoices"));
            noOfAnswers = Integer.parseInt(i.getStringExtra("tempQuestions"));
            answersList = i.getStringArrayListExtra("tempAnswers");
        }


        createAnswerKey(noOfAnswers, noOfChoices);
        loadStudentAnswers(noOfAnswers, answersList, noOfChoices);

        Evaluate = (Button) findViewById(R.id.btnEvaluate);
        Evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudentDialog();
            }
        });
    }

    @Override
    protected void onNewIntent (Intent intent){
        setIntent (intent);
    }

    public void createAnswerKey(int noOfAnswers, int noOfChoices){

        TextView textView;
        CheckBox checkBox;

        TableLayout tableLayout = findViewById(R.id.tableLayout2);
        TableRow tableRow;

        for(int i=0; i < noOfAnswers; i++){

            textView = new TextView(this);
            tableRow = new TableRow(this);

            if(i<9)
                textView.setText("\t"+String.valueOf(i+1)+")\t\t");
            else
                textView.setText(String.valueOf(i+1)+")\t\t");

            textView.setTextSize(20);
            textView.setPadding(5,0,0,0);

            tableRow.addView(textView);

            for(int j=0; j<noOfChoices; j++){

                checkBox = new CheckBox(this);
                checkBox.setId((i*noOfChoices)+j);
                checkBox.setButtonDrawable(circleIds[j]);
                checkBox.setPadding(5,30,5,30);
                checkBox.setOnCheckedChangeListener(this);

                tableRow.addView(checkBox);
            }
            tableLayout.addView(tableRow);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

        int id = compoundButton.getId();
        CheckBox checkBox;

        for (int i = (id/noOfChoices)*noOfChoices; i < (id/noOfChoices)*noOfChoices + noOfChoices; i++){
            checkBox = findViewById(i);
            if(checkBox.isChecked() && i != id){
                checkBox.setButtonDrawable(circleIds[i%noOfChoices]);
                checkBox.setChecked(false);
                break;
            }
        }

        if(checked){
            compoundButton.setButtonDrawable(R.mipmap.ic_omr_black_circle);
            compoundButton.setChecked(true);
        }
        else {
            compoundButton.setButtonDrawable(circleIds[id%noOfChoices]);
            compoundButton.setChecked(false);
        }
    }

    public void loadStudentAnswers(int noOfAnswers, final List<String> listanswers, final int noOfChoices){

        final String[] strCorrectAnswers = {""};
        final OMRKey omrKey = new OMRKey();

        for (int i = 0; i < listanswers.size(); i++){
            if(listanswers.get(i).equals("A"))
                answernew += "1,";
            else if (listanswers.get(i).equals("B"))
                answernew += "2,";
            else if (listanswers.get(i).equals("C"))
                answernew += "3,";
            else if (listanswers.get(i).equals("D"))
                answernew += "4,";
            else if (listanswers.get(i).equals("E"))
                answernew += "5,";
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if(listanswers != null)
                    strCorrectAnswers[0] = answernew;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                int[] answers;
                int correctAnswer;
                CheckBox checkBox;

                answers = AnswersUtils.strtointAnswers(strCorrectAnswers[0]);

                if(answers != null){
                    for(int i=0; i< answers.length; i++){
                        correctAnswer = answers[i];
                        if(correctAnswer != 0){
                            checkBox = findViewById((i*noOfChoices) + (correctAnswer - 1));
                            checkBox.setChecked(true);
                        }
                    }
                }
            }
        }.execute();


    }

    public void storeCorrectAnswers(final int noOfAnswers){
        studentAnswers = new int[noOfAnswers];
        final String[] strCorrectAnswers = {""};
        int cnt = -1;
        CheckBox checkBox;
        for(int i=0; i < noOfAnswers * noOfChoices; i++){
            checkBox = findViewById(i);

            if(i%noOfChoices == 0)
                cnt++;

            if(checkBox.isChecked()){
                studentAnswers[cnt] = (i % noOfChoices) + 1;
            }
        }

        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "omr").build();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if(db.omrKeyDao().findById(noOfAnswers) != null)
                    strCorrectAnswers[0] = db.omrKeyDao().findById(noOfAnswers).getStrCorrectAnswers();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                int[] correctAnswer;

                correctAnswer = AnswersUtils.strtointAnswers(strCorrectAnswers[0]);

                score = calculateScore(studentAnswers, correctAnswer);
            }
        }.execute();
    }

    public int calculateScore (int[] studentAnswers, int[] correctAnswers){
        int score = 0;
        int answerPerQuestion;

        for (int i = 0; i < noOfAnswers; i++){
            Log.i("OPENCV", "student answer = " + studentAnswers[i] + " correctAnswer "+correctAnswers[i]);
            if (studentAnswers[i] == (correctAnswers[i]-1)){
                score++;
            }
        }
        return score;
    }



    private void addStudentDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VerifyActivity.this);
        alertDialog.setTitle("Add Exam");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_student_result, null);

        final TextView Score = (TextView) add_menu_layout.findViewById(R.id.studentScore);
        final MaterialEditText studentID = (MaterialEditText)add_menu_layout.findViewById(R.id.StudentID);
        storeCorrectAnswers(noOfAnswers);
        //Set button
        Score.setText(Integer.toString(score));
        alertDialog.setView(add_menu_layout);
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (studentID.getText().toString().trim().length() != 0) {

                } else {
                    Toast.makeText(VerifyActivity.this,"Please fill up StudentID",Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }


}