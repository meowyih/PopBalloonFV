package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by yhorn on 2016/2/24.
 */
public class ThemeFrontSfx extends Theme {

    final static private String appTag = "ThemeFrontSfx";

    ItemThumb mThumb;
    ItemCenterScore mCenterScore;
    boolean mIsAlive = true;

    public ThemeFrontSfx(Context context, int screenWidth, int screenHeight) {
        super(context, screenWidth, screenHeight);

        int x,y,w,h;

        h = mScreenHeight / 2;
        w = h * mContext.getResources().getInteger( R.integer.thumb_width ) /
                mContext.getResources().getInteger( R.integer.thumb_height );
        x = ( mScreenWidth - w ) / 2;
        y = ( mScreenHeight - h ) / 2;
        mThumb = new ItemThumb( mContext, 0, x, y, w, h, mScreenWidth, mScreenHeight );

        // ItemCenterScore won't load image before setScore
        mCenterScore = new ItemCenterScore( mContext, 0, 0, 0, 0, 0, mScreenWidth, mScreenHeight );
    }

    public void showThumb( long start, long stop, long interval ) {
        mThumb.setTime( start, stop, interval );
    }

    public void showNumber( int score, long start, long stop ) {
        mCenterScore.showNumber(score, start, stop );
    }

    @Override
    public int id() { return THEME_FRONT_SFX; }

    @Override
    public DrawableItemEvent move( long time ) {

        if ( mThumb.mStopTime <= time && mCenterScore.mStopTime <= time ) {
            mIsAlive = false;
            return new DrawableItemEvent( DrawableItemEvent.DEAD, 0, 0 );
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, 0, 0 );
    }

    @Override
    public boolean drawTheme( Canvas canvas ) {
        mThumb.draw( canvas );
        mCenterScore.draw( canvas );
        return true;
    }

    @Override
    public void destroy() {
        if ( mThumb != null )
            mThumb.destroy();
        mThumb = null;

        if ( mCenterScore != null )
            mCenterScore.destroy();
        mCenterScore = null;
    }
}
