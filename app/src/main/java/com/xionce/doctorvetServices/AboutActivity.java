package com.xionce.doctorvetServices;

import static com.xionce.doctorvetServices.utilities.NetworkUtils.DOCTOR_VET_BASE_URL;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.NetworkUtils;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String versionName = "";

        try {
            versionName = getBaseContext().getPackageManager().getPackageInfo(getBaseContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        TextView txtTitle = findViewById(R.id.title);
        txtTitle.append(" " + versionName);

        TextView txtTerms = findViewById(R.id.txt_terms_2);
        txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(DOCTOR_VET_BASE_URL + "terms-and-conditions.html");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
            }
        });

        TextView txtPrivacyPolicy = findViewById(R.id.txt_privacy_policy);
        txtPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(DOCTOR_VET_BASE_URL + "privacy-policy.html");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
            }
        });

        TextView txtWeb = findViewById(R.id.txt_web);
        txtWeb.setText(DOCTOR_VET_BASE_URL);
        txtWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(DOCTOR_VET_BASE_URL);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
            }
        });

        TextView txtEmail = findViewById(R.id.txt_email);
        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HelperClass.sendEmail(AboutActivity.this, "doctorvet@xionce.com");
                } catch (HelperClass.EnterPhoneException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(AboutActivity.this), getString(R.string.error_falta_email), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        TextView txtWhatsapp = findViewById(R.id.txt_whatsapp);
        txtWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HelperClass.sendWhatsAppMessage(AboutActivity.this, "+5492612716685", "");
                } catch (HelperClass.NoWhatsAppException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(AboutActivity.this), getString(R.string.error_falta_whatsapp), Snackbar.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(AboutActivity.this), getString(R.string.error_falta_telefono), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

//        TextView txtUsing = findViewById(R.id.txt_using);
//        txtUsing.setText(DOCTOR_VET_BASE_URL);
    }
}