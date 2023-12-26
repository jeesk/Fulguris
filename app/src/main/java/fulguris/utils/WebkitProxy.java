/*
* Copyright 2015 Anthony Restaino
* Copyright 2012-2016 Nathan Freitas

*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package fulguris.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Proxy;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.webkit.WebView;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

public class WebkitProxy {

  private static final int REQUEST_CODE = 0;

  private static final String TAG = "OrbotHelpher";

  private WebkitProxy() {
    // this is a utility class with only static methods
  }

  public static boolean resetProxy(String appClass, Context ctx) throws Exception {
    resetSystemProperties();
    return setWebkitProxyLollipop(ctx, null, 0);
  }

  public static boolean setProxy(Context ctx, String host, int port) throws Exception {

    setSystemProperties(host, port);
    boolean worked = false;
    worked = setWebkitProxyLollipop(ctx, host, port);

    return worked;
  }

  private static void setSystemProperties(String host, int port) {

    System.setProperty("proxyHost", host);
    System.setProperty("proxyPort", Integer.toString(port));

    System.setProperty("http.proxyHost", host);
    System.setProperty("http.proxyPort", Integer.toString(port));

    System.setProperty("https.proxyHost", host);
    System.setProperty("https.proxyPort", Integer.toString(port));

    /*    System.setProperty("socks.proxyHost", host);
    System.setProperty("socks.proxyPort", Integer.toString(OrbotHelper.DEFAULT_PROXY_SOCKS_PORT));

    System.setProperty("socksProxyHost", host);
    System.setProperty("socksProxyPort", Integer.toString(OrbotHelper.DEFAULT_PROXY_SOCKS_PORT));*/
  }

  private static void resetSystemProperties() {

    System.setProperty("proxyHost", "");
    System.setProperty("proxyPort", "");

    System.setProperty("http.proxyHost", "");
    System.setProperty("http.proxyPort", "");

    System.setProperty("https.proxyHost", "");
    System.setProperty("https.proxyPort", "");

    /*    System.setProperty("socks.proxyHost", "");
    System.setProperty("socks.proxyPort", Integer.toString(OrbotHelper.DEFAULT_PROXY_SOCKS_PORT));

    System.setProperty("socksProxyHost", "");
    System.setProperty("socksProxyPort", Integer.toString(OrbotHelper.DEFAULT_PROXY_SOCKS_PORT));*/
  }

  // http://stackanswers.com/questions/25272393/android-webview-set-proxy-programmatically-on-android-l
  @TargetApi(21) // for android.util.ArrayMap methods
  @SuppressWarnings("rawtypes")
  private static boolean setWebkitProxyLollipop(Context appContext, String host, int port) {

    try {
      Class applictionClass = Class.forName("android.app.Application");
      Field mLoadedApkField = applictionClass.getDeclaredField("mLoadedApk");
      mLoadedApkField.setAccessible(true);
      Object mloadedApk = mLoadedApkField.get(appContext);
      Class loadedApkClass = Class.forName("android.app.LoadedApk");
      Field mReceiversField = loadedApkClass.getDeclaredField("mReceivers");
      mReceiversField.setAccessible(true);
      ArrayMap receivers = (ArrayMap) mReceiversField.get(mloadedApk);
      for (Object receiverMap : receivers.values()) {
        for (Object receiver : ((ArrayMap) receiverMap).keySet()) {
          Class clazz = receiver.getClass();
          if (clazz.getName().contains("ProxyChangeListener")) {
            Method onReceiveMethod =
                clazz.getDeclaredMethod("onReceive", Context.class, Intent.class);
            Intent intent = new Intent(Proxy.PROXY_CHANGE_ACTION);
            Object proxyInfo = null;
            if (host != null) {
              final String CLASS_NAME = "android.net.ProxyInfo";
              Class cls = Class.forName(CLASS_NAME);
              Method buildDirectProxyMethod =
                  cls.getMethod("buildDirectProxy", String.class, Integer.TYPE);
              proxyInfo = buildDirectProxyMethod.invoke(cls, host, port);
            }
            intent.putExtra("proxy", (Parcelable) proxyInfo);
            onReceiveMethod.invoke(receiver, appContext, intent);
          }
        }
      }
      return true;
    } catch (ClassNotFoundException e) {
      Log.d(
          "ProxySettings",
          "Exception setting WebKit proxy on Lollipop through ProxyChangeListener: "
              + e.toString());
    } catch (NoSuchFieldException e) {
      Log.d(
          "ProxySettings",
          "Exception setting WebKit proxy on Lollipop through ProxyChangeListener: "
              + e.toString());
    } catch (IllegalAccessException e) {
      Log.d(
          "ProxySettings",
          "Exception setting WebKit proxy on Lollipop through ProxyChangeListener: "
              + e.toString());
    } catch (NoSuchMethodException e) {
      Log.d(
          "ProxySettings",
          "Exception setting WebKit proxy on Lollipop through ProxyChangeListener: "
              + e.toString());
    } catch (InvocationTargetException e) {
      Log.d(
          "ProxySettings",
          "Exception setting WebKit proxy on Lollipop through ProxyChangeListener: "
              + e.toString());
    }
    return false;
  }
}
