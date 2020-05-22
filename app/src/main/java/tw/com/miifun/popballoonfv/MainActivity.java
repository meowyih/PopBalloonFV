package tw.com.miifun.popballoonfv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements
        ThreadSplashUi.ThreadSplashUiListener,
        View.OnClickListener,
        ConfirmDialog.ConfirmDialogListener
{

    final static private String appTag = "MainActivity";
    final static private String STATE_SPLASH_FINISH_TIMESTAMP = "STATE_SPLASH_FINISH_TIMESTAMP";

    final static private int LAYOUT_UNKNOWN = 0;
    final static private int LAYOUT_SPLASH = 1;
    final static private int LAYOUT_PURCHASE = 2;
    final static private int LAYOUT_CANVAS = 3;

    final static private int[][] mStateMachine =
            {
                    { 0, 1, 1, 1 }, // from unknown
                    { 0, 0, 1, 1 }, // from splash
                    { 0, 0, 0, 1 }, // from purchase
                    { 0, 0, 1, 0 }  // from canvas
            };

    final static private String mLayoutName[] =
            {
                    "LAYOUT_UNKNOWN",
                    "LAYOUT_SPLASH",
                    "LAYOUT_PURCHASE",
                    "LAYOUT_CANVAS"
            };

    android.os.Handler mHandler = new android.os.Handler();

    // layout
    int mCurrentLayout;
    RelativeLayout mRlMain, mRlCanvas, mRlSplash;

    // splash
    long mPreviousSplashTimeStamp = 0;
    long mSplashPageFinishedTimeStamp = 0;
    ThreadSplashUi mThreadSplashUi = null;
    ImageView mIvSplashCat;
    ImageView mIvSplashTextLogo;

    // canvas
    CanvasSurfaceView mSvCanvas;
    boolean mNeedCreateCanvasDuringResume = false;

    // sound
    EffectSoundPlayer mEffectSoundPlayer;

    ConfirmDialog mConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(appTag, "onCreate");
        setContentView(R.layout.activity_main);

        // send back the crash log to aclwapi if needed
        String crashlog = AppSetting.readString( getApplication(), AppSetting.PREFS_CRASHLOG );
        if ( crashlog != null && crashlog.length() > 0 ) {
            // cleanup the crash log
            AppSetting.writeString(getApplication(), AppSetting.PREFS_CRASHLOG, null );

            // exit the application, since it is from the AclActivity
            HttpPoster poster = new HttpPoster();
            poster.setListener( new HttpPoster.Listener() {
                @Override
                public void onComplete( String recv, boolean succeed ) {
                    if ( succeed ) {
                        Log.v( appTag, "Sent previous crash log to aclwapi" );
                        AppSetting.writeString(getApplication(), AppSetting.PREFS_CRASHLOG, null );
                    }
                    else {
                        Log.w( appTag, "Warning: failed to send previous crash log to aclwapi" );
                    }
                }
            });
            poster.sendCrashLog( getPackageName(), BuildConfig.VERSION_NAME, crashlog, false );
        }

        if (savedInstanceState != null) {
            mPreviousSplashTimeStamp = savedInstanceState.getLong(STATE_SPLASH_FINISH_TIMESTAMP);
            Log.v(appTag, "Previous splash timestamp " + mPreviousSplashTimeStamp);
        }

        // associate res ID to objects
        onCreateInitViewObject();

        // set full screen and hide nav bar
        onCreateSetFullScreen();

        // music
        mEffectSoundPlayer = new EffectSoundPlayer( this );

        mCurrentLayout = LAYOUT_UNKNOWN;

        // change to default layout
        changeLayout(LAYOUT_SPLASH);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(appTag, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(appTag, "onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(appTag, "onResume, mCurrentLayout is " + mCurrentLayout);

        if ( mNeedCreateCanvasDuringResume ) {
            launchCanvas( true );
        }

        if ( mCurrentLayout == LAYOUT_SPLASH ) {
            launchSplash();
        }
        else if ( mCurrentLayout == LAYOUT_PURCHASE ) {
            launchCanvas( true ); // for safety
        }
    }

    @Override
    public void onPause() {

        Log.v(appTag, "onPause, mCurrentLayout:" + mCurrentLayout + " mSvCanvas:" + mSvCanvas);

        if ( mCurrentLayout == LAYOUT_CANVAS ) {
            if (mSvCanvas != null) {
                mSvCanvas.saveData();
                mRlCanvas.removeView(mSvCanvas);
                mSvCanvas = null;
                mNeedCreateCanvasDuringResume = true;
            }
        }

        stopAllThreads();
        stopAllSound();

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.v(appTag, "onSaveInstanceState");

        // store the leave timestamp
        if (mSplashPageFinishedTimeStamp > 0)
            savedInstanceState.putLong(STATE_SPLASH_FINISH_TIMESTAMP, mSplashPageFinishedTimeStamp);
        else if (mPreviousSplashTimeStamp > 0)
            savedInstanceState.putLong(STATE_SPLASH_FINISH_TIMESTAMP, mPreviousSplashTimeStamp);

        super.onSaveInstanceState(savedInstanceState);
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.v(appTag, "onWindowFocusChanged");

        // hiding the nav bar after user hit the volume key
        // check onCreateSetFullScreen();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onDestroy() {
        Log.v(appTag, "onDestroy");

        stopAllThreads();
        stopAllSound();

        super.onDestroy();
    }

    /*
     * View.OnClickListener
     */
    @Override
    public void onClick( View view ) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.v(appTag, "onKeyDown KeyEvent.KEYCODE_BACK");
            startConfirmDialog(ConfirmDialog.TYPE_EXIT, R.string.title_exit, R.string.desc_exit);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }

    private void onCreateSetFullScreen() {
        // keep the landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // hide the nav bar and make it full screen
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }
    }

    private void onCreateInitViewObject() {
        // layout
        mRlMain = (RelativeLayout) findViewById( R.id.rl_main );
        mRlCanvas = (RelativeLayout) findViewById( R.id.rl_canvas_layout );
        mRlSplash = (RelativeLayout) findViewById( R.id.rl_splash_layout );

        // splash page
        mIvSplashCat = (ImageView) findViewById( R.id.iv_splash_cat );
        mIvSplashTextLogo = (ImageView) findViewById( R.id.iv_splash_text_logo );

    }

    private synchronized void changeLayout( int layout ) {

        // check state machine to see if mCurrentLayout can switch to layout
        if ( mStateMachine[mCurrentLayout][layout] == 0 ) {
            Log.e(appTag, "state error " + mLayoutName[mCurrentLayout] +
                    " cannot change to " + mLayoutName[layout]);
            return;
        }

        switch( layout ) {
            case LAYOUT_SPLASH:
                mCurrentLayout = LAYOUT_SPLASH;
                mRlCanvas.setVisibility( View.GONE );
                mRlSplash.setVisibility( View.VISIBLE );
                launchSplash();
                break;

            case LAYOUT_PURCHASE:
            case LAYOUT_CANVAS:
                mCurrentLayout = LAYOUT_CANVAS;
                mRlCanvas.setVisibility( View.VISIBLE );
                mRlSplash.setVisibility( View.GONE );
                launchCanvas( false );
                break;

            case LAYOUT_UNKNOWN:
            default:
                Log.w( appTag, "unexpected target layout " + layout );
        }

        return;
    }

    // splash page
    void launchSplash() {
        if ( mThreadSplashUi != null ) {
            Log.w(appTag, "launchSplash - mThreadSplashUi already exist, stop it");
            mThreadSplashUi.stopThread();
        }

        mThreadSplashUi = new ThreadSplashUi( this, mIvSplashCat, mIvSplashTextLogo,
                (ImageView) findViewById( R.id.iv_bk_left ),
                (ImageView) findViewById( R.id.iv_bk_center ),
                (ImageView) findViewById( R.id.iv_bk_right ));

        mThreadSplashUi.setListener( this );
        mThreadSplashUi.start();
    }

    @Override
    public void onThreadSplashUiComplete( ThreadSplashUi thread, int reason ) {

        mSplashPageFinishedTimeStamp = System.currentTimeMillis();

        if ( thread == mThreadSplashUi && reason == ThreadSplashUi.REASON_TIMEUP ) {
            mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        changeLayout(LAYOUT_CANVAS);
                    }
                });
        }
    }

    @Override
    public void onThreadSplashUiSound() {
        mEffectSoundPlayer.playEffectSound(EffectSoundPlayer.SPLASH_PAGE);
    }

    // canvas page
    void launchCanvas( boolean reloadData ) {

        Log.v( appTag, "launchCanvas, reloadData:" + reloadData );

        // remove previous video surface view
        if (mSvCanvas != null) {
            mRlCanvas.removeView(mSvCanvas);
        }

        // create SurfaceView
        mSvCanvas = new CanvasSurfaceView(this, isFullVersion(), reloadData);
        ViewGroup.LayoutParams param = mSvCanvas.getLayoutParams();

        if (param == null) {
            param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            param.width = ViewGroup.LayoutParams.MATCH_PARENT;
            param.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mSvCanvas.setLayoutParams(param);
        mRlCanvas.addView(mSvCanvas);
    }

    // in-app purchase
    boolean isFullVersion() {
        return true;
    }

    // clean-up
    void stopAllThreads() {
        if ( mThreadSplashUi != null)
            mThreadSplashUi.stopThread();
        mThreadSplashUi = null;

    }

    // clean-up
    void stopAllSound() {
        mEffectSoundPlayer.playEffectSound( EffectSoundPlayer.STOP );
    }

    private void startConfirmDialog(int dialogType, int titleResId, int descResId, String errorCode ) {
        if (mConfirmDialog == null) {
            // it is annoying to show all error dialog for each track
            FragmentManager fm = this.getFragmentManager();
            mConfirmDialog = new ConfirmDialog();
            mConfirmDialog.setListener(this);
            Bundle bundle = new Bundle();
            bundle.putInt(ConfirmDialog.PARAM_DIALOG_TYPE, dialogType);
            bundle.putString(ConfirmDialog.PARAM_DIALOG_TITLE, this.getResources().getString(titleResId));
            bundle.putString(ConfirmDialog.PARAM_DIALOG_DESC, this.getResources().getString(descResId));
            bundle.putString(ConfirmDialog.PARAM_DIALOG_ERROR_CODE, errorCode );

            mConfirmDialog.setArguments(bundle);
            mConfirmDialog.show(fm, "startConfirmDialog");
        } else {
            Log.w(appTag, "warning, cannot display dialog since it exists " + mConfirmDialog);
        }
    }

    private void startConfirmDialog(int dialogType, int titleResId, int descResId) {
        startConfirmDialog( dialogType, titleResId, descResId, "" );
    }

    /*
     * ConfirmDialog.ConfirmDialogListener
     */
    @Override
    public void onFinishConfirmDialog(int type, int result) {
        Log.v(appTag, "onFinishConfirmDialog " + type + " " + result);
        if (type == ConfirmDialog.TYPE_EXIT) {
            if (result == ConfirmDialog.POSITIVE) {
                if (mSvCanvas == null) {
                    finish();
                } else {
                    mSvCanvas.safetyFinishActivity();
                }
            }
        }
        mConfirmDialog = null;
    }
}
