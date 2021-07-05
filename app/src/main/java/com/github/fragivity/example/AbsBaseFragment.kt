package com.github.fragivity.example

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.fragivity.navigator
import com.github.fragivity.pop

abstract class AbsBaseFragment(private val _supportBack: Boolean = true) : Fragment() {

    companion object {
        const val TAG = "Fragivity"
    }

    private val key: String
        get() = "${this.javaClass.simpleName}@${
            Integer.toHexString(System.identityHashCode(this))
        }"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated:$key")
        view.findViewById<TextView>(R.id.title_name)?.text = titleName
        if (_supportBack) {
            view.findViewById<TextView>(R.id.title_back)?.let {
                it.visibility = View.VISIBLE
                it.setOnClickListener {
                    navigator.pop()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate:$key")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart:$key")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume:$key")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause:$key")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop:$key")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy:$key")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "onDestroyView:$key")
    }

    protected abstract val titleName: String?

}