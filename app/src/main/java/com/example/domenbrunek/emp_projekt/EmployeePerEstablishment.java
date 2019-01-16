package com.example.domenbrunek.emp_projekt;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmployeePerEstablishment extends AppCompatActivity {
    String token;
    String url;
    Integer id;
    RequestQueue requestQueue;
    ArrayList<Zaposlen> zaposleni;
    Intent newIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_per_establishment);
        init();
        getEmployees();
    }

    public void init() {
        requestQueue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        token = bundle.getString("token");
        id = bundle.getInt("establishmentId");
        url = "https://planitapp.azurewebsites.net/api/EmployeesApi/GetEmployeePerEstablishment/" +  id;
        zaposleni = new ArrayList<>();
        newIntent = new Intent(this, SchedulePerEmployee.class);
    }

    public void getEmployees() {
        final JsonArrayRequest request = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i=0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                JSONObject userInfo= obj.getJSONObject("User");
                                Zaposlen zaposlen = new Zaposlen(userInfo.getString("Name"), userInfo.getString("Surname"),
                                                                userInfo.getString("Email"));
                                zaposleni.add(zaposlen);

                            } catch (JSONException e) {
                                Log.d("JSON READING ERROR", e.getMessage());
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
        requestQueue.add(request);
    }

    public void izpis() {
        LinearLayout layout = findViewById(R.id.linearLayout2);

        for (Zaposlen zaposlen: zaposleni) {
            final Button textView = new Button(this);
            String string = zaposlen.getName() + " " + zaposlen.getSurname() + " (" + zaposlen.getEmail() + ")";
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(string);
            //textView.setBackgroundColor(Color.parseColor("#FFFFFF00"));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("text", textView.getText().toString());
                    Bundle bundle = new Bundle();
                    bundle.putString("token", token);
                    newIntent.putExtras(bundle);

                     startActivity(newIntent);
                }
            });
            layout.addView(textView);
        }
    }

}


class Zaposlen {
    private String name;
    private String surname;
    private String email;

    public Zaposlen(String name, String surname, String email) {
        this.setName(name);
        this.setSurname(surname);
        this.setEmail(email);
    }


    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}