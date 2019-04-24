package com.AGDevelopment.uni;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainAuthFrag extends Fragment {

    private Button signUp;
    private TextView logIn;

    public MainAuthFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_auth, container, false);

        signUp = (Button) view.findViewById(R.id.jump_to_sign_up_with_email);
        logIn = (TextView) view.findViewById(R.id.jump_to_log_in);

        final NavController navController = Navigation.findNavController(getActivity(), R.id.auth_fragments_host);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_mainAuthFrag_to_signUpFrag);
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_mainAuthFrag_to_signInFrag);
            }
        });

        return view;
    }

}
