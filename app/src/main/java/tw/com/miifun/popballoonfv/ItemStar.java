package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/21.
 */
public class ItemStar extends ItemMoving {

    final static private String appTag = "ItemStar";

    static public int mBitmapRecycleCount = 0;

    final static int ROTATE_DURATION = 6000;

    boolean mIsRotating = false;
    long mRotateStartTime = 0;


    public ItemStar(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);
    }

    @Override
    public Bitmap getCurrentBitmap() {

        // change the matrix if needed
        if ( mIsRotating ) {

            long diff = System.currentTimeMillis() - mRotateStartTime;

            if ( diff > ROTATE_DURATION ) {
                mIsRotating = false;
                mBitmapMatrix = null;
            }
            else {

                if ( mBitmapMatrix == null )
                    mBitmapMatrix = new Matrix();
                mBitmapMatrix.reset();
                float xRatio = mWidth / (float)mBitmap.getWidth();
                float yRatio = mHeight / (float)mBitmap.getHeight();
                mBitmapMatrix.postScale( xRatio, yRatio );
                mBitmapMatrix.postTranslate(mX, mY);
                mBitmapMatrix.postRotate((float) (diff * 360 / ROTATE_DURATION), (float) mX + mWidth / 2, (float) mY + mHeight / 2);
            }
        }

        return mBitmap;
    }


    public int onTouchEvent( MotionEvent event ) {
        super.onTouchEvent(event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if ( isHit( (int)event.getX(), (int)event.getY(), mWidth / 2, mHeight / 2 ) && ! mIsRotating ) {
                    mIsRotating = true;
                    mRotateStartTime = System.currentTimeMillis();
                    ThreadGaming.playAudioSfx( ThreadGaming.AUDIO_RISER );
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
