package org.nypr.cordova.hockeyappplugin;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.ConfigXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.json.JSONArray;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.RuntimeException;
import java.lang.Runnable;
import java.lang.Thread;

import android.util.Log;

public class HockeyAppPlugin extends CordovaPlugin {
    protected static final String LOG_TAG = "HockeyAppPlugin";
    private String hockeyAppId;

    private class CustomConfigXmlParser extends ConfigXmlParser {
        @Override
        public void handleStartTag(XmlPullParser xml) {
            String strNode = xml.getName();
            if (strNode.equals("hockey-app-id")) {
                hockeyAppId = xml.getAttributeValue(null, "value");
            }
        }
        @Override
        public void handleEndTag(XmlPullParser xml) {
        }
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        new CustomConfigXmlParser().parse(webView.getContext());
        Log.d(LOG_TAG, "HockeyApp Plugin: app id = " + hockeyAppId);
        _checkForCrashes();
        _checkForUpdates();
        Log.d(LOG_TAG, "HockeyApp Plugin initialized");
    }

    @Override
    public void onResume(boolean multitasking) {
        Log.d(LOG_TAG, "HockeyApp Plugin resuming");
        _checkForUpdates();
        super.onResume(multitasking);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        boolean ret=true;
        if(action.equalsIgnoreCase("forcecrash")){
            new Thread(new Runnable() {
                    public void run() {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        throw new RuntimeException("Test crash at " + df.format(c.getTime()));
                    }
                }).start();
        }else{
            callbackContext.error(LOG_TAG + " error: invalid action (" + action + ")");
            ret=false;
        }
        return ret;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "HockeyApp Plugin destroying");
        super.onDestroy();
    }

    @Override
    public void onReset() {
        Log.d(LOG_TAG, "HockeyApp Plugin onReset--WebView has navigated to new page or refreshed.");
        super.onReset();
    }

    protected void _checkForCrashes() {
        Log.d(LOG_TAG, "HockeyApp Plugin checking for crashes");
        if(hockeyAppId!=null && !hockeyAppId.equals("")){
            CrashManager.register(cordova.getActivity(), hockeyAppId);
        }
    }

    protected void _checkForUpdates() {
        // Remove this for store builds!
        //__HOCKEY_APP_UPDATE_ACTIVE_START__
        Log.d(LOG_TAG, "HockeyApp Plugin checking for updates");
        if(hockeyAppId!=null && !hockeyAppId.equals("")){
            UpdateManager.register(cordova.getActivity(), hockeyAppId);
        }
        //__HOCKEY_APP_UPDATE_ACTIVE_END__
    }
}
