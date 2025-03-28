package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Agenda;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.utilities.HelperClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ViewPetTabInfo extends FragmentBase {

    private static final String TAG = "ViewPetTabInfo";
    private ViewPetActivity viewPetActivity = null;
    private TextView txtRace;
    private TextView txtPelage;
    private TextView txtGender;
    private TextView txtCharacter;
    private LinearLayout linearPetData;
    private CheckBox chkDeath;
    private Socket socket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        socket = DoctorVetApp.get().getSocket();
        if (socket != null) {
            socket.on("server_message", serverMessage);
            Log.i(TAG, "Socket server_message listener on");
        }
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_info_pet, parent, false);
        viewPetActivity = (ViewPetActivity)getActivity();
        setSwipeRefreshLayout(rootView);
        txtRace = rootView.findViewById(R.id.txt_name_race);
        txtPelage = rootView.findViewById(R.id.txt_name_pelage);
        txtGender = rootView.findViewById(R.id.txt_name_gender);
        txtCharacter = rootView.findViewById(R.id.txt_name_character);
        linearPetData = rootView.findViewById(R.id.lista_datos_pet);
        chkDeath = rootView.findViewById(R.id.chk_death);

        TextView label_weight = rootView.findViewById(R.id.label_weight);
        label_weight.setText(label_weight.getText() + " (" + DoctorVetApp.get().getVet().getWeightUnit() + ")");

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void refreshView() {
        showPet();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        socket = DoctorVetApp.get().getSocket();
        if (socket != null) {
            socket.off("server_message", serverMessage);
            Log.i(TAG, "Socket server_message listener off");
        }
    }

    private void showPet() {
        showProgressBar();
        Integer id_pet = viewPetActivity.getIdPet();

        Integer update_last_view = getActivity().getIntent().getIntExtra(DoctorVetApp.INTENT_UPDATE_LAST_VIEW, 0);
        if (update_last_view == 1)
            getActivity().getIntent().putExtra(DoctorVetApp.INTENT_UPDATE_LAST_VIEW, 0);

        DoctorVetApp.get().getPet(id_pet, update_last_view, new DoctorVetApp.VolleyCallbackMascota() {
            @Override
            public void onSuccess(Pet resultPet) {
                try {
                    if (resultPet != null) {
                        viewPetActivity.setPet(resultPet);
                        viewPetActivity.setTitles(resultPet);
                        setUI(viewPetActivity.getPet());
                    } else {
                        showErrorMessage();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, TAG, true);
                } finally {
                    hideProgressBar();
                    hideSwipeRefreshLayoutProgressBar();
                }
            }
        });
    }
    public void setUI(final Pet pet) {
        ((TextView)viewPetActivity.findViewById(R.id.titulo)).setText(DoctorVetApp.get().getOwnerNamingPlural());
        DoctorVetApp.ObjectToTextView(viewPetActivity.findViewById(R.id.lista_datos_pet), pet, "txt_", true);

        if (pet.getRace() != null)
            txtRace.setText(pet.getRace().getName());
        if (pet.getPelage() != null)
            txtPelage.setText(pet.getPelage().getName());
        if (pet.getGender() != null)
            txtGender.setText(pet.getGender().getName());
        if (pet.getCharacter() != null)
            txtCharacter.setText(pet.getCharacter().getName());

        //llenar linear de estados de la mascota
        final LinearLayout linear_estados_pet = viewPetActivity.findViewById(R.id.linear_pet_info);
        linear_estados_pet.removeAllViews();
        //death
        if (pet.getDeath() == 1) {
            View list_item_death = getLayoutInflater().inflate(R.layout.list_item_state, null);
            TextView titulo = list_item_death.findViewById(R.id.txt_title);
            titulo.setText("Deceso");
            linear_estados_pet.addView(list_item_death);
            chkDeath.setChecked(true);
            chkDeath.setVisibility(View.VISIBLE);
        }

        Pet.Pet_states petstates = pet.getStates_pet();

        //in waiting room
        if (petstates.getWaiting_room() != null) {
            View listItemIsInWaitingRoom = getLayoutInflater().inflate(R.layout.list_item_in_waiting_room, null);
            TextView titulo = listItemIsInWaitingRoom.findViewById(R.id.txt_title);
            titulo.setText("En sala de espera");
            listItemIsInWaitingRoom.findViewById(R.id.img_check).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkWaitingRooms(petstates.getWaiting_room().getId());
                }
            });
            listItemIsInWaitingRoom.findViewById(R.id.img_remove).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteWaitingRooms(petstates.getWaiting_room().getId());
                }
            });
            linear_estados_pet.addView(listItemIsInWaitingRoom);
        }

        //owners debts
        for (Owner owner:pet.getOwners()) {
            if (owner.getBalance() != null && !owner.getBalance().equals(BigDecimal.ZERO)) {
                linear_estados_pet.addView(DoctorVetApp.get().getOwnerDebtUI(owner, getLayoutInflater(), viewPetActivity));
            }
        }

        //agenda
        for (Agenda agendaItem: petstates.getAppointments_tasks()) {
            View list_item_agenda = getLayoutInflater().inflate(R.layout.list_item_state_agenda, null);
            TextView titulo = list_item_agenda.findViewById(R.id.txt_title);
            titulo.setText("Agenda: " + agendaItem.getEvent_name() + " " + HelperClass.getTimeInLocale(agendaItem.getBegin_time(), getContext()));
            titulo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ViewAgendaEventActivity.class);
                    intent.putExtra(DoctorVetApp.INTENT_VALUES.AGENDA_ID.name(), agendaItem.getId());
                    startActivity(intent);
                }
            });
            list_item_agenda.findViewById(R.id.img_check).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkAgenda(agendaItem.getId());
                }
            });
            list_item_agenda.findViewById(R.id.img_reschedule).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rescheduleAgenda(agendaItem);
                }
            });
            linear_estados_pet.addView(list_item_agenda);
        }

        //suministro
        Pet.Supply plannedsupply = petstates.getSuministro_planificado();
        if (plannedsupply != Pet.Supply.NA) {
            View list_item_estado_suministro_planificado = getLayoutInflater().inflate(R.layout.list_item_state, null);
            TextView titulo = list_item_estado_suministro_planificado.findViewById(R.id.txt_title);

            if (plannedsupply == Pet.Supply.PENDING)
                titulo.setText(getString(R.string.pending_supply));
            else if (plannedsupply == Pet.Supply.EXPIRED)
                titulo.setText(getString(R.string.expired_supply));
            else
                titulo.setText(getString(R.string.pending_and_expired_supply));

            linear_estados_pet.addView(list_item_estado_suministro_planificado);
        }

        //cumpleaños
        if (petstates.getIs_birthday()) {
            View list_item_estado_cumpleanos = getLayoutInflater().inflate(R.layout.list_item_state, null);
            TextView titulo = list_item_estado_cumpleanos.findViewById(R.id.txt_title);
            titulo.setText(getString(R.string.hoy_cumple_anos));

            linear_estados_pet.addView(list_item_estado_cumpleanos);
        }

        //ultima visita
        if (petstates.getLast_visit() != null) {
            Date ultima_visita = petstates.getLast_visit().getDate();
            View list_item_estado_ultima_visita = getLayoutInflater().inflate(R.layout.list_item_state, null);
            TextView titulo = list_item_estado_ultima_visita.findViewById(R.id.txt_title);

            String textToDisplay = getString(R.string.last_visit, HelperClass.getDateInLocaleLong(ultima_visita));
            textToDisplay = textToDisplay.concat(" ").concat(petstates.getLast_visit().getReasonInString()).concat(". ").concat(petstates.getLast_visit().getUser_name()).concat(".");
            titulo.setText(textToDisplay);

            linear_estados_pet.addView(list_item_estado_ultima_visita);
        }

        //last food
        if (petstates.getLast_food() != null) {
            Date last_food_date = petstates.getLast_food().getDate();
            View list_item_last_food = getLayoutInflater().inflate(R.layout.list_item_state, null);
            TextView titulo = list_item_last_food.findViewById(R.id.txt_title);
            String textToDisplay = getString(R.string.last_food, petstates.getLast_food().getProduct_name(), HelperClass.getDateInLocaleShort(last_food_date));
            titulo.setText(textToDisplay);
            linear_estados_pet.addView(list_item_last_food);
        }

        //food level
        if (petstates.getFood_level() != null) {
            View list_item = getLayoutInflater().inflate(R.layout.list_item_state, null);
            TextView titulo = list_item.findViewById(R.id.txt_title);
            String textToDisplay = getString(R.string.food_level, petstates.getFood_level().setScale(0, BigDecimal.ROUND_DOWN).toString());
            titulo.setText(textToDisplay);
            linear_estados_pet.addView(list_item);
        }

        //age
        if (pet.getAge() != null) {
            View list_item = getLayoutInflater().inflate(R.layout.list_item_state, null);
            TextView titulo = list_item.findViewById(R.id.txt_title);
            String textToDisplay = "Edad: " + pet.getAge();
            titulo.setText(textToDisplay);
            linear_estados_pet.addView(list_item);
        }

        //life expectancy
        if (petstates.getLife_expectancy() != null && petstates.getLife_expectancy()) {
            View list_item = getLayoutInflater().inflate(R.layout.list_item_state, null);
            TextView titulo = list_item.findViewById(R.id.txt_title);
            String textToDisplay = "Espectativa de vida superada";
            titulo.setText(textToDisplay);
            linear_estados_pet.addView(list_item);
        }

        //llenar linear de propietarios
        final LinearLayout listaOwners = viewPetActivity.findViewById(R.id.linear_owners);
        listaOwners.removeAllViews();
        ArrayList<Owner> propietarios_de_la_pet = pet.getOwners();
        if (!propietarios_de_la_pet.isEmpty()) {
            listaOwners.setVisibility(View.VISIBLE);
            for (final Owner owner :propietarios_de_la_pet) {
                View newPropietarioRectangle = getLayoutInflater().inflate(R.layout.list_item_rectangle, null);

                ImageView thumb = newPropietarioRectangle.findViewById(R.id.scuare_thumb);
                TextView name = newPropietarioRectangle.findViewById(R.id.txt_scuare);
                TextView info = newPropietarioRectangle.findViewById(R.id.txt_info);
                name.setText(owner.getName());
                DoctorVetApp.get().setThumb(owner.getThumb_url(), thumb, R.drawable.ic_account_circle_light);

                if (owner.getEmail() != null && !owner.getEmail().isEmpty()) {
                    info.setText(owner.getEmail());
                    info.setVisibility(View.VISIBLE);
                }

                newPropietarioRectangle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), ViewOwnerActivity.class);
                        intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_ID.name(), owner.getId());
                        getActivity().startActivity(intent);
                        if (DoctorVetApp.views_abiertos > 1)
                            getActivity().finish();
                    }
                });

                listaOwners.addView(newPropietarioRectangle);
            }
        }

        DoctorVetApp.setTextViewVisibility(linearPetData);

        if (pet.getDeath() != null && pet.getDeath() == 1)
            chkDeath.setChecked(true);
        else
            chkDeath.setVisibility(View.GONE);
    }

    private void checkAgenda(Integer id_agenda) {
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_check_agenda), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();
                DoctorVetApp.get().checkAgenda(id_agenda, new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideWaitDialog();
                        if (result) {
                            refreshView();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.err_action), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
    private void rescheduleAgenda(Agenda agenda_event) {
        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflar el diseño personalizado
        final LinearLayout linear = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_date_picker, null);
        final DatePicker datePicker = linear.findViewById(R.id.date_picker);

        builder
                .setView(linear)
                .setTitle("Reagendar para")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWaitDialog();

                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), agenda_event.getBegin_time().getHours(), agenda_event.getBegin_time().getMinutes(), 0);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = dateFormat.format(selectedDate.getTime());

                        DoctorVetApp.get().rescheduleAgenda(agenda_event.getId(), formattedDate, new DoctorVetApp.VolleyCallback() {
                            @Override
                            public void onSuccess(Boolean result) {
                                hideWaitDialog();
                                if (result) {
                                    refreshView();
                                } else {
                                    Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.err_action), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Mostrar el diálogo
        builder.create().show();
    }
    private void checkWaitingRooms(Integer idWaitingRoom) {
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_check_agenda), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showWaitDialog();
                DoctorVetApp.get().checkWaitingRooms(idWaitingRoom, new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideWaitDialog();
                        if (result) {
                            refreshView();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.err_action), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
    private void deleteWaitingRooms(Integer idWaitingRoom) {
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showWaitDialog();
                DoctorVetApp.get().deleteWaitingRooms(idWaitingRoom, new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideWaitDialog();
                        if (result) {
                            refreshView();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.err_action), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private Emitter.Listener serverMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, args[0].toString());

                    JSONObject incomingData = null;
                    try {
                        incomingData = new JSONObject(args[0].toString());
                        String table_name = incomingData.getString("table_name");
                        if (table_name.equalsIgnoreCase("waiting_rooms")) {
                            refreshView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}

