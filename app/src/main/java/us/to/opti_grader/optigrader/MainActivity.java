package us.to.opti_grader.optigrader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.opencv.android.OpenCVLoader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import  us.to.opti_grader.optigrader.Common.Common;
import  us.to.opti_grader.optigrader.Model.User;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity{

/*    static {
        if (OpenCVLoader.initDebug()) {
            Log.i("opencv","OpenCV initialization successful");
        } else {
            Log.i("opencv","OpenCV initialization failed");
        }
    }*/

    Button btnSignIn,btnSignUp;
    TextView txtSlogan;
    String Phone,Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printKeyHash();

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        txtSlogan = (TextView)findViewById(R.id.txtslogan);
        txtSlogan = (TextView)findViewById(R.id.app_logo);

        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
            }
        });


        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if(user != null && pwd != null){
            if(!user.isEmpty() && !pwd.isEmpty())
                login(user,pwd);
        }
    }

    private void printKeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("example.com.engage",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch ( PackageManager.NameNotFoundException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    private void login(String phone, String pwd){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        Phone= phone;
        Password = pwd;

        if(Common.isConnectedToInternet(getBaseContext())){



            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please Wait");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(Phone).exists()) {
                        mDialog.dismiss();

                        User user = dataSnapshot.child(Phone).getValue(User.class);
                        user.setPhone(Phone);

                        if (user.getPassword().equals(Password)) {
                            Intent eventIntent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(eventIntent);
                            Toast.makeText(MainActivity.this, "Sign In Successful !", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "Sign In Failed !", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this, "User doesn't exist !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else{
            Toast.makeText(MainActivity.this,"Please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}