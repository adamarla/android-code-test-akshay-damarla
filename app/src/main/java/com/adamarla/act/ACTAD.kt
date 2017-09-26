package com.adamarla.act

import android.app.Application
import com.adamarla.act.data.MyObjectBox
import io.objectbox.BoxStore

/**
 * Created by adamarla on 9/25/17.
 */

class ACTAD: Application() {

    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder().androidContext(this).build()
    }

    companion object {
        const val TAG = "ACTADBox"
    }
}