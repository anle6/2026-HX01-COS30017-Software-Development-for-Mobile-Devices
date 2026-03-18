package com.example.week6login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY

class Bottom_Fragment_Activity : Fragment{
constructor()
lateinit var txtFullName : TextView
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
): View? { val view : View = inflater.inflate(R.layout.bottom_fragment_layout, container, false)
    txtFullName = view.findViewById(R.id.fullname)
    return view
}
fun showText(firstName: String, lastName: String){
    txtFullName.setText(firstName + " " + lastName)
}
}