package tw.com.miifun.popballoonfv;

import android.content.Context;

/**
 * Created by yhorn on 2016/2/23.
 */
public class ItemNumeric extends ItemMoving {

    public ItemNumeric(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);
    }

    public void setNumber( int number ) {
        switch( number ) {
            case 0: setBitmap( R.drawable.num0 ); break;
            case 1: setBitmap( R.drawable.num1 ); break;
            case 2: setBitmap( R.drawable.num2 ); break;
            case 3: setBitmap( R.drawable.num3 ); break;
            case 4: setBitmap( R.drawable.num4 ); break;
            case 5: setBitmap( R.drawable.num5 ); break;
            case 6: setBitmap( R.drawable.num6 ); break;
            case 7: setBitmap( R.drawable.num7 ); break;
            case 8: setBitmap( R.drawable.num8 ); break;
            case 9: setBitmap( R.drawable.num9 ); break;
            default: setBitmap( R.drawable.num0 );
        }
    }

    public void setBlueNumber( int number ) {
        switch( number ) {
            case 0: setBitmap( R.drawable.num0b ); break;
            case 1: setBitmap( R.drawable.num1b ); break;
            case 2: setBitmap( R.drawable.num2b ); break;
            case 3: setBitmap( R.drawable.num3b ); break;
            case 4: setBitmap( R.drawable.num4b ); break;
            case 5: setBitmap( R.drawable.num5b ); break;
            case 6: setBitmap( R.drawable.num6b ); break;
            case 7: setBitmap( R.drawable.num7b ); break;
            case 8: setBitmap( R.drawable.num8b ); break;
            case 9: setBitmap( R.drawable.num9b ); break;
            default: setBitmap( R.drawable.num0b );
        }
    }
}
