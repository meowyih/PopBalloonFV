package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/23.
 */
public class ItemSunnyCloud extends ItemMoving {
    final static String appTag = "ItemSunnyCloud";

    final static public int TYPE_NORMAL = 1;
    final static public int TYPE_MIRROR = 2;

    final static int MAX_ENLARGE_LEVEL = 3;
    final static int ENLARGE_DURATION = 1000;
    int mEnlargeLevel = 0;
    boolean mIsEnlarge = false;

    int mOriginalWidth, mOriginalHeight;
    int mTargetWidth, mTargetHeight;
    long mEnlargeStartTime;

    public ItemSunnyCloud(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        mOriginalWidth = width;
        mOriginalHeight = height;

        if ( type == TYPE_NORMAL ) {
            setBitmap(R.drawable.cloud);
        }
        else if ( type == TYPE_MIRROR ) {
            setBitmap(R.drawable.cloud_mirror);
        }
        else {
            Log.e(appTag, "ERROR: ItemSunnyCloud MUST assign a meaningful type to create image");
        }
    }

    @Override
    DrawableItemEvent move(long time) {
        super.move(time);

        if ( mIsEnlarge ) {

            if ( time - mEnlargeStartTime >= ENLARGE_DURATION ) {
                mIsEnlarge = false;
                mX = mX - ( mTargetWidth - mWidth ) / 2;
                mY = mY - ( mTargetHeight - mHeight ) / 2;
                mWidth = mTargetWidth;
                mHeight = mTargetHeight;
                return new DrawableItemEvent( DrawableItemEvent.NONE, mX, mY );
            }

            int width, height;

            // calculate new width and height
            width = mWidth + (int)(( mTargetWidth - mWidth ) * ( time - mEnlargeStartTime ) / ENLARGE_DURATION);
            height = mHeight + (int)(( mTargetHeight - mHeight ) * ( time - mEnlargeStartTime ) / ENLARGE_DURATION);
            mX = mX - ( width - mWidth ) / 2;
            mY = mY - ( height - mHeight ) / 2;
            mWidth = width;
            mHeight = height;
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, mX, mY );
    }

    @Override
    public int onTouchEvent( MotionEvent event ) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if ( isHit( (int)event.getX(), (int)event.getY()) ) {
                    mEnlargeLevel ++;
                    if ( mEnlargeLevel > MAX_ENLARGE_LEVEL ) {
                        mEnlargeLevel = 0;
                    }
                    mTargetWidth = (int)( mOriginalWidth * ( 1 + mEnlargeLevel * 0.3 ));
                    mTargetHeight = (int)( mOriginalHeight * ( 1 + mEnlargeLevel * 0.3 ));

                    mIsEnlarge = true;
                    mEnlargeStartTime = System.currentTimeMillis();
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
