package com.example.jnthings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

    Button btnLogin;
    TextView txtView;
    //back task;
    phpDown task;

    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = this;

        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("Login","CallMainActivity");

                EditText et_id = (EditText)findViewById(R.id.et_id);
                EditText et_pw = (EditText)findViewById(R.id.et_pw);

                String id = et_id.getText().toString();
                String pw = et_pw.getText().toString();

                task = new phpDown();
                String url = "http://211.189.20.17:8080/mysqlTest.php?"
                        + "id="+id+"&pw="+pw;

                task.execute(url);


            }
        });

    }

private class phpDown extends AsyncTask<String, Integer,String>{
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try{
                      // 연결 url 설정
                      URL url = new URL(urls[0]);
                      // 커넥션 객체 생성
                      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                      // 연결되었으면.
                      if(conn != null){

                         conn.setConnectTimeout(10000);
                         conn.setUseCaches(false);
                         // 연결되었음 코드가 리턴되면.
                         if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                             Log.i("", "Http Connection!!!!!!!.");
                             BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            for(;;){
                                // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                                String line = br.readLine();

                                if(line == null) break;
                                // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                                jsonHtml.append(line + "\n");
                             }
                          br.close();
                       }
                        conn.disconnect();
                     }
                   } catch(Exception ex){
                      ex.printStackTrace();
                   }
                   return jsonHtml.toString();

        }

        protected void onPostExecute(String str){
            txtView.setText(str);

            int cnt = 0;
            JSONObject root;
            JSONObject user = null;

            try{

                root = new JSONObject(str);
                cnt = root.getInt("num_results");
                JSONArray ja = root.getJSONArray("results");
                user = ja.getJSONObject(0);

            }catch(JSONException e){
                e.printStackTrace();
            }
            if(cnt==1){
                Intent intentMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                try {
                    intentMainActivity.putExtra("uid", user.getInt("uid"));
                    intentMainActivity.putExtra("user_name", user.getString("user_name"));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                startActivity(intentMainActivity);
            }
            else{
                Toast.makeText(activity, "login fail!", Toast.LENGTH_SHORT).show();
            }
        }

    }




}
