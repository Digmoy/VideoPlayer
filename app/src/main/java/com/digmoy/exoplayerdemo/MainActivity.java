package com.digmoy.exoplayerdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    ProgressBar progress_bar;

    private ImageView controllerLock;
    private ImageView playPause;
    private ImageView screenRotation;
    private ConstraintLayout bottomContainer;
    private ConstraintLayout topContainer;
    private ProgressBar progress;
    private ImageView backwardPlayback;
    private ImageView forwardPlayback;
    private ImageView volumeMute;
    private ImageView brightnessVolumeImage;
    private TextView counter;
    private Group counterGroup;
    private TextView increaseSpeed;
    private TextView speedText;
    private TextView decreaseSpeed;
    private Group speedControlGroup;
    private ImageView repeatOnOff;
    private TextView aspectRatio;
    private TextView pictureInPicture;

    private AudioManager audioManager;

    private boolean isControllingVolume = false;
    private boolean isControllingBrightness = false;
    private boolean isPlaying = true;
    private boolean isControlLocked = false;
    private boolean isHorizontalScrolling = false;
    private boolean isVerticalScrolling = false;
    private boolean isControllerVisible = false;
    private boolean isControllingPlayback = false;
    private boolean isVolumeMute = false;
    private boolean isRepeatEnabled = false;
    private boolean iWantToBeInPipModeNow = false;

    private float speed = 0;
    private int brightness = 0;
    private int volume = 0;
    private static final int maxValue = 100;
    private static final int minValue = 0;
    private static int horizontalScrollThreshold = 0;
    private float motionDownXPosition = 0;
    private float motionDownYPosition = 0;
    private int currentAspectRatio = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);
        progress_bar = findViewById(R.id.progress_bar);
        bottomContainer = playerView.findViewById(R.id.bottomContainer);
        screenRotation = playerView.findViewById(R.id.screenRotation);
        backwardPlayback = playerView.findViewById(R.id.backwardPlayback);
        playPause = playerView.findViewById(R.id.playPause);
        forwardPlayback = playerView.findViewById(R.id.forwardPlayback);
        repeatOnOff = playerView.findViewById(R.id.repeatOnOff);
        aspectRatio = playerView.findViewById(R.id.aspectRatio);
        pictureInPicture = playerView.findViewById(R.id.pictureInPicture);

        topContainer = playerView.findViewById(R.id.topContainer);
        controllerLock = playerView.findViewById(R.id.controllerLock);
        volumeMute = playerView.findViewById(R.id.volumeMute);


        brightnessVolumeImage = playerView.findViewById(R.id.brightnessVolumeImage);
        counter = playerView.findViewById(R.id.counter);
        counterGroup = playerView.findViewById(R.id.counterGroup);

        increaseSpeed = playerView.findViewById(R.id.increaseSpeed);
        speedText = playerView.findViewById(R.id.speedText);
        decreaseSpeed = playerView.findViewById(R.id.decreaseSpeed);
        speedControlGroup = playerView.findViewById(R.id.speedControlGroup);

        Uri videoUrl = Uri.parse("https://i.imgur.com/7bMqysJ.mp4");

        LoadControl loadControl = new DefaultLoadControl();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(MainActivity.this,trackSelector,loadControl);

        DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory("exoplayer_video");

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ExtractorMediaSource(videoUrl,factory,extractorsFactory,null,null);

        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);

        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playbackState == Player.STATE_BUFFERING){
                    progress_bar.setVisibility(View.VISIBLE);
                }
                else if (playbackState == Player.STATE_READY){
                    progress_bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        setValues();
        clickListener();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void clickListener() {

        screenRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOrientationControl();
            }
        });
        controllerLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLockControl();
            }
        });
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayPause();
            }
        });
        increaseSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeedPlayback(true);
            }
        });
        decreaseSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeedPlayback(false);
            }
        });

        backwardPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set10SecForwardBackwardPlayback(false);
            }
        });
        forwardPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set10SecForwardBackwardPlayback(true);
            }
        });
        volumeMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVolumeMute();
            }
        });
        repeatOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeatOnOff();
            }
        });
        aspectRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAspectRatio();
            }
        });
        pictureInPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPictureInPictureMode();
            }
        });
        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (simpleExoPlayer != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            motionDownXPosition = event.getX();
                            motionDownYPosition = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            handleTouchEvent(event);
                            break;
                        case MotionEvent.ACTION_UP:
                            if (isPlaying) {
                                simpleExoPlayer.setPlayWhenReady(true);
                            }
                            showHideController(event);
                            resetValues();
                            break;
                    }
                }
                return true;
            }
        });
    }

    private void handleTouchEvent(MotionEvent event) {
        if (!isControlLocked) {
            if (!isHorizontalScrolling && !isVerticalScrolling) {
                if (motionDownYPosition - event.getY() > 50 || event.getY() - motionDownYPosition > 50) {
                    isHorizontalScrolling = true;
                    isVerticalScrolling = false;
                } else if (motionDownXPosition - event.getX() > 50 || event.getX() - motionDownXPosition > 50) {
                    isHorizontalScrolling = false;
                    isVerticalScrolling = true;
                }
            }
            if (isHorizontalScrolling) {
                if (event.getX() > getScreenHeightWidth()[0] / 2) { //right
                    isControllingVolume = true;
                    controlVolume(event);
                } else if (event.getX() < getScreenHeightWidth()[0] / 2) {  //left
                    isControllingBrightness = true;
                    controlBrightness(event);
                }
            } else if (isVerticalScrolling) {
                isControllingPlayback = true;
                controlPlayback(event);
            }
        }

    }
    private void setPictureInPictureMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         //   enterPictureInPictureMode();
            navToLauncherTask(MainActivity.this);
            playerView.setUseController(false);
        }
    }

    private void setRepeatOnOff() {
        if(isRepeatEnabled){
            repeatOnOff.setImageResource(R.drawable.ic_repeat_off);
        }else{
            repeatOnOff.setImageResource(R.drawable.ic_repeat);
        }
        isRepeatEnabled = !isRepeatEnabled;
    }

    private void setAspectRatio() {
        playerView.showController();
        currentAspectRatio++;
        if(currentAspectRatio > 4){
            currentAspectRatio = 0;
        }
        switch (currentAspectRatio){
            case 0:
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                aspectRatio.setText("Fit");
                break;
            case 1:
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                aspectRatio.setText("V");
                break;
            case 2:
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                aspectRatio.setText("H");
                break;
            case 3:
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                aspectRatio.setText("Fill");
                break;
            case 4:
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                aspectRatio.setText("Zoom");
                break;

        }

    }

    private void set10SecForwardBackwardPlayback(boolean isIncreasing) {
        playerView.showController();
        long currentDuration = simpleExoPlayer.getContentPosition();
        long totalDuration = simpleExoPlayer.getDuration();
        if (isIncreasing) {
            currentDuration += 10000;
        } else {
            currentDuration -= 10000;
        }
        if (currentDuration >= totalDuration) {
            currentDuration = totalDuration;
        } else if (currentDuration <= 0) {
            currentDuration = 0;
        }
        simpleExoPlayer.seekTo(currentDuration);
    }

    private void showHideController(MotionEvent event) {
        if (motionDownXPosition == event.getX() || motionDownYPosition == event.getY()) {
            controllerVisibility();
            if (isControllerVisible) {
                playerView.hideController();
            } else {
                playerView.showController();
            }
            isControllerVisible = !isControllerVisible;
        }
    }

    private void setSpeedPlayback(boolean isIncreasing) {
        playerView.showController();
        float speedThreshold = 0.25f;
        if (simpleExoPlayer != null) {
            if (isIncreasing) {
                speed += speedThreshold;
            } else {
                speed -= speedThreshold;
            }
            simpleExoPlayer.setPlaybackParameters(new PlaybackParameters(speed,0));
            speedText.setText(String.format("%s x", String.format(Locale.ENGLISH, "%.2f", speed)));
        }
    }

    private void setVolumeMute() {
        playerView.showController();
        if (isVolumeMute) {
            simpleExoPlayer.setVolume(1f);
            volumeMute.setImageResource(R.drawable.ic_volume_up);
        } else {
            simpleExoPlayer.setVolume(0f);
            volumeMute.setImageResource(R.drawable.ic_volume_off);
        }
        isVolumeMute = !isVolumeMute;
    }

    private void setValues() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * (100 / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        try {
            brightness = (((Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)) / 255) * 100);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        horizontalScrollThreshold = (getScreenHeightWidth()[1] / maxValue) / 2;
    }

    private int[] getScreenHeightWidth() {
        int[] heightWidth = new int[2];
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        heightWidth[0] = size.x;  //width
        heightWidth[1] = size.y;  //height
        return heightWidth;
    }

    private void setOrientationControl() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    private void setLockControl() {
        if (isControlLocked) {
            controllerLock.setImageResource(R.drawable.ic_lock);
        } else {
            controllerLock.setImageResource(R.drawable.ic_unlock);
        }
        isControlLocked = !isControlLocked;
        controllerVisibility();
    }

    private void controllerVisibility() {
        if (isControllingVolume) {
            brightnessVolumeImage.setImageResource(R.drawable.ic_volume_up);
            counterGroup.setVisibility(View.VISIBLE);
            speedControlGroup.setVisibility(View.GONE);
            topContainer.setVisibility(View.GONE);
            bottomContainer.setVisibility(View.GONE);
        } else if (isControllingBrightness) {
            brightnessVolumeImage.setImageResource(R.drawable.ic_brightness);
            counterGroup.setVisibility(View.VISIBLE);
            speedControlGroup.setVisibility(View.GONE);
            topContainer.setVisibility(View.GONE);
            bottomContainer.setVisibility(View.GONE);
        } else if (isControllingPlayback) {
            brightnessVolumeImage.setImageResource(0);
            counterGroup.setVisibility(View.VISIBLE);
            speedControlGroup.setVisibility(View.GONE);
            topContainer.setVisibility(View.GONE);
            bottomContainer.setVisibility(View.GONE);
        } else {
            if (isControlLocked) {
                bottomContainer.setVisibility(View.GONE);
                topContainer.setVisibility(View.VISIBLE);
                topContainer.setBackground(null);
                volumeMute.setVisibility(View.GONE);
                counterGroup.setVisibility(View.GONE);
                speedControlGroup.setVisibility(View.GONE);
                controllerLock.setVisibility(View.VISIBLE);
            } else {
                bottomContainer.setVisibility(View.VISIBLE);
                topContainer.setVisibility(View.VISIBLE);
                topContainer.setBackground(getResources().getDrawable(R.drawable.video_controller_gradiant_background_top));
                volumeMute.setVisibility(View.VISIBLE);
                counterGroup.setVisibility(View.GONE);
                speedControlGroup.setVisibility(View.VISIBLE);
                controllerLock.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setPlayPause() {
        if (isPlaying) {
            simpleExoPlayer.setPlayWhenReady(false);
            playPause.setImageResource(R.drawable.ic_play);
        } else {
            simpleExoPlayer.setPlayWhenReady(true);
            playPause.setImageResource(R.drawable.ic_pause);
        }
        isPlaying = !isPlaying;
    }

    private void resetValues() {
        motionDownXPosition = 0;
        motionDownYPosition = 0;
        isControllingVolume = false;
        isControllingBrightness = false;
        isHorizontalScrolling = false;
        isVerticalScrolling = false;
        isControllingPlayback = false;
    }
    private void controlVolume(MotionEvent event) {
        int newVolume = volume;

        newVolume = getNewSwipedValue(event, volume, newVolume, 1, horizontalScrollThreshold);
        if (newVolume >= maxValue) {
            newVolume = maxValue;
        } else if (newVolume <= minValue) {
            newVolume = minValue;
        }
        int convertedNewVolume = (newVolume * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) / 100;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, convertedNewVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        counter.setText(String.valueOf(newVolume));
        volume = newVolume;
    }

    private int getNewSwipedValue(MotionEvent event, int value, int newValue, int step, int threshold) {
        int swipeDifference;
        controllerVisibility();
        if (motionDownYPosition > event.getY()) { // swiped up
            swipeDifference = (int) (motionDownYPosition - event.getY());
            if (swipeDifference > threshold) {
                newValue = value + step;
                motionDownYPosition = event.getY();
                playerView.showController();
            }
        } else if (motionDownYPosition < event.getY()) {  //swiped down
            swipeDifference = (int) (event.getY() - motionDownYPosition);
            if (swipeDifference > threshold) {
                newValue = value - step;
                motionDownYPosition = event.getY();
                playerView.showController();
            }
        }
        return newValue;
    }

    private void controlBrightness(MotionEvent event) {
        int newBrightness = brightness;
        newBrightness = getNewSwipedValue(event, brightness, newBrightness, 1, horizontalScrollThreshold);
        if (newBrightness >= maxValue) {
            newBrightness = maxValue;
        } else if (newBrightness <= minValue) {
            newBrightness = minValue;
        }
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = (float) newBrightness / 100;
        getWindow().setAttributes(layoutParams);
        counter.setText(String.valueOf(newBrightness));
        brightness = newBrightness;

    }

    private void controlPlayback(MotionEvent event) {
        simpleExoPlayer.setPlayWhenReady(false);
        long currentDuration = simpleExoPlayer.getContentPosition();
        long totalDuration = simpleExoPlayer.getDuration();
        float newDuration = currentDuration;
        controllerVisibility();
        playerView.showController();
        int verticalScrollThreshold = 500;
        if (motionDownXPosition > event.getX()) { //swiped left
            newDuration = currentDuration - verticalScrollThreshold;
        } else if (motionDownXPosition < event.getX()) { //swiped right
            newDuration = currentDuration + verticalScrollThreshold;
        }
        motionDownXPosition = event.getX();
        if (newDuration >= totalDuration) {
            newDuration = totalDuration;
        } else if (newDuration <= 0) {
            newDuration = 0;
        }
        simpleExoPlayer.seekTo((long) newDuration);

        long hours = TimeUnit.MILLISECONDS.toHours(simpleExoPlayer.getCurrentPosition());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(simpleExoPlayer.getCurrentPosition()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(simpleExoPlayer.getCurrentPosition()));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(simpleExoPlayer.getCurrentPosition()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(simpleExoPlayer.getCurrentPosition()));
        String milliSecToHMS = String.format(Locale.ENGLISH, "%02d.%02d.%02d", hours, minutes, seconds);
        counter.setText(milliSecToHMS);

    }

    public static void navToLauncherTask(Context appContext) {
        ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        final List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
        for (ActivityManager.AppTask task : appTasks) {
            final Intent baseIntent = task.getTaskInfo().baseIntent;
            final Set<String> categories = baseIntent.getCategories();
            if (categories != null && categories.contains(Intent.CATEGORY_LAUNCHER)) {
                task.finishAndRemoveTask();
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.getPlaybackState();
    }
}