package com.xionce.doctorvetServices;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.ReturnCode;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Resource;
import com.xionce.doctorvetServices.data.ResourcesAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_RECORD_AUDIO;
import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_TAKE_IMAGE;
import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_TAKE_THUMB;
import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_TAKE_VIDEO;
import static com.xionce.doctorvetServices.utilities.HelperClass.saveBitmap;

public abstract class EditBaseActivity extends AppCompatActivity {

    private static final String TAG = "EditBaseActivity";
    protected Toolbar toolbar;
    protected TextView toolbar_title;
    protected TextView toolbar_subtitle;
    protected ImageView toolbar_image;
    private FloatingActionButton fab_save;
    private ProgressBar mLoadingIndicator;
    private TextView mMessageDisplay;
    protected FrameLayout activityContainer;
    private Dialog dialog;

    //resources
    protected ResourcesAdapter resourcesAdapter = null;

    //to prevent fast taps
    protected long CLICK_TIME_INTERVAL = 500;
    protected long mLastClickTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (DoctorVetApp.get().isSoftKeyboardVisible(activityContainer))
            DoctorVetApp.get().closeKeyboard();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Audios
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        if (requestCode == HelperClass.REQUEST_TAKE_VIDEO) {
            final String inputPath, outputPath;

            //Obtengo path al video
            inputPath = DoctorVetApp.get().getPathToFile(); //dataFragment.getPathToFile();

            //duracion del video
            try {
                if (HelperClass.getVideoDurationInMiliseconds(this, Uri.fromFile(new File(inputPath))) > 31000) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(this), EditBaseActivity.this.getString(R.string.editClinicaActivity_VideoNoAceptado), Snackbar.LENGTH_LONG).show();
                    HelperClass.deleteFile(inputPath);
                } else {
                    //path de video temporal comprimido
                    int cut = inputPath.lastIndexOf('.');
                    outputPath = inputPath.substring(0, cut) + "_COMP.mp4";
                    new CompressVideo().execute("false", inputPath, outputPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        //dejar este codigo aca
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) return;

        switch (requestCode) {
            case REQUEST_TAKE_THUMB:
                boolean permissionToTakePhotoAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToTakePhotoAccepted)
                    DoctorVetApp.get().dispatchTakeThumbIntent(this);
                break;
            case REQUEST_TAKE_IMAGE:
                boolean permissionToTakeImageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToTakeImageAccepted)
                    DoctorVetApp.get().dispatchTakePictureIntent(this);
                break;
            case REQUEST_RECORD_AUDIO:
                boolean permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToRecordAccepted)
                    record_audio();
                break;
            case REQUEST_TAKE_VIDEO:
                boolean permissionToTakeVideoAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToTakeVideoAccepted)
                    record_video();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_remove_photo) {
            remove_thumb(getResourceObject());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (DoctorVetApp.get().isSoftKeyboardVisible(activityContainer))
            DoctorVetApp.get().closeKeyboard();

        finish();
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater layoutInflater = getLayoutInflater();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) layoutInflater.inflate(R.layout.activity_edit_base, null);
        activityContainer = coordinatorLayout.findViewById(R.id.container_frame);
        toolbar = coordinatorLayout.findViewById(R.id.toolbar);
        toolbar_title = coordinatorLayout.findViewById(R.id.txt_title);
        toolbar_subtitle = coordinatorLayout.findViewById(R.id.txt_sub_title);
        toolbar_image = coordinatorLayout.findViewById(R.id.toolbar_image_thumb);
        fab_save = coordinatorLayout.findViewById(R.id.fab_save);
        mLoadingIndicator = coordinatorLayout.findViewById(R.id.pb_loading_indicator);
        mMessageDisplay = coordinatorLayout.findViewById(R.id.tv_error_message_display);
        layoutInflater.inflate(layoutResID, activityContainer, true);
        super.setContentView(coordinatorLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUpdate()) {
                    update();
                } else {
                    save();
                }
            }
        });
    }

    protected abstract void save();
    protected abstract void update();
    protected Integer getMethod() {
        if (isUpdate())
            return Request.Method.PUT;

        return Request.Method.POST;
    }
    protected abstract URL getUrl();
    protected abstract Object getObjectFromUI();
    protected abstract Object getObject();
    protected abstract void setObjectToUI(Object object);
    protected void remove_thumb(DoctorVetApp.IResourceObject photoObject) {
//        dataFragment.removeImages();
        //getFilesArrayList().clear();
//        setImageRemoved2();
        photoObject.setThumb_url(null);
        photoObject.setPhoto_url(null);
        photoObject.setThumb_deleted(1);
        setRoundEmptyTakePhoto();
    }
    protected abstract DoctorVetApp.IResourceObject getResourceObject();
    protected abstract String getUploadTableName();

    private int initRequests = 0;
    protected void initializeInitRequestNumber(int number) {
        initRequests = number;
        showWaitDialog();
    }
    protected void setRequestCompleted() {
        initRequests--;
        if (initRequests == 0) {
            hideWaitDialog();
            if (!isUpdate())
                onAllRequestsCompleted();
        }
    }
    protected void onAllRequestsCompleted() {

    }

    public void loadThumb(String thumb) {
        if (thumb == null) {
            setRoundEmptyTakePhoto();
            return;
        }

        //on device search first
        String str_thumb_file_name = DoctorVetApp.get().getLocalFromUrl(thumb);
        if (HelperClass.fileExists(str_thumb_file_name))
            thumb = str_thumb_file_name;

        unsetRoundEmptyTakePhoto();
        Glide.with(getApplicationContext()).load(thumb).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(toolbar_image);
    }

    protected boolean isUpdate() {
        Intent intent = getIntent();
        return intent.hasExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE)
                && (intent.getIntExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE, 0) == HelperClass.REQUEST_UPDATE);
    }
    protected boolean isNew() {
        return !isUpdate();
    }
    protected boolean isInit() {
        Intent i = getIntent();
        if (!i.hasExtra(HelperClass.INTENT_EXTRA_INIT)) {
            i.putExtra(HelperClass.INTENT_EXTRA_INIT, true);
            return true;
        }

        return false;
    }

    public void hideActivityContainer() {
        activityContainer.setVisibility(View.GONE);
    }
    public void showActivityContainer() {
        activityContainer.setVisibility(VISIBLE);
    }
    public void showOnCreateLoadingErrorMessage() {
        activityContainer.setVisibility(View.GONE);
        mMessageDisplay.setVisibility(VISIBLE);
        mMessageDisplay.setText(this.getString(R.string.error_conexion_servidor));
    }
    public void showProgressBar() {
        mLoadingIndicator.setVisibility(VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void hideProgressBar() {
        mLoadingIndicator.setVisibility(INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void showWaitDialog() {
        mLoadingIndicator.setVisibility(VISIBLE);
        HelperClass.setEnabledViews(this.findViewById(android.R.id.content), false);
//        mOverlayDialog = HelperClass.getOverlayDialog(this);
//        mOverlayDialog.show();
    }
    public void hideWaitDialog() {
        HelperClass.setEnabledViews(this.findViewById(android.R.id.content), true);
        mLoadingIndicator.setVisibility(INVISIBLE);
        //mOverlayDialog.dismiss();
    }
    public void showErrorToast(String error_message, String TAG) {
        Snackbar.make(DoctorVetApp.getRootForSnack(this), error_message, Snackbar.LENGTH_SHORT).show();
        Log.e(TAG, error_message);
    }
    public void showVolleyError(VolleyError error, String tag) {
        DoctorVetApp.get().handle_volley_error(error, /*this,*/ tag, true);
    }
    public void implementTakePhoto(DoctorVetApp.IResourceObject photoObject) {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(EditBaseActivity.this, R.style.PopUpMenu);
                PopupMenu popupMenu = new PopupMenu(wrapper, toolbar);
                popupMenu.getMenuInflater().inflate(R.menu.menu_take_image, popupMenu.getMenu());

                if (photoObject.getPhoto_url() != null) {
                    MenuItem itemRemove = popupMenu.getMenu().findItem(R.id.action_remove_photo);
                    itemRemove.setVisible(true);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_take_photo) {
                            checkSoftKeyboard();
                            if (HelperClass.checkPermission(Manifest.permission.CAMERA, getApplicationContext())) {
                                DoctorVetApp.get().dispatchTakeThumbIntent(EditBaseActivity.this);
                            } else {
                                HelperClass.requestPermission(EditBaseActivity.this, Manifest.permission.CAMERA, REQUEST_TAKE_THUMB);
                            }
                        } else if (item.getItemId() == R.id.action_open_gallery) {
                            DoctorVetApp.get().dispatchOpenGalleryThumbIntent(EditBaseActivity.this);
                        } else if (item.getItemId() == R.id.action_remove_photo) {
                            remove_thumb(getResourceObject());
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }
    public void implementShowPhoto(DoctorVetApp.IResourceObject photoObject) {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(EditBaseActivity.this, ViewFullScreenPhoto.class);
                activity.putExtra(HelperClass.INTENT_IMAGE_URL, photoObject.getPhoto_url());
                startActivity(activity);
            }
        });
    }
//    public void setPhoto(DoctorVetApp.IResourceObject photoObject, Response_insert response) {
//        if (response.isPhoto_updated()) {
//            photoObject.setPhoto_url(response.getPhoto_url());
//            photoObject.setThumb_url(response.getThumb_url());
//        }
//    }
    private void checkSoftKeyboard() {
        if (DoctorVetApp.get().isSoftKeyboardVisible(activityContainer))
            DoctorVetApp.get().closeKeyboard();
    }

    public void hideToolbarImage() {
        toolbar_image.setVisibility(View.GONE);
    }
    public void showToolbarImage() {
        toolbar_image.setVisibility(VISIBLE);
    }
    public void setRoundEmptyTakePhoto() {
        toolbar_image.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_circle));
        toolbar_image.setImageResource(R.drawable.ic_add_photo_dark);
    }
    public void unsetRoundEmptyTakePhoto() {
        toolbar_image.setBackground(null);
        toolbar_image.setImageResource(android.R.color.transparent);
    }
    public void hideFab() {
        fab_save.setVisibility(View.GONE);
    }
    public void hideSubtitle() {
        toolbar_subtitle.setVisibility(View.GONE);
    }

    //Audios
    protected String last_AudioFilePath = "";
    protected MediaRecorder recorder = null;
    protected boolean mStartRecording = true;
    protected void record_audio() {

    }
    protected void onRecord(boolean start, String audioFilePath) {
        if (start) {
            startRecording(audioFilePath);
        } else {
            stopRecording();
        }
    }
    protected void startRecording(String audioFilePath) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(audioFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }
    protected void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        Resource resource = new Resource(last_AudioFilePath, getUploadTableName());

        ArrayList<Resource> resources = new ArrayList<>();
        resources.add(resource);
        uploadResources(resources);
    }

    private MediaPlayer mediaPlayer = null;
    private int mediaPlayerSeekTo = 0;
    private ImageView playButton;
    private SeekBar seekBar;
    private Handler handler;
    private void handleAudioClick(Resource audioResource) {
        mediaPlayer = getMediaPlayer();

        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayerSeekTo = mediaPlayer.getCurrentPosition();
            showPlay(true);
        } else {
            try {
                playAudio(audioResource.getLocal_path());
            } catch (Exception e) {
                DoctorVetApp.get().handle_error(e, TAG, true);
            }
        }
    }
    public void showPlay(boolean show) {
        if (show) {
            playButton.setImageResource(R.drawable.ic_play_light);
            playButton.setVisibility(VISIBLE);
        } else {
            playButton.setVisibility(INVISIBLE);
        }
    }
    public void showPause(boolean show) {
        if (show) {
            this.playButton.setImageResource(R.drawable.ic_pause_light);
            this.playButton.setVisibility(VISIBLE);
        } else {
            this.playButton.setVisibility(INVISIBLE);
        }
    }
    private void playAudio(String resourcePath) throws Exception {
        killMediaPlayer();
        mediaPlayer = getMediaPlayer();

        if (HelperClass.isValidUri(resourcePath)) {
            mediaPlayer.setDataSource(resourcePath);
        } else {
            mediaPlayer.setDataSource(EditBaseActivity.this, Uri.fromFile(new File(resourcePath)));
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mp.getDuration());
                //playButton.setImageResource(R.drawable.ic_stop_light);
                showPause(true);
                if (mediaPlayerSeekTo != 0)
                    mediaPlayer.seekTo(mediaPlayerSeekTo);
                mediaPlayer.start();
                changeAudioSeekBar();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayerSeekTo = 0;
                mediaPlayer.seekTo(0);
                showPlay(true);
                seekBar.setProgress(0);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.prepare();
    }
    private void changeAudioSeekBar() {
        //mediaPlayer = ((ResourceContainer.PlayableContent) clientActivity).getMediaPlayer();
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        //Log.i(TAG, String.valueOf(mediaPlayer.getCurrentPosition()));

        if (mediaPlayer.isPlaying()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    changeAudioSeekBar();
                }
            };

            handler.postDelayed(runnable, 500);
        }
    }
    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    public MediaPlayer getMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        return mediaPlayer;
    }

    //video
    protected void record_video() {

    }
    private class CompressVideo extends AsyncTask<String, String, String> {
        //Dialog dialog;

        @Override protected void onPreExecute()
        {
            super.onPreExecute();
            dialog = ProgressDialog.show(EditBaseActivity.this, "", "Procesando video...");
        }

        @Override
        protected String doInBackground(String... strings)
        {
            String inputVideoPath = strings[1];
            String outputVideoPath = strings[2];

            try {
                FFmpegSession session = FFmpegKit.execute("-i " + inputVideoPath + " -crf 26 -s 540x960 -c:v mpeg4 " + outputVideoPath);

                if (ReturnCode.isSuccess(session.getReturnCode())) {
                    Log.d(TAG, "SUCCESS");
                    HelperClass.deleteFile(inputVideoPath);
                } else if (ReturnCode.isCancel(session.getReturnCode())) {
                    Log.d(TAG, "CANCEL");
                } else {
                    // FAILURE
                    Log.d("TAG", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));
                }
            } catch (Exception e) {
                DoctorVetApp.get().handle_error(e, TAG, true);
            }

            return outputVideoPath;
        }

        @Override protected void onPostExecute(String outputVideoPath)
        {
            super.onPostExecute(outputVideoPath);
            dismissProgressDialog();
            Resource resource = new Resource(outputVideoPath, getUploadTableName());
            //resourcesAdapter.addItem(resource);

            ArrayList<Resource> resources = new ArrayList<>();
            resources.add(resource);
            uploadResources(resources);
        }
    }

    protected abstract void restoreFromBundle(Bundle savedInstanceState);

    //thumbs and photos
    public ActivityResultLauncher<Intent> getThumbCameraLauncher() {
        return thumbCameraLauncher;
    }
    private ActivityResultLauncher<Intent> thumbCameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() != RESULT_OK) return;

            if (!DoctorVetApp.get().getPathToFile().isEmpty()) {
                Uri pathToFile = Uri.fromFile(new File(DoctorVetApp.get().getPathToFile()));
                manageThumbAndPhoto(pathToFile);
            }
        }
    });
    public ActivityResultLauncher<Intent> getThumbOpenGalleryLauncher() {
        return thumbOpenGalleryLauncher;
    }
    public ActivityResultLauncher<Intent> thumbOpenGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() != RESULT_OK) return;

            if (result.getData() != null) {
                try {
                    Uri pathToFile = result.getData().getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pathToFile);

                    //copy file
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    ByteArrayInputStream in = new ByteArrayInputStream(bitmapdata);
                    //String outputFileName = HelperClass.createFile(getApplicationContext(), HelperClass.getExtensionFromUri(pathToFile, getContentResolver()));
                    File photoFile = DoctorVetApp.get().createImageFile();
                    OutputStream out = new FileOutputStream(photoFile);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    in.close();

                    Uri photoURI = Uri.fromFile(photoFile);
                    manageThumbAndPhoto(photoURI);
                } catch (IOException e) {
                    DoctorVetApp.get().handle_error(e, TAG, true);
                }
            }
        }
    });
    private void manageThumbAndPhoto(Uri pathToFile) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pathToFile);

            //check rotation
            bitmap = HelperClass.checkRotation(pathToFile.getPath(), bitmap);

            Bitmap bitmap_photo = HelperClass.compressBitmapToJpg_2(bitmap);
            Bitmap bitmap_thumb = DoctorVetApp.get().getThumb(bitmap_photo);
            String path_photo = pathToFile.getPath();
            String path_thumb = HelperClass.appendToFileName(path_photo, "_thumb");

            saveBitmap(path_photo, bitmap_photo);
            saveBitmap(path_thumb, bitmap_thumb);
            Glide.with(getApplicationContext()).load(bitmap_thumb).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(toolbar_image);
            getResourceObject().setThumb_url(path_thumb);

            //create resources
            Resource photo = new Resource(path_photo, getUploadTableName(), "IS_THUMB_HIGH");
            Resource thumb = new Resource(path_thumb, getUploadTableName(), "IS_THUMB");
            ArrayList<Resource> resources = new ArrayList<>();
            resources.add(photo);
            resources.add(thumb);

            uploadResources(resources);
        } catch (IOException e) {
            DoctorVetApp.get().handle_error(e, TAG, true);
        }
    }

    //general resources
    public ActivityResultLauncher<Intent> getCameraLauncher() {
        return cameraLauncher;
    }
    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() != RESULT_OK) return;

            if (!DoctorVetApp.get().getPathToFile().isEmpty()) {
                Uri pathToFile = Uri.fromFile(new File(DoctorVetApp.get().getPathToFile()));
                managePhoto(pathToFile);
            }
        }
    });
    public ActivityResultLauncher<Intent> getOpenGalleryLauncher() {
        return openGalleryLauncher;
    }
    public ActivityResultLauncher<Intent> openGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() != RESULT_OK) return;

            ContentResolver contentResolver = getContentResolver();
            Uri uri = result.getData().getData();

            if (uri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                    //Bitmap bitmapCompress = HelperClass.compressBitmapToJpg_2(bitmap);

                    //copy file
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    ByteArrayInputStream in = new ByteArrayInputStream(bitmapdata);
                    //String outputFileName = HelperClass.createFile(getApplicationContext(), HelperClass.getExtensionFromUri(pathToFile, getContentResolver()));
                    File photoFile = DoctorVetApp.get().createImageFile();
                    OutputStream out = new FileOutputStream(photoFile);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    in.close();

                    Uri photoURI = Uri.fromFile(photoFile);
                    managePhoto(photoURI);
                } catch (IOException e) {
                    DoctorVetApp.get().handle_error(e, TAG, true);
                }
            }

        }
    });
    private void managePhoto(Uri pathToFile) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pathToFile);

            //check rotation
            bitmap = HelperClass.checkRotation(pathToFile.getPath(), bitmap);

            Bitmap bitmap_photo = HelperClass.compressBitmapToJpg_2(bitmap);
            String path_photo = pathToFile.getPath();
            saveBitmap(path_photo, bitmap_photo);

            //create resources
            Resource photo = new Resource(path_photo, getUploadTableName());
            ArrayList<Resource> resources = new ArrayList<>();
            resources.add(photo);

            uploadResources(resources);
        } catch (IOException e) {
            DoctorVetApp.get().handle_error(e, TAG, true);
        }
    }

    public ActivityResultLauncher<Intent> getOpenFileLauncher() {
        return openFileLauncher;
    }
    public ActivityResultLauncher<Intent> openFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() != RESULT_OK) return;

            ContentResolver contentResolver = getContentResolver();
            Uri uri = result.getData().getData();

            if (HelperClass.getSizeInKBFromUri(uri, contentResolver) < 5100) {
                try {
                    String outputFileName = HelperClass.createFile(getApplicationContext(), HelperClass.getExtensionFromUri(uri, contentResolver));
                    InputStream in = getContentResolver().openInputStream(uri);
                    OutputStream out = new FileOutputStream(new File(outputFileName));

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    in.close();

                    //create resources
                    Resource file = new Resource(outputFileName, getUploadTableName());
                    ArrayList<Resource> resources = new ArrayList<>();
                    resources.add(file);

                    uploadResources(resources);
                } catch (IOException e) {
                    DoctorVetApp.get().handle_error(e, TAG, true);
                }
            } else {
                Snackbar.make(DoctorVetApp.getRootForSnack(EditBaseActivity.this), R.string.error_archivo_muy_grande,Snackbar.LENGTH_LONG).show();
            }
        }
    });

    private void uploadResources(ArrayList<Resource> resources) {
        dialog = ProgressDialog.show(EditBaseActivity.this, "", "Subiendo archivos, espera por favor...");
        DoctorVetApp.get().uploadFiles(resources, new DoctorVetApp.VolleyCallback() {
            @Override
            public void onSuccess(Boolean result) {
                //dialog.dismiss();
                dismissProgressDialog();

                if (result) {
                    getResourceObject().getResources().addAll(resources);
                    if (resourcesAdapter != null)
                        resourcesAdapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(DoctorVetApp.getRootForSnack(EditBaseActivity.this), R.string.error_conexion_servidor, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}