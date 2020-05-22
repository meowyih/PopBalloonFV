package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/20.
 */
public class ItemBalloon extends DrawableMovingItem {

    final static private String appTag = "ItemBalloon";

    final static public int BALLOON_CONSUMED_TOUCH_EVENT = 100;

    // debug: recycle counter
    static public int mBitmapRecycleCount = 0;

    // png is 431x628
    final static public int TOTAL_BALLOON = 7;
    final static public int TOTAL_BALLOON_TRIAL = 3;

    // balloon state
    final static int RAISING = 1;
    final static int EXPLODING = 2;

    // configuration
    final static int EXPLODING_DURATION = 200; // 500ms, 0.5sec

    int mState;
    long mExplodingStartTime;
    boolean mIsAlive;
    double mSpeed; // ms per pixel

    Bitmap mBitmapBalloon;
    int mBalloonResId;
    Bitmap mBitmapPop;
    int mPopResId;
    Context mContext;

    int mPopHeight;
    int mPopWidth;

    static int getRandomType() { return (int)(Math.random() * TOTAL_BALLOON) + 1; }
    static int getRandomTypeForTrial() { return (int)(Math.random() * TOTAL_BALLOON_TRIAL) + 1; }

    static int getHeightByWidth( Context context, int type, int width ) {
        switch( type ) {
            case 1:
                return width * context.getResources().getInteger( R.integer.b01_height ) / context.getResources().getInteger( R.integer.b01_width );
            case 2:
                return width * context.getResources().getInteger( R.integer.b02_height ) / context.getResources().getInteger( R.integer.b02_width );
            case 3:
                return width * context.getResources().getInteger( R.integer.b03_height ) / context.getResources().getInteger( R.integer.b03_width );
            case 4:
                return width * context.getResources().getInteger( R.integer.b04_height ) / context.getResources().getInteger( R.integer.b04_width );
            case 5:
                return width * context.getResources().getInteger( R.integer.b05_height ) / context.getResources().getInteger( R.integer.b05_width );
            case 6:
                return width * context.getResources().getInteger( R.integer.b06_height ) / context.getResources().getInteger( R.integer.b06_width );
            case 7:
                return width * context.getResources().getInteger( R.integer.b07_height ) / context.getResources().getInteger( R.integer.b07_width );
            case 8:
                return width * context.getResources().getInteger( R.integer.b08_height ) / context.getResources().getInteger( R.integer.b08_width );
            case 9:
                return width * context.getResources().getInteger( R.integer.b09_height ) / context.getResources().getInteger( R.integer.b09_width );
            case 10:
                return width * context.getResources().getInteger( R.integer.b10_height ) / context.getResources().getInteger( R.integer.b10_width );
            case 11:
                return width * context.getResources().getInteger( R.integer.b11_height ) / context.getResources().getInteger( R.integer.b11_width );
            case 12:
                return width * context.getResources().getInteger( R.integer.b12_height ) / context.getResources().getInteger( R.integer.b12_width );
            case 13:
                return width * context.getResources().getInteger( R.integer.b13_height ) / context.getResources().getInteger( R.integer.b13_width );
            default:
                return width;
        }
    }

    static int getImageResourceIdByType( int type ) {
        switch( type ) {
            case 1: return R.drawable.b01;
            case 2: return R.drawable.b02;
            case 3: return R.drawable.b03;
            case 4: return R.drawable.b04;
            case 5: return R.drawable.b05;
            case 6: return R.drawable.b06;
            case 7: return R.drawable.b07;
            case 8: return R.drawable.b08;
            case 9: return R.drawable.b09;
            case 10: return R.drawable.b10;
            case 11: return R.drawable.b11;
            case 12: return R.drawable.b12;
            case 13: return R.drawable.b13;
            default: return R.drawable.b01;
        }
    }

    public ItemBalloon( Context context, AppGameData.BalloonData data, int screenWidth, int screenHeight ) {
        super( context, data.mItemType, data.mX, data.mY, data.mWidth, data.mHeight, screenWidth, screenHeight );

        mContext = context;
        mItemType = data.mItemType;
        mSpeed = data.mSpeed;
        mIsAlive = true;
        mState = RAISING;

        // compute the new x, y, width, height
        mPopHeight = mWidth * mContext.getResources().getInteger( R.integer.balloon_pop_height ) /
                mContext.getResources().getInteger( R.integer.balloon_pop_width );
        mPopWidth = mWidth;
        mPopResId = R.drawable.pop;

        setDestination( mX, 0 - mHeight, System.currentTimeMillis(), (long)(( screenHeight * 1000 / mSpeed ) * mY / mScreenHeight ));

        mBalloonResId = getImageResourceIdByType(mItemType);
        mBitmapBalloon = BitmapWarehouse.getBitmap( mContext, mBalloonResId );

        if (mBitmapBalloon != null)
            mBitmapRecycleCount++;

        mBitmapPop = BitmapWarehouse.getBitmap( mContext, mPopResId );

        if (mBitmapPop != null)
            mBitmapRecycleCount ++;
    }

    // speed - pixel per millisecond
    public ItemBalloon(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight, double speed) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        //Log.v(appTag, "create x,y=" + x + " " + y +
        //        " width,height=" + width + " " + height +
        //        " screenWidth,screenHeight=" + screenWidth + " " + screenHeight +
        //        " speed=" + speed);

        mContext = context;
        mItemType = type;
        mSpeed = speed;
        mIsAlive = true;
        mState = RAISING;

        // compute the new x, y, width, height
        mPopHeight = mWidth * mContext.getResources().getInteger( R.integer.balloon_pop_height ) /
                mContext.getResources().getInteger( R.integer.balloon_pop_width );
        mPopWidth = mWidth;
        mPopResId = R.drawable.pop;

        setDestination( x, 0 - height, System.currentTimeMillis(), (long)( screenHeight * 1000 / speed ) );

        mBalloonResId = getImageResourceIdByType(mItemType);
        mBitmapBalloon = BitmapWarehouse.getBitmap( mContext, mBalloonResId );

        if (mBitmapBalloon != null)
            mBitmapRecycleCount++;

        mBitmapPop = BitmapWarehouse.getBitmap( mContext, mPopResId );

        if (mBitmapPop != null)
            mBitmapRecycleCount ++;
    }

    @Override
    public void setDestination( int x, int y, long startTime, long movingInterval ) {

        super.setDestination( x, y, startTime, movingInterval );

        mDestX = mNextDestX;
        mDestY = mNextDestY;
        mDuration = mNextDuration;
    }

    // implement abstract function
    @Override
    DrawableItemEvent move(long time) {

        super.move( time );

        if ( mState == EXPLODING ) {
            if ( time - mExplodingStartTime > EXPLODING_DURATION ) {
                return new DrawableItemEvent( DrawableItemEvent.DEAD_EXPLODED, mX, mY );
            }
            return new DrawableItemEvent( DrawableItemEvent.NONE, mX, mY );
        }
        else if ( mY <= mDestY || ! mIsAlive) {
            // Log.v( appTag, "balloon dead at x:" + mX + " y:" + mY + " desty:" + mDestY);
            return new DrawableItemEvent( DrawableItemEvent.DEAD, mX, mY );
        }
        else {
            return new DrawableItemEvent( DrawableItemEvent.NONE, mX, mY );
        }
    }

    @Override
    public boolean draw(Canvas canvas) {

        Bitmap bp;
        Rect srcRect = null;
        Rect destRect;

        if ( canvas == null ) {
            Log.w(appTag, "error, holder is null");
            return false;
        }

        if ( mX > mScreenWidth || mX + mWidth < 0 || mY > mScreenHeight || mY + mHeight < 0 ) {
            // Log.v(appTag, "ignore draw, outside the screen");
            return false;
        }

        if ( mState == RAISING ) {
            bp = mBitmapBalloon;
        }
        else {
            bp = mBitmapPop;
            mY = mY + ( mHeight - mPopHeight ) / 2;
            mHeight = mPopHeight;
            mWidth = mPopWidth;
        }

        if ( mX < 0 || mX + mWidth > mScreenWidth ||  mY < 0 || mY + mHeight > mScreenHeight ) {
            srcRect = getSrcRect( bp.getWidth(), bp.getHeight() );

            if ( srcRect == null ) {
                return false;
            }

            destRect = new Rect(
                    mX >= 0 ? mX : 0,
                    mY >= 0 ? mY : 0,
                    mX + mWidth <= mScreenWidth ? mX + mWidth : mScreenWidth,
                    mY + mHeight <= mScreenHeight ? mY + mHeight : mScreenHeight
            );
        }
        else {
            destRect = new Rect( mX, mY, mX + mWidth, mY + mHeight );
        }

        canvas.drawBitmap(bp, srcRect, destRect, null);

        return true;
    }

    @Override
    public void destroy() {

       // Log.v( appTag, "destroy" );
        mIsAlive = false;

        if ( mBitmapBalloon != null ) {
            BitmapWarehouse.releaseBitmap( mPopResId );
            mBitmapRecycleCount --;
            mBitmapBalloon = null;
        }

        if ( mBitmapPop != null ) {
            BitmapWarehouse.releaseBitmap(mBalloonResId);
            mBitmapRecycleCount --;
            mBitmapPop = null;
        }
    }

    @Override
    public int onTouchEvent( MotionEvent event ) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if ( isHit( (int)event.getX(), (int)event.getY()) && mState == RAISING ) {
                    stopMoving();
                    mState = EXPLODING;
                    mExplodingStartTime = System.currentTimeMillis();

                    // play audio
                    if ( mItemType % 3 == 0 ) {
                        ThreadGaming.playAudioSfx(ThreadGaming.AUDIO_POP_1);
                    }
                    else if ( mItemType % 3 == 1 ) {
                        ThreadGaming.playAudioSfx(ThreadGaming.AUDIO_POP_2);
                    }
                    else {
                        ThreadGaming.playAudioSfx(ThreadGaming.AUDIO_POP_3);
                    }

                    return BALLOON_CONSUMED_TOUCH_EVENT;
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
