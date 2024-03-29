package il.ac.huji.x_change.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.parse.ParseUser;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;

import il.ac.huji.x_change.R;

/**
 * Created by Gil on 09/08/2015.
 */
public class MessageService extends Service implements SinchClientListener {

    private static String APP_KEY;
    private static String APP_SECRET;
    private static String ENVIRONMENT;
    private final MessageServiceInterface serviceInterface = new MessageServiceInterface();
    private SinchClient sinchClient = null;
    private MessageClient messageClient = null;
    private LocalBroadcastManager broadcaster;
    private Intent broadcastIntent = new Intent("il.ac.huji.x_change.Activity.MainActivity");


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        APP_KEY = getResources().getString(R.string.sinch_key);
        APP_SECRET = getResources().getString(R.string.sinch_secret);
        ENVIRONMENT = getResources().getString(R.string.sinch_hostname);

        //get the current user id from Parse
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && !isSinchClientStarted()) {
            String currentUserId = currentUser.getObjectId();
            startSinchClient(currentUserId);
        }
        broadcaster = LocalBroadcastManager.getInstance(this);
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean isSinchClientStarted() {
        return sinchClient != null && sinchClient.isStarted();
    }

    public void startSinchClient(String username) {
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(username)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();
        //this client listener requires that you define
        //a few methods below
        sinchClient.addSinchClientListener(this);

        //messaging is "turned-on", but calling is not
        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportActiveConnectionInBackground(true);

        //push notifications is "turned-on"
        sinchClient.setSupportPushNotifications(true);
        sinchClient.setSupportManagedPush(true);

        sinchClient.checkManifest();
        sinchClient.start();
    }


    //The next 5 methods are for the sinch client listener

    @Override
    public void onClientFailed(SinchClient client, SinchError error) {
        broadcastIntent.putExtra("success", false);
        broadcaster.sendBroadcast(broadcastIntent);

        sinchClient = null;
    }

    @Override
    public void onClientStarted(SinchClient client) {
        broadcastIntent.putExtra("success", true);
        broadcaster.sendBroadcast(broadcastIntent);

        client.startListeningOnActiveConnection();
        messageClient = client.getMessageClient();
    }

    @Override
    public void onClientStopped(SinchClient client) {
        sinchClient = null;
    }

    @Override
    public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration clientRegistration) {}

    @Override
    public void onLogMessage(int level, String area, String message) {}

    @Override
    public IBinder onBind(Intent intent) {
        return serviceInterface;
    }

    public void sendMessage(String recipientUserId, String textBody) {
        if (messageClient != null) {
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            messageClient.send(message);
        }
    }

    public void addMessageClientListener(MessageClientListener listener) {
        if (messageClient != null) {
            messageClient.addMessageClientListener(listener);
        }
    }

    public void removeMessageClientListener(MessageClientListener listener) {
        if (messageClient != null) {
            messageClient.removeMessageClientListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }

    //public interface for ListUsersActivity & MessagingActivity
    public class MessageServiceInterface extends Binder {

        public void sendMessage(String recipientUserId, String textBody) {
            MessageService.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            MessageService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            MessageService.this.removeMessageClientListener(listener);
        }

        public boolean isSinchClientStarted() {
            return MessageService.this.isSinchClientStarted();
        }

    }
}
