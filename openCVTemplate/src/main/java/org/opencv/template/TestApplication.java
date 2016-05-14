package org.opencv.template;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

public class TestApplication extends Application {
    private final String TAG = "DEBUG-APPLICATION";

    private int memberCount_;
    private int numberOfMember_;

    private String pictureFileName_;

    @Override
    public void onCreate() {
        /** Called when the Application-class is first created. */
        Log.v(TAG,"--- onCreate() in ---");
    }

    @Override
    public void onTerminate() {
        /** This Method Called when this Application finished. */
        Log.v(TAG,"--- onTerminate() in ---");
    }

    public void setMemberCount(int memberCount)
    {
        memberCount_ = memberCount;
    }

    public int getMemberCount()
    {
        return memberCount_;
    }

    public void setNumberOfMember( int numberOfMember )
    {
        numberOfMember_ = numberOfMember;
    }

    public void setPictureFileName( String pictureFileName ){ pictureFileName_ = pictureFileName; }

    public String getPictureFileName(){ return pictureFileName_; }
}