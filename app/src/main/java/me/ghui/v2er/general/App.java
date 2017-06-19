package me.ghui.v2er.general;

import android.app.Application;
import android.preference.PreferenceManager;

import com.bugtags.library.Bugtags;
import com.orhanobut.logger.Logger;
import com.zzhoujay.richtext.RichText;

import me.ghui.v2er.R;
import me.ghui.v2er.injector.component.AppComponent;
import me.ghui.v2er.injector.component.DaggerAppComponent;
import me.ghui.v2er.injector.module.AppModule;
import me.ghui.v2er.network.APIService;

/**
 * Created by ghui on 05/03/2017.
 */

public class App extends Application {

    private static App sInstance;
    private AppComponent mAppComponent;

    public static App get() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        sInstance = this;
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(sInstance))
                .build();
        APIService.init();
        Logger.init().methodCount(1).hideThreadInfo();
        RichText.initCacheDir(getCacheDir());
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        Bugtags.start("47494b9a4cd7b13bcdf8be1eaacd4190", this, Bugtags.BTGInvocationEventBubble);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
