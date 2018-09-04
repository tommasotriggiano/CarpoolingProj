package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class UserNotRegister extends Fragment {
    private ImageView img1,img2,img4,img5,img6;
    private Button registrati;

    private OpenProfileListener openProfileListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.HomePage);
        View view= inflater.inflate(R.layout.user_not_register_layout, container,false);
        img1= (ImageView) view.findViewById(R.id.img1);
        img2= (ImageView) view.findViewById(R.id.img2);
        img4= (ImageView) view.findViewById(R.id.img4);
        img5= (ImageView) view.findViewById(R.id.img5);
        img6= (ImageView) view.findViewById(R.id.img6);
        registrati=(Button) view.findViewById(R.id.btnReg);
        Animation fade= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_imageview);
        Animation fade2=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_imageview) ;
        fade2.setStartOffset(700);
        img1.startAnimation(fade);
        img2.startAnimation(fade2);
        img4.startAnimation(fade);
        img5.startAnimation(fade2);
        img6.startAnimation(fade);
        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileListener.openProfile();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OpenProfileListener) {
            openProfileListener = (OpenProfileListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OpenProfileListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        openProfileListener = null;
    }

    public interface OpenProfileListener {
        void openProfile();
    }
}
