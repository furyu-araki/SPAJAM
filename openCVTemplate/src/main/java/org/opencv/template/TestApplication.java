package org.opencv.template;

import android.app.Application;
import android.os.Environment;

import java.io.File;

public class TestApplication extends Application {

    public static final String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/SuperBulletTime";

    private int memberCount_;
    private int numberOfMember_;

    private String pictureFileName_;

    @Override
    public void onCreate() {
        super.onCreate();

        File directory = new File(DIRECTORY);
        directory.mkdir();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
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

    public int getNumberOfMember(){ return numberOfMember_; }

    public void setPictureFileName( String pictureFileName ){ pictureFileName_ = pictureFileName; }

    public String getPictureFileName(){ return pictureFileName_; }

    public String getPictureFilePath() {
        return DIRECTORY + "/" + getPictureFileName();
    }
}