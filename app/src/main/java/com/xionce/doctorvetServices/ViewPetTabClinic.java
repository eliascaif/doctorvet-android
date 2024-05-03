package com.xionce.doctorvetServices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.widget.PopupMenu;

import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Pet_clinic;
import com.xionce.doctorvetServices.data.Pet_clinic2;
import com.xionce.doctorvetServices.data.Pet_clinic_root;
import com.xionce.doctorvetServices.data.Pet_clinicAdapter;
import com.xionce.doctorvetServices.data.Pet_study;
import com.xionce.doctorvetServices.data.Pet_recipe;
import com.xionce.doctorvetServices.data.Pet_supply;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;

public class ViewPetTabClinic extends RecyclerViewFragment
        implements HelperClass.RecyclerOnPaginationHandler {

    private static final String TAG = "ViewPetTabClinic";
    private ViewPetActivity viewPetActivity = null;
    private Pet_clinicAdapter petsClinicsAdapter;

    //En onStart por la herencia de recyclerViewFragment
    @Override
    public void onStart() {
        super.onStart();
        viewPetActivity = (ViewPetActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void refreshView() {
//        if (avoid_double_refresh_on_creation_and_on_selected_page) {
//            avoid_double_refresh_on_creation_and_on_selected_page = false;
//            return;
//        }

        //viewPetActivity.showTitles();
        showClinica();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE, requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onPagination() {
        recyclerView.startLoading();
        showProgressBar();
        recyclerView.addPage();
        DoctorVetApp.get().getClinicsPagination(viewPetActivity.getIdPet(), recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Pet_clinic_root> petClinicRoots = ((Pet_clinic_root.Get_pagination_clinics)pagination).getContent();
                    petsClinicsAdapter.addItems(petClinicRoots);
                    recyclerView.finishLoading();
                } else {
                    DoctorVetApp.get().handle_null_adapter("PetClinics", TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void showClinica() {
        if (created) {
            showProgressBar();
            hideDisplayMessage();
            hideRecyclerView();

            Integer id_pet = viewPetActivity.getIdPet();
            DoctorVetApp.get().getClinicsPagination(id_pet, 1, new DoctorVetApp.VolleyCallbackPagination() {
                @Override
                public void onSuccess(Get_pagination pagination) {
                    try {
                        ArrayList<Pet_clinic_root> pet_clinic_roots = ((Pet_clinic_root.Get_pagination_clinics) pagination).getContent();
                        petsClinicsAdapter = new Pet_clinicAdapter(pet_clinic_roots, viewPetActivity);
                        recyclerView.setHasFixedSize(false);

                        if (petsClinicsAdapter.getItemCount() == 0) {
                            showEmptyListMessage();
                        } else {
                            recyclerView.setAdapter(petsClinicsAdapter);

                            petsClinicsAdapter.setOnLongClickHandler(new HelperClass.AdapterOnLongClickHandler() {
                                @Override
                                public void onLongClick(Object data, View view, int pos) {
                                    showPopUpMenu(view, pos);
                                }
                            });

                            showRecyclerView();
                        }
                    } catch (Exception ex) {
                        DoctorVetApp.get().handle_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                    } finally {
                        hideProgressBar();
                        hideSwipeRefreshLayoutProgressBar();
                    }
                }
            });
        }
    }

    private void showPopUpMenu(View view, int index) {
        Pet_clinic_root clinica_root = petsClinicsAdapter.getArrayList().get(index);

        //validar
        Integer id_user = clinica_root.getIdUser();
        if (id_user == null || !id_user.equals(DoctorVetApp.get().getUser().getId())) {
            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), R.string.usuario_distinto, Snackbar.LENGTH_LONG).show();
            return;
        }

        Context wrapper = new ContextThemeWrapper(getContext(), R.style.PopUpMenu);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);

        Pet_clinicAdapter.ClinicaAdapter_types clinica_type = clinica_root.getClinicaType();
        switch (clinica_type) {
            case CLINIC:
            case STUDY:
            case CLINIC2:
            case RECIPE:
                popupMenu.getMenuInflater().inflate(R.menu.menu_edit_delete, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_edit) {
                            edit(index);
                        } else if (item.getItemId() == R.id.action_delete) {
                            HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    delete(index);
                                }
                            });
                        }
                        return true;
                    }
                });
                break;
            case SUPPLY:
                popupMenu.getMenuInflater().inflate(R.menu.menu_delete, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_delete) {
                            HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    delete(index);
                                }
                            });
                        }
                        return true;
                    }
                });
                break;
        }

        popupMenu.show();
    }
    private void edit(int index) {
        Pet_clinic_root clinica_root = petsClinicsAdapter.getArrayList().get(index);

        Integer id_user = clinica_root.getIdUser();
        if (id_user == null || !id_user.equals(DoctorVetApp.get().getUser().getId())) {
            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), R.string.usuario_distinto, Snackbar.LENGTH_LONG).show();
            return;
        }

        Pet_clinicAdapter.ClinicaAdapter_types clinica_type = clinica_root.getClinicaType();
        switch (clinica_type) {
            case CLINIC:
                Pet_clinic petclinic = clinica_root.getClinic();
                Intent edit_clinica_activity = new Intent(viewPetActivity, EditClinicActivity.class);
                edit_clinica_activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(viewPetActivity.getPet()));
                edit_clinica_activity.putExtra(DoctorVetApp.INTENT_VALUES.CLINIC_OBJ.name(), MySqlGson.getGson().toJson(petclinic));
                startActivityForResult(edit_clinica_activity, HelperClass.REQUEST_UPDATE);
                break;
            case STUDY:
                Pet_study petstudy = clinica_root.getStudy();
                Intent edit_estudio_activity = new Intent(viewPetActivity, EditStudyActivity_1.class);
                edit_estudio_activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(viewPetActivity.getPet()));
                edit_estudio_activity.putExtra(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name(), MySqlGson.getGson().toJson(petstudy));
                startActivityForResult(edit_estudio_activity, HelperClass.REQUEST_UPDATE);
                break;
            case SUPPLY:
//                Pet_supply petsupply = clinica_root.getSupply();
//                Intent edit_suministro_activity = new Intent(viewPetActivity, EditSupplyActivity_YesNo_old.class);
//                edit_suministro_activity.putExtra(DoctorVetApp.INTENT_SUPPLY_OBJECT, MySqlGson.getGson().toJson(petsupply));
//                edit_suministro_activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(viewPetActivity.getPet()));
//                startActivityForResult(edit_suministro_activity, HelperClass.REQUEST_UPDATE);
//                break;
            case CLINIC2:
                Pet_clinic2 petclinic2 = clinica_root.getClinic2();
                Intent edit_clinica2_activity = new Intent(viewPetActivity, EditClinic2Activity.class);
                edit_clinica2_activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(viewPetActivity.getPet()));
                edit_clinica2_activity.putExtra(DoctorVetApp.INTENT_VALUES.CLINIC2_OBJ.name(), MySqlGson.getGson().toJson(petclinic2));
                startActivityForResult(edit_clinica2_activity, HelperClass.REQUEST_UPDATE);
                break;
            case RECIPE:
                Pet_recipe petRecipe = clinica_root.getRecipe();
                Intent edit_recipe_activity = new Intent(viewPetActivity, EditRecipeActivity_1.class);
                edit_recipe_activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(viewPetActivity.getPet()));
                edit_recipe_activity.putExtra(DoctorVetApp.INTENT_VALUES.RECIPE_OBJ.name(), MySqlGson.getGson().toJson(petRecipe));
                startActivityForResult(edit_recipe_activity, HelperClass.REQUEST_UPDATE);
                break;
        }
    }
    private void delete(int index) {
        Pet_clinic_root clinica_root = petsClinicsAdapter.getArrayList().get(index);
        Pet_clinicAdapter.ClinicaAdapter_types clinica_type = clinica_root.getClinicaType();
        switch (clinica_type) {
            case CLINIC:
                Pet_clinic petclinic = clinica_root.getClinic();
                delete_pets_clinica(petclinic.getId(), index);
                break;
            case STUDY:
                Pet_study petstudy = clinica_root.getStudy();
                delete_pets_estudio(petstudy.getId(), index);
                break;
            case RECIPE:
                Pet_recipe petrecipe = clinica_root.getRecipe();
                delete_pets_recipe(petrecipe.getId(), index);
                break;
            case SUPPLY:
                Pet_supply petsupply = clinica_root.getSupply();
                delete_pets_suministro(petsupply.getId(), index);
                break;
            case CLINIC2:
                Pet_clinic2 petclinic2 = clinica_root.getClinic2();
                delete_pets_clinica2(petclinic2.getId(), index);
                break;
        }
    }
    private void delete_pets_clinica(Integer id_clinica, Integer index) {
        showWaitDialog();

        URL delete_clinicaUrl = NetworkUtils.buildDeleteClinicaUrl(id_clinica);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_clinicaUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
//                            int response_value = Integer.parseInt(response);
//                            if (response_value == 1) {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                showClinica();
                            } else {
                                DoctorVetApp.get().handle_error(new Exception(getString(R.string.error_borrando_registro)), TAG, true);
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                        } finally {
                            hideWaitDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        hideWaitDialog();
                    }
                });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void delete_pets_estudio(Integer id_estudio, Integer index) {
        showWaitDialog();

        URL delete_estudioUrl = NetworkUtils.buildDeleteEstudioUrl(id_estudio);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_estudioUrl.toString(),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
//                        int response_value = Integer.parseInt(response);
//                        if (response_value == 1) {
                        String success = MySqlGson.getStatusFromResponse(response);
                        if (success.equalsIgnoreCase("success")) {
                            //mascotasClinicaAdapter.deleteItem(index);
                            showClinica();
                        } else {
                            DoctorVetApp.get().handle_error(new Exception(getString(R.string.error_borrando_registro)), TAG, true);
                        }
                    } catch (Exception ex) {
                        DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                    } finally {
                        hideWaitDialog();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DoctorVetApp.get().handle_volley_error(error, TAG, true);
                    hideWaitDialog();
                }
            });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void delete_pets_recipe(Integer id_recipe, Integer index) {
        showWaitDialog();

        URL delete_recetaUrl = NetworkUtils.buildDeleteRecipeUrl(id_recipe);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_recetaUrl.toString(),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
//                        int response_value = Integer.parseInt(response);
//                        if (response_value == 1) {
                        String success = MySqlGson.getStatusFromResponse(response);
                        if (success.equalsIgnoreCase("success")) {
                            //mascotasClinicaAdapter.deleteItem(index);
                            showClinica();
                        } else {
                            DoctorVetApp.get().handle_error(new Exception(getString(R.string.error_borrando_registro)), TAG, true);
                        }
                    } catch (Exception ex) {
                        DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                    } finally {
                        hideWaitDialog();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DoctorVetApp.get().handle_volley_error(error, TAG, true);
                    hideWaitDialog();
                }
            });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void delete_pets_suministro(Integer id_suministro, Integer index) {
        showWaitDialog();

        URL delete_suministroUrl = NetworkUtils.buildDeleteSupplyUrl(id_suministro);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_suministroUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideWaitDialog();
                        try {
//                            int response_value = Integer.parseInt(response);
//                            if (response_value == 1) {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                //mascotasClinicaAdapter.deleteItem(index);
                                showClinica();
                                //viewMascotaActivity.recargarSuministro = true;
                            } else {
                                DoctorVetApp.get().handle_error(new Exception(getString(R.string.error_borrando_registro)), TAG, true);
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        hideWaitDialog();
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void delete_pets_clinica2(Integer id_clinica2, Integer index) {
        showWaitDialog();

        URL delete_clinica2Url = NetworkUtils.buildDeleteClinica2Url(id_clinica2);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_clinica2Url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
//                            int response_value = Integer.parseInt(response);
//                            if (response_value == 1) {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                //mascotasClinicaAdapter.deleteItem(index);
                                showClinica();
                            } else {
                                DoctorVetApp.get().handle_error(new Exception(getString(R.string.error_borrando_registro)),TAG, true);
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                        } finally {
                            hideWaitDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        hideWaitDialog();
                    }
                });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    public void showEmptyListMessage() {
        super.showEmptyListMessage();
        mErrorMessageDisplay.setText(getContext().getString(R.string.error_lista_vacia_2));
    }

}
