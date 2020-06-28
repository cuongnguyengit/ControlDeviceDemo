package gad.hiai.chat.hiaichat;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DevicesArrayAdapter extends ArrayAdapter<ListModel>    implements View.OnClickListener {
    private Context context;
    private ArrayList data;
    private ArrayList<ListModel> listModels;
    ListModel tempValues=null;

    public DevicesArrayAdapter(Context context,  int resource, ArrayList<ListModel> listModels) {
        super(context, resource, listModels);
        this.data = listModels;
        this.context = context;
        this.listModels = listModels;
    }

    private static class ViewHolder {
        TextView txtdes;
        TextView txtstt;
    }

    public void postRequest(String postUrl, RequestBody postBody) {
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
                Log.d("FAIL", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                String loginResponseString = response.body().string().trim();
                Log.d("OK", loginResponseString);
            }
        });
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListModel listModel = getItem(position);
        final ListModel model = this.listModels.get(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
            viewHolder.txtdes = (TextView) convertView.findViewById(R.id.txtDescriptor);
            viewHolder.txtstt = (TextView) convertView.findViewById(R.id.txtStatus);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (listModel != null) {
            Button btnOnOff = (Button) convertView.findViewById(R.id.btnOnOff);
            btnOnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = model.getDeviceName();
                    String status = model.getStatusDevice();
                    String command = "";
                    Toast.makeText(context, name + " is turning ON/ OFF!", Toast.LENGTH_LONG).show();
                    if (status.equals("1")){
                        command += "tat ";
                    } else {
                        command += "bat ";
                    }
                    command += name;
                    JSONObject updateForm = new JSONObject();
                    try {
                        updateForm.put("password", MainActivity.password);
                        updateForm.put("username", MainActivity.username);
                        updateForm.put("command", command);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), updateForm.toString());
                    postRequest(MainActivity.postUrlUpdate, body);
                }
            });
        }
        if(data.size()<=0)
        {
            viewHolder.txtdes.setText("No Data");
        }
        else
        {
            viewHolder.txtdes.setText(listModel.getDeviceName());
            if (listModel.getStatusDevice().trim().equals("1")) {
                viewHolder.txtstt.setText("ON");
            } else {
                viewHolder.txtstt.setText("OFF");
            }

        }
        return convertView;
    }

    @Override
    public void onClick(View view) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }
}