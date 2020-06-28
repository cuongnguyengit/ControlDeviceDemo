package gad.hiai.chat.hiaichat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View v) {
        EditText usernameView = findViewById(R.id.username);
        EditText passwordView = findViewById(R.id.password);
        EditText comfirmPassView = findViewById(R.id.password_check);

        TextView responseText = findViewById(R.id.responseTextRegister);

        String username = usernameView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();
        String password_check = comfirmPassView.getText().toString().trim();

        int checker = 1;

        if ( username.length() == 0 || password.length() == 0)
            Toast.makeText(getApplicationContext(), "Something is wrong. Please check your inputs.", Toast.LENGTH_LONG).show();
        else if (password.length() < 8)
            Toast.makeText(getApplicationContext(), "Your password is so short. Please chose another password.", Toast.LENGTH_LONG).show();
        else if (!password.equals(password_check))
            Toast.makeText(getApplicationContext(), "Your password is different from your comfirm password. Please enter your inputs again.", Toast.LENGTH_LONG).show();
        else {
            checker = 0;
            JSONObject registrationForm = new JSONObject();
            try {
                registrationForm.put("subject", "register");
                registrationForm.put("username", username);
                registrationForm.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), registrationForm.toString());

            postRequest(MainActivity.postUrl, body);
        }
        if (checker == 1)
            responseText.setText("Something is wrong");
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.d("FAIL", e.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseTextRegister);
                        responseText.setText("Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                final TextView responseTextRegister = findViewById(R.id.responseTextRegister);
                try {
                    final String responseString = response.body().string().trim();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseString.equals("success")) {
                                responseTextRegister.setText("Registration completed successfully.");
                                finish();
                            } else if (responseString.equals("username")) {
                                responseTextRegister.setText("Username already exists. Please chose another username.");
                            } else {
                                responseTextRegister.setText("Something went wrong. Please try again later.");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}