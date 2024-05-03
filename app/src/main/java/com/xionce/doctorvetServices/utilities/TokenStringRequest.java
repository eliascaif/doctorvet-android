package com.xionce.doctorvetServices.utilities;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.xionce.doctorvetServices.DoctorVetApp;

import java.util.HashMap;
import java.util.Map;

public class TokenStringRequest extends StringRequest {
    public TokenStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap headers = new HashMap();
        headers.put("Authorization", "Bearer " + DoctorVetApp.get().preferences_getUserToken()); //.get_user_access_token());
        return headers;
    }
}
