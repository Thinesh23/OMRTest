package us.to.opti_grader.optigrader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.instacart.library.truetime.TrueTime;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;

import us.to.opti_grader.optigrader.omrkey.OMRKeyActivity;
import us.to.opti_grader.optigrader.truetime.InitTrueTimeAsyncTask;

public class AddTestActivity extends AppCompatActivity {
    private String noOfAnswers, noOfChoices;
    private Button answer, scan;
    private MaterialSpinner examtype, totalquest, availableChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);


        answer = (Button) findViewById(R.id.btnAnswer);
        scan = (Button) findViewById(R.id.btnScan);
        totalquest = (MaterialSpinner) findViewById(R.id.exam_question);
        examtype = (MaterialSpinner) findViewById(R.id.exam_type);
        availableChoice = (MaterialSpinner) findViewById(R.id.exam_choices);

        totalquest.setItems("Choose total question", "20", "50");
        totalquest.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                noOfAnswers = item.toString();
            }
        });
        examtype.setItems("Choose exam type", "First-Term", "Mid-Term", "Second-Term" ,"Final-Term");

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
                if (availableChoice.getSelectedIndex() == 0 || totalquest.getSelectedIndex() == 0){
                    Toast.makeText(AddTestActivity.this, "Please select all the options", Toast.LENGTH_SHORT).show();
                } else {
                    Intent omrKeyActivity = new Intent(AddTestActivity.this, OMRKeyActivity.class);
                    omrKeyActivity.putExtra("noOfAnswers", noOfAnswers);
                    omrKeyActivity.putExtra("noOfChoices", noOfChoices);
                    startActivity(omrKeyActivity);
                }

            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (availableChoice.getSelectedIndex() == 0 || totalquest.getSelectedIndex() == 0){
                    Toast.makeText(AddTestActivity.this, "Please select all the options", Toast.LENGTH_SHORT).show();
                } else {
                    Intent cameraIntent = new Intent(AddTestActivity.this, CameraActivity.class);
                    cameraIntent.putExtra("noOfAnswers", noOfAnswers);
                    cameraIntent.putExtra("noOfChoices", noOfChoices);
                    startActivity(cameraIntent);
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


