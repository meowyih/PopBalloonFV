package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

/**
 * Created by yhorn on 2016/2/24.
 */
public class ThemeBackSfx extends Theme {

    final static private String appTag = "ThemeSfx";

    boolean mIsAlive = true;
    long mSplashStart;
    long mSplashStop;
    long mSplashInterval;

    Point[] mOutPoints;
    Point mCenter;

    public ThemeBackSfx(Context context, int screenWidth, int screenHeight) {
        super(context, screenWidth, screenHeight);

        mOutPoints  = new Point[12];

        for ( int i = 0; i < 12; i ++ ) {
            mOutPoints[i] = new Point();
        }

        mCenter = new Point();

        mOutPoints[0].x = mOutPoints[9].x =mScreenWidth / 5;
        mOutPoints[1].x = mOutPoints[8].x =mScreenWidth * 2 / 5;
        mOutPoints[2].x = mOutPoints[7].x =mScreenWidth * 3 / 5;
        mOutPoints[3].x = mOutPoints[6].x =mScreenWidth * 4 / 5;
        mOutPoints[10].x = mOutPoints[11].x = 0;
        mOutPoints[4].x = mOutPoints[5].x = mScreenWidth;

        mOutPoints[0].y = mOutPoints[1].y =mOutPoints[2].y =mOutPoints[3].y = 0;
        mOutPoints[6].y = mOutPoints[7].y =mOutPoints[8].y =mOutPoints[9].y = mScreenHeight;
        mOutPoints[4].y = mOutPoints[11].y = mScreenHeight / 3;
        mOutPoints[5].y = mOutPoints[10].y = mScreenHeight * 2 / 3;

        mCenter.x = mScreenWidth / 2;
        mCenter.y = mScreenHeight / 2;
    }

    public void showSplash( long start, long stop, long interval ) {
        mSplashStart = start;
        mSplashStop = stop;
        mSplashInterval = interval;
    }

    @Override
    public int id() { return THEME_BACK_SFX; }

    @Override
    public DrawableItemEvent move( long time ) {

        if ( mSplashStop <= time )
            mIsAlive = false;

        return new DrawableItemEvent( DrawableItemEvent.NONE, 0, 0 );
    }

    @Override
    public boolean drawTheme( Canvas canvas ) {

        boolean firstPic;
        long time = System.currentTimeMillis();
        long diff = time - mSplashStart;
        long remain = ( mSplashInterval > 0 ? (diff % ( mSplashInterval * 2 )) : 0);

        if ( time > mSplashStart ) {

            Path path = new Path();
            Paint paint = new Paint();

            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setAntiAlias(true);

            if ( remain < mSplashInterval ) {
                firstPic = true;
            }
            else {
                firstPic = false;
            }

            for ( int i = 0; i < 12; i ++ ) {
                if ( i % 2 == 0 ) {
                    if ( firstPic )
                        paint.setColor(DrawUtil.getColorFromResource(mContext, R.color.ThemeSfxYellow));
                    else
                        paint.setColor(DrawUtil.getColorFromResource(mContext, R.color.ThemeSfxRed));
                }
                else {
                    if ( firstPic )
                        paint.setColor(DrawUtil.getColorFromResource(mContext, R.color.ThemeSfxRed));
                    else
                        paint.setColor(DrawUtil.getColorFromResource(mContext, R.color.ThemeSfxYellow));
                }

                path.moveTo(mCenter.x, mCenter.y);
                path.lineTo(mOutPoints[i].x, mOutPoints[i].y);

                if ( i == 3 )
                    path.lineTo( mScreenWidth, 0 );
                else if ( i == 5 )
                    path.lineTo( mScreenWidth, mScreenHeight );
                else if ( i == 9 )
                    path.lineTo( 0, mScreenHeight );
                else if ( i == 11 )
                    path.lineTo( 0, 0 );

                path.lineTo(mOutPoints[(i + 1) % 12].x, mOutPoints[(i + 1) % 12].y);
                path.lineTo(mCenter.x, mCenter.y);
                path.close();

                canvas.drawPath( path, paint );

                path.reset();
            }

        }

        return true;
    }

    @Override
    public void destroy() {
    }
}
