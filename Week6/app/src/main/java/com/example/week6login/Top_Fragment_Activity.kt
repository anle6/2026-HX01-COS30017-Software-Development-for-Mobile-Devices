package com.example.week6login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY

class Top_Fragment_Activity : Fragment{
    constructor()
    lateinit var txtFirstName: EditText
    lateinit var txtLastName: EditText
    lateinit var btnOk: Button
    lateinit var mainActivity: Main_Fragment_Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.top_fragment_layout, container, false)
        txtFirstName = view.findViewById(R.id.firstname)
        txtLastName = view.findViewById(R.id.lastname)
        btnOk = view.findViewById( R . id . button_ok)

        btnOk.setOnClickListener(View.OnClickListener {
            val firstName = txtFirstName.getText().toString()
            val lastName = txtLastName.getText().toString()
            mainActivity.showText(firstName,lastName)
        })
        return view
    }
}

