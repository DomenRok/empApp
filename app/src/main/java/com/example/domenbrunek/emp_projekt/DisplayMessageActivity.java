package com.example.domenbrunek.emp_projekt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayMessageActivity extends AppCompatActivity  {
    String idUrl;
    String userId;
    String establishmentsUrl;
    String token;
    RequestQueue requestQueue;
    ArrayList<Lokal> lokali;
    Intent newIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        init();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        token = bundle.getString("token");
        idUrl = "https://planitapp.azurewebsites.net/api/UsersApi/GetUserId?username=" + bundle.getString("mail");
        requestQueue = Volley.newRequestQueue(this);
        getIdRequestWithHeader();
        newIntent = new Intent(this, EmployeePerEstablishment.class);

    }

    public void init() {
        lokali = new ArrayList<>();
    }


    public void izpis() {
        LinearLayout layout = findViewById(R.id.linearLayout);

        for (Lokal lokal: lokali) {
            final TextView textView = new TextView(this);
            String string = lokal.getName() + " " + lokal.getDesc() + " " + lokal.getLocation();
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(string);
            textView.setTag(lokal.getId());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("text", textView.getText().toString());
                    Integer establishmentId = (int) textView.getTag();
                    Bundle bundle = new Bundle();
                    bundle.putString("token", token);
                    bundle.putInt("establishmentId", establishmentId);
                    newIntent.putExtras(bundle);
                    startActivity(newIntent);
                }
            });

            layout.addView(textView);
        }

    }


    public void getIdRequestWithHeader() {
        StringRequest getRequest = new StringRequest(Request.Method.GET, idUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        userId = response.substring(1, response.length()-1);
                       establishmentsUrl = "https://planitapp.azurewebsites.net/api/EstablishmentsApi/GetEstablishmentsPerUser/" + userId;
                        getEstablishmentsWithHeaders();
                        Log.d("userid", userId);
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





    public void getEstablishmentsWithHeaders() {
        final JsonArrayRequest request = new JsonArrayRequest(
                establishmentsUrl,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i=0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Integer id = obj.getInt("Id");
                                String Name = obj.getString("Name");
                                String Desc = obj.getString("Description");
                                String Location = obj.getString("Location");
                                Lokal lokal = new Lokal(id, Name, Desc, Location);
                                lokali.add(lokal);


                            } catch (JSONException e) {
                                Log.d("JSON READING ERROR", e.getMessage());
                            }
                        }
                        Log.d("response", String.valueOf(response.length()));
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
        requestQueue.add(request);

    }

}

class Lokal {
    private String name;
    private String desc;
    private String location;
    private Integer id;

    public Lokal(int id,  String name, String desc, String location) {
        this.setId(id);
        this.setName(name);
        this.setDesc(desc);
        this.setLocation(location);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
