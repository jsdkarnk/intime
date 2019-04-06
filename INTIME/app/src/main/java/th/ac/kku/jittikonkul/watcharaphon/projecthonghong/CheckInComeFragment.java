package th.ac.kku.jittikonkul.watcharaphon.projecthonghong;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckInComeFragment extends Fragment{
    ImageButton chk;
    TextView date,time;
    View v ;


    public CheckInComeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_check_in_come, container, false);
        chk = v.findViewById(R.id.imageBT);
        date = v.findViewById(R.id.day);
        time = v.findViewById(R.id.time);
        // Inflate the layout for this fragment
        chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("size0817", "[jaaaa");
                chk.setImageResource(R.drawable.checkedd);
                date.setText("Next class : Wedneds 25 September 2018");
                time.setText("10:30 AM");
            }
        });
        return v;

    }




}
