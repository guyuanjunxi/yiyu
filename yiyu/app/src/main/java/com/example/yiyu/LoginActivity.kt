@file:Suppress("DEPRECATION")

package com.example.yiyu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor.err
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap


class LoginActivity : AppCompatActivity() {

    lateinit var login: Button
    lateinit var pswsee:ToggleButton
    lateinit var name:EditText
    lateinit var password:EditText
    lateinit var textRegister:TextView
    lateinit var textForget:TextView
    lateinit var name1:String
    lateinit var psw:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login=this.findViewById(R.id.login)
        pswsee=this.findViewById(R.id.pswsee)
        name=this.findViewById(R.id.name)
        password=this.findViewById(R.id.password)
        textRegister=this.findViewById(R.id.newuser)
        textForget=this.findViewById(R.id.forgetpsw)

        login.setOnClickListener {
            beforelogin()
        }

        textRegister.setOnClickListener{
            textRegister()
        }

        textForget.setOnClickListener{
            beforeforget()
        }

        pswsee.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(isChecked){
                //显示密码
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                //隐藏密码
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            //每次显示或者关闭时，密码显示编辑的线不统一在最后，下面是为了统一
            password.setSelection(password.length());
        }
    }

    fun textRegister(){
        var intent:Intent=intent.setClass(this,RegisterActivity::class.java)
        startActivity(intent)
    }
    fun toast(str:String) {
        runOnUiThread {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        }
    }

    fun beforelogin(){
        val name:String=name.getText().toString()
        val password:String=password.getText().toString()
        if(name.isEmpty()){
            Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT).show()
        }else if(password.isEmpty()){
            Toast.makeText(this,"请输入密码",Toast.LENGTH_SHORT).show()
        }else{
            //调用Java后台登录接口
            object : Thread() {
                override fun run() {
                    try {
                        val path = "http://132.232.81.77:8080/ForAndroid/loginServlet?name=" + name + "&password=" + password

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
                            while (len!= -1) {
                                baos.write(buffer, 0, len)
                            }
                            val result = baos.toString()
                            runOnUiThread {
                                if (result == "1") {
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java)
                                    )
                                } else {
                                    Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT).show()
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

    fun beforeforget(){
        var name:String=name.getText().toString()
        if(name.isEmpty()){
            Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT).show()
        }else{
            //forget(name1,"******")
        }
    }


}
