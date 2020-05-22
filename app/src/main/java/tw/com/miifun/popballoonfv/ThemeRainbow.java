package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;

/**
 * Created by yhorn on 2016/2/21.
 */
public class ThemeRainbow extends Theme {

    final static private String appTag = "ThemeRainbow";

    private long DURATION_IN = 5000;
    private long DURATION_OUT = 5000;
    final static int NUMBER_OF_RAINBOW = 4;

    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;

    long mThemeInTime, mThemeOutTime;

    ItemRainbow[] mRainbows = new ItemRainbow[NUMBER_OF_RAINBOW];
    int mRainbowWidth, mRainbowHeight;

    public ThemeRainbow( Context context, int screenWidth, int screenHeight ) {
        super( context, screenWidth, screenHeight );

        mContext = context;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;

        mRainbowWidth = mScreenWidth / 3;
        mRainbowHeight = mRainbowWidth * context.getResources().getInteger( R.integer.rainbow_height ) /
                context.getResources().getInteger( R.integer.rainbow_width );

        // 4 rainbows;
        mRainbows[0] = new ItemRainbow(mContext, 0, 0 - mRainbowWidth, mRainbowHeight / 2, mRainbowWidth, mRainbowHeight, mScreenWidth, mScreenHeight);
        mRainbows[1] = new ItemRainbow(mContext, 0, mScreenWidth / 2, 0 - mRainbowHeight, mRainbowWidth, mRainbowHeight, mScreenWidth, mScreenHeight);
        mRainbows[2] = new ItemRainbow(mContext, 0, mRainbowHeight / 2, mScreenHeight, mRainbowWidth, mRainbowHeight, mScreenWidth, mScreenHeight);
        mRainbows[3] = new ItemRainbow(mContext, 0, mScreenWidth + mRainbowWidth, mScreenHeight / 2, mRainbowWidth, mRainbowHeight, mScreenWidth, mScreenHeight);
        mRainbows[0].setBitmap(R.drawable.rainbow);
        mRainbows[1].setBitmap(R.drawable.rainbow);
        mRainbows[2].setBitmap(R.drawable.rainbow);
        mRainbows[3].setBitmap(R.drawable.rainbow);
    }

    @Override
    public int id() { return THEME_RAINBOW; }

    @Override
    public long durationIn() {
        return DURATION_IN;
    }

    @Override
    public long durationOut() {
        return DURATION_OUT;
    }

    @Override
    public void skipThemeIn( long time ) {
        mState = Theme.STATE_RUN;

        for ( int i = 0; i < NUMBER_OF_RAINBOW; i ++ ) {
            mRainbows[i].setDestination(mRainbows[i].getDefaultPosition().x, mRainbows[i].getDefaultPosition().y, time, 0);
        }
    }

    @Override
    public void startThemeIn( long time ) {
        mState = Theme.STATE_IN;
        mThemeInTime = time;

        for ( int i = 0; i < NUMBER_OF_RAINBOW; i ++ ) {
            mRainbows[i].setDestination(mRainbows[i].getDefaultPosition().x, mRainbows[i].getDefaultPosition().y, time, durationIn());
        }
    }

    @Override
    public void startThemeOut( long time ) {
        mState = Theme.STATE_OUT;
        mThemeOutTime = time;

        mRainbows[0].setDestination(0 - mRainbowWidth, mRainbowHeight / 2, mRainbowWidth, mRainbowHeight);
        mRainbows[1].setDestination(mScreenWidth / 2, 0 - mRainbowHeight, mRainbowWidth, mRainbowHeight);
        mRainbows[2].setDestination(mRainbowHeight / 2, mScreenHeight, mRainbowWidth, mRainbowHeight);
        mRainbows[3].setDestination(mScreenWidth + mRainbowWidth, mScreenHeight / 2, mRainbowWidth, mRainbowHeight);
    }

    @Override
    public DrawableItemEvent move( long time ) {

        if ( mState == Theme.STATE_IN && time - mThemeInTime > durationIn() ) {
            mState = Theme.STATE_RUN;
        }

        if ( mState == Theme.STATE_OUT && time - mThemeOutTime > durationOut() ) {
            mState = Theme.STATE_RUN;
        }

        for ( int i = 0; i < NUMBER_OF_RAINBOW; i ++ ) {
            mRainbows[i].move(time);
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, 0, 0 );
    }

    @Override
    public boolean drawTheme( Canvas canvas ) {

        // draw background
        Paint paint = new Paint();
        Rect rect;

        if ( mState == STATE_IN ) {
            int y = (int)( mScreenHeight * ( System.currentTimeMillis() - mThemeInTime ) / durationIn());
            if ( y < 0 ) y = 0;
            if ( y > mScreenHeight ) y = mScreenHeight;
            rect = new Rect( 0, 0, mScreenWidth, y );
        }
        else if ( mState == STATE_OUT ) {
            int y = (int)( mScreenHeight * ( System.currentTimeMillis() - mThemeOutTime ) / durationOut());
            if ( y < 0 ) y = 0;
            if ( y > mScreenHeight ) y = mScreenHeight;
            rect = new Rect( 0, y, mScreenWidth, mScreenHeight );
        }
        else {
            rect = new Rect(0, 0, mScreenWidth, mScreenHeight);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(mContext, R.color.ThemeRainbowBackground));
        canvas.drawRect(rect, paint);

        for ( int i = NUMBER_OF_RAINBOW - 1; i >= 0; i -- ) {
            mRainbows[i].draw(canvas);
        }

        return true;
    }

    @Override
    public void destroy() {

        for ( int i = 0; i < NUMBER_OF_RAINBOW; i ++ ) {
            mRainbows[i].destroy();
        }
    }

    // MotionEvent handler
    public void onTouchEvent( MotionEvent event ) {

        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                for ( int i = 0; i < NUMBER_OF_RAINBOW; i ++ ) {
                    if ( mRainbows[i].onTouchEvent( event ) == ItemRainbow.ACTION_START_MOVING )
                        break;
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
    }
}
