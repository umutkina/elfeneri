package com.umutkina.elfeneri.pushnotif;

import android.os.Bundle;
import android.widget.ListView;

import com.umutkina.elfeneri.R;
import com.umutkina.elfeneri.pushnotif.adapters.NotificationAdapter;
import com.umutkina.elfeneri.pushnotif.modals.Notification;
import com.umutkina.elfeneri.pushnotif.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationActivity extends BaseActivity {

    @Bind(R.id.lv_notifications)
    ListView lvNotifications;
    NotificationAdapter notificationAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_notification;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<Notification> notifList = Utils.getNotifList(this);

        notificationAdapter = new NotificationAdapter(this, notifList);
        lvNotifications.setAdapter(notificationAdapter);

    }

    public void itemDelete(Notification notification) {

        Utils.deleteNotification(this, notification);
        ArrayList<Notification> notifList = Utils.getNotifList(this);
        notificationAdapter.setNotifications(notifList);
        notificationAdapter.notifyDataSetChanged();

    }
}
