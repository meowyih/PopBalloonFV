package tw.com.miifun.popballoonfv;

import java.util.LinkedList;

/**
 * Created by yhorn on 2016/3/3.
 */
public class AppGameData {

    public class BalloonData {
        public int mItemType;
        public int mState;
        public double mSpeed;
        public int mX;
        public int mY;
        public int mWidth;
        public int mHeight;
    }

    public int score = 0;
    public int lastThumbScore = 0;
    public int lastSfxScore = 0;
    public int theme = Theme.THEME_SUNNY;
    public LinkedList<BalloonData> balloons = new LinkedList<>();

    public void addBalloonData( int type, int state, double speed, int x, int y, int w, int h ) {
        BalloonData data = new BalloonData();
        data.mItemType = type;
        data.mState = state;
        data.mSpeed = speed;
        data.mX = x;
        data.mY = y;
        data.mWidth = w;
        data.mHeight = h;
        balloons.add( data );
    }

    public void addBalloonData( LinkedList<ItemBalloon> items ) {
        balloons.clear();

        if ( items == null ) {
            return;
        }

        for ( int i = 0; i < items.size(); i ++ ) {
            ItemBalloon item = items.get(i);
            addBalloonData( item.mItemType, item.mState, item.mSpeed, item.mX, item.mY, item.mWidth, item.mHeight );
        }
    }
}
