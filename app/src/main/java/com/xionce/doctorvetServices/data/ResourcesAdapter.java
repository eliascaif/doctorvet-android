package com.xionce.doctorvetServices.data;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.xionce.doctorvetServices.utilities.HelperClass.viewFile;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.ViewFullScreenPhoto;
import com.xionce.doctorvetServices.ViewFullScreenVideo;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.io.File;
import java.util.ArrayList;

public class ResourcesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "Pet_clinic_resourcesAd2";
    private final ArrayList<Resource> resources;
    private final ArrayList<Resource> deleted_resources = new ArrayList<>();
    private boolean canRemove = false;
    public boolean isCanRemove() {
        return canRemove;
    }
    public void setCanRemove(boolean canRemove) {
        this.canRemove = canRemove;
    }
    private final Activity baseActivity;

    public ResourcesAdapter(ArrayList<Resource> resources, Boolean canRemove, Activity baseActivity) {
        this.baseActivity = baseActivity;
        this.resources = resources;
        this.canRemove = canRemove;

        for (Resource resource:resources) {
            fillResourcePath(resource);
        }
    }
    private void fillResourcePath(Resource resource) {
        if (resource.getLocal_path() == null) {
            File directory = new File(DoctorVetApp.get().getApplicationContext().getExternalFilesDir(resource.getAndroid_dir_type()).getPath());
            File final_file = new File(directory, resource.getFile_name());
            resource.setLocal_path(final_file.getPath());
        }
    }

    @Override
    public int getItemViewType(int position) {
        Resource resource = this.resources.get(position);

        if (resource.fileExists())
            return resource.getResourceType().ordinal();

        return Resource.resource_types.DOWNLOAD.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        Resource.resource_types resourceType = Resource.resource_types.values()[viewType];
        switch (resourceType) {
            case IMAGE:
                View view_image = inflater.inflate(R.layout.resource_container_image, viewGroup, false);
                return new ResourcesHolderImage(view_image);
            case VIDEO:
                View view_video = inflater.inflate(R.layout.resource_container_video, viewGroup, false);
                return new ResourcesHolderVideo(view_video);
            case AUDIO:
                View view_audio = inflater.inflate(R.layout.resource_container_audio, viewGroup, false);
                return new ResourcesHolderAudio(view_audio);
            case FILE:
                View view_file = inflater.inflate(R.layout.resource_container_file, viewGroup, false);
                return new ResourcesHolderFile(view_file);
            case DOWNLOAD:
                View view_download = inflater.inflate(R.layout.resource_container_download, viewGroup, false);
                return new ResourcesHolderDownload(view_download);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Resource resource = this.resources.get(position);

//        holder.itemView.setVisibility(View.VISIBLE);
//        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        if (resource.getDeleted() != null && resource.getDeleted() == 1) {
//            holder.itemView.setVisibility(View.GONE);
//            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
//            return;
//        }

        switch (resource.getResourceType()) {
            case IMAGE:
                ResourcesHolderImage imageHolder = (ResourcesHolderImage) holder;
                Glide.with(imageHolder.imageView).load(resource.getLocal_path()).apply(RequestOptions.fitCenterTransform()).into(imageHolder.imageView);
                break;
        }
    }

    public void addItems(ArrayList<Resource> items) {
        this.resources.addAll(items);
        notifyDataSetChanged();
    }
    public void addItem(Resource resource) {
        this.resources.add(resource);
        notifyDataSetChanged();
    }
    private void deleteResource(int pos) {
        Resource resource = resources.get(pos);
        resource.setDeleted(1);
        deleted_resources.add(resource);

        resources.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    public ArrayList<Resource> getResources() {
        ArrayList<Resource> all_resources = new ArrayList<>();
        all_resources.addAll(resources);
        all_resources.addAll(deleted_resources);
        return all_resources;
    }

    public class ResourcesHolderImage extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ImageView deleteButton;

        public ResourcesHolderImage(View view) {
            super(view);

            imageView = view.findViewById(R.id.img_image);
            deleteButton = view.findViewById(R.id.img_delete_button);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Resource resource = resources.get(pos);

                    Intent activity = new Intent(baseActivity, ViewFullScreenPhoto.class);
                    activity.putExtra(HelperClass.INTENT_IMAGE_URL, resource.getLocal_path());
                    baseActivity.startActivity(activity);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context ctx = view.getContext();
                    HelperClass.getOKCancelDialog(ctx, ctx.getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteResource(getAdapterPosition());
                        }
                    });
                }
            });

            if (canRemove)
                deleteButton.setVisibility(VISIBLE);
        }
    }
    public class ResourcesHolderVideo extends RecyclerView.ViewHolder {
        private ImageView playButton;
        private ImageView deleteButton;

        public ResourcesHolderVideo(View view) {
            super(view);
            playButton = view.findViewById(R.id.img_play_button);
            deleteButton = view.findViewById(R.id.img_delete_button);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Resource resource = resources.get(pos);
                    Intent intent = new Intent(baseActivity, ViewFullScreenVideo.class);
                    intent.putExtra(HelperClass.INTENT_VIDEO_URL, resource.getLocal_path());
                    baseActivity.startActivity(intent);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context ctx = view.getContext();
                    HelperClass.getOKCancelDialog(ctx, ctx.getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteResource(getAdapterPosition());
                        }
                    });
                }
            });

            if (canRemove)
                deleteButton.setVisibility(VISIBLE);
        }
    }
    public class ResourcesHolderAudio extends RecyclerView.ViewHolder {
        private ImageView playButton;
        private ImageView deleteButton;
        private SeekBar seekBar;

        public ResourcesHolderAudio(View view) {
            super(view);
            playButton = view.findViewById(R.id.img_play_button);
            deleteButton = view.findViewById(R.id.img_delete_button);
            seekBar = view.findViewById(R.id.seekbar);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Resource resource = resources.get(pos);
                    handleAudioClick(resource);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context ctx = view.getContext();
                    HelperClass.getOKCancelDialog(ctx, ctx.getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteResource(getAdapterPosition());
                        }
                    });
                }
            });

            if (canRemove)
                deleteButton.setVisibility(VISIBLE);
        }

        private MediaPlayer mediaPlayer = null;
        private int mediaPlayerSeekTo = 0;
        private Handler handler = new Handler();
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
                    DoctorVetApp.get().handle_error(e, /*baseActivity,*/ TAG, true);
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
                mediaPlayer.setDataSource(baseActivity, Uri.fromFile(new File(resourcePath)));
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
                    mediaPlayer = null;
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        public MediaPlayer getMediaPlayer() {
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();

            return mediaPlayer;
        }
    }
    public class ResourcesHolderFile extends RecyclerView.ViewHolder {
        private ImageView fileImage;
        private ImageView deleteButton;

        public ResourcesHolderFile(View view) {
            super(view);
            fileImage = view.findViewById(R.id.img_file);
            deleteButton = view.findViewById(R.id.img_delete_button);

            fileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Resource resource = resources.get(pos);
                    boolean success = viewFile(resource.getLocal_path(), baseActivity);
                    if (!success)
                        Snackbar.make(DoctorVetApp.getRootForSnack(baseActivity), "No hay app para abrir " + resource.getExtension(), Snackbar.LENGTH_SHORT).show();
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context ctx = view.getContext();
                    HelperClass.getOKCancelDialog(ctx, ctx.getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteResource(getAdapterPosition());
                        }
                    });
                }
            });

            if (canRemove)
                deleteButton.setVisibility(VISIBLE);

        }
    }
    public class ResourcesHolderDownload extends RecyclerView.ViewHolder {
        private ImageView downloadButton;
        private ProgressBar downloadProgressBar;
        private Long downloadProgressLong;

        public ResourcesHolderDownload(View view) {
            super(view);
            downloadButton = view.findViewById(R.id.img_download_button);
            downloadProgressBar = view.findViewById(R.id.resource_container_horizontal_progress_bar);

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Resource resource = resources.get(pos);
                    downloadDownloadManager(resource);
                }
            });

        }

        public void showDownloadProgressBar(boolean show) {
            if (show) {
                this.downloadProgressBar.setIndeterminate(false);
                this.downloadProgressBar.setMax(100);
                this.downloadProgressBar.setVisibility(VISIBLE);
            } else {
                this.downloadProgressBar.setVisibility(INVISIBLE);
            }
        }

        private void downloadDownloadManager(Resource resource) {
            DownloadManager downloadmanager = (DownloadManager) baseActivity.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(resource.getFile_url()));
            request.setVisibleInDownloadsUi(false);
            String fileName = HelperClass.getFileNameFromPath(resource.getLocal_path());
            request.setDestinationInExternalFilesDir(baseActivity, resource.getAndroid_dir_type(), fileName);
            downloadProgressLong = downloadmanager.enqueue(request);
            showDownloadProgressBar(true);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    DownloadManager downloadManager = (DownloadManager) baseActivity.getSystemService(Context.DOWNLOAD_SERVICE);
                    boolean downloading = true;
                    while (downloading) {
                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(downloadProgressLong); //filter by id which you have receieved when reqesting download from download manager
                        Cursor cursor = downloadManager.query(q);
                        cursor.moveToFirst();
                        int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;
                            downloadProgressBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    downloadProgressBar.setVisibility(INVISIBLE);
                                    notifyDataSetChanged();
                                }
                            });
                        }

                        final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                        downloadProgressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                downloadProgressBar.setProgress((int) dl_progress);
                            }
                        });

                        // Log.d(Constants.MAIN_VIEW_ACTIVITY, statusMessage(cursor));
                        cursor.close();
                    }

                }
            }).start();
        }
    }

}