package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by yhorn on 2016/2/20.
 */
abstract public class DrawableMovingItem extends DrawableItem {

    final static private String appTag = "DrawableMovingItem";

    int mInitX;
    int mInitY;

    int mDestX;
    int mDestY;
    long mStartTime;
    long mDuration;

    int mNextDestX;
    int mNextDestY;
    long mNextMovingTime;
    long mNextDuration;

    public DrawableMovingItem( Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        mInitX = x;
        mInitY = y;

        mDestX = x;
        mDestY = y;
        mStartTime = System.currentTimeMillis();
        mDuration = 0;

        mNextDestX = x;
        mNextDestY = y;
        mNextMovingTime = 0;
        mNextDuration = 0;
    }

    DrawableItemEvent move( long time ) {

        // check if next moving should start
        if ( mNextMovingTime != 0 && time >= mNextMovingTime ) {
            mInitX = mX;
            mInitY = mY;
            mDestX = mNextDestX;
            mDestY = mNextDestY;
            mDuration = mNextDuration;
            mStartTime = mNextMovingTime;
            mNextMovingTime = 0;
        }

        // check if move is necessary
        if ( mDestX != mInitX || mDestY != mInitY ) {
            // need to move position
            if ( time >= mStartTime + mDuration ) {
                // too late, just move to the destination
                // Log.v( appTag, "too late, skip to dest " + mDestX + " " + mDestY );
                mX = mDestX;
                mY = mDestY;
                mInitX = mX;
                mInitY = mY;
            }
            else {
                // move!
                // Log.v( appTag, "move x:" + mX + " destx:" + mDestX + " initx:" + mInitX + " duration:" + mDuration + " interval:" + (time - mStartTime) );
                mX = mInitX + (int)(( mDestX - mInitX ) * ( time - mStartTime ) / mDuration );
                // Log.v( appTag, "move y:" + mY + " desty:" + mDestY + " inity:" + mInitY + " duration:" + mDuration + " interval:" + (time - mStartTime) );
                mY = mInitY + (int)(( mDestY - mInitY ) * ( time - mStartTime ) / mDuration);
            }
        }

        // Log.v( appTag, "no need move, mDestX " + mDestX + " = mInit X" + mInitX );

        return new DrawableItemEvent( DrawableItemEvent.NONE, mX, mY );
    }

    public void setDestination( int x, int y, long startTime, long movingInterval ) {

        // Log.v(appTag, "setDestination x:" + x + " y:" + y + " start:" + startTime + " interval:" + movingInterval );

        mNextDestX = x;
        mNextDestY = y;
        mNextMovingTime = startTime;
        mNextDuration = movingInterval;
    }

    public void stopMoving() {
        mDestX = mX;
        mDestY = mY;
        mInitX = mX;
        mInitY = mY;
        mDuration = 0;
        mNextMovingTime = 0;
        mNextDuration = 0;
    }

    // public abstract method
    abstract public boolean draw( Canvas canvas );
    abstract public void destroy();
}
