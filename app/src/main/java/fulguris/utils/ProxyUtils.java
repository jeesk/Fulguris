package fulguris.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import fulguris.App;
import fulguris.R;
import fulguris.browser.ProxyChoice;
import fulguris.di.Injector;
import fulguris.dialog.BrowserDialog;
import fulguris.extensions.ActivityExtensions;
import fulguris.extensions.AlertDialogExtensionsKt;
import fulguris.settings.preferences.DeveloperPreferences;
import fulguris.settings.preferences.UserPreferences;
import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import kotlin.Pair;
import kotlin.Unit;

@Singleton
public final class ProxyUtils {

  private static final String TAG = "ProxyUtils";

  ;

  private final UserPreferences userPreferences;
  private final DeveloperPreferences developerPreferences;

  @Inject
  public ProxyUtils(UserPreferences userPreferences, DeveloperPreferences developerPreferences) {
    this.userPreferences = userPreferences;
    this.developerPreferences = developerPreferences;
  }

  /*
   * If Orbot/Tor or I2P is installed, prompt the user if they want to enable
   * proxying for this session
   */
  public void checkForProxy(@NonNull final Activity activity) {}

  /*
   * Initialize WebKit Proxying
   */
  private void initializeProxy(@NonNull Activity activity) {
    String host;
    int port;

    switch (userPreferences.getProxyChoice()) {
      case NONE:
        // We shouldn't be here
        return;
      default:
      case MANUAL:
        host = userPreferences.getProxyHost();
        port = userPreferences.getProxyPort();
        break;
    }

    try {
      WebkitProxy.setProxy(activity.getApplicationContext(), host, port);
    } catch (Exception e) {
      Log.d(TAG, "error enabling web proxying", e);
    }
  }

  public boolean isProxyReady(@NonNull Activity activity) {
    return true;
  }

  public void updateProxySettings(@NonNull Activity activity) {
    if (userPreferences.getProxyChoice() != ProxyChoice.NONE) {
      initializeProxy(activity);
    } else {
      try {
        WebkitProxy.resetProxy(App.class.getName(), activity.getApplicationContext());
      } catch (Exception e) {
        Log.e(TAG, "Unable to reset proxy", e);
      }
    }
  }

  public void onStop() {
  }

  public void onStart(final Activity activity) {
    if (userPreferences.getProxyChoice() == ProxyChoice.I2P) {
      // Try to bind to I2P Android
      /*            i2PAndroidHelper.bind(() -> {
          sI2PHelperBound = true;
          if (sI2PProxyInitialized && !i2PAndroidHelper.isI2PAndroidRunning())
              i2PAndroidHelper.requestI2PAndroidStart(activity);
      });*/
    }
  }

  public static ProxyChoice sanitizeProxyChoice(ProxyChoice choice, @NonNull Activity activity) {
    switch (choice) {
      case MANUAL:
        break;
    }
    return choice;
  }
}
