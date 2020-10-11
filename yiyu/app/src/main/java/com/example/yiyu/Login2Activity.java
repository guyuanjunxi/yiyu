package com.example.yiyu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login2Activity extends AppCompatActivity implements View.OnClickListener{

    Button login;
    ToggleButton pswsee;
    EditText name1;
    EditText password1;
    TextView textRegister;
    TextView textForget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        login = this.findViewById(R.id.login);
        pswsee = this.findViewById(R.id.pswsee);
        name1 = this.findViewById(R.id.name);
        password1 = this.findViewById(R.id.password);
        textRegister = this.findViewById(R.id.newuser);
        textForget = this.findViewById(R.id.forgetpsw);

        login.setOnClickListener(this);
        textRegister.setOnClickListener(this);
        textForget.setOnClickListener(this);
        pswsee.setOnCheckedChangeListener(new ToggleButtonClick());
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                final String name = name1.getText().toString().trim();
                final String password = password1.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    //调用Java后台登录接口
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                String path = "http://132.232.81.77:8080/ForAndroid/loginServlet?name=" + name + "&password=" + password;
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
                                                Intent intent =new Intent(Login2Activity.this, MainActivity.class);
                                                Toast.makeText(Login2Activity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(Login2Activity.this, "登录失败", Toast.LENGTH_SHORT).show();
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
                    case R.id.newuser:
                        startActivity(new Intent(Login2Activity.this, Register2Activity.class));
                        break;

                    case R.id.forgetpsw:
                        final String name2 = name1.getText().toString().trim();
                        if(name2.isEmpty()){
                            Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent=new Intent(Login2Activity.this, Forget2Activity.class);
                            intent.putExtra("name",name2);
                            startActivity(intent);
                        }
                        break;

        }
    }
    //3、密码可见性按钮监听
    private class ToggleButtonClick implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            //5、判断事件源的选中状态
            if (isChecked){

                //显示密码
                //etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else {
                // 隐藏密码
                //etPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password1.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
            //6、每次显示或者关闭时，密码显示编辑的线不统一在最后，下面是为了统一
            password1.setSelection(password1.length());
        }
    }
}
