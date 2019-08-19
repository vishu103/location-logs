package in.edureal.locationlogs;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

class VollySingleton {

    private static volatile VollySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context mCtx;

    private VollySingleton(Context context) {
        if(mInstance == null){
            mCtx = context;
            requestQueue = getRequestQueue();
        }else{
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    static VollySingleton getInstance(Context context) {
        if (mInstance == null) {
            synchronized(VollySingleton.class){
                if(mInstance == null){
                    mInstance = new VollySingleton(context);
                }
            }
        }
        return mInstance;
    }

    RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }

    <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
