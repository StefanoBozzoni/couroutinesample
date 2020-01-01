package com.antonioleiva.coroutines

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.Executors

class UseCaseAskPin {

    private lateinit var mObserver : Observer<String>
    private var liveStr : MutableLiveData<String> = MutableLiveData<String>()

    fun execute(observer:Observer<String>):MutableLiveData<String> {

        liveStr = MutableLiveData<String>()
        mObserver=observer
        liveStr.observeForever(observer)

        val executor = Executors.newSingleThreadExecutor()
        executor.submit() {
            try {
                Thread.sleep(1000)
                val rnds = (0..10).random()
                liveStr.apply { postValue(rnds.toString()) }
            }
            catch(e:Exception){
                liveStr.apply { postValue("END") }
            }
        }
        return liveStr
    }


    fun dispose() {
        liveStr.removeObserver(mObserver)
    }


}