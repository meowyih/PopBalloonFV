package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/20.
 */
public class ItemSun extends ItemMoving {

    final static private String appTag = "ItemSun";

    // debug: recycle counter
    static public int mBitmapRecycleCount = 0;

    final static public int BLINK_LENGTH = 500;

    Bitmap mBitmapSun;
    int mResIdSun;
    Bitmap mBitmapSunBlinkLeft;
    int mResIdSunBlinkLeft;
    Bitmap mBitmapSunBlinkRight;
    int mResIdSunBlinkRight;
    boolean mIsBlinking = false;
    boolean mIsBlinkingLeft = true;
    long mBlinkStartTime = 0;

    public ItemSun(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        // mBitmapSun = BitmapFactory.decodeResource( mContext.getResources(), R.drawable.sun01 );
        mBitmapSun = BitmapWarehouse.getBitmap( mContext, R.drawable.sun01 );
        if ( mBitmapSun != null ) {
            mResIdSun = R.drawable.sun01;
            mBitmapRecycleCount++;
        }

        // mBitmapSunBlinkLeft = BitmapFactory.decodeResource( mContext.getResources(), R.drawable.sun02 );
        mBitmapSunBlinkLeft = BitmapWarehouse.getBitmap( mContext, R.drawable.sun02 );
        if ( mBitmapSunBlinkLeft != null ) {
            mResIdSunBlinkLeft = R.drawable.sun02;
            mBitmapRecycleCount++;
        }

        // mBitmapSunBlinkRight = BitmapFactory.decodeResource( mContext.getResources(), R.drawable.sun03 );
        mBitmapSunBlinkRight = BitmapWarehouse.getBitmap( mContext, R.drawable.sun03 );
        if ( mBitmapSunBlinkRight != null ) {
            mResIdSunBlinkRight = R.drawable.sun03;
            mBitmapRecycleCount++;
        }
    }

    @Override
    public Bitmap getCurrentBitmap() {
        if ( mIsBlinking ) {
            if ( System.currentTimeMillis() - mBlinkStartTime > BLINK_LENGTH ) {
                mIsBlinking = false;
                return mBitmapSun;
            }
            else if ( mIsBlinkingLeft ) {
                return mBitmapSunBlinkLeft;
            }
            else {
                return mBitmapSunBlinkRight;
            }
        }
        return mBitmapSun;
    }

    @Override
    public void setBitmap( int resid ) {
        // we won't accept bitmap from outside
    }

    @Override
    public void destroy() {
        super.destroy();
        if ( mBitmapSun != null && mResIdSun != 0 ) {
            BitmapWarehouse.releaseBitmap( mResIdSun );
            mBitmapSun = null;
            mBitmapRecycleCount--;
        }
        if ( mBitmapSunBlinkLeft != null && mResIdSunBlinkLeft != 0 ) {
            BitmapWarehouse.releaseBitmap( mResIdSunBlinkLeft );
            mBitmapSunBlinkLeft = null;
            mBitmapRecycleCount--;
        }
        if ( mBitmapSunBlinkRight != null && mResIdSunBlinkLeft != 0 ) {
            BitmapWarehouse.releaseBitmap( mResIdSunBlinkRight );
            mBitmapSunBlinkRight = null;
            mBitmapRecycleCount--;
        }
    }

    @Override
    public int onTouchEvent( MotionEvent event ) {
        super.onTouchEvent( event );
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if ( isHit( (int)event.getX(), (int)event.getY())) {
                    mIsBlinking = true;
                    mIsBlinkingLeft = !mIsBlinkingLeft;
                    mBlinkStartTime = System.currentTimeMillis();
                    ThreadGaming.playAudioSfx( ThreadGaming.AUDIO_KID_GIGGLING );
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
            default:
        }

        return 0;
    }
}
