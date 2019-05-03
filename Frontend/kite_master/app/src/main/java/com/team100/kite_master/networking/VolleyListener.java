package com.team100.kite_master.networking;

import com.android.volley.VolleyError;

public interface VolleyListener<T>
{
    void getResult(T object);
    void getError(VolleyError err);
}