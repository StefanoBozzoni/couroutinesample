package com.antonioleiva.coroutines

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.toast
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope, pinRequest by PinActivity() {

    var mResult: CompletableDeferred<ActivityResult?> = CompletableDeferred()
    private val mainViewModel:PinViewModel by viewModel()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    private val userService = UserService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()

        login.setOnClickListener { doLogin(user.text.toString(), password.text.toString()) }

        mainViewModel.getAskPinLiveDataState().observe(this, Observer {str-> handle(str)})

        mainViewModel.initPinViewModel()

    }

    private fun handle(str:String?) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, 100)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==100) {
            mainViewModel.myDeferred.complete("FINITO")
        }
        else
            mResult.complete(ActivityResult(resultCode,data))

    }



    private fun doLogin(username: String, password: String) {

        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }

        val context=this

        GlobalScope.launch(Dispatchers.Main) {
            mResult=launchIntent(context,intent)
            val result= mResult.await()
            //mResult = launchIntent(context,intent).await()
            result?.data?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), it.data);
                myImageView.setImageBitmap(bitmap)
                Log.d("TAG", "presa immagine")
            }
        }

        launch {
            progress.visibility = View.VISIBLE

            val user = withContext(Dispatchers.IO) { userService.doLogin(username, password) }
            val currentFriends   = async(Dispatchers.IO) { userService.requestCurrentFriends(user) }
            val suggestedFriends = async(Dispatchers.IO) { userService.requestSuggestedFriends(user) }

            val finalUser = user.copy(friends = currentFriends.await() + suggestedFriends.await())
            toast("User ${finalUser.name} has ${finalUser.friends.size} friends")

            progress.visibility = View.GONE
        }
    }


    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}