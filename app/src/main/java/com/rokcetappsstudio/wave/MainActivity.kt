package com.rokcetappsstudio.wave

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window.FEATURE_NO_TITLE
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException


class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(FEATURE_NO_TITLE);


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        if (isSignedIn()) {
            val account = GoogleSignIn.getLastSignedInAccount(this);
            Toast.makeText(this, "Sign in successfull ${account?.email}", Toast.LENGTH_LONG).show()
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)

        } else {
            // todo: request server auth code later.
            // https://developers.google.com/identity/sign-in/android/offline-access
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
            signInButton.setOnClickListener {
                val signInIntent = mGoogleSignInClient.getSignInIntent()
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        }

    }

    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(this) != null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful.
                val account = task.getResult(ApiException::class.java)
                if (account == null) {
                    throw Exception("no user info found");
                }
                val email = account.email
                Toast.makeText(this, "Sign in successfull", Toast.LENGTH_LONG).show()

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w( "Google sign in failed", e)
                // ...
            }
            catch (e: Exception) {
                Log.w( "Google sign in failed.", e)
            }
        }
    }


}
