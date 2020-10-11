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
import java.util.regex.Pattern;

public class Register2Activity extends AppCompatActivity implements View.OnClickListener{

    EditText name1;
    EditText psw;
    EditText psw2;
    EditText telenum;
    Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        name1=this.findViewById(R.id.name);
        psw=this.findViewById(R.id.password);
        psw2=this.findViewById(R.id.password2);
        telenum=this.findViewById(R.id.phone);
        register=this.findViewById(R.id.buttonRegister);
        register.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonRegister:
                final String name = name1.getText().toString().trim();
                final String password = psw.getText().toString().trim();
                final String phone = telenum.getText().toString().trim();

                if(check()) {
                    //调用Java后台登录接口
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                String path = "http://132.232.81.77:8080/ForAndroid/registerServlet?name=" + name + "&password=" + password + "&phone=" + phone;
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
                                                Toast.makeText(Register2Activity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Register2Activity.this, Login2Activity.class));
                                            } else {
                                                Toast.makeText(Register2Activity.this, "注册失败,用户名已存在", Toast.LENGTH_SHORT).show();
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

    public Boolean check(){
        boolean isok=false;
        String name11=name1.getText().toString();
        if(name11.isEmpty()){
            Toast.makeText(this,"昵称不能为空",Toast.LENGTH_LONG).show();
            return isok;
        }
        if(name11.length()>10){
            Toast.makeText(this,"昵称不能超过10位",Toast.LENGTH_LONG).show();
            return isok;
        }

        String psw1=psw.getText().toString();
        if(psw1.isEmpty()){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_LONG).show();
            return isok;
        }
        if(psw1.length()>16){
            Toast.makeText(this,"密码不能超过16位",Toast.LENGTH_LONG).show();
            return isok;
        }

        String psw22=psw2.getText().toString();
        if(!psw22.equals(psw1)){
            Toast.makeText(this,"两次密码不一致",Toast.LENGTH_LONG).show();
            return isok;
        }
        String telenum1=telenum.getText().toString();
        if(telenum1.isEmpty()){
            Toast.makeText(this,"手机不能为空",Toast.LENGTH_LONG).show();
            return isok;
        }else{
            String p="[1][3578]\\d{9}";
            boolean match= Pattern.matches(p, telenum1);
            if (!match) {
                Toast.makeText(this,"请输入正确的手机号",Toast.LENGTH_LONG).show();
                return isok;
            }else{
                isok=true;
                return isok;
            }
        }
    }
}
