package com.xionce.doctorvetServices;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.xionce.doctorvetServices.utilities.HelperClass;

public class ViewFullScreenPhoto extends AppCompatActivity {
    //TODO: Unificar esta forma de ver imagenes y las de intents a apps locales
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_screen_photo);
        progressBar = findViewById(R.id.pb_loading_indicator);
        progressBar.setVisibility(View.VISIBLE);

        final ImageView fullScreenImageView = findViewById(R.id.img_fullscreen);
        String imageUrl = getIntent().getStringExtra(HelperClass.INTENT_IMAGE_URL);

        Glide.with(getApplicationContext())
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .apply(RequestOptions.fitCenterTransform())
                .into(fullScreenImageView);
    }
}
