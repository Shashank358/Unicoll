package com.AGDevelopment.uni;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class SignUpFrag extends Fragment {

    private EditText signUpName, signUpMail, signUpPass;
    private Button signUp;
    private ConstraintLayout selectProfile;
    private CircleImageView userImage;

    private ProgressDialog dialog;
    private FirebaseAuth mAuth;

    private static final int PICK_IMAGE = 1;

    public SignUpFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(getActivity());

        signUpMail = (EditText) view.findViewById(R.id.sign_up_email_edittext);
        signUpName = (EditText) view.findViewById(R.id.sign_up_user_name_edittext);
        signUpPass = (EditText) view.findViewById(R.id.sign_up_password_edittext);
        signUp = (Button) view.findViewById(R.id.sign_up_btn);
        selectProfile = (ConstraintLayout) view.findViewById(R.id.sign_up_select_image_layout);
        userImage = (CircleImageView) view.findViewById(R.id.sign_up_user_profile_image);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = signUpName.getText().toString();
                String user_email = signUpMail.getText().toString();
                String user_pass = signUpPass.getText().toString();
                if (!TextUtils.isEmpty(user_email) || !TextUtils.isEmpty(user_pass)){
                    creatingAccount(user_name, user_email, user_pass);

                }else {
                    Toast.makeText(getActivity(), "fill all the credential", Toast.LENGTH_SHORT).show();
                }
            }
        });

        selectProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });


        return view;
    }

    private void creatingAccount(final String user_name, String user_email, String user_pass) {

        dialog.setMessage("Creating account");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mAuth.createUserWithEmailAndPassword(user_email, user_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                if (task.isSuccessful()){
                    final String currentUser = mAuth.getCurrentUser().getUid();

                    Map<String, Object> userMap = new HashMap();
                    userMap.put("user_id", currentUser);
                    userMap.put("user_name", user_name);

                    firestore.collection("Users").document(currentUser)
                            .set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + currentUser);
                            dialog.dismiss();

                            Intent introIntent = new Intent(getActivity(), MainActivity.class);
                            introIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(introIntent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);

                        }
                    });

                }else {
                    dialog.hide();
                    Toast.makeText(getActivity(), "Error in creating account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                dialog.setTitle("Updating Profile");
                dialog.setMessage("please wait while profile is updating");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                Uri resultUri = result.getUri();

                String image = result.getUri().toString();
                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);

                picasso.load(image).placeholder(R.drawable.default_avatar).into(userImage);


                final File thumb_filePath = new File(resultUri.getPath());

                Bitmap thumb_bitmap = new Compressor(getContext())
                        .setMaxWidth(300)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final String currentUser = mAuth.getCurrentUser().getUid();

                final StorageReference profileStorage = FirebaseStorage.getInstance().getReference()
                        .child("profile_images").child(currentUser);
                profileStorage.putBytes(thumb_byte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        profileStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();

                                Map profileMap = new HashMap();
                                profileMap.put("profile_image", downloadUrl);

                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                firestore.collection("Users").document(currentUser)
                                        .set(profileMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + currentUser);
                                        dialog.dismiss();

                                        Intent introIntent = new Intent(getActivity(), MainActivity.class);
                                        introIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(introIntent);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);

                                    }
                                });
                            }
                        });
                    }
                });
            }
        }

    }
}
