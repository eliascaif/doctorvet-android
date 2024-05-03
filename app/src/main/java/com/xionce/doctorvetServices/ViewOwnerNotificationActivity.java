package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.data.OwnerNotification;
import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.data.SellsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;

public class ViewOwnerNotificationActivity extends ViewBaseActivity {

    private static final String TAG = "ViewOwnerDebtsActivity";
    private TextView txt_subject;
    private TextView txt_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_owner_notification);

        toolbar_title.setText("Notificaciones");
        hideSubtitle();
        hideToolbarImage();
        hideFab();
        hideActivityContainer();
        setSwipeRefreshLayout();

        txt_subject = findViewById(R.id.txt_subject);
        txt_message = findViewById(R.id.txt_message);

        showOwnerNotification();
    }

    private void showOwnerNotification() {
        showProgressBar();
        Integer notificationId = getNotificationId();
        DoctorVetApp.get().getOwnerNotification(notificationId,new DoctorVetApp.VolleyCallbackObject() {
            @Override
            public void onSuccess(Object ownerNotification) {
                try {
                    showActivityContainer();
                    if (ownerNotification != null) {
                        setUI(ownerNotification);
                        setLoadedFinished();
                    } else {
                        showErrorMessage();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, TAG, true);
                    showErrorMessage();
                } finally {
                    hideProgressBar();
                }
            }
        });
    }
    @Override
    protected void setUI(Object object) {
        OwnerNotification ownerNotification = (OwnerNotification)object;
        txt_subject.setText(ownerNotification.getData().getSubject());
        txt_message.setText(ownerNotification.getData().getMessage());
    }

    @Override
    protected void invisibilizeEmptyViews() {

    }

    @Override
    protected void refreshView() {
        hideSwipeRefreshLayoutProgressBar();
    }

    @Override
    protected void go_update() {
    }

    @Override
    protected void go_delete() {
    }

    @Override
    protected void on_update_complete(Intent data) {
    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {
    }

    private Integer getNotificationId() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.OWNER_NOTIFICATION_ID.name(), 0);
    }

}
