package com.AGDevelopment.uni;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFrag extends Fragment {

    private EditText signInMail, signInPass;
    private Button signIn;

    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    public SignInFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(getActivity());

        signInMail = (EditText) view.findViewById(R.id.log_in__email_edittext);
        signInPass = (EditText) view.findViewById(R.id.log_in_passowrd_edittext);
        signIn = (Button) view.findViewById(R.id.log_in_btn);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_email = signInMail.getText().toString();
                String user_pass = signInPass.getText().toString();

                if (!TextUtils.isEmpty(user_email) || !TextUtils.isEmpty(user_pass)){

                    dialog.setMessage("Logging");
                    dialog.show();
                    logInUser(user_email, user_pass);
                }
            }
        });


        return view;
    }

    private void logInUser(String user_email, String user_pass) {

        mAuth.signInWithEmailAndPassword(user_email, user_pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                dialog.dismiss();

                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.hide();
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
