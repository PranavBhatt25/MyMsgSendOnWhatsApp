package com.andoid.mymsgsendonwhatsapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andoid.mymsgsendonwhatsapp.Utils.HelperMethods
import com.andoid.mymsgsendonwhatsapp.Utils.HelperMethods.GetCountryZipCode
import com.andoid.mymsgsendonwhatsapp.hbb20.CountryCodePicker

class ManActivity : AppCompatActivity() {

    lateinit var code_picker: CountryCodePicker
    lateinit var actEtMobileNo: EditText
    lateinit var actEtDescription: EditText
    lateinit var actBtnSend: Button

    lateinit var mMobileNumber: String
    lateinit var mDescription: String
    lateinit var fullPhoneNumber: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        code_picker = findViewById<CountryCodePicker>(R.id.code_picker)

        // finding the edit text
        actEtMobileNo = findViewById<EditText>(R.id.actEtMobileNo)
        actEtDescription = findViewById<EditText>(R.id.actEtDescription)

        // finding the button
        actBtnSend = findViewById<Button>(R.id.actBtnSend)

        // Setting On Click Listener
        actBtnSend.setOnClickListener {

            // Getting the user input
            CheckValidationsAndCallFunction()
        }


        val mCountryCodeForPhone: String = GetCountryZipCode(applicationContext)
        if (mCountryCodeForPhone != null && mCountryCodeForPhone != "") {
            code_picker.setCountryForPhoneCode(mCountryCodeForPhone.toInt())
        } else {
            code_picker.setCountryForPhoneCode(91)
        }
    }

    private fun CheckValidationsAndCallFunction() {
        mMobileNumber = actEtMobileNo.text.toString()
        mDescription = actEtDescription.text.toString()
        fullPhoneNumber = ""

        if (mMobileNumber.equals("")) {
            Toast.makeText(
                this,
                getString(R.string.please_enter_phone_number),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (HelperMethods.isValidPhoneNumber(mMobileNumber)) {
            fullPhoneNumber = "+" + code_picker.selectedCountryCode.toString() + mMobileNumber
        } else {
            Toast.makeText(
                this,
                getString(R.string.enter_proper_phone_number),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (mDescription.equals("")) {
            Toast.makeText(
                this,
                getString(R.string.please_enter_message),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        ExecuteWhatsAppFunction(fullPhoneNumber, mDescription.toString())
    }

    private fun ExecuteWhatsAppFunction(mMobileNumber: String, mDescription: String) {
//        val url = "https://api.whatsapp.com/send?phone=" + mMobileNumber + "?text=" + mDescription
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://wa.me/$mMobileNumber?text=$mDescription")
            )
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(
                applicationContext,
                getString(R.string.whatsapp_not_found),
                Toast.LENGTH_SHORT
            ).show()
            try {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ //Do something after 100ms
                    runOnUiThread {
                        try {
                            val uri = Uri.parse("market://details?id=com.whatsapp")
                            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                            goToMarket.addFlags(
                                Intent.FLAG_ACTIVITY_NO_HISTORY or
                                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                            )
                            startActivity(goToMarket)
                        } catch (ee: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=com.whatsapp")
                                )
                            )
                        }
                    }
                }, 2000)
            } catch (eee: Exception) {
                eee.stackTrace
            }
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}