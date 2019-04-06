package th.ac.kku.jittikonkul.watcharaphon.projecthonghong;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    EditText Editemail, Editpassword;
    String email, password, name, lastN, Gmail;
    FirebaseAuth mAuth;
    String TAG = "123456";
    int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Editpassword = (EditText) findViewById(R.id.password);
        Editemail = (EditText) findViewById(R.id.email);
        SignInButton googleButton = (SignInButton) findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        ((Button) findViewById(R.id.login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = Editemail.getText().toString();
                password = Editpassword.getText().toString();

                if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter Email and Password", Toast.LENGTH_LONG).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_LONG).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Success", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUILogin(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("Failed", "signInWithEmail:failure", task.getException());

                                        Toast.makeText(MainActivity.this,"Please try again",Toast.LENGTH_LONG).show();
                                        updateUILogin(null);
                                    }

                                }
                            });
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();
    }

    public void gotoSG(View viwe) {
        intent = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    public void updateUILogin(FirebaseUser firebaseUser) {

        if (firebaseUser != null) {
            Intent intent = new Intent(MainActivity.this, afterLogin.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Sign in Success", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            Toast.makeText(this, "Please try agian", Toast.LENGTH_SHORT).show();
        }

    }

    public void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            @SuppressLint("RestrictedApi") String udi = firebaseAuth.getUid();
            mRootRef.child("Users").child(udi).child("FirstName").setValue(name);
            mRootRef.child("Users").child(udi).child("LastName").setValue(lastN);
            mRootRef.child("Users").child(udi).child("Email").setValue(Gmail);
            Intent intent = new Intent(MainActivity.this, afterLogin.class);
            startActivity(intent);

            finish();

        } else {

        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Gmail = account.getEmail();
                name = account.getGivenName();
                lastN = account.getFamilyName();

                Log.e("hon", name + lastN);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("123456", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this,"Please try again",Toast.LENGTH_LONG).show();
                            Log.w("123456", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUILogin(currentUser);
    }



}
