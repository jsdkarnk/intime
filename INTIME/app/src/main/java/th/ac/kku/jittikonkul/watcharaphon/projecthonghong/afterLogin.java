package th.ac.kku.jittikonkul.watcharaphon.projecthonghong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class afterLogin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    TextView tv_email_header;
    String TAG = "123456";
    GoogleApiClient mGoogleSignInClient;
    String Uid, name;
    RecyclerView recyclerView;
    String uid = user.getUid();
    String displayName = user.getDisplayName();
    String email = user.getEmail();
    List<String> indList = new ArrayList<>();
    List<String> uidList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Uid = user.getUid();
        name = user.getDisplayName();
        recyclerView = (RecyclerView) findViewById(R.id.listview);
        addRecyclerView();

        //add config to mGoogleSignInClient
        mGoogleSignInClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(afterLogin.this, "You got Error.",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //////
                addClassroom();
                //////
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tv_email_header = (TextView) header.findViewById(R.id.tv_email_header);
        Toast.makeText(afterLogin.this, "Hello !! " + name, Toast.LENGTH_SHORT).show();
        if (user != null) {
            String user_email = user.getEmail();

            Log.e("test", user_email + " ");
            tv_email_header.setText(user_email);
            mRootRef.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = user.getDisplayName();
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Log.e("test", name + " ");
                    ((TextView) findViewById(R.id.tv_name_header)).setText(name);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Failed to read value.", error.toException());
                }
            });
        }
        CheckNumberOfClass();
    }

    int numThisUser;

    private void CheckNumberOfClass() {
        mRootRef.child("Class").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (firebaseAuth.getCurrentUser().getEmail().equals(dataSnapshot.child("Teacher").getValue(String.class))) {
                    numThisUser++;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    public void addClassroom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(afterLogin.this);
        LayoutInflater inflater = getLayoutInflater();

        View dialog_view = inflater.inflate(R.layout.dialog_addclassroom, null);
        builder.setView(dialog_view);

        final EditText ETclasscode = (EditText) dialog_view.findViewById(R.id.et_classcode);
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String classcode = (String) ETclasscode.getText().toString();
                Log.e("honhon", classcode);
                // Check Name and Section
                if (TextUtils.isEmpty(classcode)) {
                    Toast.makeText(getApplicationContext(), "Class code is Null!",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    createMemAndClassList(classcode);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private Map<String, String> categories = new HashMap<>();
    String emailTeacher ;
    boolean isTeacher = false;
    public void createMemAndClassList(final String classcode) {

        mRootRef.child("Class").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    collectClassName((Map<String, Object>) dataSnapshot.getValue());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if(checkClass(classcode)){



            mRootRef.child("Class").child(classcode).child("members").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null) {
                        collectClassMembers((Map<String, Object>) dataSnapshot.getValue());
                    }else {

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            mRootRef.child("Class").child(classcode).child("Teacher").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null) {
                        emailTeacher =  dataSnapshot.getValue().toString();
                        Log.d("mailT" ,emailTeacher + " " + user.getEmail());
                        if(emailTeacher.equals(user.getEmail())){
                            Toast.makeText(afterLogin.this, "You are teacher of class.", Toast.LENGTH_LONG).show();
                        }else if (checkInMembers(Uid)) {
                                Toast.makeText(afterLogin.this, "You are already in class.", Toast.LENGTH_LONG).show();
                        } else {
                                DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
                                DB.child("Class").child(classcode).child("members").child(uid).child("uid").setValue(Uid);
                        }

                    }else {

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }else{
            Toast.makeText(afterLogin.this, "This class isn't exist", Toast.LENGTH_LONG).show();
        }

    }

    private boolean checkClass(String classcode) {
        for(int i = 0; i < indList.size(); i++){
            if(indList.get(i).equals(classcode)){
                Log.d("id1234",indList.get(i));
                return true;
            }
        }
        return false;
    }


    private boolean checkInMembers(String uid) {
        for(int i = 0; i < uidList.size(); i++){
            if(uidList.get(i).equals(uid)){
                Log.d("uidja",uidList.get(i));
                return true;
            }
        }
        Log.d("uidja", String.valueOf(uidList.size()));
        return false;
    }


    List<String> classlist = new ArrayList<>();
    List<String> class_Tname = new ArrayList<>();
    List<String> class_ID = new ArrayList<>();
    List<String> sectionlist = new ArrayList<>();

    public void addRecyclerView() {
        mRootRef.child("Class").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    class_Tname.clear();
                    classlist.clear();
                    class_ID.clear();
                    sectionlist.clear();
                    indList.clear();
                    uidList.clear();
                    recyclerView.setAdapter(null);
                   collectClassName((Map<String, Object>) dataSnapshot.getValue());
                    Log.d("size4551", String.valueOf(indList.size()));
                    for (int i = 0; i < indList.size(); i++) {
                        if(dataSnapshot.child(indList.get(i)).child("members").getValue() != null) {
                            uidList.clear();
                            collectClassMembers((Map<String, Object>) dataSnapshot.child(indList.get(i)).child("members").getValue());
                            Log.d("size4552", String.valueOf(uidList.size()));
                            for (int j = 0; j < uidList.size(); j++) {
                                if (Uid.equals(uidList.get(j))) {
                                    String nameVal = dataSnapshot.child(indList.get(i)).child("Teacher").getValue(String.class);
                                    String classnameVal = dataSnapshot.child(indList.get(i)).child("ClassName").getValue(String.class);
                                    String secVal = dataSnapshot.child(indList.get(i)).child("Section").getValue(String.class);
                                    String classIDVal = dataSnapshot.child(indList.get(i)).child("id").getValue(String.class);
                                    class_Tname.add(nameVal);
                                    classlist.add(classnameVal);
                                    class_ID.add(classIDVal);
                                    sectionlist.add(secVal);
                                }
                            }
                        }
                    }
                    recyclerView.setAdapter(new RecyclerViewAdapter());
                    Log.d("size4555", String.valueOf(class_Tname.size()));
                } else {
                    class_Tname.clear();
                    classlist.clear();
                    class_ID.clear();
                    sectionlist.clear();
                    indList.clear();
                    recyclerView.setAdapter(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    private void collectClassMembers(Map<String, Object> users) {
            //iterate through each user, ignoring their UID
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                //Get user map
                Map idClass = (Map) entry.getValue();
                //Get phone field and append to list
                uidList.add((String) idClass.get("uid"));
                Log.d("123456d", uidList.get(0));
            }
    }

    private void collectClassName(Map<String,Object> users) {

        int i = 0;
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            //Get user map
            Map idClass = (Map) entry.getValue();
            //Get phone field and append to list
            indList.add((String) idClass.get("id"));
            Log.d("44455", String.valueOf(indList.get(i)));
            i++;
        }
    }

        @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.nav_myclass) {
            //  intent = new Intent(afterLogin.this,afterLogin.class);
            //  startActivity(intent);
        } else if (id == R.id.nav_classroom) {
            intent = new Intent(afterLogin.this, TeachingActivity.class);
            startActivity(intent);
            this.finish();
        } else if (id == R.id.nav_signout) {
            signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOut() {
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleSignInClient);
        Toast.makeText(afterLogin.this, "Sign Out Success!",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(afterLogin.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<afterLogin.ViewHoldder>{

        @Override
        public afterLogin.ViewHoldder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_listview,parent,false);

            return new afterLogin.ViewHoldder(v);
        }

        @Override
        public void onBindViewHolder(ViewHoldder holder, int position) {
            holder.class_name.setText(classlist.get(position));
            holder.teacher_name.setText("Teacher : " +class_Tname.get(position));
            holder.section_num.setText("Section " + sectionlist.get(position));
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return classlist.size();
        }
    }

    public class ViewHoldder extends RecyclerView.ViewHolder {

        TextView class_name;
        TextView teacher_name;
        TextView section_num;

        public ViewHoldder(View itemView) {
            super(itemView);
            class_name = itemView.findViewById(R.id.tv_classname);
            teacher_name = itemView.findViewById(R.id.tv_nameT);
            section_num = itemView.findViewById(R.id.tv_section_Teacher_Act);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    String indKey = class_ID.get(index);
                    Intent intent = new Intent(afterLogin.this,afterLogin_class_Activity.class);
                    intent.putExtra("Class_id",indKey);
                    startActivity(intent);
                    Log.d("indK", String.valueOf(index));
                }
            });
        }
    }

}
