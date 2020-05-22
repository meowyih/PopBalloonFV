package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by yhorn on 2016/2/23.
 */
public class ItemScore extends ItemMoving {

    int mTens, mHundreds, mOnes;
    int mCharWidth, mCharHeight;
    ItemNumeric mItemTens, mItemHundreds, mItemOnes;

    public ItemScore(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        mCharWidth = width / 3; // 3 digits
        mCharHeight = height;

        mItemHundreds = new ItemNumeric( context, 0, x, y, mCharWidth, mCharHeight, mScreenWidth, mScreenHeight );
        mItemTens = new ItemNumeric( context, 0, x + mCharWidth, y, mCharWidth, mCharHeight, mScreenWidth, mScreenHeight );
        mItemOnes = new ItemNumeric( context, 0, x + mCharWidth * 2, y, mCharWidth, mCharHeight, mScreenWidth, mScreenHeight );

        mItemHundreds.setNumber(mHundreds);
        mItemTens.setNumber(mTens);
        mItemOnes.setNumber(mOnes);

        mTens = 0;
        mHundreds = 0;
        mOnes = 0;
    }

    public void setScore( int score ) {
        if ( score > 999 )
            score = 999;
        else if ( score < 0 )
            score = 0;

        mHundreds = score / 100;
        mTens = ( score - mHundreds * 100 ) / 10;
        mOnes = score % 10;

        mItemHundreds.setNumber(mHundreds);
        mItemTens.setNumber(mTens);
        mItemOnes.setNumber(mOnes);
    }

    @Override
    public boolean draw( Canvas canvas ) {
        mItemHundreds.draw( canvas );
        mItemTens.draw(canvas);
        mItemOnes.draw( canvas );
        return true;
    }

    @Override
    public void destroy() {
        mItemHundreds.destroy();
        mItemTens.destroy();
        mItemOnes.destroy();
    }
}
