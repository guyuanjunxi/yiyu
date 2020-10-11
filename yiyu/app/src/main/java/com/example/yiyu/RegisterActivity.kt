package com.example.yiyu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_forget.*
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap
import java.util.regex.Pattern

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {

    lateinit var name:EditText
    lateinit var psw:EditText
    lateinit var psw2:EditText
    lateinit var telenum:EditText
    lateinit var register:Button
    lateinit var name1:String
    lateinit var psw1:String
    lateinit var telenum1:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        name=this.findViewById(R.id.name)
        psw=this.findViewById(R.id.password)
        psw2=this.findViewById(R.id.password2)
        telenum=this.findViewById(R.id.phone)
        register=this.findViewById(R.id.buttonRegister)



        register.setOnClickListener {
            if(check()){
            //调用Java后台登录接口
                var name:String=name.getText().toString()
                var password:String=psw.getText().toString()
                var phone:String=telenum.getText().toString()
                object : Thread() {
                    override fun run() {
                        try {
                            val path =
                                "http://132.232.81.77:8080/ForAndroid/registerServlet?name=" + name + "&password=" + password
                            val url = URL(path)
                            val connection = url.openConnection() as HttpURLConnection
                            connection.connect()
                            val responseCode = connection.responseCode
                            if (responseCode == 200) {
                                val `is` = connection.inputStream
                                val baos = ByteArrayOutputStream()
                                val buffer = ByteArray(1024)
                                var len = -1
                                len = `is`.read(buffer)
                                while (len != -1) {
                                    baos.write(buffer, 0, len)
                                }
                                val result = baos.toString()
                                runOnUiThread {
                                    if (result == "1") {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "注册成功",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "注册失败",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }.start()
            }
        }
    }
    fun toast(str:String) {
        runOnUiThread {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        }
    }



    fun check(): Boolean {
        var isok:Boolean=false
        name1=name.getText().toString()
        if(name1.isEmpty()){
            Toast.makeText(this,"昵称不能为空",Toast.LENGTH_LONG).show()
            return isok
        }
        if(name1.length>10){
            Toast.makeText(this,"昵称不能超过10位",Toast.LENGTH_LONG).show()
            return isok
        }

        psw1=psw.getText().toString()
        if(psw1.isEmpty()){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_LONG).show()
            return isok
        }
        if(psw1.length>16){
            Toast.makeText(this,"密码不能超过16位",Toast.LENGTH_LONG).show()
            return isok
        }

        var psw2:String=psw2.getText().toString()
        if(!psw2.equals(psw1)){
            Toast.makeText(this,"两次密码不一致",Toast.LENGTH_LONG).show()
            return isok
        }
        telenum1=telenum.getText().toString()
        if(telenum1.isEmpty()){
            Toast.makeText(this,"手机不能为空",Toast.LENGTH_LONG).show()
            return isok
        }else{
            var p="[1][3578]\\d{9}"
            var match:Boolean= Pattern.matches(p, telenum1);
            if (!match) {
                Toast.makeText(this,"请输入正确的手机号",Toast.LENGTH_LONG).show()
                return isok;
            }else{
                isok=true
                return isok
            }
        }
    }

}
