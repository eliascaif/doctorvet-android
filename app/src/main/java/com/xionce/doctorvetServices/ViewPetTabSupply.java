package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Pet_supply;
import com.xionce.doctorvetServices.data.Pet_supplyAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class ViewPetTabSupply extends RecyclerViewFragment
        implements HelperClass.RecyclerOnPaginationHandler {

    private static final String TAG = "ViewPetTabSupply";
    private ViewPetActivity viewPetActivity = null;
    private Pet_supplyAdapter supplyAdapter;

    //En onStart por la herencia de recyclerViewFragment
    @Override
    public void onStart() {
        super.onStart();
        viewPetActivity = (ViewPetActivity) getActivity();
    }

    @Override
    public void refreshView() {
        showSuministro();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE, requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    public void showSuministro() {
        if (created) {
            showProgressBar();
            hideDisplayMessage();
            hideRecyclerView();

            Integer id_pet = viewPetActivity.getIdPet();
            DoctorVetApp.get().getSupplyPagination(id_pet, 1, new DoctorVetApp.VolleyCallbackPagination() {
                @Override
                public void onSuccess(Get_pagination pagination) {
                    try {
                        ArrayList<Pet_supply> supplyArrayList = ((Pet_supply.Get_pagination_supply) pagination).getContent();
                        supplyAdapter = new Pet_supplyAdapter(supplyArrayList, Pet_supplyAdapter.Pet_supplyAdapter_types.EDIT_SUPPLY);

                        if (supplyAdapter.getItemCount() == 0) {
                            showEmptyListMessage();
                        } else {
                            recyclerView.setAdapter(supplyAdapter);

                            supplyAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
                                @Override
                                public void onRemoveItem(Object data, View view, int pos) {
                                    removeItem(((Pet_supply) data).getId(), pos);
                                }
                            });
                            supplyAdapter.setOnCheckItemHandler(new HelperClass.AdapterOnClickHandler() {
                                @Override
                                public void onClick(Object data, View view, int pos) {
                                    checkItem(((Pet_supply) data).getId(), pos);
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

    private void checkItem(Integer id_pet_suministro, int pos) {
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_check_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressBar();
                DoctorVetApp.get().checkSupply(id_pet_suministro, new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideProgressBar();
                        if (result) {
                            supplyAdapter.checkItem(pos);
                        } else {
                            Snackbar.make(viewPetActivity.tabLayout, "El registro no pudo ser marcado como suministrado", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void removeItem(Integer id_supply, int pos) {
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressBar();
                DoctorVetApp.get().deletePet_supply(id_supply, new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideProgressBar();
                        if (result) {
                            supplyAdapter.deleteItem(pos);
                        } else {
                            Snackbar.make(viewPetActivity.tabLayout, "El registro no pudo ser eliminado", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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
                    ArrayList<Pet_supply> petSupply = ((Pet_supply.Get_pagination_supply)pagination).getContent();
                    supplyAdapter.addItems(petSupply);
                    recyclerView.finishLoading();
                } else {
                    DoctorVetApp.get().handle_null_adapter("PetSupply", TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void showEmptyListMessage() {
        super.showEmptyListMessage();
        mErrorMessageDisplay.setText(getContext().getString(R.string.error_lista_vacia_2));
    }

}
