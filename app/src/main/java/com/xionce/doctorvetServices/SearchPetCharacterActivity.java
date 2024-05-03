package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.data.Pet_character;
import com.xionce.doctorvetServices.data.Pet_charactersAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

public class SearchPetCharacterActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    //los caracteres son pocos, se cargan todos
    private static final String TAG = "SearchPetCharacterActiv";
    private Pet_charactersAdapter petcharactersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSearch();
        search(false);
    }

    @Override
    public void search(boolean on_first_fill) {
        showProgressBar();
        hideFailSearch();
        DoctorVetApp.get().getPets_charactersAdapter(new DoctorVetApp.VolleyCallbackAdapter() {
            @Override
            public void onSuccess(RecyclerView.Adapter resultAdapter) {
                hideProgressBar();
                //hideStartSearch();
                if (resultAdapter != null) {
                    showBottomBar();
                    setSugiereUnoListener(EditPetCharacterActivity.class);
                    petcharactersAdapter = (Pet_charactersAdapter) resultAdapter;
                    petcharactersAdapter.setOnClickHandler(SearchPetCharacterActivity.this);
                    recyclerView.setAdapter(petcharactersAdapter);
                    manageShowRecyclerView(petcharactersAdapter, on_first_fill);
                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Mascota_characteres", /*SearchPetCharacterActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    protected void doPagination() {

    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Pet_character petcharacter = (Pet_character) data;
        Intent dataBackIntent = getIntent(); //new Intent();
        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PET_CHARACTER_OBJ.name(), MySqlGson.getGson().toJson(petcharacter));
        this.setResult(RESULT_OK, dataBackIntent);
        finish();
    }
}
