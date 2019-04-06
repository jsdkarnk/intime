package th.ac.kku.jittikonkul.watcharaphon.projecthonghong;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CheckFragment extends Fragment {


    private static final String TAG = "Check1234" ;

    public CheckFragment() {
        // Required empty public constructor

    }

    public DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    public String class_id;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatePickerDialog mDatePicker;
    private TimePickerDialog mTimePicker;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();

    private Calendar mCalendar;
    private TextView mTextTime, mTextDate,mStatus;

    private Button sw_status;
    private boolean check_status = false;
    View v;
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_check, container, false);
        sw_status = v.findViewById(R.id.sw_t_check);
        mTextTime = v.findViewById(R.id.tv_time);
        mTextDate = v.findViewById(R.id.day);
        mStatus = v.findViewById(R.id.tv_status);
        Bundle bundle = getArguments();
        if(bundle != null) {
            class_id = bundle.getString("class_id");
        }
        checkedStatus();
        sw_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkedStatus();
                        if (!check_status) {
                            //check
                            AlertDialog.Builder builder =
                                    new AlertDialog.Builder(getActivity());
                            builder.setMessage("Do you want to Check is now?");
                            builder.setPositiveButton("On Check", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mRootRef.child("Class").child(class_id).child("Information").child("Check_Status").setValue("true");
                                    addClassDB();
                                    mStatus.setText("Status : On");
                                    Toast.makeText(getActivity(),
                                            "Check on!", Toast.LENGTH_SHORT).show();

                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //dialog.dismiss();
                                }
                            });
                            builder.show();
                            ///////
                        } else {
                            mStatus.setText("Status : Off");
                            mRootRef.child("Class").child(class_id).child("Information").child("Check_Status").setValue("false");
                            Toast.makeText(getActivity(),
                                    "Check off!", Toast.LENGTH_SHORT).show();
                        }
                    }
        });
        mCalendar = Calendar.getInstance();
        mTextTime.setText(String.format("%02d:%02d", mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE)));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        Date date = mCalendar.getTime();
        String textDate = dateFormat.format(date);

        mTextDate.setText(textDate);

        mDatePicker = DatePickerDialog.newInstance(onDateSetListener,
                mCalendar.get(Calendar.YEAR),       // ปี
                mCalendar.get(Calendar.MONTH),      // เดือน
                mCalendar.get(Calendar.DAY_OF_MONTH),// วัน (1-31)
                false);                             // ให้สั่นหรือไม่?

        mTimePicker = TimePickerDialog.newInstance(onTimeSetListener,
                mCalendar.get(Calendar.HOUR_OF_DAY),     // หน่วยเข็มชั่วโมง
                mCalendar.get(Calendar.MINUTE),     // เข็มนาที
                true,   // ใช้ระบบนับแบบ 24-Hr หรือไม่? (0 - 23 นาฬิกา)
                false); // ให้สั่นหรือไม่?

        mTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker.show(getFragmentManager(), "timePicker");
            }
        });

        mTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePicker.setYearRange(2000, 2020);
                mDatePicker.show(getFragmentManager(), "datePicker");
            }
        });

        return v;

    }

    public void checkedStatus() {

        mRootRef.child("Class").child(class_id).child("Information").child("Check_Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                check_status =  Boolean.valueOf((String) dataSnapshot.getValue());
                if (check_status) {
                    mStatus.setText("Status : On");
                } else {
                    mStatus.setText("Status : Off");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException());
            }
        });
    }


    public void addClassDB() {
        String hh = String.valueOf(mTextTime.getText().subSequence(0,1));
        String mm = String.valueOf(mTextTime.getText().subSequence(3,4));
        // Create a new user with a first and last name
        Map<String, Object> t_time = new HashMap<>();


        // Add a new document with a generated ID


        db.collection("classDB")
                .document(class_id)
                .collection(mTextDate.getText().toString())
                .document(mTextTime.getText().toString())
                .set(t_time);
        Log.d("DB1234",class_id + "save");
    }


    private TimePickerDialog.OnTimeSetListener onTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                    mTextTime.setText(String.format("%02d:%02d",hourOfDay,minute));
                }
            };

    private DatePickerDialog.OnDateSetListener onDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

                    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
                    mCalendar.set(year, month, day);
                    Date date = mCalendar.getTime();
                    String textDate = dateFormat.format(date);

                    mTextDate.setText(textDate);
                }
            };



}
