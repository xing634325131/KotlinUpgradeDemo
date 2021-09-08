package com.example.kotlinupgradedemo

import android.app.Activity
import android.app.Dialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent


private val ownerToProgressMap = mutableMapOf<LifecycleOwner, Dialog>()

private val progressCleaner = object : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner : LifecycleOwner) {
        ownerToProgressMap.remove(owner)?.dismiss()
        owner.lifecycle.removeObserver(this)
    }
}

fun LifecycleOwner.showProgress() {
    val context = when (this) {
        is Activity -> this
        is Fragment -> this.context
        else -> null
    } ?: return

    ownerToProgressMap[this]
        ?.apply { show() }
        ?: Dialog(context).also {
            it.setTitle("Tips")
            it.show()
        }.let {
            ownerToProgressMap[this] = it
            this.lifecycle.addObserver(progressCleaner)
        }
}

fun LifecycleOwner.dismissProgress() {
    ownerToProgressMap[this]?.dismiss()
}
