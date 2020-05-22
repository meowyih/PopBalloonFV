package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by yhorn on 2016/2/21.
 */
public class ThemeGrid extends Theme {

    final static private String appTag = "ThemeGrid";

    final static int GRID_HORIZONTAL = 10;
    final static int GRID_VERTICAL = 5;


    private long DURATION_IN = 5000;
    private long DURATION_OUT = 5000;
    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;

    long mThemeInTime, mThemeOutTime;

    int mGridWidth, mGridWidthLeft, mGridWidthRight;
    int mGridHeight, mGridHeightTop, mGridHeightBottom;

    ItemGrid mGridsOut[][] = new ItemGrid[GRID_HORIZONTAL][GRID_VERTICAL];
    ItemGrid mGridsStatic[][] = new ItemGrid[GRID_HORIZONTAL][GRID_VERTICAL];
    ItemGrid mGrids[][] = new ItemGrid[GRID_HORIZONTAL][GRID_VERTICAL];

    final static int TOTAL_COLOR = 4;
    int[] mColorList = new int[TOTAL_COLOR];

    public ThemeGrid( Context context, int screenWidth, int screenHeight ) {
        super( context, screenWidth, screenHeight );

        int color, previousColor = 0;
        mContext = context;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;

        mGridWidth = mScreenWidth / GRID_HORIZONTAL;
        mGridWidthLeft = mGridWidth + ( mScreenWidth - mGridWidth * GRID_HORIZONTAL ) / 2;
        mGridWidthRight = mScreenWidth - mGridWidth * ( GRID_HORIZONTAL - 2 ) - mGridWidthLeft;

        mGridHeight = mScreenHeight / GRID_VERTICAL;
        mGridHeightTop = mGridHeight + ( mScreenHeight - mGridHeight * GRID_VERTICAL ) / 2;
        mGridHeightBottom = mScreenHeight - mGridHeight * ( GRID_VERTICAL - 2 ) - mGridHeightTop;

        mColorList[0] = R.color.ThemeGridColorBrown;
        mColorList[1] = R.color.ThemeGridColorYellow;
        mColorList[2] = R.color.ThemeGridColorRed;
        mColorList[3] = R.color.ThemeGridColorGreen;

        // create grid size and color
        for ( int i = 0; i < GRID_HORIZONTAL; i ++ ) {
            for ( int j = 0; j < GRID_VERTICAL; j ++ ) {
                int width, height;
                int x, y;

                if ( i == 0 )
                    width = mGridWidthLeft;
                else if ( i == GRID_HORIZONTAL - 1 )
                    width = mGridWidthRight;
                else
                    width = mGridWidth;

                if ( j == 0 )
                    height = mGridHeightTop;
                else if ( j == GRID_VERTICAL - 1 )
                    height = mGridHeightBottom;
                else
                    height = mGridHeight;

                if ( i == 0 )
                    x = 0;
                else if ( i == 1 )
                    x = mGridWidthLeft;
                else
                    x = mGridWidthLeft + ( i - 1 ) * mGridWidth;

                if ( j == 0 )
                    y = 0;
                else if ( j == 1 )
                    y = mGridHeightTop;
                else
                    y = mGridHeightTop + ( j - 1 ) * mGridHeight;

                if ( i > 0 )
                    previousColor = mGrids[i-1][j].mColorId;
                else if ( j > 0 )
                    previousColor = mGrids[i][j-1].mColorId;

                color = getRandomColor( previousColor );
                mGrids[i][j] = new ItemGrid( mContext, 0,
                        x, y - mScreenHeight - ( i + 1 ) * height - (( GRID_VERTICAL - 1 ) * ( GRID_VERTICAL - 1 ) - j*j ) * height,
                        width, height, mScreenWidth, mScreenHeight, color );
                mGridsStatic[i][j] = new ItemGrid( mContext, 0, x, y, width, height, mScreenWidth, mScreenHeight, color );
                mGridsOut[i][GRID_VERTICAL - j - 1] =
                        new ItemGrid( mContext, 0,
                        x, y + mScreenHeight + ( i + 1 ) * height + (( GRID_VERTICAL - 1 ) * ( GRID_VERTICAL - 1 ) - j*j ) * height,
                        width, height, mScreenWidth, mScreenHeight, color );
                previousColor = color;
            }
        }
    }

    int getRandomColor( int exclusiveColorId ) {

        int index = (int)( Math.random() * TOTAL_COLOR );

        if ( mColorList[index] == exclusiveColorId ) {
            index = ( index + 1 ) % TOTAL_COLOR;
        }

        // Log.d( appTag, "color " + mColorList[index] + " " + exclusiveColorId );

        return mColorList[index];
    }

    @Override
    public int id() { return THEME_GRID; }

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

        for ( int i = 0; i < GRID_HORIZONTAL; i ++ ) {
            for ( int j = 0; j < GRID_VERTICAL; j ++) {
                ItemGrid grid = mGrids[i][j];
                grid.setDestination( mGridsStatic[i][j].mX, mGridsStatic[i][j].mY, time, 0 );
            }
        }
    }

    @Override
    public void startThemeIn( long time ) {
        mState = Theme.STATE_IN;
        mThemeInTime = time;

        for ( int i = 0; i < GRID_HORIZONTAL; i ++ ) {
            for ( int j = 0; j < GRID_VERTICAL; j ++) {
                ItemGrid grid = mGrids[i][j];
                grid.setDestination( mGridsStatic[i][j].mX, mGridsStatic[i][j].mY, time, durationIn() );
            }
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void startThemeOut( long time ) {
        mState = Theme.STATE_OUT;
        mThemeOutTime = time;

        for ( int i = 0; i < GRID_HORIZONTAL; i ++ ) {
            for ( int j = 0; j < GRID_VERTICAL; j ++) {
                ItemGrid grid = mGrids[i][j];
                grid.setDestination( mGridsOut[i][j].mX, mGridsOut[i][j].mY, time, durationIn() );
            }
        }
    }

    @Override
    public DrawableItemEvent move( long time ) {

        if ( mState == Theme.STATE_IN && time - mThemeInTime > durationIn() ) {
            mState = Theme.STATE_RUN;
        }

        if ( mState == Theme.STATE_OUT && time - mThemeOutTime > durationOut() ) {
            mState = Theme.STATE_RUN;
        }

        for (int i = 0; i < GRID_HORIZONTAL; i++) {
            for (int j = 0; j < GRID_VERTICAL; j++) {
                ItemGrid grid = mGrids[i][j];
                grid.move(time);
            }
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, 0, 0 );
    }

    @Override
    public boolean drawTheme( Canvas canvas ) {

        for ( int i = 0; i < GRID_HORIZONTAL; i ++ ) {
            for ( int j = 0; j < GRID_VERTICAL; j ++) {
                ItemGrid grid = mGrids[i][j];
                grid.draw( canvas );
            }
        }

        return true;
    }

    // MotionEvent handler
    public void onTouchEvent( MotionEvent event ) {

        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                if ( mState == Theme.STATE_RUN ) {
                    long time = System.currentTimeMillis();
                    for (int i = 0; i < GRID_HORIZONTAL; i++) {
                        for (int j = 0; j < GRID_VERTICAL; j++) {
                            ItemGrid grid = mGrids[i][j];

                            if ( grid.isHit( (int)event.getX(), (int)event.getY()) ) {
                                // grid.mColorId = getRandomColor( grid.mColorId );
                                grid.changeColorTemperary( R.color.black, time, (long)(Math.random()*2000));
                            }
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
    }
}
