package com.example.mmercier.workshop_example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("lastname");
        String email = intent.getStringExtra("email");
        String birthday = intent.getStringExtra("birthday");
        String gender = intent.getStringExtra("gender");

        TextView nameView = (TextView) findViewById(R.id.name_surname);
        TextView emailView = (TextView) findViewById(R.id.text_email);
        TextView birthdayView = (TextView) findViewById(R.id.text_birthday);
        TextView genderView = (TextView) findViewById(R.id.text_gender);

        nameView.setText(" " + name + " " + surname);
        emailView.setText(email);
        birthdayView.setText(birthday);
        genderView.setText(gender);

        ProfilePictureView profilePicture = (ProfilePictureView) findViewById(R.id.profilePicture);
        profilePicture.setProfileId(userID);

        Button logout = (Button)findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        Button posts = (Button) findViewById(R.id.posts_button);
        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPosts();
            }
        });
    }

    private void logout() {
        LoginManager.getInstance().logOut();
        Intent login = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(login);
        finish();
    }

    private void getPosts() {
        GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/posts", null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                String posts = "";
                try {
                    JSONObject responseJSON = response.getJSONObject();
                    JSONArray array = responseJSON.getJSONArray("data");
                    int size = array.length();

                    for (int i = 0; i < size; i++) {
                        JSONObject obj = array.getJSONObject(i);
                        if (obj.has("story")) {
                            posts += "Story: " + obj.get("story");
                        }
                        if (obj.has("message")) {
                            posts += "\nMessage: " + obj.get("message");
                        }
                        if (obj.has("created_time")) {
                            posts += "\nDate: " + obj.get("created_time");
                        }
                        posts += "\n\n";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent postsIntent = new Intent(ProfileActivity.this, PostsActivity.class);
                postsIntent.putExtra("posts", posts);
                //postsIntent.putExtra("posts", response.toString());
                startActivity(postsIntent);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "message, story, created_time");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
