package th.ac.kku.jittikonkul.watcharaphon.projecthonghong;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    Button bt;
    EditText Eemail, Epass, et_fname, et_lname, et_conpass;
    String email, pass, str_conpass, str_fname, str_lname;

    DatabaseReference mRootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        bt = (Button) findViewById(R.id.but_singup);
        Eemail = (EditText) findViewById(R.id.editText_email);
        Epass = (EditText) findViewById(R.id.editText_pass);
        et_fname = (EditText) findViewById(R.id.editfname);
        et_lname = (EditText) findViewById(R.id.editText_lname);
        et_conpass = (EditText) findViewById(R.id.editText_Conpass);

        bt.setOnClickListener(SignupActivity.this);

    }

    @Override
    public void onClick(View view) {
        email = Eemail.getText().toString().trim();
        pass = Epass.getText().toString().trim();
        str_conpass = et_conpass.getText().toString().trim();
        str_fname = et_fname.getText().toString().trim();
        str_lname = et_lname.getText().toString().trim();

        if (TextUtils.isEmpty(str_fname)) {
            Toast.makeText(getApplicationContext(), "Enter your first name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(str_lname)) {
            Toast.makeText(getApplicationContext(), "Enter your last name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pass) && TextUtils.isEmpty(str_conpass)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 8) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 8 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!str_conpass.equals(pass)) {
            Toast.makeText(getApplicationContext(), "Password Not Match!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mRootRef = FirebaseDatabase.getInstance().getReference();
                            @SuppressLint("RestrictedApi") String udi = firebaseAuth.getUid();
                            mRootRef.child("Users").child(udi).child("FirstName").setValue(str_fname);
                            mRootRef.child("Users").child(udi).child("LastName").setValue(str_lname);
                            mRootRef.child("Users").child(udi).child("Email").setValue(email);
                            Log.e("hon", udi);
                            Toast.makeText(SignupActivity.this, "Success!",
                                    Toast.LENGTH_SHORT).show();
                            // go to login
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    public void GotoSignIn(View view) {
        // go to login
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
