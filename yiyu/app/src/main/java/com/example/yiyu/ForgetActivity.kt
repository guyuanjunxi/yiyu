package com.example.yiyu

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mob.MobSDK
import kotlinx.android.synthetic.main.activity_forget.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern



@Suppress("DEPRECATION")
class ForgetActivity : AppCompatActivity() ,View.OnClickListener{

    lateinit var telenum:EditText
    lateinit var yzm:EditText
    lateinit var getyzm:Button
    lateinit var submit:Button
    lateinit var tt:TimerTask
    lateinit var tm:Timer
    var TIME=60
    var country="86"
    var code_again=1

    lateinit var name:String
    lateinit var phone:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)

        telenum=findViewById(R.id.phone)
        yzm=findViewById(R.id.yzm)
        getyzm=findViewById(R.id.getyzm)
        submit=findViewById(R.id.submit)
        getyzm.setOnClickListener(this as View.OnClickListener)
        submit.setOnClickListener(this as View.OnClickListener)

        name = getIntent().getStringExtra("name").toString()
        MobSDK.init(this)//注册自己的
        SMSSDK.registerEventHandler(eh) //注册短信回调（记得销毁，避免泄露内存）
    }

    fun forgetpsw(name: String, phone: String) {
        //请求地址
        val url = "http://132.232.81.77:8080/server/ForgetPassword"
        val tag = "忘记密码"
        //取得请求队列
        val requestQueue = Volley.newRequestQueue(applicationContext)
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag)
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        val res = false

        val request = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response).get("params") as JSONObject
                    val result = jsonObject.getString("Result")
                    if (result == "手机不匹配") {
                        Toast.makeText(this, "手机号与用户预留不一致", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "手机号有效", Toast.LENGTH_SHORT).show()
                        val intent =
                            Intent(this, ChangePasswordActivity::class.java)
                        intent.putExtra("name", name)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                    Log.e("TAG", e.message, e)
                }
            }, Response.ErrorListener { error ->
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.e("TAG", error.message, error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["name"] = name
                params["phone"] = phone
                return params
            }
        }

        //设置Tag标签
        request.setTag(tag)

        //将请求添加到队列中
        requestQueue.add(request)
    }

    internal var hd: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == code_again) {
                getyzm.setEnabled(true)
                submit.setEnabled(true)
                tm.cancel()//取消任务
                tt.cancel()//取消任务
                TIME = 60//时间重置
                getyzm.setText("再次获取")
            } else {
                getyzm.setText(TIME.toString() + "秒后可再次获取")
            }
        }
    }




    fun toast(str:String) {
        runOnUiThread {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        }
    }


    //回调
    var eh: EventHandler = object : EventHandler() {
        override fun afterEvent(event: Int, result: Int, data: Any?) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    toast("验证码正确")
                    forgetpsw(name, phone)

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {       //获取验证码成功

                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//如果你调用了获取国家区号类表会在这里回调
                    //返回支持发送验证码的国家列表
                }
            } else {//错误等在这里（包括验证失败）
                toast("验证码错误")
            }
        }
    }

    //弹窗确认下发
    fun alterWarning() {
        //构造器
        var builder:AlertDialog.Builder= AlertDialog.Builder(this);
        builder.setTitle("提示") //设置标题
        builder.setMessage(phone + " 将会收到验证码") //设置内容
//        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("好的" ){ dialog,which ->
            //设置确定按钮
                dialog.dismiss(); //关闭dialog
                //通过sdk发送短信验证（请求获取短信验证码，在监听（eh）中返回）
                SMSSDK.getVerificationCode(country, phone)
                //做倒计时操作
                Toast.makeText(this, "已发送", Toast.LENGTH_SHORT).show()
                getyzm.setEnabled(false)
                submit.setEnabled(true)
                tm = Timer()
                tt = object :TimerTask(){

                    override fun run() {
                        hd.sendEmptyMessage(TIME--)
                    }
                }
                tm.schedule(tt,0,1000)
        }
        builder.setNegativeButton("取消"){dialog,which->
            //设置取消按钮
            //fun onClick( dialog:DialogInterface, which:Int) {
                dialog.dismiss()
                Toast.makeText(this, "已取消" , Toast.LENGTH_SHORT).show()
           // }
        }
        //参数都设置完成了，创建并显示出来
        builder.create().show()
    }


    override fun onClick(v:View ) {

        when (v.getId()) {
            R.id.getyzm ->{
            phone = telenum.getText().toString().trim().replace("/s","")
            if (!TextUtils.isEmpty(phone)) {
                //定义需要匹配的正则表达式的规则
                var REGEX_MOBILE_SIMPLE:String =  "[1][3578]\\d{9}";
                //把正则表达式的规则编译成模板
                var pattern:Pattern= Pattern.compile(REGEX_MOBILE_SIMPLE);
                //把需要匹配的字符给模板匹配，获得匹配器
                var matcher:Matcher = pattern.matcher(phone);
                // 通过匹配器查找是否有该字符，不可重复调用重复调用matcher.find()
                if (matcher.find()) {//匹配手机号是否存在
                    alterWarning();

                } else {
                    Toast.makeText(this, "手机号码错误!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "请输入手机号码!", Toast.LENGTH_SHORT).show()
            }
            }


            R.id.submit ->{
            //获得用户输入的验证码
            var code:String= yzm.getText().toString().replace("/s","")
            if (!TextUtils.isEmpty(code)) {//判断验证码是否为空
                //验证
                SMSSDK.submitVerificationCode( country,  phone,  code)
            }else{//如果用户输入的内容为空，提醒用户
                Toast.makeText(this, "请输入验证码!", Toast.LENGTH_SHORT).show()
            }
          }
        }
    }

    //销毁短信注册
    override fun onDestroy() {
        super.onDestroy();
        // 注销回调接口registerEventHandler必须和unregisterEventHandler配套使用，否则可能造成内存泄漏。
        SMSSDK.unregisterEventHandler(eh);
    }
}
