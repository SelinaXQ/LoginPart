package com.example.sophia.w7_googlelogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private LinearLayout demo_section;
    private Button btnlogout;
    private TextView tvname, tvemail;
    private ImageView imdemo;
    private static final int REQ_CODE = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    private String cur_name;
    private String cur_email;
    private String img_url = "";

    private int loginType = 0;//0: google, 1: treasure hunter, 2:ins
    DatabaseReference databaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        demo_section = (LinearLayout)findViewById(R.id.demo_section);
        btnlogout = (Button)findViewById(R.id.btnlogout);
        tvname = (TextView)findViewById(R.id.tvname);
        tvemail = (TextView)findViewById(R.id.tvemail);
        imdemo = (ImageView)findViewById(R.id.imdemo);

        btnlogout.setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by signInOptions.
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);
        databaseUser = FirebaseDatabase.getInstance().getReference("User");
        //databaseUser = FirebaseDatabase.getInstance().getReferenceFromUrl(https://eastern-lattice-235402.firebaseio.com/User);
        Bundle bundle=getIntent().getExtras();
        cur_name = bundle.getString("name");
        cur_email = bundle.getString("email");
        //user login (not third party)
        if(cur_name.isEmpty()){
            loginType = 1;
        } else if(cur_email.isEmpty()){
            loginType = 2;
        }
        //profile from database
        databaseUser.orderByChild("email").equalTo(cur_email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            Log.d("User key", child.getKey());
                            img_url = child.child("profileUrl").getValue(String.class);
                            System.out.println("Stop!");
                            if(img_url == "default")
                                imdemo.setImageResource(R.drawable.user);
                            else {
                                Glide.with(getApplicationContext()).load(img_url).into(imdemo);
                            }
                            if(loginType == 1){
                                cur_name = child.child("userName").getValue(String.class);
                                tvname.setText(cur_name);
                                tvemail.setText(cur_email);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        tvname.setText(cur_name);
        tvemail.setText(cur_email);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnlogout:
                signOut();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signOut(){
        if(loginType == 0){
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(false);
                        }
                    });
        }else if(loginType == 1){
            updateUI(false);
        }

    }

    private void updateUI(boolean isLogIn){
        if(isLogIn){
            Toast.makeText(this, "Unsuccessful Logout", Toast.LENGTH_SHORT).show();
        }else {
            Intent i = new Intent(getApplicationContext(), MainActivity.class );
            startActivity(i);
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        }
    }

}
