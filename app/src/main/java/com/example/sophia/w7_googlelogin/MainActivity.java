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
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private SignInButton btnlogin;
    private Button btnsignin;
    private EditText edtuseremail;
    private EditText edtpwd;

    private static final int REQ_CODE = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    private String cur_name;
    private String cur_email;
    private String cur_img_url = "";

    DatabaseReference databaseUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnlogin = (SignInButton)findViewById(R.id.btngoogle);
        btnsignin = (Button)findViewById(R.id.btnsignin);
        edtuseremail = (EditText)findViewById(R.id.edtuseremail);
        edtpwd = (EditText)findViewById(R.id.edtpwd);

        btnlogin.setOnClickListener(this);//Google
        btnsignin.setOnClickListener(this);//treasure hunter

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by signInOptions.
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);
        databaseUser = FirebaseDatabase.getInstance().getReference("User");
        //databaseUser = FirebaseDatabase.getInstance().getReferenceFromUrl(https://eastern-lattice-235402.firebaseio.com/User);
    }

    @Override
    public void onStart(){
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if(account != null){
//
//            cur_name = account.getDisplayName();
//            cur_email = account.getEmail();
//            if(account.getPhotoUrl() == null)
//                cur_img_url = "default";
//            else {
//                cur_img_url = account.getPhotoUrl().toString();
//            }
//            updateUI(true);
//        }
//        else
//            updateUI(false);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btngoogle:
                System.out.println("login button clicked!");
                GoogleSignIn();
                break;
            case R.id.btnsignin:
                UsersignIn();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void UsersignIn(){
        final String useremail = edtuseremail.getText().toString();
        final String pwd = edtpwd.getText().toString();
        if(useremail.isEmpty() || pwd.isEmpty())
            Toast.makeText(this, "User doesn't exist or wrong password.", Toast.LENGTH_SHORT).show();
        else {
            //search in the database
            String search = useremail + "_" + pwd;
            databaseUser.orderByChild("email_pwd").equalTo(search)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                cur_email = useremail;
                                cur_name = "";
                                Intent i = new Intent(getApplicationContext(), Main2Activity.class );
                                i.putExtra("name", cur_name);
                                i.putExtra("email", cur_email);
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void GoogleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQ_CODE);
        System.out.println("func signIn!");
    }

    private void handleResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            final String name = account.getDisplayName();
            final String email = account.getEmail();
            cur_name = name;
            cur_email = email;
            if(account.getPhotoUrl() == null)
                cur_img_url = "default";
            else {
                cur_img_url = account.getPhotoUrl().toString();
                //Glide.with(this).load(img_url).into(imdemo);
            }
            // Signed in successfully, show authenticated UI.
            updateUI(true);
            //if yes
            databaseUser.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                System.out.println("user already exist");
                            } else {
                                //if no create user account for this user
                                String userId = databaseUser.push().getKey();
                                User user = new User(userId, name, email, "",email+"_", "", cur_img_url);
                                databaseUser.child(userId).setValue(user);
                                System.out.println("new user added");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            System.out.println("handleResult finished");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            updateUI(false);
        }
    }

    private void updateUI(boolean isLogIn){
        if(isLogIn){
            Intent i = new Intent(getApplicationContext(), Main2Activity.class );
            i.putExtra("name", cur_name);
            i.putExtra("email", cur_email);
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if(requestCode == REQ_CODE){
            System.out.println("requestCode == REQ_CODE");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleResult(task);
        }
    }
}
