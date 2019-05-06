package com.siano.view.budget


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.siano.R
import com.siano.base.AuthorizedActivity

class AddUserToBudgetActivity : AuthorizedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //textbox
        val codetextbox = findViewById (R.id.text_box) as EditText
        codetextbox.setHint("Your Hint")

        // get reference to button
        val btn = findViewById(R.id.code_enter) as Button
        // set on-click listener
        btn.setOnClickListener {
            // your code to perform when the user clicks on the button
        }





    }
}