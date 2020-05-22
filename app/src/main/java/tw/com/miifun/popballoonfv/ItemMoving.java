package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by yhorn on 2016/2/20.
 */
public class ItemMoving extends DrawableMovingItem {

    final static private String appTag = "ItemMoving";

    // debug: recycle counter
    static public int mBitmapRecycleCount = 0;

    int mResId;
    Bitmap mBitmap;
    Matrix mBitmapMatrix;

    public ItemMoving(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);
    }

    public void setBitmap( int resid ) {
        if ( mBitmap != null && mResId != 0 ) {
            BitmapWarehouse.releaseBitmap( mResId );
            mBitmapRecycleCount --;
            mBitmap = null;
        }

        mBitmap = BitmapWarehouse.getBitmap( mContext, resid );

        if ( mBitmap != null ) {
            mResId = resid;
        }
        else {
            mResId = 0;
            Log.w(appTag, "error, BitmapWarehouse.getBitmap return null");
        }
    }

    // public abstract method
    public boolean draw( Canvas canvas ) {

        Rect srcRect = null;
        Rect destRect;
        Bitmap bp;

        bp = getCurrentBitmap();

        if ( canvas == null ) {
            Log.w(appTag, "error, holder is null");
            return false;
        }

        if ( mX > mScreenWidth || mX + mWidth < 0 || mY > mScreenHeight || mY + mHeight < 0 ) {
            // Log.v(appTag, "ignore draw, outside the screen");
            return false;
        }

        if ( mX < 0 || mX + mWidth > mScreenWidth ||  mY < 0 || mY + mHeight > mScreenHeight ) {
            srcRect = getSrcRect( bp.getWidth(), bp.getHeight() );

            if ( srcRect == null ) {
                return false;
            }
        }

        destRect = getDestRect();

        // Log.v( appTag, "drawBitmap bp " + bp + " x:" + mX + " y:" + mY + " w:" + mWidth + " h:" + mHeight );
        if ( mBitmapMatrix == null ) {
            canvas.drawBitmap(bp, srcRect, destRect, null);
        }
        else {
            canvas.drawBitmap(bp, mBitmapMatrix, null);
        }

        return true;
    }

    public Bitmap getCurrentBitmap() {
        return mBitmap;
    }

    public void destroy() {

        // Log.v( appTag, "destroy" );
        if ( mBitmap != null && mResId != 0 ) {
            BitmapWarehouse.releaseBitmap( mResId );
            mResId = 0;
            mBitmapRecycleCount--;
        }
    }
}
