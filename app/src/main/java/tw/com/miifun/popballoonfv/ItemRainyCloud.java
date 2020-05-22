package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/21.
 */
public class ItemRainyCloud extends ItemMoving {

    final static private String appTag = "ItemRainyCloud";

    final static int SHAKE_DURATION = 1000; // shake twice
    final static int SHAKE_LENGTH = 500; // each shake takes 1000 ms
    final static int SHAKE_DEGREE = 10;

    final static public int NONE = 0;
    final static public int SHAKE = 1;

    boolean mIsShaking = false;
    long mShakeStartTime = 0;

    public ItemRainyCloud(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);
    }

    public boolean isShaking() { return mIsShaking; }

    @Override
    DrawableItemEvent move( long time ) {
        DrawableItemEvent event = super.move(time);
        return event;
    }

    @Override
    public Bitmap getCurrentBitmap() {

        // change the matrix if needed
        if ( mIsShaking ) {

            long diff = System.currentTimeMillis() - mShakeStartTime;

            if ( diff > SHAKE_DURATION ) {
                mIsShaking = false;
                mBitmapMatrix = null;
            }
            else {
                if ( mBitmapMatrix == null )
                    mBitmapMatrix = new Matrix();

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
        }

        return mBitmap;
    }

    public int onTouchEvent( MotionEvent event ) {
        super.onTouchEvent(event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if ( isHit( (int)event.getX(), (int)event.getY() ) && ! mIsShaking) {
                    mIsShaking = true;
                    mShakeStartTime = System.currentTimeMillis();
                    ThreadGaming.playAudioSfx( ThreadGaming.AUDIO_WATER_DING_DONG );
                    return SHAKE;
                }
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

        return NONE;
    }
}
