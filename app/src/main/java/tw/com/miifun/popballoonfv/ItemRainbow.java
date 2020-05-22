package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/22.
 */
public class ItemRainbow extends ItemMoving {

    final static private String appTag = "ItemRainbow";

    final static private int RAINBOW_MOVING_INTERVAL = 6000;
    final static private int SHAKE_LENGTH = 500;
    final static private int SHAKE_DEGREE = 10;

    final static public int ACTION_NONE = 0;
    final static public int ACTION_START_MOVING = 1;

    // debug: recycle counter
    static public int mBitmapRecycleCount = 0;

    final static int TOTAL_POSITION = 4;
    Point mPos[] = new Point[TOTAL_POSITION];
    int mPosIndex = 0;
    boolean mIsMoving = false;
    long mStartMovingTime = 0;

    public ItemRainbow(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        mPos[0] = new Point();
        mPos[1] = new Point();
        mPos[2] = new Point();
        mPos[3] = new Point();

        mPos[0].x = mScreenWidth / 4 - mWidth / 2;
        mPos[0].y = mScreenHeight / 4 - mHeight / 2;
        mPos[1].x = mScreenWidth * 3 / 4 - mWidth / 2;
        mPos[1].y = mScreenHeight / 4 - mHeight / 2;
        mPos[2].x = mScreenWidth * 3 / 4 - mWidth / 2;
        mPos[2].y = mScreenHeight * 3 / 4 - mHeight / 2;
        mPos[3].x = mScreenWidth / 4 - mWidth / 2;
        mPos[3].y = mScreenHeight * 3 / 4 - mHeight / 2;
    }

    public Point getDefaultPosition() {
        return mPos[0];
    }

    @Override
    public void setDestination( int x, int y, long startTime, long movingInterval ) {
        super.setDestination( x, y, startTime, movingInterval );
        mStartMovingTime = startTime;
    }

    @Override
    public Bitmap getCurrentBitmap() {

        // check if moving
        if ( mX != mDestX || mY != mDestY ) {

            long diff = System.currentTimeMillis() - mStartMovingTime;

            if ( mBitmapMatrix == null )
                mBitmapMatrix = new Matrix();

            diff = diff % ( SHAKE_LENGTH * 2 );

            if ( diff < SHAKE_LENGTH / 2 ||
                    ( diff >= SHAKE_LENGTH && diff < SHAKE_LENGTH + SHAKE_LENGTH /2 )) {

                mBitmapMatrix.reset();
                float xRatio = mWidth / (float)mBitmap.getWidth();
                float yRatio = mHeight / (float)mBitmap.getHeight();
                mBitmapMatrix.postScale(xRatio, yRatio);
                mBitmapMatrix.postTranslate(mX, mY);
                mBitmapMatrix.postRotate((float) (SHAKE_DEGREE), (float) mX + mWidth / 2, (float) mY + mHeight / 2);

            } else {
                mBitmapMatrix.reset();
                float xRatio = mWidth / (float)mBitmap.getWidth();
                float yRatio = mHeight / (float)mBitmap.getHeight();
                mBitmapMatrix.postScale( xRatio, yRatio );
                mBitmapMatrix.postTranslate(mX, mY);
                mBitmapMatrix.postRotate((float) -(SHAKE_DEGREE), (float) mX + mWidth / 2, (float) mY + mHeight / 2);
            }
        }
        else {
            mBitmapMatrix = null;
        }

        return mBitmap;
    }

    @Override
    public int onTouchEvent( MotionEvent event ) {
        super.onTouchEvent(event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if ( isHit( (int)event.getX(), (int)event.getY()) && mX == mDestX && mY == mDestY ) {
                    mPosIndex = ( mPosIndex + 1 ) % TOTAL_POSITION;
                    setDestination(mPos[mPosIndex].x, mPos[mPosIndex].y, System.currentTimeMillis(), RAINBOW_MOVING_INTERVAL);
                    ThreadGaming.playAudioSfx( ThreadGaming.AUDIO_BIRD_CHU_CHU );
                    return ACTION_START_MOVING;
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

        return ACTION_NONE;
    }
}
