package com.example.otp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button otp, checkOtpBtn;
    EditText number, otpCheck;
    int finalOtp;
    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.ForceResendingToken token;
    String verificationId;
    Boolean verificationInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        otp = findViewById(R.id.otpBtn);
        number = findViewById(R.id.number);
        otpCheck = findViewById(R.id.otpCheck);
        checkOtpBtn = findViewById(R.id.checkOtpBtn);

        firebaseAuth = FirebaseAuth.getInstance();


        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  if(!verificationInProgress) {

                    if (!number.getText().toString().isEmpty() && number.getText().toString().length() == 10) {
                        requestOtp(number.getText().toString());



                    }
                        else {
                        number.setError("Phone number is not Valid");
                    }
                //}


            }
        });

        checkOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(otpCheck.getText().toString().equals(String.valueOf(finalOtp))){
                    Toast.makeText(MainActivity.this, "Correct Otp", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Wrong Otp", Toast.LENGTH_LONG).show();
                }

                */

                String userOtp = otpCheck.getText().toString();
                if(!userOtp.isEmpty()){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, userOtp);
                    verifyAuth(credential);

                } else {
                    otpCheck.setError("Enter valid Otp");
                }

            }
        });

    }

    private void requestOtp(String phoneNum) {


        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phoneNum, 60L, TimeUnit.SECONDS, MainActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationId = s;
                token = forceResendingToken;
                verificationInProgress = true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }


            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Toast.makeText(MainActivity.this, "Cannot create account because" + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void verifyAuth(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Authentication Successful", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void callApi(){
        int otp = genrateOtp();
        finalOtp = otp;

        RequestBody formBody = new FormBody.Builder()
                .add("to", "+91" + number.getText().toString())
                .add("body", "Lfyd otp is " + otp)
                .add("sender", "KLRHXA") //LFYDIN
                .add("type", "OTP")
                .add("template_id", "213456789078")
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.kaleyra.io/v1/HXAP1682279070IN/messages/")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("api-key", "A9b9b2e6fd110504e18ab4f75c30b8de4")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {

            }
        });
    }

    private int genrateOtp(){
        Random rand = new Random();

        int otp = rand.nextInt(10000);
        return otp;
    }
//.requiressmsvalidation

}