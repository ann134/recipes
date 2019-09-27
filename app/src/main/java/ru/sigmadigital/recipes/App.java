package ru.sigmadigital.recipes;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    private Realm realm;
    private static App context;

    public static Context getAppContext(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context  = this;
        Realm.init(this);
    }

    public static void showToast(int textId){
        Message ms = new Message();
        ms.what = 1;
        ms.arg1 = textId;
        context.handler.sendMessage(ms);
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 1){
                Toast.makeText(App.context, msg.arg1, Toast.LENGTH_LONG).show();
            }
            return false;
        }
    });


    private static Realm createRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("db.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        return Realm.getInstance(config);
    }

    public synchronized static Realm getRealm() {
        if(context == null){
            return null;
        }
        if (context.realm != null) {
            return context.realm;
        } else {
            context.realm = createRealm();
            return context.realm;
        }
    }
}
