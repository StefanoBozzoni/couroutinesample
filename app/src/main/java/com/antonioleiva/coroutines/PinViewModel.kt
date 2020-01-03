package com.antonioleiva.coroutines

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class PinViewModel(
    private val useCaseSomeProcess: UseCaseSimulation,
    private val useCaseAskPin: UseCaseAskPin,
    private val useCaseSusp: UseCaseSuspended
) : ViewModel() {

    private val askPinLiveData : MutableLiveData<String> = MutableLiveData()
    lateinit var myDeferred    : CompletableDeferred<String>

    fun getAskPinLiveDataState() = askPinLiveData

    fun initPinViewModel() {
        viewModelScope.launch(Dispatchers.Main) {
            var risultato: String
            var counterRetry = 0

            do {
                myDeferred = CompletableDeferred()
                useCaseSomeProcess.execute(ObserverProcess())
                risultato = myDeferred.await() //waiting use case observer result
                useCaseSomeProcess.dispose()
                Log.d("TAG", "usecase eseguito, risultato=${risultato.toString()}")

                if (counterRetry++==2)  //counterRetry should be passed to the usecase observer to send "FINITO" as result after error or success and after 2 retries
                    risultato = "FINITO"
                else if (risultato == "Errore") {
                    myDeferred.cancel()
                    Log.d("TAG", "domanda pin")
                    askPinLiveData.postValue("DOMANDA PIN") //activity is observing and asks a value
                    risultato = myDeferred.await()  //waiting live data observer completation
                }

            } while (risultato != "FINITO")

            //eventuale esempio di nuovo use case che usa couroutine
            val risultato3 = async { useCaseSusp.execute() }.await()
            Log.d("TAG", "Risultato sospensione : $risultato3")
        }
    }

    inner class ObserverProcess : Observer<String> {
        override fun onChanged(t: String?) {
            if (t=="4")
                myDeferred.complete("FINITO")
            else
                myDeferred.complete("Errore")

        }
    }

}
