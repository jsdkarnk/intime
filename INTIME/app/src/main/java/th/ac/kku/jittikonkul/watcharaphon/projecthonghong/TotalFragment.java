package th.ac.kku.jittikonkul.watcharaphon.projecthonghong;


import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class TotalFragment extends Fragment {

    private EditText et_l,et_r;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private String class_id;


    public TotalFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_total, container, false);
        et_l = v.findViewById(R.id.et_numAllClass);
        et_r = v.findViewById(R.id.et_numMissClass);
        TextView tv_idClass = v.findViewById(R.id.tv_id_class);
        Button bt_edit = v.findViewById(R.id.bt_edit_in_class);
        Button bt_save = v.findViewById(R.id.bt_save_in_class);
        et_l.setEnabled(false);
        et_r.setEnabled(false);



        Bundle bundle = getArguments();
        if(bundle != null) {
            class_id = bundle.getString("class_id");
        }
        tv_idClass.setText("Password Class: " + class_id);
        tv_idClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(class_id);
                Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        UpdateInformation();

        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTime();
            }
        });
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_l.setEnabled(false);
                et_r.setEnabled(false);
                String allTimeStr = et_l.getText().toString();
                String missTimeStr = et_r.getText().toString();
                    mRootRef.child("Class").child(class_id).child("Information").child("AllTime").setValue(allTimeStr);
                    mRootRef.child("Class").child(class_id).child("Information").child("MissTime").setValue(missTimeStr);
                Toast.makeText(getContext(),
                        "Save Complete!", Toast.LENGTH_SHORT).show();
            }
        });
        return v;

    }

    public void editTime(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to edit?");
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                et_l.setEnabled(true);
                et_r.setEnabled(true);
                et_l.setFocusable(true);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
            }
        });
        builder.show();
    }

    public void UpdateInformation(){
        mRootRef.child("Class").child(class_id).child("Information").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (class_id != null) {
                    et_l.setText(dataSnapshot.child("AllTime").getValue(String.class));
                    et_r.setText(dataSnapshot.child("MissTime").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
