package org.opencv.template;

import android.content.Context;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by arakitaku on 2016/05/15.
 */
public class FirebaseHelper implements ChildEventListener {
    private static final String PATH_MESSAGE = "message";

    Firebase firebase;

    OnNextDeviceBroadcastReceiver receiver;

    public interface OnNextDeviceBroadcastReceiver {
        /**
         * 次に撮るべき端末の番号を受け取った時によばれる。
         * 自分がその端末なら、シャッターを切ること
         *
         * @param num
         */
        void onNextDeviceBroadcastReceive(int num);
    }

    public FirebaseHelper(Context context, OnNextDeviceBroadcastReceiver receiver) {
        Firebase.setAndroidContext(context);
        firebase = new Firebase("https://torrid-torch-6405.firebaseio.com/").child(PATH_MESSAGE);
        firebase.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                firebase.addChildEventListener(FirebaseHelper.this);
            }
        });
        this.receiver = receiver;
    }

    /**
     * 次に撮る端末の番号をブロードキャストします。
     *
     * @param num
     */
    public void broadcastNextDevice(Integer num) {
        firebase.push().setValue(num);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Integer num = dataSnapshot.getValue(Integer.class);
        receiver.onNextDeviceBroadcastReceive(num);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
    }
}
