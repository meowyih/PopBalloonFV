package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/22.
 */
public class ItemBird extends ItemMoving {

    final static private String appTag = "ItemBird";
    final static private int DEFAULT_WING_CLAP_INTERVAL = 200;

    final static public int RIGHT_TO_LEFT = 0;
    final static public int LEFT_TO_RIGHT = 1;

    Bitmap mRed1, mRed2, mOrange1, mOrange2;

    boolean mIsChangedColor = false;

    int mWinClapInterval = DEFAULT_WING_CLAP_INTERVAL;

    public ItemBird(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        if ( type == RIGHT_TO_LEFT ) {
            mRed1 = BitmapWarehouse.getBitmap(context, R.drawable.bird01);
            mRed2 = BitmapWarehouse.getBitmap(context, R.drawable.bird02);
            mOrange1 = BitmapWarehouse.getBitmap(context, R.drawable.birdo01);
            mOrange2 = BitmapWarehouse.getBitmap(context, R.drawable.birdo02);
        }
        else if ( type == LEFT_TO_RIGHT ) {
            mRed1 = BitmapWarehouse.getBitmap(context, R.drawable.bird01r);
            mRed2 = BitmapWarehouse.getBitmap(context, R.drawable.bird02r);
            mOrange1 = BitmapWarehouse.getBitmap(context, R.drawable.birdo01r);
            mOrange2 = BitmapWarehouse.getBitmap(context, R.drawable.birdo02r);
        }
        else {
            Log.e( appTag, "ERROR! ItemBird type MUST be RIGHT_TO_LEFT or LEFT_TO_RIGHT" );
        }

        ThreadGaming.playAudioSfx( ThreadGaming.AUDIO_BIRD_NORMAL );
    }

    @Override
    public void destroy() {
        if ( mItemType == RIGHT_TO_LEFT ) {
            BitmapWarehouse.releaseBitmap(R.drawable.bird01);
            BitmapWarehouse.releaseBitmap(R.drawable.bird02);
            BitmapWarehouse.releaseBitmap(R.drawable.birdo01);
            BitmapWarehouse.releaseBitmap(R.drawable.birdo02);
        }
        else if ( mItemType == LEFT_TO_RIGHT ) {
            BitmapWarehouse.releaseBitmap(R.drawable.bird01r);
            BitmapWarehouse.releaseBitmap(R.drawable.bird02r);
            BitmapWarehouse.releaseBitmap(R.drawable.birdo01r);
            BitmapWarehouse.releaseBitmap(R.drawable.birdo02r);
        }
    }

    @Override
    public void setBitmap( int resid ) {
        Log.e(appTag, "setBitmap cannot be used in ItemBird class!");
    }

    @Override
    public DrawableItemEvent move( long time ) {

        super.move(time);

        if ( mX == mDestX && mY == mDestY ) {
            return new DrawableItemEvent( DrawableItemEvent.DEAD, mX, mY );
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, mX, mY );
    }

    @Override
    public Bitmap getCurrentBitmap() {

        long time = System.currentTimeMillis();

        if ( mIsChangedColor ) {
            if (( time - mCreateTimeStamp ) % ( mWinClapInterval * 2 ) < mWinClapInterval ) {
                return mRed1;
            }
            else {
                return mRed2;
            }
        }
        else {
            if (( time - mCreateTimeStamp ) % ( mWinClapInterval * 2 ) < mWinClapInterval ) {
                return mOrange1;
            }
            else {
                return mOrange2;
            }
        }
    }

    @Override
    public int onTouchEvent( MotionEvent event ) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if ( isHit( (int)event.getX(), (int)event.getY())) {
                    long time = System.currentTimeMillis();
                    mIsChangedColor = true;
                    mWinClapInterval = DEFAULT_WING_CLAP_INTERVAL / 2;
                    setDestination( mDestX, mDestY, time, ( mDuration - ( time - mStartTime )) / 2 );

                    ThreadGaming.playAudioSfx(ThreadGaming.AUDIO_BIRD_CHU_CHU);
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
