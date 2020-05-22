package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by yhorn on 2016/2/24.
 */
public class ItemThumb extends ItemMoving {

    Bitmap mBitmap01, mBitmap02, mBitmap03, mBitmap04;
    long mStartTime, mStopTime, mSwitchInterval;

    public ItemThumb(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        mBitmap01 = BitmapWarehouse.getBitmap( mContext, R.drawable.thumb01 );
        mBitmap02 = BitmapWarehouse.getBitmap( mContext, R.drawable.thumb02 );
        mBitmap03 = BitmapWarehouse.getBitmap( mContext, R.drawable.thumb03 );
        mBitmap04 = BitmapWarehouse.getBitmap( mContext, R.drawable.thumb04 );
    }

    public void setTime( long startTime, long stopTime, long switchInterval ) {
        mStartTime = startTime;
        mStopTime = stopTime;
        mSwitchInterval = switchInterval;
    }

    @Override
    public boolean draw( Canvas canvas ) {
        long time = System.currentTimeMillis();
        if ( time < mStartTime || time > mStopTime )
            return false;
        else
            return super.draw( canvas );
    }

    @Override
    public Bitmap getCurrentBitmap() {
        long time = System.currentTimeMillis();
        long diff = time - mStartTime;
        long remain = diff % ( mSwitchInterval * 4 );
        if ( time < mStartTime || time > mStopTime )
            return mBitmap01;

        if ( remain < mSwitchInterval ) {
            return mBitmap01;
        }
        else if ( remain < mSwitchInterval * 2 ) {
            return mBitmap02;
        }
        else if ( remain < mSwitchInterval * 3 ) {
            return mBitmap03;
        }
        else {
            return mBitmap04;
        }
    }

    @Override
    public void destroy() {
        BitmapWarehouse.releaseBitmap( R.drawable.thumb01 );
        BitmapWarehouse.releaseBitmap( R.drawable.thumb02 );
        BitmapWarehouse.releaseBitmap( R.drawable.thumb03 );
        BitmapWarehouse.releaseBitmap( R.drawable.thumb04 );
    }
}
