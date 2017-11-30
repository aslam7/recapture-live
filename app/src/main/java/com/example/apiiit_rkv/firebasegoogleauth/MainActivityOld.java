package com.example.apiiit_rkv.firebasegoogleauth;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apiiit_rkv.R;
import com.example.apiiit_rkv.VideoPlayback.app.VideoPlayback.VideoPlayback;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


//import com.google.android.gms.appindexing.AppIndex;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
public class MainActivityOld extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{
  private static final int PERMISSION_CALLBACK_CONSTANT = 100;
  private static final int REQUEST_PERMISSION_SETTING = 101;
  String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION};
  private SharedPreferences permissionStatus;
  private boolean sentToSettings = false;

  //Google SignIn
  private static final String TAG = "GoogleActivity";
  private static final int RC_SIGN_IN = 9001;

  // [START declare_auth]
  private FirebaseAuth mAuth;
  // [END declare_auth]

  private GoogleApiClient mGoogleApiClient;
  private TextView mStatusTextView;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_old);

    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F0F8FF")));
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setIcon(R.drawable.recapturew);

    permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
    if (ActivityCompat.checkSelfPermission(MainActivityOld.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(MainActivityOld.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(MainActivityOld.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivityOld.this, permissionsRequired[0])
              || ActivityCompat.shouldShowRequestPermissionRationale(MainActivityOld.this, permissionsRequired[1])
              || ActivityCompat.shouldShowRequestPermissionRationale(MainActivityOld.this, permissionsRequired[2])) {
        //Show Information about why you need the permission
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityOld.this);
        builder.setTitle("Need Multiple Permissions");
        builder.setMessage("This app needs Camera and Location permissions.");
        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            ActivityCompat.requestPermissions(MainActivityOld.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
          }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });
        builder.show();
      } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
        //Previously Permission Request was cancelled with 'Dont Ask Again',
        // Redirect to Settings after showing Information about why you need the permission
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityOld.this);
        builder.setTitle("Need Multiple Permissions");
        builder.setMessage("This app needs Camera and Location permissions.");
        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            sentToSettings = true;
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            Toast.makeText(getBaseContext(), "Go to Permissions to Grant  Camera and Location", Toast.LENGTH_LONG).show();
          }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });
        builder.show();
      } else {
        //just request the permission
        ActivityCompat.requestPermissions(MainActivityOld.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
      }

      //   txtPermissions.setText("Permissions Required");

      SharedPreferences.Editor editor = permissionStatus.edit();
      editor.putBoolean(permissionsRequired[0], true);
      editor.commit();
    } else {
      //You already have the permission, just go ahead.
      proceedAfterPermission();
    }
    mStatusTextView = (TextView) findViewById(R.id.status);
    findViewById(R.id.sign_in_button).setOnClickListener(this);
    // Button listeners
    findViewById(R.id.sign_out_button).setOnClickListener(this);
    findViewById(R.id.upload_button).setOnClickListener(this);
    findViewById(R.id.scan_button).setOnClickListener(this);
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();
    mAuth = FirebaseAuth.getInstance();


  }
  private void signIn() {
    showProgressDialog();
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        // Google Sign In was successful, authenticate with Firebase
        GoogleSignInAccount account = result.getSignInAccount();
        firebaseAuthWithGoogle(account);

      } else {
        // Google Sign In failed, update UI appropriately
        // ...
        updateUI(null);
      }
    }
  }
  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  // Sign in success, update UI with the signed-in user's information
                  Log.d(TAG, "signInWithCredential:success");
                  FirebaseUser user = mAuth.getCurrentUser();
                  updateUI(user);
                } else {
                  // If sign in fails, display a message to the user.
                  Log.w(TAG, "signInWithCredential:failure", task.getException());
                  Toast.makeText(MainActivityOld.this, "Authentication failed.",
                          Toast.LENGTH_SHORT).show();
                  updateUI(null);
                }

                // ...
              }
            });
  }
  @Override
  public void onStart() {
    super.onStart();
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser currentUser = mAuth.getCurrentUser();
    updateUI(currentUser);
  }
  private void updateUI(FirebaseUser user) {
    hideProgressDialog();
    if (user != null) {
      mStatusTextView.setText(getString(R.string.google_status_fmt, user.getDisplayName()));
//      mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

      findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//      findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
//      Toast.makeText(getApplicationContext(),"Signed In!!",Toast.LENGTH_LONG).show();
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
    } else {
      mStatusTextView.setText(R.string.signed_out);
//      mDetailTextView.setText(null);

      findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
    }
  }
  private void proceedAfterPermission() {

    //  txtPermissions.setText("We've got all permissions");
    Toast.makeText(getBaseContext(), "Access Granted!", Toast.LENGTH_SHORT).show();
//        startActivity(new Intent(MainActivityOld.this, Login.class));

  }
  public void scanHandler(View view) {
    startActivity(new Intent(MainActivityOld.this,VideoPlayback.class));
  }

  public void uploadHandler(View view) {
    startActivity(new Intent(MainActivityOld.this,UploadFirst.class));
  }

  @Override
  public void onClick(View v) {
    int i = v.getId();
    if (i == R.id.sign_in_button) {
      signIn();
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.upload_button) {
      startActivity(new Intent(MainActivityOld.this,UploadFirst.class));
        }
    else if (i == R.id.scan_button) {
      startActivity(new Intent(MainActivityOld.this,VideoPlayback.class));    }
  }
  private void signOut() {
    showProgressDialog();
    // Firebase sign out
    mAuth.signOut();

    // Google sign out
    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
            new ResultCallback<Status>() {
              @Override
              public void onResult(@NonNull Status status) {
                updateUI(null);
              }
            });
  }
  private void revokeAccess() {
    // Firebase sign out
    mAuth.signOut();

    // Google revoke access
    Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
            new ResultCallback<Status>() {
              @Override
              public void onResult(@NonNull Status status) {
                updateUI(null);
              }
            });
  }
  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.d(TAG, "onConnectionFailed:" + connectionResult);
    Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
  }
}