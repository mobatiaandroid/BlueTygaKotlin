package com.vkc.bluetyga.manager

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.vkc.bluetyga.R

class GenericTextWatcher(private val currentView: View, private val nextView: View?) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val text = s.toString()
        when (currentView.id) {
            R.id.editOtp1 -> {
                if (text.length == 1) {
                    nextView!!.requestFocus()
                    currentView.setBackgroundResource(R.drawable.rounded_rect_full_white)
                } else if (text.isEmpty()){
                    currentView.setBackgroundResource(R.drawable.rounded_rect_line)
                }
            }
            R.id.editOtp2 -> {
                if (text.length == 1) {
                    nextView!!.requestFocus()
                    currentView.setBackgroundResource(R.drawable.rounded_rect_full_white)
                } else if (text.isEmpty()){
                    currentView.setBackgroundResource(R.drawable.rounded_rect_line)
                }
            }
            R.id.editOtp3 -> {
                if (text.length == 1) {
                    nextView!!.requestFocus()
                    currentView.setBackgroundResource(R.drawable.rounded_rect_full_white)
                } else if (text.isEmpty()){
                    currentView.setBackgroundResource(R.drawable.rounded_rect_line)
                }
            }
            R.id.editOtp4 -> {
                if (text.length == 1) {
                    currentView.setBackgroundResource(R.drawable.rounded_rect_full_white)
                } else if (text.isEmpty()){
                    currentView.setBackgroundResource(R.drawable.rounded_rect_line)
                }
            }
        }
    }
}