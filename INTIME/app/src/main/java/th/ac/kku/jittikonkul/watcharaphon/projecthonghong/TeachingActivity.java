package th.ac.kku.jittikonkul.watcharaphon.projecthonghong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.Random;

public class TeachingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    ProgressBar mProgressbar;
    private static final int REFRESH_SCREEN = 1;
    GoogleApiClient mGoogleSignInClient;
    String name , email , uid;
    TextView tv_email_header;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar_TeacherAct);
        setSupportActionBar(toolbar);
        mProgressbar.setVisibility(View.INVISIBLE);
        CheckNumberOfClass();
        name = user.getDisplayName();
        email = user.getEmail();
        uid = user.getUid();
        mGoogleSignInClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(TeachingActivity.this, "You got Error.",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateClassDialog();
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
        tv_email_header = (TextView) header.findViewById(R.id.textView);

        recyclerView = (RecyclerView) findViewById(R.id.listview);

        if (user != null) {
            tv_email_header.setText(email);
            mRootRef.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = user.getDisplayName();
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Log.e("test", name + " ");
                    ((TextView) findViewById(R.id.tv_name_header_t)).setText(name);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Failed to read value.", error.toException());
                }
            });
        }


    }





    protected void showCreateClassDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(TeachingActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View dialog_view = inflater.inflate(R.layout.dialog_teaching_custom, null);
        builder.setView(dialog_view);

        final EditText et_classname = (EditText) dialog_view.findViewById(R.id.et_className_dialog);
        final EditText et_section = (EditText) dialog_view.findViewById(R.id.et_section_dialog);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Check Name and Section
                if (TextUtils.isEmpty(et_classname.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Name is Null!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if ( TextUtils.isEmpty(et_section.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Section is Null!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                    Toast.makeText(getApplicationContext(), "Create success!",
                            Toast.LENGTH_SHORT).show();

                    AddClassToDATA(et_classname.getText().toString(),et_section.getText().toString());

                }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

    }

    protected String getidString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    int numThisUser;


    public void CheckNumberOfClass() {
        mRootRef.child("Class").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(firebaseAuth.getCurrentUser().getEmail().equals(dataSnapshot.child("Teacher").getValue(String.class))) {
                        numThisUser++;
                    }
                checkClassCurrent();
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

    private Map<String, String> categories = new HashMap<>();

    @SuppressLint("RestrictedApi")
    protected void AddClassToDATA(final String class_name, final String class_sec){

            CheckNumberOfClass();
            Log.e("cctv",class_name);
        recyclerView.setAdapter(null);

            categories.put("Teacher", firebaseAuth.getCurrentUser().getEmail());
            categories.put("ClassName", class_name);
            categories.put("Section", class_sec);
            String idClass =  createID();
            categories.put("id", idClass);
            mRootRef.child("Class").child(idClass).setValue(categories);
             mRootRef.child("Class").child(idClass).child("Information").child("AllTime").setValue("00");
             mRootRef.child("Class").child(idClass).child("Information").child("MissTime").setValue("00");
            recyclerView.setAdapter(new RecyclerViewAdapter());
    }

    public boolean allDifferent( String newID){
        for(int i = 0; i < indList.size(); i++){
            if(indList.get(i).equals(newID)){
                return true;
            }
        }
        return false;
    }

    public String createID(){
        String new_id ;
        do{
            new_id = getidString();
        }
        while(allDifferent(new_id));
        return new_id;
    }

    public void startScan() {
        new Thread() {
            public void run() {
                try{
                    Thread.sleep(1000);
                    hRefresh.sendEmptyMessage(REFRESH_SCREEN);
                }catch(Exception e){
                }
            }
        }.start();
    }


    @SuppressLint("HandlerLeak")
    Handler hRefresh = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case REFRESH_SCREEN:
                  mProgressbar.setVisibility(View.INVISIBLE); // Hide ProgressBar
                    ShowText();
                    break;
                default:
                    break;
            }
        }
    };
    public void ShowText(){
        recyclerView.setVisibility(View.VISIBLE);
    }

    List<String> classlist= new ArrayList<>() ;
    List<String> class_Tname = new ArrayList<>();
    List<String> class_ID = new ArrayList<>();
    List<String> sectionlist = new ArrayList<>();
    List<String> indList= new ArrayList<>() ;





    private void collectClassName(Map<String,Object> users) {


        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map idClass = (Map) entry.getValue();
            //Get phone field and append to list
            indList.add((String) idClass.get("id"));
        }

    }

    public void checkClassCurrent() {
                mRootRef.child("Class").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mProgressbar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        startScan();
                        if(dataSnapshot.getValue() != null) {
                            class_Tname.clear();
                            classlist.clear();
                            class_ID.clear();
                            sectionlist.clear();
                            indList.clear();
                            recyclerView.setAdapter(null);
                            collectClassName((Map<String, Object>) dataSnapshot.getValue());
                            for (int i = 0; i < indList.size(); i++) {
                                String TeacherVal = dataSnapshot.child(indList.get(i)).child("Teacher").getValue(String.class);
                                if (TeacherVal.equals(firebaseAuth.getCurrentUser().getEmail())) {
                                    String classnameVal = dataSnapshot.child(indList.get(i)).child("ClassName").getValue(String.class);
                                    String secVal = dataSnapshot.child(indList.get(i)).child("Section").getValue(String.class);
                                    String classIDVal = dataSnapshot.child(indList.get(i)).child("id").getValue(String.class);
                                    class_Tname.add(TeacherVal);
                                    classlist.add(classnameVal);
                                    class_ID.add(classIDVal);
                                    sectionlist.add(secVal);
                                }
                            }
                            recyclerView.setAdapter(new RecyclerViewAdapter());
                            Log.d("size", String.valueOf(class_Tname.size()));
                        }else {
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

    public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHoldder>{

        @Override
        public ViewHoldder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_listview,parent,false);

            return new ViewHoldder(v);
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
                    Intent intent = new Intent(TeachingActivity.this,Teaching_class_Activity.class);
                    intent.putExtra("Class_id",indKey);
                    startActivity(intent);
                    Log.d("indK", String.valueOf(index));
                }
            });
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
        if (id == R.id.nav_myclass) {
            //  Intent intent = new Intent(this. intentActivity.class);
            //  startAcitivity(intent);
            //  return true;

            Intent intent = new Intent(TeachingActivity.this,afterLogin.class);
            startActivity(intent);
            this.finish();

        } else if (id == R.id.nav_classroom) {
            Intent intent = new Intent(this,TeachingActivity.class);
         startActivity(intent);

        }  else if (id == R.id.nav_signout){
            signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void signOut() {
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleSignInClient);
        Toast.makeText(TeachingActivity.this, "Sign Out Success!",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TeachingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
//edit kub