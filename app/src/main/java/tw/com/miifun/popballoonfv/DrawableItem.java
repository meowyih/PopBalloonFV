package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/18.
 */

// indicate an item that can be shown on SurfaceView
abstract public class DrawableItem {

    final static private String appTag = "DrawableItem";

    int mItemType;
    long mCreateTimeStamp;
    long mLastMoveTimeStamp;
    int mX, mY;
    int mWidth, mHeight;
    int mScreenWidth, mScreenHeight;
    Context mContext;

    int mTolerantPixel = 0;

    public DrawableItem( Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {

        mContext = context;
        mItemType = type;
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;

        mTolerantPixel = DrawUtil.dpToPx( DrawUtil.mmToDp( 5 ));

        mCreateTimeStamp = System.currentTimeMillis();
        mLastMoveTimeStamp = mCreateTimeStamp;

    }

    public boolean isHit(int x, int y, int scaleWidth, int scaleHeight ) {
        return (x + mTolerantPixel >= ( mX + mWidth / 2 ) - scaleWidth / 2 &&
                x - mTolerantPixel < ( mX + mWidth / 2 ) + scaleWidth / 2 &&
                y + mTolerantPixel >= ( mY + mHeight / 2 ) - scaleHeight / 2 &&
                y - mTolerantPixel < ( mY + mHeight / 2 ) + scaleHeight / 2 );
    }

    public boolean isHit(int x, int y) {
        return (x + mTolerantPixel >= mX &&
                x - mTolerantPixel < (mX + mWidth) &&
                y + mTolerantPixel >= mY &&
                y - mTolerantPixel< (mY + mHeight));
    }

    public Rect getSrcRect( int bitmapWidth, int bitmapHeight ) {
        return DrawUtil.getSrcRect( mX, mY, mWidth, mHeight, bitmapWidth, bitmapHeight, mScreenWidth, mScreenHeight );
    }

    public Rect getDestRect() {
        return new Rect(
                mX >= 0 ? mX : 0,
                mY >= 0 ? mY : 0,
                mX + mWidth <= mScreenWidth ? mX + mWidth : mScreenWidth,
                mY + mHeight <= mScreenHeight ? mY + mHeight : mScreenHeight
        );
    }

    // MotionEvent handler
    public int onTouchEvent( MotionEvent event ) {
        /*
        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
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
        */
        return 0;
    }

    // protected abstract method
    void onDrawableItemEvent( DrawableItemEvent event ) { return; }

    abstract DrawableItemEvent move( long time ); // return false if not alive anymore

    // public abstract method
    abstract public boolean draw( Canvas canvas );
    abstract public void destroy();
}
