package com.example.domenbrunek.emp_projekt;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;

import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    public static String mail;// = "tester@gmail.com";
    public static String password;// = "tester";
    public static String tokenUrl;
    public static String idUrl = "https://planitapp.azurewebsites.net/api/UsersApi/GetUserId?username=" + mail ;
    public static String establishmentsUrl;
    private RequestQueue requestQueue;
    private String token;
    private String userId;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        intent = new Intent(this, DisplayMessageActivity.class);
    }


    public void doSomething(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        mail = editText.getText().toString();
        password = editText2.getText().toString();
        tokenUrl = "https://planitapp.azurewebsites.net/api/token/getTokenAndroid?username=" + mail + "&password=" + password;
        try {
            getToken();
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
    }


    public void getToken() throws Exception {
        StringRequest request = new StringRequest(Request.Method.GET, tokenUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        token = response;
                        if (response.length() < 300) {
                            Bundle bundle = new Bundle();
                            bundle.putString("token", token);
                            bundle.putString("mail", mail);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            error();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response error", error.getMessage());
            }
        });

        requestQueue.add(request);
    }

    public void error() {
        ConstraintLayout layout = findViewById(R.id.constraintLayout);

        TextView textView = new TextView(this);
        String string = "Wrong username/password";
        textView.setText(string);
        textView.setTextColor(Color.RED);
        layout.addView(textView);
    }


}
