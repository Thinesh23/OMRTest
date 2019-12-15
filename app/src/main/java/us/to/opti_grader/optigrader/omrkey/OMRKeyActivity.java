package us.to.opti_grader.optigrader.omrkey;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import us.to.opti_grader.optigrader.R;
import us.to.opti_grader.optigrader.omrkey.AppDatabase;
import us.to.opti_grader.optigrader.omrkey.OMRKey;
import us.to.opti_grader.optigrader.utils.AnswersUtils;

public class OMRKeyActivity extends AppCompatActivity implements RadioButton.OnCheckedChangeListener{

    private int circleIds[] = new int[]{R.mipmap.ic_omr_circle_a, R.mipmap.ic_omr_circle_b, R.mipmap.ic_omr_circle_c, R.mipmap.ic_omr_circle_d, R.mipmap.ic_omr_circle_e};

    private int[] correctAnswers;

    private int noOfAnswers, noOfChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omrkey);

        Intent i = getIntent();
        if (i != null){
            noOfChoices = Integer.parseInt(i.getStringExtra("noOfChoices"));
            noOfAnswers = Integer.parseInt(i.getStringExtra("noOfAnswers"));
        }
        createAnswerKey(noOfAnswers);
        loadCorrectAnswers(noOfAnswers);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        storeCorrectAnswers(noOfAnswers);
    }

    public void createAnswerKey(int noOfAnswers){

        TextView textView;
        CheckBox checkBox;

        TableLayout tableLayout = findViewById(R.id.tableLayout);
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

    public void loadCorrectAnswers(final int noOfAnswers){

        final String[] strCorrectAnswers = {""};
        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "omr").build();
        final OMRKey omrKey = new OMRKey();
        omrKey.setOmrkeyid(noOfAnswers);
        omrKey.setStrCorrectAnswers(strCorrectAnswers[0]);

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

    public void storeCorrectAnswers(int noOfAnswers){
        correctAnswers = new int[noOfAnswers];
        int cnt = -1;
        CheckBox checkBox;
        for(int i=0; i < noOfAnswers * noOfChoices; i++){
            checkBox = findViewById(i);

            if(i%noOfChoices == 0)
                cnt++;

            if(checkBox.isChecked()){
                correctAnswers[cnt] = (i % noOfChoices) + 1;
            }
        }

        String strCorrectAnswers = AnswersUtils.inttostrAnswers(correctAnswers);

        if(strCorrectAnswers != null){
            final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "omr").build();
            final OMRKey omrKey = new OMRKey();
            omrKey.setOmrkeyid(noOfAnswers);
            omrKey.setStrCorrectAnswers(strCorrectAnswers);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.omrKeyDao().insertOMRKey(omrKey);
                    return null;
                }
            }.execute();
            Toast.makeText(this,"Answers saved",Toast.LENGTH_LONG).show();
        }
    }
}