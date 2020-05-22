package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by yhorn on 2016/2/25.
 */
public class ItemCenterScore extends ItemMoving {

    int mTens, mHundreds, mOnes;
    int mCharWidth, mCharHeight;
    ItemNumeric mItemTens, mItemHundreds, mItemOnes;
    long mStartTime, mStopTime;

    public ItemCenterScore(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        mCharHeight = mScreenHeight / 3;
        mCharWidth = mCharHeight *
                mContext.getResources().getInteger( R.integer.numeric_character_width ) /
                mContext.getResources().getInteger( R.integer.numeric_character_height );
    }

    public void showNumber( int score, long start, long stop ) {
        if ( score > 999 )
            score = 999;
        else if ( score < 0 )
            score = 0;

        mStartTime = start;
        mStopTime = stop;

        mHundreds = score / 100;
        mTens = ( score - mHundreds * 100 ) / 10;
        mOnes = score % 10;

        if ( mItemHundreds != null )
            mItemHundreds.destroy();
        if ( mItemTens != null )
            mItemTens.destroy();
        if ( mItemOnes != null )
            mItemOnes.destroy();

        mItemHundreds = null;
        mItemTens = null;
        mItemOnes = null;

        // crete image during the set number
        if ( score <= 9 ) {
            // only one digit
            mItemOnes = new ItemNumeric( mContext, 0,
                    mScreenWidth / 2 - mCharWidth / 2, mScreenHeight / 2 - mCharHeight / 2,
                    mCharWidth, mCharHeight, mScreenWidth, mScreenHeight );
            mItemOnes.setBlueNumber(mOnes);
        }
        else if ( score <= 99 ) {
            // two digits
            mItemTens = new ItemNumeric( mContext, 0,
                    mScreenWidth / 2 - mCharWidth, mScreenHeight / 2 - mCharHeight / 2,
                    mCharWidth, mCharHeight, mScreenWidth, mScreenHeight );
            mItemTens.setBlueNumber(mTens);
            mItemOnes = new ItemNumeric( mContext, 0,
                    mScreenWidth / 2, mScreenHeight / 2 - mCharHeight / 2,
                    mCharWidth, mCharHeight, mScreenWidth, mScreenHeight );
            mItemOnes.setBlueNumber(mOnes);
        }
        else {
            // three digits
            mItemHundreds = new ItemNumeric( mContext, 0,
                    mScreenWidth / 2 - mCharWidth * 3 / 2, mScreenHeight / 2 - mCharHeight / 2,
                    mCharWidth, mCharHeight, mScreenWidth, mScreenHeight );
            mItemHundreds.setBlueNumber(mHundreds);
            mItemTens = new ItemNumeric( mContext, 0,
                    mScreenWidth / 2 - mCharWidth / 2, mScreenHeight / 2 - mCharHeight / 2,
                    mCharWidth, mCharHeight, mScreenWidth, mScreenHeight );
            mItemTens.setBlueNumber(mTens );
            mItemOnes = new ItemNumeric( mContext, 0,
                    mScreenWidth / 2 + mCharWidth / 2, mScreenHeight / 2 - mCharHeight / 2,
                    mCharWidth, mCharHeight, mScreenWidth, mScreenHeight );
            mItemOnes.setBlueNumber( mOnes );
        }
    }

    @Override
    public boolean draw( Canvas canvas ) {

        long time = System.currentTimeMillis();
        if ( time < mStartTime || time > mStopTime )
            return false;

        if ( mItemHundreds != null )
            mItemHundreds.draw(canvas);

        if ( mItemTens != null )
            mItemTens.draw(canvas);

        if ( mItemOnes != null )
            mItemOnes.draw(canvas);
        return true;
    }

    @Override
    public void destroy() {
        if ( mItemHundreds != null )
            mItemHundreds.destroy();
        if ( mItemTens != null )
            mItemTens.destroy();
        if ( mItemOnes != null )
            mItemOnes.destroy();

        mItemHundreds = null;
        mItemTens = null;
        mItemOnes = null;
    }

}
