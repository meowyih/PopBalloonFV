package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by yhorn on 2016/2/18.
 */
public class CanvasSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String appTag = "CanvasSurfaceView";

    ThreadGaming mThread;
    Context mContext;
    SurfaceHolder mSurfaceHolder;
    int mScreenHeight;
    int mScreenWidth;

    boolean mRequestToFinishActivity = false;
    boolean mIsFullVersion;
    boolean mReloadData;

    public CanvasSurfaceView( Context context, boolean isFullVersion, boolean reloadData ) {
        super(context);
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mIsFullVersion = isFullVersion;
        mReloadData = reloadData;
    }

    public void saveData() {
        if ( mThread != null ) {
            mThread.saveData();
        }
        else {
            Log.w( appTag, "saveData failed, mThread is null" );
        }
    }

    /*
     * SurfaceHolder.Callback
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(appTag, "surfaceChanged " + format + " " + " width " + width + " height " + height);
        mScreenWidth = width;
        mScreenHeight = height;

        if ( mRequestToFinishActivity ) {
            Log.v( appTag, "surfaceChanged -no need to create game thread since Activity request to finish" );
            return;
        }

        if ( mThread != null ) {
            Log.w( appTag, "surfaceChanged - warning: existing another game thread, but it is ok if application is going to finish" );
            return;
            // mThread.stopThread();
        }


        if ( mIsFullVersion ) {
            mThread = new ThreadGaming(mContext, holder, width, height, mReloadData );
        }
        else {
            // [2016/06/06] convert to ad-only and remove the limitation of ad version
            mThread = new ThreadGaming(mContext, holder, width, height, mReloadData );
            // mThread = new ThreadGamingTrial(mContext, holder, width, height, mReloadData );
        }
        mThread.start();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Create MediaPlayer only after the SurfaceView has been created
        Log.v(appTag, "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if ( mThread != null ) {
            mThread.stopThread();
            Log.v(appTag, "surfaceDestroyed - stop game thread");
        }
        else {
            Log.w( appTag, "surfaceDestroyed - no game thread to stop");
        }
        mThread = null;

        Log.v(appTag, "surfaceDestroyed");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                // Log.v( appTag, "ACTION_DOWN" );
                break;
            case MotionEvent.ACTION_MOVE:
                // Log.v( appTag, "ACTION_MOVE" );
                break;
            case MotionEvent.ACTION_UP:
                // Log.v( appTag, "ACTION_UP" );
                break;
            case MotionEvent.ACTION_CANCEL:
                // Log.v( appTag, "ACTION_CANCEL" );
                break;
            case MotionEvent.ACTION_OUTSIDE:
                // Log.v( appTag, "ACTION_OUTSIDE" );
                break;
            default:
        }

        if ( mThread != null )
            return mThread.onTouchEvent( event );
        else
            return false;
    }

    public void safetyFinishActivity() {

        Log.v( appTag, "safetyFinishActivity" );

        mRequestToFinishActivity = true;

        if ( mThread != null )
            mThread.stopThread( true );
    }
}
