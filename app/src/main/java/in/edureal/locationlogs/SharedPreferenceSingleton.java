package in.edureal.locationlogs;

import android.content.Context;
import android.content.SharedPreferences;

class SharedPreferenceSingleton {

    private static volatile SharedPreferenceSingleton mInstance;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private static Context mCtx;

    private SharedPreferenceSingleton(Context context){
        if(mInstance == null){
            mCtx=context;
            sp=getSp();
            spEditor=getSpEditor();
        }else{
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    static SharedPreferenceSingleton getInstance(Context context){
        if (mInstance == null) {
            synchronized(SharedPreferenceSingleton.class){
                if(mInstance == null){
                    mInstance = new SharedPreferenceSingleton(context);
                }
            }
        }
        return mInstance;
    }

    SharedPreferences getSp(){
        if(sp==null){
            sp=mCtx.getApplicationContext().getSharedPreferences("in.edureal.billsplit",Context.MODE_PRIVATE);
        }
        return sp;
    }

    SharedPreferences.Editor getSpEditor(){
        if(spEditor==null){
            spEditor=sp.edit();
        }
        return spEditor;
    }

}
