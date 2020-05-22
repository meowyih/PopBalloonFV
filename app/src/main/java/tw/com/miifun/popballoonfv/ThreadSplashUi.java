package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by yhorn on 2016/2/17.
 */
public class ThreadSplashUi extends Thread {

    final static private String appTag = "ThreadSplashUi";

    final static public int REASON_TIMEUP = 1;
    final static public int REASON_INTERRUPT = 2;

    Context mContext;
    android.os.Handler mHandler;
    ImageView mIvLogo;
    ImageView mIvLogoText;
    ImageView mIvLeft, mIvCenter, mIvRight;
    boolean mKeepRunning = true;
    volatile boolean mCalledListener = false;
    volatile boolean mIsUpdateUiComplete;
    long mStartTimeStamp;
    volatile int mIvResId;

    ThreadSplashUiListener mListener = null;


    public interface ThreadSplashUiListener {
        void onThreadSplashUiComplete( ThreadSplashUi thread, int reason );
        void onThreadSplashUiSound();
    }

    public ThreadSplashUi( Context context, ImageView iv, ImageView ivText, ImageView ivLeft, ImageView ivCenter, ImageView ivRight ) {
        mContext = context;
        mHandler = new android.os.Handler(context.getMainLooper());
        mIvLogo = iv;
        mIvLogoText = ivText;
        mIvLeft = ivLeft;
        mIvCenter = ivCenter;
        mIvRight = ivRight;
    }

    public void setListener( ThreadSplashUiListener listener ) {
        mListener = listener;
    }

    // warning! this object is NOT reusable
    // ... although I don't know other than me, whom else will see code :p
    public void stopThread() {
        mKeepRunning = false;
        interrupt();
    }

    @Override
    public void run() {
        Log.v(appTag, "Enter run()");

        try {
            sleep(500);
        }
        catch( Exception e ) {

        }

        mIvResId = 0;
        mStartTimeStamp = System.currentTimeMillis();

        // start sound
        if (mListener != null && mKeepRunning ) {
            mListener.onThreadSplashUiSound();
        }

        while( mKeepRunning ) {
            updateUiThread();
            while( ! mIsUpdateUiComplete ) {
                // do nothing
            }
        }

        if ( ! mCalledListener && mListener != null ) {
            mListener.onThreadSplashUiComplete( ThreadSplashUi.this, REASON_INTERRUPT );
        }

        Log.v(appTag, "Leave run()");
    }

    synchronized void updateUiThread() {
        mIsUpdateUiComplete = false;
        mHandler.post( new Runnable() {
            @Override public void run(){
                long interval = System.currentTimeMillis() - mStartTimeStamp;

                if ( interval >=3000 ) {
                    // leave
                    if ( mListener != null ) {
                        mListener.onThreadSplashUiComplete( ThreadSplashUi.this, REASON_TIMEUP );
                    }
                    mCalledListener = true;
                    mKeepRunning = false;
                }
                else if ( interval >= 1600 ) {

                    if ( mIvResId != R.drawable.splash_cat_middle ) {
                        mIvResId = R.drawable.splash_cat_middle;
                        mIvLogo.setImageResource(mIvResId);

                        // start text animation
                        Animation aniBounce = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                        mIvLogoText.setImageResource(R.drawable.logo_miifun);
                        mIvLogoText.startAnimation(aniBounce);

                        // show balloon background
                        mIvRight.setImageResource(R.drawable.splash_right );
                        mIvCenter.setImageResource(R.drawable.splash_center );
                        mIvLeft.setImageResource(R.drawable.splash_left );
                    }
                }
                else if ( interval >= 600 ) {

                    if ( mIvResId != R.drawable.splash_cat_right ) {
                        mIvResId = R.drawable.splash_cat_right;
                        mIvLogo.setImageResource(mIvResId);
                    }
                }
                else if ( interval >= 400 ) {

                    if ( mIvResId != R.drawable.splash_cat_left ) {
                        mIvResId = R.drawable.splash_cat_left;
                        mIvLogo.setImageResource(mIvResId);
                    }
                }
                else if ( interval >=200 ) {

                    if ( mIvResId != R.drawable.splash_cat_right ) {
                        mIvResId = R.drawable.splash_cat_right;
                        mIvLogo.setImageResource(mIvResId);
                    }
                }
                else if ( interval < 200 ) {

                    if ( mIvResId != R.drawable.splash_cat_left ) {
                        mIvResId = R.drawable.splash_cat_left;
                        // mIvLogo.setImageResource(mIvResId);
                    }
                }

                mIsUpdateUiComplete = true;
            }
        });
    }
}
