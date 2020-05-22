package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/22.
 */
public class ItemHelicopter extends ItemMoving {

    final static String appTag = "ItemHelicopter";

    final static int RUN_AWAY_DURATION = 1000;
    final static int FAN_SPEED = 100; // change fan direction every 500 ms

    private boolean mIsRunningAway = false;
    private boolean mIsSetDestination = false;

    final static int TOTAL_BITMAP = 5;
    final static int TYPE_RIGHT_IN = 0;
    final static int TYPE_LEFT_IN = 1;

    EffectSoundPlayer mAudioSfx;

    Bitmap mBitmap[] = new Bitmap[TOTAL_BITMAP];

    public ItemHelicopter(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        mAudioSfx = new EffectSoundPlayer( mContext );

        if ( mItemType == TYPE_RIGHT_IN ) {
            mBitmap[0] = BitmapWarehouse.getBitmap(context, R.drawable.heli01);
            mBitmap[1] = BitmapWarehouse.getBitmap(context, R.drawable.heli02);
            mBitmap[2] = BitmapWarehouse.getBitmap(context, R.drawable.heli03);
            mBitmap[3] = BitmapWarehouse.getBitmap(context, R.drawable.heli04);
            mBitmap[4] = BitmapWarehouse.getBitmap(context, R.drawable.heli05);
        }
        else {
            mBitmap[0] = BitmapWarehouse.getBitmap(context, R.drawable.heli01r);
            mBitmap[1] = BitmapWarehouse.getBitmap(context, R.drawable.heli02r);
            mBitmap[2] = BitmapWarehouse.getBitmap(context, R.drawable.heli03r);
            mBitmap[3] = BitmapWarehouse.getBitmap(context, R.drawable.heli04r);
            mBitmap[4] = BitmapWarehouse.getBitmap(context, R.drawable.heli05r);
        }
    }

    @Override
    public void setDestination( int x, int y, long startTime, long movingInterval ) {
        super.setDestination(x, y, startTime, movingInterval);
        mIsSetDestination = true;

        mAudioSfx.playEffectSound(EffectSoundPlayer.TYPE_ASSET, ThreadGaming.AUDIO_HELICOPTER);
    }

    @Override
    public void setBitmap( int resid ) {
        Log.e(appTag, "setBitmap cannot be used in ItemHelicapter class!" );
    }

    @Override
    public Bitmap getCurrentBitmap() {

        long time = System.currentTimeMillis();
        long diff = time - mCreateTimeStamp;
        long remain = diff % ( TOTAL_BITMAP * FAN_SPEED );
        int fanPos = (int)(remain / FAN_SPEED);

        return mBitmap[fanPos];
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
        mAudioSfx = null;

        if ( mItemType == TYPE_RIGHT_IN ) {
            BitmapWarehouse.releaseBitmap(R.drawable.heli01);
            BitmapWarehouse.releaseBitmap(R.drawable.heli02);
            BitmapWarehouse.releaseBitmap(R.drawable.heli03);
            BitmapWarehouse.releaseBitmap(R.drawable.heli04);
            BitmapWarehouse.releaseBitmap(R.drawable.heli05);
        }
        else {
            BitmapWarehouse.releaseBitmap(R.drawable.heli01r);
            BitmapWarehouse.releaseBitmap(R.drawable.heli02r);
            BitmapWarehouse.releaseBitmap(R.drawable.heli03r);
            BitmapWarehouse.releaseBitmap(R.drawable.heli04r);
            BitmapWarehouse.releaseBitmap(R.drawable.heli05r);
        }
    }

    @Override
    public int onTouchEvent( MotionEvent event ) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if ( isHit( (int)event.getX(), (int)event.getY())) {
                    if ( ! mIsRunningAway ) {
                        // go up
                        setDestination( mX, 0 - mHeight, System.currentTimeMillis(), RUN_AWAY_DURATION);
                        mIsRunningAway = true;
                        mAudioSfx.playEffectSound( EffectSoundPlayer.STOP );
                        ThreadGaming.playAudioSfx(ThreadGaming.AUDIO_AIREPLANE_RUN_AWAY );
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
