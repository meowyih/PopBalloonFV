package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;

/**
 * Created by yhorn on 2016/2/19.
 */

/**
 * Created by yhorn on 2016/2/19.
 */
public class ThemeNight extends Theme {

    final static private String appTag = "ThemeNight";

    static public int mBitmapRecycleCounter = 0;

    private long DURATION_IN = 5000;
    private long DURATION_OUT = 5000;

    final static int FIRE_BALLOON_CREATE_INTERVAL = 5000;
    final static int FIRE_BALLOON_FLIGHT_INTERVAL = 15000;

    long mThemeInTime, mThemeOutTime;

    // moon#1
    ItemMoon mM;
    int mMw, mMh;
    int mMx, mMy;
    int mMxIn, mMyIn;
    int mMxOut, mMyOut;

    // star#1
    ItemStar mS1;
    int mS1w, mS1h;
    int mS1x, mS1y;
    int mS1xIn, mS1yIn;
    int mS1xOut, mS1yOut;

    // star#2
    ItemStar mS2;
    int mS2w, mS2h;
    int mS2x, mS2y;
    int mS2xIn, mS2yIn;
    int mS2xOut, mS2yOut;

    // star#3
    ItemStar mS3;
    int mS3w, mS3h;
    int mS3x, mS3y;
    int mS3xIn, mS3yIn;
    int mS3xOut, mS3yOut;

    // fire balloon
    ItemFireBalloon mFireBalloon;
    int mFbWidth, mFbHeight;
    boolean mFbLeftIn = true;
    long mLastFbInTime;

    public ThemeNight( Context context, int screenWidth, int screenHeight ) {

        super( context, screenWidth, screenHeight );

        int longSide;

        // moon#1
        mMx = 0;
        mMy = 0;
        mMh = mScreenHeight / 2;
        mMw = mMh * context.getResources().getInteger( R.integer.moon_width ) /
                context.getResources().getInteger( R.integer.moon_height );

        longSide = ( mScreenWidth - mMx ) > ( mMy + mMh ) ? ( mScreenWidth - mMx ) : ( mMy + mMh );

        mMxIn = mMx - longSide;
        mMyIn = mMy - longSide;

        longSide = ( mMw - mMx + mMw ) > ( mScreenHeight - mMy ) ? ( mMw - mMx + mMw ) : ( mScreenHeight - mMy );

        mMxOut = mMx + longSide;
        mMyOut = mMy + longSide;

        mM= new ItemMoon( context, 0,
                mMxIn, mMyIn, // x, y
                mMw, mMh, // height, width
                screenWidth, screenHeight ); // screen size

        mM.setBitmap(R.drawable.moon);

        // star#1
        mS1x = mScreenWidth * 36 / 50;
        mS1y = mScreenHeight * 3 / 24;
        mS1h = mScreenHeight * 11 / 40;
        mS1w = mS1h * context.getResources().getInteger( R.integer.star_width ) /
                context.getResources().getInteger( R.integer.star_height );

        longSide = ( mScreenWidth - mS1x ) > ( mS1y + mS1h ) ? ( mScreenWidth - mS1x ) : ( mS1y + mS1h );

        mS1xIn = mS1x - longSide;
        mS1yIn = mS1y - longSide;

        longSide = ( mS1w - mS1x + mS1w ) > ( mScreenHeight - mS1y ) ? ( mS1w - mS1x + mS1w ) : ( mScreenHeight - mS1y );

        mS1xOut = mS1x + longSide;
        mS1yOut = mS1y + longSide;

        mS1 = new ItemStar( context, 0,
                mS1xIn, mS1yIn, // x, y
                mS1w, mS1h, // height, width
                screenWidth, screenHeight ); // screen size

        mS1.setBitmap(R.drawable.star );

        // star#2
        mS2x = mS1x + mS1w / 5;
        mS2y = mS1y + mS1h;
        mS2h = mS1h;
        mS2w = mS1w;

        longSide = ( mScreenWidth - mS2x ) > ( mS2y + mS2h ) ? ( mScreenWidth - mS2x ) : ( mS2y + mS2h );

        mS2xIn = mS2x - longSide;
        mS2yIn = mS2y - longSide;

        longSide = ( mS2w - mS2x + mS2w ) > ( mScreenHeight - mS2y ) ? ( mS2w - mS2x + mS2w ) : ( mScreenHeight - mS2y );

        mS2xOut = mS2x + longSide;
        mS2yOut = mS2y + longSide;

        mS2 = new ItemStar( context, 0,
                mS2xIn, mS2yIn, // x, y
                mS2w, mS2h, // height, width
                screenWidth, screenHeight ); // screen size

        mS2.setBitmap(R.drawable.star);

        // star#3
        mS3x = mS1x - mS1w / 2;
        mS3y = mS2y + mS2h * 2 / 3;
        mS3h = mS1h;
        mS3w = mS1w;

        longSide = ( mScreenWidth - mS3x ) > ( mS3y + mS3h ) ? ( mScreenWidth - mS3x ) : ( mS3y + mS3h );

        mS3xIn = mS3x - longSide;
        mS3yIn = mS3y - longSide;

        longSide = ( mS3w - mS3x + mS3w ) > ( mScreenHeight - mS3y ) ? ( mS3w - mS3x + mS3w ) : ( mScreenHeight - mS3y );

        mS3xOut = mS3x + longSide;
        mS3yOut = mS3y + longSide;

        mS3 = new ItemStar( context, 0,
                mS3xIn, mS3yIn, // x, y
                mS3w, mS3h, // height, width
                screenWidth, screenHeight ); // screen size

        mS3.setBitmap(R.drawable.star);

        // fire balloon
        mFbHeight = mScreenHeight * 2 / 3;
        mFbWidth = mFbHeight * mContext.getResources().getInteger( R.integer.fire_balloon_width )
                /  mContext.getResources().getInteger( R.integer.fire_balloon_height );

        // state
        mState = Theme.STATE_UNKNOWN;
    }

    @Override
    public int id() { return THEME_NIGHT; }

    @Override
    public long durationIn() {
        return DURATION_IN;
    }

    @Override
    public void startThemeIn( long time ) {
        mState = Theme.STATE_IN;
        mThemeInTime = time;

        // set sun destination
        mM.setDestination(mMx, mMy, time, durationIn());
        mS1.setDestination( mS1x, mS1y, time, durationIn() );
        mS2.setDestination( mS2x, mS2y, time, durationIn() );
        mS3.setDestination( mS3x, mS3y, time, durationIn() );
    }

    @Override
    public void startThemeOut( long time ) {
        mState = Theme.STATE_OUT;
        mThemeOutTime = time;

        // set sun destination
        mM.setDestination( mMxOut, mMyOut, time, durationOut() );
        mS1.setDestination( mS1xOut, mS1yOut, time, durationOut() );
        mS2.setDestination( mS2xOut, mS2yOut, time, durationOut() );
        mS3.setDestination( mS3xOut, mS3yOut, time, durationOut() );
    }

    @Override
    public void skipThemeIn( long time ) {
        mState = Theme.STATE_RUN;

        // reset position
        mM.setDestination( mMx, mMy, time, 0 );
        mS1.setDestination( mS1x, mS1y, time, 0 );
        mS2.setDestination( mS2x, mS2y, time, 0 );
        mS3.setDestination( mS3x, mS3y, time, 0 );
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
            }
        }

        if ( mState == STATE_OUT ) {
            if ( time - mThemeOutTime > durationOut() ) {
                mState = STATE_RUN;
            }
        }

        if ( mState == STATE_RUN ) {
            if ( mLastFbInTime == 0 || time - mLastFbInTime >= FIRE_BALLOON_CREATE_INTERVAL ) {
                if ( mFireBalloon == null ) {
                    int x, xOut, y;
                    y = mScreenHeight / 2 - mFbHeight / 2;
                    if ( mFbLeftIn ) {
                        x = 0 - mFbWidth;
                        xOut = mScreenWidth;
                    }
                    else {
                        xOut = 0 - mFbWidth;
                        x = mScreenWidth;
                    }
                    mFireBalloon = new ItemFireBalloon( mContext, ItemFireBalloon.TYPE_RANDOM, x, y, mFbWidth, mFbHeight, mScreenWidth, mScreenHeight);
                    mFireBalloon.setDestination( xOut, y, time, FIRE_BALLOON_FLIGHT_INTERVAL );
                    mLastFbInTime = time;
                    mFbLeftIn = ! mFbLeftIn;
                }
            }
        }

        mM.move(time);
        mS1.move(time);
        mS2.move(time);
        mS3.move(time);

        if ( mFireBalloon != null ) {
            if ( mFireBalloon.move(time).type == DrawableItemEvent.DEAD ) {
                mFireBalloon.destroy();
                mFireBalloon = null;
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
        paint.setColor(ContextCompat.getColor(mContext, R.color.ThemeNightBackground));
        canvas.drawRect(rect, paint);

        // draw item
        mM.draw( canvas );
        mS1.draw(canvas);
        mS2.draw(canvas);
        mS3.draw(canvas);

        if ( mFireBalloon != null )
            mFireBalloon.draw( canvas );
        return true;
    }

    @Override
    public void destroy() {

        Log.v(appTag, "destroy");

        if ( mM != null )
            mM.destroy();
        mM = null;

        if ( mS1 != null )
            mS1.destroy();
        mS1 = null;

        if ( mS2 != null )
            mS2.destroy();
        mS2 = null;

        if ( mS3 != null )
            mS3.destroy();
        mS3 = null;

        if ( mFireBalloon != null )
            mFireBalloon.destroy();
        mFireBalloon = null;
    }

    // MotionEvent handler
    public void onTouchEvent( MotionEvent event ) {

        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mM.onTouchEvent( event );
                mS1.onTouchEvent( event );
                mS2.onTouchEvent( event );
                mS3.onTouchEvent( event );

                if ( mFireBalloon != null )
                    mFireBalloon.onTouchEvent( event );
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
