package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/24.
 */
public class ItemSpeedMeter extends ItemMoving {

    final static private String appTag = "appTag";

    final static int SPEED_LEVEL = 9; // 0 to 8, total 9 level (displayed as 1 - 9)
    final static int SPEED_DEFAULT = 2;

    ItemMoving mSpeedUp;
    ItemMoving mSpeedDown;
    ItemNumeric mSpeedLevel;
    int mLevel;
    int mMaxLevel = SPEED_LEVEL;

    DrawableItemEvent mSpeedEvent = null;

    public ItemSpeedMeter(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        int sx, sy, sw, sh;

        mLevel = SPEED_DEFAULT;

        // speedup
        sx = x;
        sy = y;
        sh = height;
        sw = sh * mContext.getResources().getInteger( R.integer.speed_icon_width ) /
                mContext.getResources().getInteger( R.integer.speed_icon_height);
        mSpeedUp = new ItemMoving( context, 0, sx, sy, sw, sh, mScreenWidth, mScreenHeight );
        mSpeedUp.setBitmap( R.drawable.speedup );

        // speed meter
        sh = height;
        sw = sh * mContext.getResources().getInteger( R.integer.numeric_character_width ) /
                mContext.getResources().getInteger( R.integer.numeric_character_height );
        sx = mSpeedUp.mX + mSpeedUp.mWidth + mContext.getResources().getDimensionPixelSize( R.dimen.game_speed_meter_gap );
        sy = y;
        mSpeedLevel = new ItemNumeric( context, 0, sx, sy, sw, sh, mScreenWidth, mScreenHeight );
        mSpeedLevel.setNumber( mLevel + 1);

        // speed down
        sx = mSpeedLevel.mX + mSpeedLevel.mWidth + mContext.getResources().getDimensionPixelSize( R.dimen.game_speed_meter_gap );
        sy = y;
        sw = mSpeedUp.mWidth;
        sh = mSpeedUp.mHeight;
        mSpeedDown = new ItemMoving( context, 0, sx, sy, sw, sh, mScreenWidth, mScreenHeight );
        mSpeedDown.setBitmap( R.drawable.speeddown );
    }

    public void setSpeed( int speed ) {
        if ( speed >= SPEED_LEVEL )
            mLevel = SPEED_LEVEL - 1;
        else if ( speed < 0 )
            mLevel = 0;
        else
            mLevel = speed;

        mSpeedLevel.setNumber( mLevel + 1);
    }

    static public int estimateWidthByHeight( Context context, int height ) {
        int speedup = height * context.getResources().getInteger( R.integer.speed_icon_width ) /
                context.getResources().getInteger( R.integer.speed_icon_height );
        int gap = context.getResources().getDimensionPixelSize( R.dimen.game_speed_meter_gap );
        int number = height * context.getResources().getInteger( R.integer.numeric_character_width ) /
                context.getResources().getInteger( R.integer.numeric_character_height );
        int speeddown = speedup;

        return speedup + gap + number + gap + speeddown;
    }

    public void setMaxLevel( int maxLevel ) {
        if ( maxLevel > SPEED_LEVEL )
            mMaxLevel = SPEED_LEVEL;
        else if ( mMaxLevel < SPEED_DEFAULT )
            mMaxLevel = SPEED_DEFAULT;
        else
            mMaxLevel = maxLevel;
    }

    @Override
    public DrawableItemEvent move( long time ) {
        super.move( time );
        if ( mSpeedEvent != null ) {
            DrawableItemEvent event = mSpeedEvent;
            mSpeedEvent = null;
            return event;
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, 0, 0 );
    }

    @Override
    public boolean draw( Canvas canvas ) {
        if ( mLevel < mMaxLevel - 1 )
            mSpeedUp.draw( canvas );

        mSpeedLevel.draw(canvas);

        if ( mLevel > 0 )
            mSpeedDown.draw( canvas );

        return true;
    }

    @Override
    public int onTouchEvent( MotionEvent event ) {
        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                if ( mSpeedUp.isHit( (int)event.getX(), (int)event.getY()) && mLevel + 1 < mMaxLevel && mSpeedEvent == null ) {
                    mSpeedUp.setBitmap( R.drawable.speedup_pressed );
                }
                else if ( mSpeedDown.isHit( (int)event.getX(), (int)event.getY()) && mLevel - 1 >= 0 && mSpeedEvent == null ) {
                    mSpeedDown.setBitmap( R.drawable.speeddown_pressed );
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if ( mSpeedUp.isHit( (int)event.getX(), (int)event.getY()) && mLevel + 1 < mMaxLevel && mSpeedEvent == null ) {
                    mSpeedUp.setBitmap( R.drawable.speedup );
                    mLevel ++;
                    mSpeedEvent = new DrawableItemEvent( DrawableItemEvent.SPEED_UP, 0, 0 );
                    updateSpeed();
                }
                else if ( mSpeedDown.isHit( (int)event.getX(), (int)event.getY()) && mLevel - 1 >= 0 && mSpeedEvent == null ) {
                    mSpeedDown.setBitmap( R.drawable.speeddown );
                    mLevel --;
                    mSpeedEvent = new DrawableItemEvent( DrawableItemEvent.SPEED_DOWN, 0, 0 );
                    updateSpeed();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
            default:
        }
        return 0;
    }

    @Override
    public void destroy() {

        if ( mSpeedUp != null )
            mSpeedUp.destroy();
        mSpeedUp = null;

        if ( mSpeedDown != null )
            mSpeedDown.destroy();
        mSpeedDown = null;

        if ( mSpeedLevel != null )
            mSpeedLevel.destroy();
        mSpeedLevel = null;
    }

    public void updateSpeed() {
        if ( mLevel >= mMaxLevel )
            mLevel = mMaxLevel - 1;
        else if ( mLevel < 0 )
            mLevel = 0;

        mSpeedLevel.setNumber( mLevel + 1 );

    }

    public int getSpeed() {
        return mLevel;
    }
}
