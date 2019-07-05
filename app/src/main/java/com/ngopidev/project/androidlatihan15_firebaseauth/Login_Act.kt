package com.ngopidev.project.androidlatihan15_firebaseauth

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Log.d
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.login_act.*


/**
 *   created by Irfan Assidiq on 2019-06-30
 *   email : assidiq.irfan@gmail.com
 **/
class Login_Act : AppCompatActivity(){

    //untuk request code
    private val RC_SIGN_IN = 7
    //untuk sign in clien
    private lateinit var mGoogleSignIn : GoogleSignInClient
    //untuk firebase authentication
    private lateinit var fAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_act)
        //inisialisasi
        fAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignIn = GoogleSignIn.getClient(this, gso)
        sign_in_button.setOnClickListener {
            signIN()
        }

        btn_regis.setOnClickListener {
            startActivity(Intent(this, Registrasi_Act::class.java))
        }

        btn_login.setOnClickListener {
            val email = et_email.text.toString()
            val password = et_password.text.toString()

            if(email.isNotEmpty() || password.isNotEmpty() ||
                   !email.equals("") || !password.equals("") ){
                fAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        startActivity(Intent(this,
                            MainActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "LOGIN GAGAL",
                            Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(this, "LOGIN GAGAL",
                    Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun signIN(){
        val signInIntent = mGoogleSignIn.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(account : GoogleSignInAccount){
        d("FAUTH_LOGIN", "firebaseAuth : ${account.id}")

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        fAuth.signInWithCredential(credential).addOnCompleteListener(this){
            if(it.isSuccessful){
                val user = fAuth.currentUser
                updateUI(user)
            }else{
                Toast.makeText(this,
                    "LOGIN GAGAL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
            if(user != null){
                Toast.makeText(this,
                "Login Berhasil Welcome ${user.displayName} , ${user.uid}",
                Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
    }

    override fun onStart() {
        super.onStart()
        val user = fAuth.currentUser
        if(user != null){
            updateUI(user)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            }catch (e : ApiException){
                Log.e("AUTH_LOGIN", "LOGIN GAGAL", e)
            }
        }
    }




}