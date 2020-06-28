package gad.hiai.chat.hiaichat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Trace;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {
    public static Context mainActivityContext;
    public static String username = "";
    public static String password = "";
    public EditText editTextIp;
    public Button btnUpdateIp;
    public TextView txtResponse;

    static String postUrl = "http://192.168.43.103:5000/";
    static String getUrl = postUrl + "get-all-devices";
    static String postUrlUpdate = postUrl + "update-device";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 0);
        editTextIp = (EditText) findViewById(R.id.edit_ip);
        editTextIp.setText(postUrl);
        txtResponse = (TextView) findViewById(R.id.responseText);
        btnUpdateIp = (Button) findViewById(R.id.btnUpdateIp);
        btnUpdateIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = editTextIp.getText().toString();
                if (!string.contains("http")) {
                    string = "http://" + string;
                }
                if (!string.contains(":5000")) {
                    string += ":5000/";
                }
                postUrl = string;
                txtResponse.setText("Update Ip Server Successful");
                getUrl = postUrl + "get-all-devices";
                postUrlUpdate = postUrl + "update-device";
            }
        });
        mainActivityContext = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logout();
    }

    public void logout() {
        if (MainActivity.username.length() == 0)
            return;
        JSONObject logoutForm = new JSONObject();
        try {
            logoutForm.put("subject", "logout");
            logoutForm.put("username", MainActivity.username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), logoutForm.toString());
        postRequest(MainActivity.postUrl, body);
        MainActivity.username = "";
        MainActivity.password = "";
    }

    public void postRequest(final String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage() + postUrl);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("OK", postUrl);
                    }
                });
            }
        });
    }

    public void login(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void register(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Toast.makeText(this, "@Copyright by NGUYỄN MẠNH CƯỜNG", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.exit:
                //Khoi tao lai Activity main
                Intent intent = new Intent(getApplicationContext(), R.class);
                startActivity(intent);
                // Tao su kien ket thuc app
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startActivity(startMain);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}