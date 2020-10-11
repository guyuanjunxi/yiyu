package com.example.yiyu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mob.MobSDK;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class Forget2Activity extends AppCompatActivity implements View.OnClickListener{

    EditText telenum;
    EditText yzm;
    Button getyzm;
    Button submit;
    TimerTask tt;
    Timer tm;
    int TIME=60;
    String country="86";
    int code_again=1;
    String name;
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget2);

        telenum=findViewById(R.id.phone);
        yzm=findViewById(R.id.yzm);
        getyzm=findViewById(R.id.getyzm);
        submit=findViewById(R.id.submit);
        getyzm.setOnClickListener((View.OnClickListener) this);
        submit.setOnClickListener((View.OnClickListener) this);

        name = getIntent().getStringExtra("name");
        MobSDK.init(this);
        SMSSDK.registerEventHandler(eh) ;//注册短信回调（记得销毁，避免泄露内存）
    }

    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Forget2Activity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void forgetpsw(final String name,final String phone){
        //调用Java后台登录接口
        new Thread() {
            @Override
            public void run() {
                try {
                    String path = "http://132.232.81.77:8080/ForAndroid/forgetPasswordServlet?name=" + name + "&phone=" + phone;
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if(responseCode == 200){
                        InputStream is = connection.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer= new byte[1024];
                        int len = -1;
                        while((len=is.read(buffer))!=-1){
                            baos.write(buffer,0,len);
                        }
                        final String result = baos.toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(result.equals("1")){
                                    Toast.makeText(Forget2Activity.this,"手机号与预留匹配",Toast.LENGTH_SHORT).show();
                                    Intent intent =new Intent(Forget2Activity.this, ChangePassword2Activity.class);
                                    intent.putExtra("name",name);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(Forget2Activity.this,"手机号与预留不匹配",Toast.LENGTH_LONG).show();
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
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.getyzm:
                phone = telenum.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    //定义需要匹配的正则表达式的规则
                    String REGEX_MOBILE_SIMPLE =  "[1][3578]\\d{9}";
                    //把正则表达式的规则编译成模板
                    Pattern pattern = Pattern.compile(REGEX_MOBILE_SIMPLE);
                    //把需要匹配的字符给模板匹配，获得匹配器
                    Matcher matcher = pattern.matcher(phone);
                    // 通过匹配器查找是否有该字符，不可重复调用重复调用matcher.find()
                    if (matcher.find()) {//匹配手机号是否存在
                        alterWarning();

                    } else {
                        toast("手机号不正确 ");
                    }
                } else {
                    toast("请输入手机号");
                }

                break;
            case R.id.submit:
                //获得用户输入的验证码
               String code = yzm.getText().toString().replaceAll("/s","");
                if (!TextUtils.isEmpty(code)) {//判断验证码是否为空
                    //验证
                    SMSSDK.submitVerificationCode( country,  phone,  code);
                }else{//如果用户输入的内容为空，提醒用户
                    toast("请输入验证码");
                }
                break;
        }

    }
    Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == code_again) {
                getyzm.setEnabled(true);
                submit.setEnabled(true);
                tm.cancel();//取消任务
                tt.cancel();//取消任务
                TIME = 60;//时间重置
                getyzm.setText("再次获取");
            }else {
                getyzm.setText(TIME + "秒后再次获取");
            }
        }
    };

    //回调
    EventHandler eh=new EventHandler(){
        @Override
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    toast("验证码正确！");
                    phone = telenum.getText().toString().trim();
                    forgetpsw(name,phone);

                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){       //获取验证码成功
                    toast("获取验证码成功");
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//如果你调用了获取国家区号类表会在这里回调
                    //返回支持发送验证码的国家列表
                }
            }else{//错误等在这里（包括验证失败）
                //错误码请参照http://wiki.mob.com/android-api-错误码参考/这里我就不再继续写了
                ((Throwable)data).printStackTrace();
                String str = data.toString();
                //toast(str);
                toast("验证码错误!");
            }
        }
    };
    //弹窗确认下发
    private void alterWarning() {
        //构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示"); //设置标题
        builder.setMessage(phone + " 将会收到验证码"); //设置内容
//        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                //通过sdk发送短信验证（请求获取短信验证码，在监听（eh）中返回）
                SMSSDK.getVerificationCode(country, phone);
                //做倒计时操作
                Toast.makeText(Forget2Activity.this, "已发送", Toast.LENGTH_SHORT).show();
                getyzm.setEnabled(false);
                submit.setEnabled(true);
                tm = new Timer();
                tt = new TimerTask() {
                    @Override
                    public void run() {
                        hd.sendEmptyMessage(TIME--);
                    }
                };
                tm.schedule(tt,0,1000);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(Forget2Activity.this, "已取消" , Toast.LENGTH_SHORT).show();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }


    //销毁短信注册
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销回调接口registerEventHandler必须和unregisterEventHandler配套使用，否则可能造成内存泄漏。
        SMSSDK.unregisterEventHandler(eh);
    }
}
