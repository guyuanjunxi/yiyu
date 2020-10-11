package com.example.yiyu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity() {

    lateinit var password1:EditText
    lateinit var password2:EditText
    lateinit var submit:Button
    lateinit var name:String
    lateinit var psw1:String
    lateinit var psw2:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        password1=this.findViewById(R.id.password1)
        password2=this.findViewById(R.id.password2)
        submit=this.findViewById(R.id.submit)

        name = getIntent().getStringExtra("name").toString()
        submit.setOnClickListener{
            changebefore()
        }
    }

    fun changepsw(name: String, password: String) {
        //请求地址
        val url = "http://132.232.81.77:8080/server/ChangePassword"
        val tag = "重置密码"
        //取得请求队列
        val requestQueue = Volley.newRequestQueue(applicationContext)
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag)
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)

        val request = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response).get("params") as JSONObject
                    val result = jsonObject.getString("Result")
                    if (result == "重置成功") {
                        Toast.makeText(this, "重置成功!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else if (result == "重置失败") {
                        Toast.makeText(this, "用户名不存在!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
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
                params["password"] = password
                return params
            }
        }


        //设置Tag标签
        request.tag = tag

        //将请求添加到队列中
        requestQueue.add(request)

    }

    fun check(): Boolean {
        var isok:Boolean=false
        var psw:String=password1.getText().toString()
        if(psw.isEmpty()){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_LONG).show()
            return isok
        }
        if(psw.length>16){
            Toast.makeText(this,"密码不能超过16位",Toast.LENGTH_LONG).show()
            return isok
        }

        var psw2:String=password2.getText().toString()
        if(!psw2.equals(psw)){
            Toast.makeText(this,"两次密码不一致",Toast.LENGTH_LONG).show()
            return isok
        }else{
            isok=true
            return isok
        }
    }

    fun changebefore(){
        if(check()){
            changepsw(name,psw1)
            //Toast.makeText(this,"重置成功",Toast.LENGTH_SHORT).show()
           // val intent = Intent(this, LoginActivity::class.java)
            //startActivity(intent)
        }
    }

}
