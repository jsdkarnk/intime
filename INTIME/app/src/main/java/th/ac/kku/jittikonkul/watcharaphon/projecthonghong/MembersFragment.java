package th.ac.kku.jittikonkul.watcharaphon.projecthonghong;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MembersFragment extends Fragment {

    private String class_id;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private RecyclerView recyclerView;

    public MembersFragment() {
        // Required empty public constructor
        UpdateMember();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_members, container, false);
        Bundle bundle = getArguments();
        if(bundle != null) {
            class_id = bundle.getString("class_id");
        }
        Log.d("size0812", String.valueOf(class_id));
        recyclerView = (RecyclerView) v.findViewById(R.id.mem_list_view);
        UpdateMember();
        return v;

    }

    public void UpdateMember(){
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memList.clear();
                uidList.clear();
                recyclerView.setAdapter(null);

                try {
                    collectuid((Map<String, Object>) dataSnapshot.child("Class").child(class_id).child("members").getValue());
                    for (int i = 0; i < uidList.size(); i++) {
                        String f_name = dataSnapshot.child("Users").child(uidList.get(i)).child("FirstName").getValue().toString();
                        String l_name = dataSnapshot.child("Users").child(uidList.get(i)).child("LastName").getValue().toString();
                        memList.add(f_name + "  " + l_name);
                    }
                }catch (NullPointerException e){
                    Log.e("err", "Null point DB");
                }

                Log.d("size081", String.valueOf(memList.size()));
                recyclerView.setAdapter(new RecyclerViewAdapter());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void collectuid(Map<String,Object> users) {

        int i = 0;
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            //Get user map
            Map idClass = (Map) entry.getValue();
            //Get phone field and append to list
            uidList.add((String) idClass.get("uid"));
            Log.d("4445a", String.valueOf(uidList.get(i)));
            i++;
        }
    }

    List<String> memList = new ArrayList<>();
    List<String> uidList = new ArrayList<>();

    public class RecyclerViewAdapter extends RecyclerView.Adapter<MembersFragment.ViewHoldder>{

        @Override
        public MembersFragment.ViewHoldder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_member,parent,false);

            return new MembersFragment.ViewHoldder(v);
        }

        @Override
        public void onBindViewHolder(MembersFragment.ViewHoldder holder, int position) {
            holder.member_name.setText(memList.get(position));


        }

        @Override
        public int getItemCount() {

            return memList.size();
        }
    }

    public class ViewHoldder extends RecyclerView.ViewHolder {

        TextView member_name;

        public ViewHoldder(View itemView) {
            super(itemView);
            member_name = itemView.findViewById(R.id.tv_name_member);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    String uid   = uidList.get(index);

                }
            });
        }
    }


}
