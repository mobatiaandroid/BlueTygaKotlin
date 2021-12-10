package com.vkc.bluetyga.utils

import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.vkc.bluetyga.R

class GenericKeyEvent(private val currentView: EditText, private val previousView: EditText?) :
    View.OnKeyListener {
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN
            && keyCode == KeyEvent.KEYCODE_DEL
            && currentView.id != R.id.editOtp1
            && currentView.text.isEmpty()) {
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }

}