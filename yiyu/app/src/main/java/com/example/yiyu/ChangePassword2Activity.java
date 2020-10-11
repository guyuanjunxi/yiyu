package com.example.yiyu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChangePassword2Activity extends AppCompatActivity implements View.OnClickListener{

    EditText password1;
    EditText password2;
    Button submit;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password2);

        password1=this.findViewById(R.id.password1);
        password2=this.findViewById(R.id.password2);
        submit=this.findViewById(R.id.submit);

        name = getIntent().getStringExtra("name");
        submit.setOnClickListener((View.OnClickListener) this);
    }

    public boolean check()  {
        boolean isok =false;
        String psw=password1.getText().toString();
        if(psw.isEmpty()){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_LONG).show();
            return isok;
        }
        if(psw.length()>16){
            Toast.makeText(this,"密码不能超过16位",Toast.LENGTH_LONG).show();
            return isok;
        }

        String psw2=password2.getText().toString();
        if(!psw2.equals(psw)){
            Toast.makeText(this,"两次密码不一致",Toast.LENGTH_LONG).show();
            return isok;
        }else{
            isok=true;
            return isok;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                final String password = password1.getText().toString();
                //Toast.makeText(this,name+password,Toast.LENGTH_LONG).show();
                if (check()) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                String path = "http://132.232.81.77:8080/ForAndroid/changePasswordServlet?name=" + name + "&password=" + password;
                                URL url = new URL(path);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.connect();
                                int responseCode = connection.getResponseCode();
                                if (responseCode == 200) {
                                    InputStream is = connection.getInputStream();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    byte[] buffer = new byte[1024];
                                    int len = -1;
                                    while ((len = is.read(buffer)) != -1) {
                                        baos.write(buffer, 0, len);
                                    }
                                    final String result = baos.toString();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (result.equals("1")) {
                                                Toast.makeText(ChangePassword2Activity.this, "密码重置成功", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(ChangePassword2Activity.this, Login2Activity.class));
                                            } else {
                                                Toast.makeText(ChangePassword2Activity.this, "密码重置失败", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                break;
        }
    }
}
