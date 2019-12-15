package us.to.opti_grader.optigrader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import us.to.opti_grader.optigrader.Common.Common;
import us.to.opti_grader.optigrader.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import us.to.opti_grader.optigrader.R;

import java.util.HashMap;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity{

    EditText edtPhone,edtPassword;
    Button btnSignIn;
    CheckBox ckbRemember;
    TextView txtForgotPwd;

    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        ckbRemember = (CheckBox)findViewById(R.id.ckbRemember);
        txtForgotPwd = (TextView) findViewById(R.id.txtForgotPwd);

        Paper.init(this);


        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.isConnectedToInternet(getBaseContext())){

                    if(ckbRemember.isChecked()){
                        Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());
                    }


                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please Wait");
                    mDialog.show();

                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if(edtPhone.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()){
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "Please fill up all the details !", Toast.LENGTH_SHORT).show();
                            } else {
                                if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                    mDialog.dismiss();

                                    User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                    user.setPhone(edtPhone.getText().toString());

                                    if (user.getPassword().equals(edtPassword.getText().toString())) {
                                        Intent eventIntent = new Intent(SignIn.this,Home.class);
                                        Common.currentUser = user;
                                        startActivity(eventIntent);
                                        status("online");
                                        Toast.makeText(SignIn.this, "Sign In Successful !", Toast.LENGTH_SHORT).show();
                                        finish();

                                        table_user.removeEventListener(this);

                                    } else {
                                        Toast.makeText(SignIn.this, "Sign In Failed !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(SignIn.this, "User doesn't exist !", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else{
                    Toast.makeText(SignIn.this,"Please check your connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void status (String status){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        DatabaseReference table_user = FirebaseDatabase.getInstance().getReference("User");
        table_user.child(Common.currentUser.getPhone())
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });


    }

    private void  showForgotPwdDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout,null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone = (MaterialEditText)forgot_view.findViewById(R.id.edtPhone);
        final MaterialEditText edtSecureCode = (MaterialEditText)forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                            User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);

                            if (user.getSecureCode().equals(edtSecureCode.getText().toString()))
                                Toast.makeText(SignIn.this, "Your password : " + user.getPassword(), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(SignIn.this, "Invalid secure code !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignIn.this, "User doesn't exist !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

}