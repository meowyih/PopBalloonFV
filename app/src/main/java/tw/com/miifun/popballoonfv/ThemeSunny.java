package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;

/**
 * Created by yhorn on 2016/2/19.
 */
public class ThemeSunny extends Theme {

    final static private String appTag = "ThemeSunny";
    final static private int AIR_CAB_APPEAR_INTERVAL = 15000; // 15 sec
    final static private int AIR_CAB_FLIGHT_DURATION = 5000; // 5 sec

    final static private int HELICOPTER_FIRST_APPEAR_TIME = 3000;
    final static private int HELICOPTER_APPEAR_INTERVAL = 15000; // 15 sec
    final static private int HELICOPTER_FLIGHT_DURATION = 6000; // 10 sec

    static public int mBitmapRecycleCounter = 0;

    private long DURATION_IN = 5000;
    private long DURATION_OUT = 5000;

    long mThemeInTime, mThemeOutTime;
    long mThemeEnterRunStateTime;

    // sun
    ItemMoving mItemSun;
    int mSunWidth, mSunHeight;
    int mSunInX, mSunInY;
    int mSunOutX, mSunOutY;
    int mSunX, mSunY;

    // cloud#1
    ItemSunnyCloud mC1;
    int mC1w, mC1h;
    int mC1x, mC1y;
    int mC1xIn, mC1yIn;
    int mC1xOut, mC1yOut;

    // cloud#2
    ItemSunnyCloud mC2;
    int mC2w, mC2h;
    int mC2x, mC2y;
    int mC2xIn, mC2yIn;
    int mC2xOut, mC2yOut;

    // cloud#3
    ItemSunnyCloud mC3;
    int mC3w, mC3h;
    int mC3x, mC3y;
    int mC3xIn, mC3yIn;
    int mC3xOut, mC3yOut;

    // AirCab
    long mLastAircabCreate;
    ItemMoving mAircab;
    int mAircabWidth, mAircabHeight;
    int mAircabXin, mAircabYin;

    // helicopter
    long mLastHeliCreate;
    ItemHelicopter mHeli;
    int mHeliWidth, mHeliHeight;
    int mHeliXin, mHeliYin;

    public ThemeSunny( Context context, int screenWidth, int screenHeight ) {

        super( context, screenWidth, screenHeight );

        Bitmap bp;

        mContext = context;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;

        mSunX = mScreenWidth / 10;
        mSunY = mScreenHeight / 12;
        mSunWidth = mScreenWidth / 4;
        mSunHeight = ( mScreenWidth / 4 ) * 332 / 461;

        // right in , left out
        mSunInX = mScreenWidth;
        mSunInY = mSunY;
        mSunOutX = 0 - mSunWidth;
        mSunOutY = mSunY;

        mItemSun = new ItemSun( context, 0,
                mSunOutX, mSunOutY, // x, y
                mSunWidth, mSunHeight, // height, width
                screenWidth, screenHeight ); // screen size

        // cloud#1
        mC1x = mSunX - mSunWidth / 5;
        mC1y = mSunY + mSunHeight * 4 / 5;
        mC1h = mSunWidth / 2;
        mC1w = mC1h * 425 / 228;

        mC1xIn = mScreenWidth;
        mC1yIn = mC1y;
        mC1xOut = 0 - mC1w;
        mC1yOut = mC1y;

        mC1 = new ItemSunnyCloud( context, ItemSunnyCloud.TYPE_NORMAL,
                mC1xIn, mC1yIn, // x, y
                mC1w, mC1h, // height, width
                screenWidth, screenHeight ); // screen size

        // cloud#2
        mC2x = mScreenWidth * 2 / 3;
        mC2y = mScreenHeight / 5;
        mC2h = mScreenHeight / 5;
        mC2w = mC2h * 425 / 228;

        mC2xIn = 0 - mC2w;
        mC2yIn = mC2y;
        mC2xOut = 0 - mC2w;
        mC2yOut = mC2y;

        mC2 = new ItemSunnyCloud( context, ItemSunnyCloud.TYPE_MIRROR,
                mC2xIn, mC2yIn, // x, y
                mC2w, mC2h, // height, width
                screenWidth, screenHeight ); // screen size

        // cloud#3
        mC3x = mScreenWidth * 3 / 5;
        mC3y = mScreenHeight * 3 / 5;
        mC3h = (int)(mC1h * 1.3);
        mC3w = mC3h * 425 / 228;

        mC3xIn = mScreenWidth;
        mC3yIn = mC3y;
        mC3xOut = 0 - mC3w;
        mC3yOut = mC3y;

        mC3 = new ItemSunnyCloud( context, ItemSunnyCloud.TYPE_NORMAL,
                mC3xIn, mC3yIn, // x, y
                mC3w, mC3h, // height, width
                screenWidth, screenHeight ); // screen size

        // create an aircab immediately after theme created
        mAircabWidth = mScreenWidth / 4;
        mAircabHeight = mAircabWidth * context.getResources().getInteger( R.integer.aircab_height )
                / context.getResources().getInteger( R.integer.aircab_width );
        mAircabXin = mScreenWidth;
        mAircabYin = mScreenHeight / 2;

        // create an aircab immediately after theme created
        mHeliWidth = mScreenWidth / 4;
        mHeliHeight = mAircabWidth * context.getResources().getInteger( R.integer.helicopter_height )
                / context.getResources().getInteger( R.integer.helicopter_width );
        mHeliXin = mScreenWidth;
        mHeliYin = mScreenHeight / 2;

        // state
        mState = Theme.STATE_UNKNOWN;
    }

    @Override
    public int id() { return THEME_SUNNY; }

    @Override
    public long durationIn() {
        return DURATION_IN;
    }

    @Override
     public void startThemeIn( long time ) {
        mState = Theme.STATE_IN;
        mThemeInTime = time;

        // set sun destination
        mItemSun.setDestination( mSunX, mSunY, time, durationIn() );
        mC1.setDestination( mC1x, mC1y, time, durationIn() );
        mC2.setDestination( mC2x, mC2y, time, durationIn() );
        mC3.setDestination( mC3x, mC3y, time, durationIn() );
    }

    @Override
    public void startThemeOut( long time ) {
        mState = Theme.STATE_OUT;
        mThemeOutTime = time;

        // set sun destination
        mItemSun.setDestination( mSunOutX, mSunOutY, time, durationOut() );
        mC1.setDestination( mC1xOut, mC1yOut, time, durationOut() );
        mC2.setDestination( mC2xOut, mC2yOut, time, durationOut() );
        mC3.setDestination( mC3xOut, mC3yOut, time, durationOut() );
    }

    @Override
    public void skipThemeIn( long time ) {
        mState = Theme.STATE_RUN;

        mThemeEnterRunStateTime = time;

        // reset position
        mItemSun.setDestination( mSunX, mSunY, time, 0 );
        mC1.setDestination( mC1x, mC1y, time, 0 );
        mC2.setDestination( mC2x, mC2y, time, 0 );
        mC3.setDestination( mC3x, mC3y, time, 0 );

        mLastAircabCreate = time;
    }

    @Override
    public long durationOut() {
        return DURATION_OUT;
    }

    @Override
    public DrawableItemEvent move( long time ) {

        if ( mState == STATE_IN ) {
            if ( time - mThemeInTime > durationIn() ) {
                mState = STATE_RUN;
                mThemeEnterRunStateTime = time;
            }
        }

        if ( mState == STATE_OUT ) {
            if ( time - mThemeOutTime > durationOut()) {
                mState = STATE_RUN;
                mThemeEnterRunStateTime = time;
            }
        }

        mItemSun.move(time);
        mC1.move(time);
        mC2.move(time);
        mC3.move(time);
        if ( mAircab != null ) {
            DrawableItemEvent event = mAircab.move(time);

            if ( event.type == DrawableItemEvent.DEAD ) {
                mAircab.destroy();
                mAircab = null;
            }
        }

        if ( mAircab == null && ( mLastAircabCreate == 0 ||
                time - mLastAircabCreate >= AIR_CAB_APPEAR_INTERVAL ) &&
                mState == Theme.STATE_RUN ) {
            int xOut;
            mAircabXin = ( mAircabXin <= 0 ? mScreenWidth : 0 - mAircabWidth );
            mAircabYin = ( mAircabYin == mScreenHeight / 2 ? mScreenHeight / 3 : mScreenHeight / 2 );
            mAircab = new ItemAircab( mContext, 0, mAircabXin, mAircabYin, mAircabWidth, mAircabHeight, mScreenWidth, mScreenHeight );
            mAircab.setBitmap( R.drawable.airplane);
            xOut = ( mAircabXin <= 0 ? mScreenWidth : 0 - mAircabWidth );
            mAircab .setDestination( xOut, mAircabYin, time, AIR_CAB_FLIGHT_DURATION );
            mLastAircabCreate = time;
        }

        if ( mHeli != null ) {
            DrawableItemEvent event = mHeli.move(time);

            if ( event.type == DrawableItemEvent.DEAD ) {
                mHeli.destroy();
                mHeli = null;
            }
        }

        if ( mHeli == null &&
                (( mLastHeliCreate == 0 && time - mThemeEnterRunStateTime > HELICOPTER_FIRST_APPEAR_TIME ) ||
                ( mLastHeliCreate != 0 && time - mLastHeliCreate >= HELICOPTER_APPEAR_INTERVAL )) &&
                mState == Theme.STATE_RUN ) {

            if ( (int)(Math.random() * 2) == 0 ) {
                // right in
                mHeli = new ItemHelicopter( mContext, ItemHelicopter.TYPE_RIGHT_IN, mHeliXin, mHeliYin, mHeliWidth, mHeliHeight, mScreenWidth, mScreenHeight );
                mHeli.setDestination(0 - mHeliWidth, mHeliYin, time, HELICOPTER_FLIGHT_DURATION);
                mLastHeliCreate = time;
            }
            else {
                // left in
                mHeli = new ItemHelicopter( mContext, ItemHelicopter.TYPE_LEFT_IN, 0, mHeliYin, mHeliWidth, mHeliHeight, mScreenWidth, mScreenHeight );
                mHeli.setDestination( mHeliXin, mHeliYin, time, HELICOPTER_FLIGHT_DURATION);
                mLastHeliCreate = time;
            }
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, 0, 0 );
    }

    @Override
    public boolean drawTheme( Canvas canvas ) {

        // draw background
        Paint paint = new Paint();
        Rect rect;

        if ( mState == STATE_IN ) {
            int y = (int)( mScreenHeight * ( System.currentTimeMillis() - mThemeInTime ) / durationIn());
            if ( y < 0 ) y = 0;
            if ( y > mScreenHeight ) y = mScreenHeight;
            rect = new Rect( 0, 0, mScreenWidth, y );
        }
        else if ( mState == STATE_OUT ) {
            int y = (int)( mScreenHeight * ( System.currentTimeMillis() - mThemeOutTime ) / durationOut());
            if ( y < 0 ) y = 0;
            if ( y > mScreenHeight ) y = mScreenHeight;
            rect = new Rect( 0, y, mScreenWidth, mScreenHeight );
        }
        else {
            rect = new Rect(0, 0, mScreenWidth, mScreenHeight);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(mContext, R.color.ThemeSunnyBackground));
        canvas.drawRect(rect, paint);

        // draw item
        mItemSun.draw(canvas);
        mC1.draw(canvas);
        mC2.draw(canvas);
        mC3.draw(canvas);

        if ( mAircab != null )
            mAircab.draw( canvas );

        if ( mHeli != null )
            mHeli.draw( canvas );

        return true;
    }

    @Override
    public void destroy() {

        Log.v(appTag, "destroy");

        if ( mAircab != null )
            mAircab.destroy();
        mAircab = null;

        if ( mHeli != null )
            mHeli.destroy();
        mHeli = null;

        if ( mItemSun != null )
            mItemSun.destroy();
        mItemSun = null;

        if ( mC1 != null )
            mC1.destroy();
        mC1 = null;

        if ( mC2 != null )
            mC2.destroy();
        mC2 = null;

        if ( mC3 != null )
            mC3.destroy();
        mC3 = null;
    }

    // MotionEvent handler
    public void onTouchEvent( MotionEvent event ) {

        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:

                if ( mState == STATE_RUN ) {

                    mItemSun.onTouchEvent(event);
                    mC1.onTouchEvent(event);
                    mC2.onTouchEvent(event);
                    mC3.onTouchEvent(event);

                    if (mAircab != null) {
                        mAircab.onTouchEvent(event);
                    }

                    if (mHeli != null) {
                        mHeli.onTouchEvent(event);
                    }
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
    }
}
