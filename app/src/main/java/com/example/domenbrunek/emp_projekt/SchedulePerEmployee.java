package com.example.domenbrunek.emp_projekt;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SchedulePerEmployee extends AppCompatActivity {
    String token;
    String url;
    int establishementId;
    String userId;
    RequestQueue requestQueue;
    ArrayList<String> dates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        init();
    }

    public void init() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        token = bundle.getString("token");
        establishementId = bundle.getInt("establishmentId");
        userId = bundle.getString("userId");
        requestQueue = Volley.newRequestQueue(this);
        url = "https://planitapp.azurewebsites.net/api/UsersApi/GetSchedulePerUser/" + userId;
        dates = new ArrayList<>();
        makeRequest();
    }


    public void makeRequest() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator<String> keys = response.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            try {
                                dates.add(response.getString(key));
                            } catch (JSONException e) {
                                Log.v("JSON ERROR", e.getMessage());
                            }
                        }
                        izpis();
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Test");
                params.put("Accept-Language", "eng");
                params.put("Authorization", "Bearer" + " " + token);
                return params;
            }
        };
        requestQueue.add(getRequest);
    }

    public void izpis() {
        LinearLayout layout = findViewById(R.id.linearLayout3);

        for (String date : dates) {
            final TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(date);
            textView.setTextSize(25);
            textView.setPadding(20,20,20,0);
            layout.addView(textView);
        }
    }
}
