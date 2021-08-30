package com.example.geekingout.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.example.geekingout.R;
import com.example.geekingout.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Register extends AppCompatActivity {
    EditText registerName,registerEmail,registerPassword,registerAbout,registerAddress,registerProffassion;
    Button registerRegisterButton;
    FirebaseAuth fauth;
    ImageView registerImage;
    Uri resultUri;
    MultiAutoCompleteTextView registerIntrestsList;
    public static final String[] intrestsList={"AI","Comics","Star wars"};
    String [] chosenIntrest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fauth= FirebaseAuth.getInstance();

        registerName= findViewById(R.id.registerName);
        registerEmail= findViewById(R.id.registerEmail);
        registerPassword= findViewById(R.id.registerPassword);
        registerAbout=findViewById(R.id.registerAbout);
       // registerAddress=findViewById(R.id.registerAddress);
        registerProffassion=findViewById(R.id.registerProffession);

        //intrests
        registerIntrestsList=findViewById(R.id.registerIntrestsList);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,intrestsList);
        registerIntrestsList.setAdapter(adapter);
        registerIntrestsList.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        chosenIntrest=registerIntrestsList.getText().toString().split("\\s*,\\s*");


        //b++tons

        //image button
        registerImage=findViewById(R.id.registerImage);
        registerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageAcrivity();
            }
        });

        //current location button


        //register buttons

//        if(fauth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(),MainActivity.class));
//            finish();
//        }

        registerRegisterButton = findViewById(R.id.registerRegisterButton);
        registerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=registerEmail.getText().toString().trim();
                String name=registerName.getText().toString().trim();
                String password=registerPassword.getText().toString().trim();
                String proffassion=registerProffassion.getText().toString().trim();
                String about= registerAbout.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    registerEmail.setError("Email is required");
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    registerEmail.setError("Email is not valid");
                    return;
                }
                if(TextUtils.isEmpty(name)){
                    registerName.setError("Name is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    registerPassword.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    registerPassword.setError("Password must be at least 6 characters");
                    return;
                }

                fauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name,chosenIntrest,about,proffassion,resultUri);
                            FirebaseUser currentUser= fauth.getCurrentUser();
//                            currentUser.sendEmailVerification();
                            FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isCanceled()){
                                        Toast.makeText(Register.this,"NO!",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            Toast.makeText(Register.this,"Check your email to verify your account",Toast.LENGTH_LONG).show();
                            Intent nextActivity=new Intent(getApplicationContext(),Login.class);
                            nextActivity.putExtra("userData", user);
                            startActivity(nextActivity);
                        }else{
                            Toast.makeText(Register.this,"Error "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


    }
    private void cropImageAcrivity(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                .start(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Picasso.get().load(resultUri).resize(120,120).into(registerImage);
                //registerImage.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}