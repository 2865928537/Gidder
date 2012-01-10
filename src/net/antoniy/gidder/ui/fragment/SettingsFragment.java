package net.antoniy.gidder.ui.fragment;

import net.antoniy.gidder.R;
import net.antoniy.gidder.service.SSHDaemonService;
import net.antoniy.gidder.ui.activity.SlideActivity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingsFragment extends BaseFragment implements OnClickListener {
	private final static int SSH_STARTED_NOTIFICATION_ID = 1;
	
	private Button startSshdButton;
	private Button stopSshdButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout mainContainer = (LinearLayout) inflater.inflate(R.layout.settings, null);

		startSshdButton = (Button) mainContainer.findViewById(R.id.startSshdButton);
		startSshdButton.setOnClickListener(this);
		
		stopSshdButton = (Button) mainContainer.findViewById(R.id.stopSshdButton);
		stopSshdButton.setOnClickListener(this);
		
		return mainContainer;
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		
		Intent intent = new Intent(getActivity(), SSHDaemonService.class);
		if(viewId == R.id.startSshdButton) {
			if(isSshServiceRunning()) {
				Toast.makeText(getActivity(), "Service already started!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			getActivity().startService(intent);
			
			Notification notification = new Notification(R.drawable.ic_launcher, "SSH server started!", System.currentTimeMillis());
			notification.defaults |= Notification.DEFAULT_SOUND;
//			notification.defaults |= Notification.DEFAULT_VIBRATE;
//			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
			
			Intent notificationIntent = new Intent(getActivity(), SlideActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 1, notificationIntent, 0);

//			notification.setLatestEventInfo(getActivity(), "Gidder", "SSH server is running", contentIntent);
			notification.setLatestEventInfo(getActivity(), "SSH server is running", "IP: 192.168.1.100, Port: 6666", contentIntent);
			
			NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(SSH_STARTED_NOTIFICATION_ID, notification);
		} else if(viewId == R.id.stopSshdButton) {
			if(!isSshServiceRunning()) {
				Toast.makeText(getActivity(), "Service already stopped!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			getActivity().stopService(intent);
			
			NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(SSH_STARTED_NOTIFICATION_ID);
		}
	}
	
	private boolean isSshServiceRunning() {
	    ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (SSHDaemonService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
