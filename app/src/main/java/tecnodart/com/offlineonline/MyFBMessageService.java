package tecnodart.com.offlineonline;

import android.util.Log;

import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ashutosh on 23/3/18.
 */

public class MyFBMessageService extends FirebaseMessagingService{
    String TAG = "firebasenotify", title="", body="";
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mNotificationRef = mRootRef.child("notifications");
    public MyFBMessageService() {
        Log.d(TAG,"service called");
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        //mNotificationRef.push().setValue(remoteMessage.getNotification().getBody());
        Log.d(TAG,"onMessageReceived called");
        Log.d(TAG, "Notification Title: " +
                remoteMessage.getNotification().getTitle());
        title=remoteMessage.getNotification().getTitle();

        Log.d(TAG, "Notification Message: " +
                remoteMessage.getNotification().getBody());
        body=remoteMessage.getNotification().getBody();
        mNotificationRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                currentData.child(title).setValue(body);
                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
                //This method will be called once with the results of the transaction.
            }
        });
    }
}
