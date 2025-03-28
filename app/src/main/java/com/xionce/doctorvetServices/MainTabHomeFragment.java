package com.xionce.doctorvetServices;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.UserNotification;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainTabHomeFragment extends FragmentBase {

    private static final String TAG = "MainTabHomeFragment";
    private View rootView;

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    public MainTabHomeFragment() {
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, parent, false);
        setSwipeRefreshLayout(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void refreshView() {
        showProgressBar();
        URL notificacionesUrl = NetworkUtils.buildGetNotificationsUrl();
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, notificacionesUrl.toString(),
                response -> {
                    try {
                        String data = MySqlGson.getDataFromResponse(response).toString();
                        ArrayList<UserNotification> notificaciones = MySqlGson.getGson().fromJson(data, new TypeToken<List<UserNotification>>(){}.getType());
                        showNotificaciones(notificaciones, rootView);
                    } catch (Exception ex) {
                        DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                    } finally {
                        hideProgressBar();
                        hideSwipeRefreshLayoutProgressBar();
                    }
                },
                error -> {
                    DoctorVetApp.get().handle_volley_error(error, TAG, true);
                    hideProgressBar();
                    hideSwipeRefreshLayoutProgressBar();
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void showNotificaciones(ArrayList<UserNotification> notificaciones, View rootView) {
        LinearLayout linearLayout = rootView.findViewById(R.id.lista_datos_inicio);
        linearLayout.removeAllViews();

        View view = LayoutInflater.from(linearLayout.getContext()).inflate(R.layout.cardview_inicio, linearLayout, false);
        TextView txt_notificacion = view.findViewById(R.id.txt_notificacion);
        txt_notificacion.setText("Resumen del día. " + HelperClass.getDateInLocaleLong(Calendar.getInstance().getTime()));
        txt_notificacion.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txt_notificacion.setGravity(Gravity.CENTER);
        linearLayout.addView(view);

        if (notificaciones.isEmpty())
            return;

        if (getActivity() == null)
            return;

        for (UserNotification notificacionUsuario :notificaciones) {
            if (notificacionUsuario.getNotification_type() == null)
                continue;

            showNotificacion(linearLayout, notificacionUsuario);
        }
    }
    private void showNotificacion(LinearLayout container, UserNotification notificacionUsuario) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.cardview_inicio, container, false);
        CardView cardView = view.findViewById(R.id.cardview_inicio);
        TextView txt_notificacion = view.findViewById(R.id.txt_notificacion);
        ImageView img_thumb = view.findViewById(R.id.img_thumb);
        img_thumb.setVisibility(View.VISIBLE);

        switch (notificacionUsuario.getNotification_type()) {
            case WELCOME:
                img_thumb.setVisibility(View.GONE);
                txt_notificacion.setText("Bienvenido a Doctor Vet");
                container.addView(view);
                break;
            case USER_ACCEPTED_JOIN_REQUEST:
                Glide.with(img_thumb).load(R.drawable.ic_user_accepted_join_request).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Nuevos usuarios");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.NEW_VET_USERS);
                    }
                });
                container.addView(view);
                break;
            case OWNERS_NOTIFICATIONS:
                Glide.with(img_thumb).load(R.drawable.ic_owners_notifications).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Notificaciones a propietarios");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.OWNERS_NOTIFICATIONS);
                    }
                });
                container.addView(view);
                break;
            case CREDITORS:
                Glide.with(img_thumb).load(R.drawable.ic_provider).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Acreedores");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.CREDITORS);
                    }
                });
                container.addView(view);
                break;
            case CREDITORS_TOLERANCE:
                Glide.with(img_thumb).load(R.drawable.ic_provider).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Acreedores sin pagos por más de 35 días");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.CREDITORS_TOLERANCE);
                    }
                });
                container.addView(view);
                break;
            case DEBTORS:
                Glide.with(img_thumb).load(R.drawable.ic_creditors_tolerance).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Deudores");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.DEBTORS);
                    }
                });
                container.addView(view);
                break;
            case DEBTORS_TOLERANCE:
                Glide.with(img_thumb).load(R.drawable.ic_creditors_tolerance).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Deudores sin pagos por más de 35 días");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.DEBTORS_TOLERANCE);
                    }
                });
                container.addView(view);
                break;
            case USER_JOIN_REQUEST:
                Glide.with(img_thumb).load(R.drawable.ic_user_join_request).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Solicitudes para unirse a la veterinaria");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.USER_JOIN_REQUEST);
                    }
                });
                container.addView(view);
                break;
            case PENDING_AGENDA_VET:
                Glide.with(img_thumb).load(R.drawable.ic_agenda).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Agenda pendiente");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.PENDING_AGENDA_VET);
                    }
                });
                container.addView(view);
                break;
            case PENDING_AGENDA_USER:
                Glide.with(img_thumb).load(R.drawable.ic_agenda).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Agenda pendiente (usr)");
                txt_notificacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.PENDING_AGENDA_USER);
                    }
                });
                container.addView(view);
                break;
            case EXPIRED_AGENDA_VET:
                Glide.with(img_thumb).load(R.drawable.ic_agenda).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Agenda vencida ayer");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.EXPIRED_AGENDA_VET);
                    }
                });
                container.addView(view);
                break;
            case EXPIRED_AGENDA_USER:
                Glide.with(img_thumb).load(R.drawable.ic_agenda).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Agenda vencida ayer (usr)");
                txt_notificacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.EXPIRED_AGENDA_USER);
                    }
                });
                container.addView(view);
                break;
            case EXPIRED_AGENDA_ALL_VET:
                Glide.with(img_thumb).load(R.drawable.ic_agenda).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Agenda vencida");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.EXPIRED_AGENDA_ALL_VET);
                    }
                });
                container.addView(view);
                break;
            case TOMORROW_AGENDA_VET:
                Glide.with(img_thumb).load(R.drawable.ic_agenda).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Agenda de mañana");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.TOMORROW_AGENDA_VET);
                    }
                });
                container.addView(view);
                break;
            case TOMORROW_AGENDA_USER:
                Glide.with(img_thumb).load(R.drawable.ic_agenda).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Agenda de mañana (usr)");
                txt_notificacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.TOMORROW_AGENDA_USER);
                    }
                });
                container.addView(view);
                break;

            case PENDING_SUPPLY_VET:
                Glide.with(img_thumb).load(R.drawable.ic_supply).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Suministro pendiente");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.PENDING_SUPPLY_VET);
                    }
                });
                container.addView(view);
                break;
            case PENDING_SUPPLY_USER:
                Glide.with(img_thumb).load(R.drawable.ic_supply).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Suministro pendiente (usr)");
                txt_notificacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.PENDING_SUPPLY_USER);
                    }
                });
                container.addView(view);
                break;
            case DOMICILIARY_PENDING_SUPPLY_VET:
                Glide.with(img_thumb).load(R.drawable.ic_supply).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Suministro domiciliario pendiente");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.DOMICILIARY_PENDING_SUPPLY_VET);
                    }
                });
                container.addView(view);
                break;
            case DOMICILIARY_PENDING_SUPPLY_USER:
                Glide.with(img_thumb).load(R.drawable.ic_supply).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Suministro domiciliario pendiente (usr)");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.DOMICILIARY_PENDING_SUPPLY_USER);
                    }
                });
                container.addView(view);
                break;
            case EXPIRED_SUPPLY_VET:
                Glide.with(img_thumb).load(R.drawable.ic_supply).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Suministro vencido ayer");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.EXPIRED_SUPPLY_VET);
                    }
                });
                container.addView(view);
                break;
            case EXPIRED_SUPPLY_USER:
                Glide.with(img_thumb).load(R.drawable.ic_supply).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Suministro vencido ayer (usr)");
                txt_notificacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.EXPIRED_SUPPLY_USER);
                    }
                });
                container.addView(view);
                break;
            case DOMICILIARY_EXPIRED_SUPPLY_VET:
                Glide.with(img_thumb).load(R.drawable.ic_supply).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Suministro domiciliario vencido ayer");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.DOMICILIARY_EXPIRED_SUPPLY_VET);
                    }
                });
                container.addView(view);
                break;
            case EXPIRED_SUPPLY_ALL_VET:
                Glide.with(img_thumb).load(R.drawable.ic_supply).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Suministro vencido");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.EXPIRED_SUPPLY_ALL_VET);
                    }
                });
                container.addView(view);
                break;
            case TOMORROW_SUPPLY_VET:
                Glide.with(img_thumb).load(R.drawable.ic_supply).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Suministro de mañana");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.TOMORROW_SUPPLY_VET);
                    }
                });
                container.addView(view);
                break;
            case TOMORROW_SUPPLY_USER:
                Glide.with(img_thumb).load(R.drawable.ic_supply).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Suministro de mañana (usr)");
                txt_notificacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.TOMORROW_SUPPLY_USER);
                    }
                });
                container.addView(view);
                break;
            case BIRTHDAY_PETS:
                Glide.with(img_thumb).load(R.drawable.ic_birthdays).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Cumpleaños " + DoctorVetApp.get().getPetNamingPlural().toLowerCase());
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.BIRTHDAY_PETS);
                    }
                });
                container.addView(view);
                break;
            case PRODUCTS_BELOW_MINIMUN:
                Glide.with(img_thumb).load(R.drawable.ic_product).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Productos por debajo del mínimo");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.PRODUCTS_BELOW_MINIMUN);
                    }
                });
                container.addView(view);
                break;
            case LIFE_EXPECTANCY:
                Glide.with(img_thumb).load(R.drawable.pets).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Espectativa de vida superada");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.LIFE_EXPECTANCY);
                    }
                });
                container.addView(view);
                break;
            case WAITING_ROOM:
                Glide.with(img_thumb).load(R.drawable.ic_hourglass).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText(getString(R.string.waiting_room));
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.WAITING_ROOM);
                    }
                });
                container.addView(view);
                break;
            case WAITING_ROOM_AUTO_DELETED:
                Glide.with(img_thumb).load(R.drawable.ic_hourglass).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText(getString(R.string.waiting_room_auto_deleted));
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.WAITING_ROOM_AUTO_DELETED);
                    }
                });
                container.addView(view);
                break;
            case SUBSCRIPTION_EXP:
                Glide.with(img_thumb).load(R.drawable.ic_payment).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Suscripción vencida / por vencer");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent activity = new Intent(getContext(), ViewVetActivity.class);
                        activity.putExtra(DoctorVetApp.INTENT_VALUES.VET_ID.name(), DoctorVetApp.get().getVet().getId());
                        startActivity(activity);
                    }
                });
                container.addView(view);
                break;
            case IN_TRANSIT_MOVEMENT:
                Glide.with(img_thumb).load(R.drawable.ic_arrow_right_alt_48px).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Remitos en tránsito");
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.IN_TRANSIT_MOVEMENTS);
                    }
                });
                container.addView(view);
                break;
            case LOW_FOOD:
                Glide.with(img_thumb).load(R.drawable.ic_product).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Alimento / Pienso bajo");
                txt_notificacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMainActivity().set_view_pager_current_item(MainActivity.TAB_NAV_PANELS.REPORTS);
                        getMainActivity().getMainTabReportsFragment().setReport(DoctorVetApp.reports.LOW_FOOD);
                    }
                });
                container.addView(view);
                break;
            case FIRST_HELP:
                Glide.with(img_thumb).load(R.drawable.ic_help_24).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Presiona aquí para aprender como usar Doctor Vet en pocos minutos");
                txt_notificacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse("https://www.youtube.com/playlist?list=PLjf2zjrZ8W8vL6nBbOfbVvf4dCtGGxuOh");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(browserIntent);
                    }
                });
                container.addView(view);
                break;
            case PAYMENT_RECEIVED:
                Glide.with(img_thumb).load(R.drawable.ic_creditors_tolerance).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
                txt_notificacion.setText("Pago recibido ¡Gracias por suscribirte a Doctor Vet!");
                container.addView(view);
                break;
        }
    }

}
