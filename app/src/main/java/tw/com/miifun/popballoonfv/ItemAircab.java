package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/22.
 */
public class ItemAircab extends ItemMoving {

    final static String appTag = "ItemAircab";

    final static int RUN_AWAY_DURATION = 1000;

    private boolean mIsRunningAway = false;
    private boolean mIsSetDestination = false;

    Bitmap mAirplane2;

    EffectSoundPlayer mAudioSfx;

    public ItemAircab(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);
        mAudioSfx = new EffectSoundPlayer( mContext );
        mAudioSfx.playEffectSound(EffectSoundPlayer.TYPE_ASSET, ThreadGaming.AUDIO_AIRPLANE);
    }

    @Override
    public void setDestination( int x, int y, long startTime, long movingInterval ) {
        super.setDestination(x, y, startTime, movingInterval);
        mIsSetDestination = true;
    }

    @Override
    public Bitmap getCurrentBitmap() {

        // check if moving
        if ( mX > mDestX  ) {
            return mBitmap; // right to left
        }
        else { // right to left
            if ( mAirplane2 == null ) {
                mAirplane2 = BitmapWarehouse.getBitmap( mContext, R.drawable.airplane2 );
            }
            return mAirplane2;
        }
    }

    @Override
    DrawableItemEvent move(long time) {
        super.move(time);

        if ( mIsSetDestination ) {
            if ( mX == mDestX && mY == mDestY ) {
                return new DrawableItemEvent( DrawableItemEvent.DEAD, mX, mY );
            }
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, mX, mY );
    }

    @Override
    public void destroy() {
        super.destroy();
        mAudioSfx.destroy();
        BitmapWarehouse.releaseBitmap( R.drawable.airplane2 );
        mAirplane2 = null;
    }

    @Override
    public int onTouchEvent( MotionEvent event ) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if ( isHit( (int)event.getX(), (int)event.getY())) {
                    if ( ! mIsRunningAway ) {
                        // turn back
                        if ( mDestX > mX ) {
                            setDestination( 0 - mWidth, mY, System.currentTimeMillis(), RUN_AWAY_DURATION);
                            mIsRunningAway = true;
                            mAudioSfx.playEffectSound(EffectSoundPlayer.TYPE_ASSET, ThreadGaming.AUDIO_AIREPLANE_RUN_AWAY);
                        }
                        else {
                            setDestination( mScreenWidth, mY, System.currentTimeMillis(), RUN_AWAY_DURATION);
                            mIsRunningAway = true;
                            mAudioSfx.playEffectSound(EffectSoundPlayer.TYPE_ASSET, ThreadGaming.AUDIO_AIREPLANE_RUN_AWAY);
                        }
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

        return 0;
    }
}
