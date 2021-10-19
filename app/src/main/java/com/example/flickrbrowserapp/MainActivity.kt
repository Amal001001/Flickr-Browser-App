package com.example.flickrbrowserapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Insets.add
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets.add
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class Image(var title: String, var link: String)

class MainActivity : AppCompatActivity() {

    private val APIkey = "0d61010c62f30a906bef0afde9d841ff"
    lateinit var et:EditText
    lateinit var button:Button
    var search =""

    lateinit var photos : ArrayList<Image>
    private lateinit var myRV: RecyclerView
    private lateinit var rvAdapter: RecyclerViewAdapter

    lateinit var photolayout:LinearLayout
    private lateinit var imageView2: ImageView
    lateinit var linearLayout : LinearLayout

    lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et = findViewById(R.id.et)
        button = findViewById(R.id.button)

        photos = arrayListOf()
        myRV = findViewById(R.id.recyclerView)
        rvAdapter = RecyclerViewAdapter(this,photos)
        myRV.adapter = rvAdapter
        myRV.layoutManager = LinearLayoutManager(this)

        button.setOnClickListener {
            search = et.text.toString()
            if(et.text.isNotEmpty()) { requestAPI() }
            et.text.clear()
            photos.clear()
            rvAdapter.notifyDataSetChanged()

            progressDialog = ProgressDialog(this@MainActivity)
            progressDialog.setMessage("Please wait")
            progressDialog.show()

            // Hide Keyboard
            val imm = ContextCompat.getSystemService(this, InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
        }

        photolayout = findViewById(R.id.photolayout)
        imageView2 = findViewById(R.id.imageView2)
        photolayout.setOnClickListener { closePhoto() }

        linearLayout = findViewById(R.id.linearLayout)
    }

    private fun requestAPI(){
        CoroutineScope(IO).launch {
            Log.d("main","CoroutineScope")
            val data = async { fetchPhoto() }.await()
            if(data.isNotEmpty()){ showPhotos(data)}
        }
    }

        private fun fetchPhoto(): String {
                var response = ""
                try
                { Log.d("main","fetchPhoto")
                    response =
                        URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=$APIkey&tags=$search&format=json&nojsoncallback=1")
                            .readText(Charsets.UTF_8)
                }catch (e: Exception)
                { println("Error: $e")
                }
                return response
        }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun showPhotos(data: String){
//        https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
//https://live.staticflickr.com/{server-id}/{id}_{secret}_{size-suffix}.jpg

        withContext(Main) {
            Log.d("main","showPhotos")
            progressDialog.dismiss()
            val jsonObj = JSONObject(data)
            val phot = jsonObj.getJSONObject("photos").getJSONArray("photo")

            for (i in 0..50 ) //until photos.size seems too big
            {
                val title = phot.getJSONObject(i).getString("title")
                val farmID = phot.getJSONObject(i).getString("farm")
                val serverID = phot.getJSONObject(i).getString("server")
                val id = phot.getJSONObject(i).getString("id")
                val secret = phot.getJSONObject(i).getString("secret")
                val photoLink = "https://farm$farmID.staticflickr.com/$serverID/${id}_${secret}.jpg"

                photos.add(Image(title, photoLink))
            }
            rvAdapter.notifyDataSetChanged()
        }
    }

    fun bigPhoto(link :String) {
        Glide.with(this).load(link).into(imageView2)
        photolayout.isVisible = true
        linearLayout.isVisible = false
        myRV.isVisible = false
    }

    fun closePhoto(){
        photolayout.isVisible = false
        linearLayout.isVisible = true
        myRV.isVisible = true
    }
}