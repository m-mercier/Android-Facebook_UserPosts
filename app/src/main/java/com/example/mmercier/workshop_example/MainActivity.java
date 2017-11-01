package com.example.mmercier.workshop_example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG =  "MainActivity";
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "user_birthday","user_posts");

        // Calback Registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Login success");
                AccessToken accessToken = loginResult.getAccessToken();
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
                get_profile(accessToken);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Login error");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void get_profile(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);

                        try {
                            String userID = object.getString("id");
                            profileIntent.putExtra("id", userID);

                            String firstname = "";
                            String lastname = "";
                            String email = "";
                            String birthday = "";
                            String gender = "";

                            if (object.has("first_name")) {
                                firstname = object.getString("first_name");
                            }
                            if (object.has("last_name")) {
                                lastname = object.getString("last_name");
                            }
                            if (object.has("email")){
                                email = object.getString("email");
                            }
                            if (object.has("birthday")) {
                                birthday = object.getString("birthday");
                            }
                            if (object.has("gender")) {
                                gender = object.getString("gender");
                            }

                            profileIntent.putExtra("name", firstname);
                            profileIntent.putExtra("lastname", lastname);
                            profileIntent.putExtra("email", email);
                            profileIntent.putExtra("birthday", birthday);
                            profileIntent.putExtra("gender", gender);


                            startActivity(profileIntent);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, birthday, gender");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
