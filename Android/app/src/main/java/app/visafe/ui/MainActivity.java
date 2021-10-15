
package app.visafe.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.VpnService;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.visafe.R;
import app.visafe.domain.DomainVisafe;
import app.visafe.net.doh.Transaction;
import app.visafe.sys.MyFirebaseService;
import app.visafe.sys.ViSafeVpnService;
import app.visafe.sys.firebase.AnalyticsWrapper;
import app.visafe.sys.firebase.LogWrapper;
import app.visafe.sys.InternalNames;
import app.visafe.sys.PersistentState;
import app.visafe.sys.QueryTracker;
import app.visafe.sys.VpnController;
import app.visafe.sys.VpnState;
import app.visafe.sys.firebase.RemoteConfig;
import app.visafe.ui.settings.NewSettingFragment;
import app.visafe.ui.settings.ServerChooserFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.Normalizer;
import java.util.Enumeration;
import java.util.Queue;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.view.Window;
import android.os.Vibrator;

public class MainActivity extends AppCompatActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {
  private static final String LOG_TAG = "MainActivity";
  public static int login = -1;
  private static final int REQUEST_CODE_PREPARE_VPN = 100;
  public static final int RESULT_OK = -1;
  public static String token = "";
  private ServerChooserFragment sv = null;
  private ActionBarDrawerToggle drawerToggle;
  private RecyclerView recyclerView;
  private RecyclerAdapter adapter;
  private RecyclerView.LayoutManager layoutManager;
  private View controlView = null;
  private Timer activityTimer;
  private int count = 0,intro =0;
  private NewSettingFragment settingsFragment = null;
  private ImageButton detail_mode_status = null;
  private Boolean sendNotificationWhenClickButtonOnOff = false;
  private BroadcastReceiver messageReceiver =
          new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
              if (InternalNames.RESULT.name().equals(intent.getAction())) {
                updateStatsDisplay(getNumRequests(),
                        (Transaction) intent.getSerializableExtra(InternalNames.TRANSACTION.name()));
              } else if (InternalNames.DNS_STATUS.name().equals(intent.getAction())) {
                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                  syncDnsStatus();
                }
              }
            }
          };
  public static String MAC, IP,groupId,groupName;
  private void updateStatsDisplay(long numRequests, Transaction transaction) {
    showTransaction(transaction);
  }

  private void showTransaction(Transaction transaction) {
    if (isHistoryEnabled()) {
      adapter.add(transaction);
    }
  }


  @Override
  public void onAttachedToWindow() {
    intro=1;
    getIntroApproval();
//    prepareAndStartDnsVpn();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
    if (PersistentState.URL_KEY.equals(key)) {
      updateServerName();
    }
  }

  public String getAndroidVersion() {
    String release = Build.VERSION.RELEASE;
    int sdkVersion = Build.VERSION.SDK_INT;
    return sdkVersion + " (" + release +")";
  }

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  public void onClick(View view) {
      chooseView(R.id.settings);
      getSupportActionBar().show();
  }
  public void PostRequestSendToken(String deviceId, String token)
  {
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          URL url = new URL(DomainVisafe.DOMAIN_SEND_TOKEN);
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setRequestMethod("POST");
          conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
          conn.setRequestProperty("Accept","application/json");
          conn.setDoOutput(true);
          conn.setDoInput(true);
          JSONObject jsonParam_noti = new JSONObject();
          jsonParam_noti.put("token",token);
          jsonParam_noti.put("deviceId",deviceId);
          jsonParam_noti.toString();
          DataOutputStream os = new DataOutputStream(conn.getOutputStream());
          os.writeBytes(String.valueOf(jsonParam_noti.toString()));
          System.out.println(jsonParam_noti.toString());
          System.out.println(deviceId);
          os.flush();
          os.close();

          if  (String.valueOf(conn.getResponseCode()).equals("200"))
          {
            runOnUiThread(() -> {
              System.out.println("SEND_TOKEN: TRUE");
              SharedPreferences sendToken = getSharedPreferences("sendToken", Context.MODE_PRIVATE);
              SharedPreferences.Editor editor = sendToken.edit();
              editor.clear();
              editor.putBoolean("status",true);
              editor.commit();
            });
          }
          else
          {
               System.out.println("SEND_TOKEN: FALSE");
              SharedPreferences sendToken = getSharedPreferences("sendToken", Context.MODE_PRIVATE);
              SharedPreferences.Editor editor = sendToken.edit();
              editor.clear();
              editor.putBoolean("status",false);
              editor.commit();
          }
          conn.connect();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();
  }

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    PersistentState.syncLegacyState(this);

    RemoteConfig.update();

    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    setContentView(R.layout.activity_main);

    Intent intent = new Intent();
    SharedPreferences sharedPreferences = getSharedPreferences("userID",Context.MODE_PRIVATE);
    String userID = sharedPreferences.getString("userID", null);

    SharedPreferences sendToken = getSharedPreferences("sendToken", Context.MODE_PRIVATE);
    if (sendToken.getBoolean("status",false) == false)
    {
       FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
        @Override
        public void onComplete(@NonNull Task<InstanceIdResult> task) {
          if (!task.isSuccessful()) {
            token = task.getException().getMessage();
            Log.w("FCM TOKEN Failed", task.getException());
          } else {
            token = task.getResult().getToken();
            Log.i("FCM TOKEN", token);
            runOnUiThread(()->{
              PostRequestSendToken(userID, token);
            });
          }
        }
      });
    }

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
    drawerToggle = new ActionBarDrawerToggle(
        this,                  /* host Activity */
        drawerLayout,         /* DrawerLayout object */
        toolbar,
        R.string.drawer_open,  /* "open drawer" description */
        R.string.drawer_close  /* "close drawer" description */
    );
    drawerLayout.addDrawerListener(drawerToggle);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().hide();
    drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //chooseView(R.id.frame_main);
      }
    });

    NavigationView drawer = (NavigationView) findViewById(R.id.drawer);
    drawer.removeAllViews();
    drawer.setVisibility(View.INVISIBLE);
    drawerLayout.closeDrawers();
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


    DisplayMetrics displaymetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
    int height = displaymetrics.heightPixels;
    recyclerView = (RecyclerView) findViewById(R.id.recycler);
    recyclerView.setHasFixedSize(true);
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new RecyclerAdapter(this);
    adapter.reset(getHistory());
    recyclerView.setAdapter(adapter);
    Window window = getWindow();
    window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.statusbar));

    // Register broadcast receiver
    IntentFilter intentFilter = new IntentFilter(InternalNames.RESULT.name());
    intentFilter.addAction(InternalNames.DNS_STATUS.name());
    LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter);

    prepareHyperlinks(this, findViewById(R.id.activity_main));

    boolean check = getIntent().getBooleanExtra("checkPassCode",false);
    SharedPreferences sharedPreferences1 = getSharedPreferences("passcode",Context.MODE_PRIVATE);
   // int checkdns = sharedPreferences1.getInt("check_set_passcode",0);
    if (check == true) {
      count = 0;
      View dialog_join;
      AlertDialog.Builder builder;
      AlertDialog alert;
      builder = new AlertDialog.Builder(MainActivity.this,R.style.CustomAlertDialog);
      dialog_join = getLayoutInflater().inflate(R.layout.dialog_turn_off, null);
      builder.setView(dialog_join);

      TextView title = new TextView(getApplicationContext());
      title.setText("Bạn có muốn tắt \ntính năng bảo vệ của Visafe?");
      title.setPadding(10, 40, 10, 10);
      title.setGravity(Gravity.CENTER);
      title.setTextColor(Color.BLACK);
      title.setTextSize(17);
//            title.setTypeface(null, Typeface.BOLD);
      builder.setCustomTitle(title)
              .setCancelable(false);
      alert = builder.create();
      alert.show();
      //set size of dialog
      WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
      layoutParams.copyFrom(alert.getWindow().getAttributes());
      DisplayMetrics displayMetrics = new DisplayMetrics();
      getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

      int displayWidth = displayMetrics.widthPixels;
      int displayHeight = displayMetrics.heightPixels;
      int dialogWindowWidth = (int) (displayWidth * 0.9f);
      layoutParams.width = dialogWindowWidth;
      layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
      alert.getWindow().setAttributes(layoutParams);
      alert.getWindow().setGravity(Gravity.BOTTOM);
      Button btn_agree = dialog_join.findViewById(R.id.button_agree);
      btn_agree.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          status_button = 1;
          stopDnsVpnService();
          alert.dismiss();
        }
      });
      Button btn_cancel = dialog_join.findViewById(R.id.button_cancel);
      btn_cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          status_button = 0;
          alert.dismiss();
        }
      });
    }

    boolean check_passcode_setting = intent.getBooleanExtra("check_passcode_setting",false);
    if (check_passcode_setting == true)
    {
      chooseView(R.id.settings);
      getSupportActionBar().show();
    }

    if (intent.getBooleanExtra("intro_button_click",false) == true)
    {
      prepareAndStartDnsVpn();
    }

    Intent intent1 = getIntent();
    boolean checkLogin = intent1.getBooleanExtra("isLogined",false);
    if ( checkLogin == true)
    {
      chooseView(R.id.dashboard_layout);
      Window window2 = getWindow();
      window2.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
      window2.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    maybeAutostart();
  }
  private int status_button = 1;
  private String convertStreamToString(InputStream is) {
    Scanner s = new Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next().replace(",", ",\n") : "";
  }
  public String covertToString(String value) {
    try {
      String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
      Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
      return pattern.matcher(temp).replaceAll("");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public View getControlView(ViewGroup parent) {
    if (controlView != null) {
      return controlView;
    }

    controlView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_content, parent,
        false);
    // Set up the main UI
    final SwitchMaterial switchButton = controlView.findViewById(R.id.dns_switch);
    switchButton.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          }
        });

    final Button btn_status = controlView.findViewById(R.id.button_active);
    final ImageView image_status = controlView.findViewById(R.id.image_status);
    final ImageView button_status = controlView.findViewById(R.id.button_status);
    final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    final ImageView roundImage = controlView.findViewById(R.id.round_image);
    btn_status.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendNotificationWhenClickButtonOnOff = true;
        vibrator.vibrate(50);
        if (status_button == 0)
        {
          status_button = 1;
          SharedPreferences sharedPreferences1 = getSharedPreferences("passcode",Context.MODE_PRIVATE);
          if (sharedPreferences1.getString("password","").equals("")==false) {
          }
          else {
            View dialog_join;
            AlertDialog.Builder builder;
            AlertDialog alert;
            builder = new AlertDialog.Builder(MainActivity.this,R.style.CustomAlertDialog);
            dialog_join = getLayoutInflater().inflate(R.layout.dialog_turn_off, null);
            builder.setView(dialog_join);
            String reqString = Build.MANUFACTURER
                    + " " + Build.MODEL ;
            TextView title = new TextView(getApplicationContext());
            title.setText("Bạn có muốn tắt \ntính năng bảo vệ của Visafe?");
            title.setPadding(10, 40, 10, 0);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.BLACK);
            title.setTextSize(17);
//            title.setTypeface(null, Typeface.BOLD);
            builder.setCustomTitle(title)
                    .setCancelable(false);
            alert = builder.create();
            alert.show();
            //set size of dialog
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alert.getWindow().getAttributes());
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int displayWidth = displayMetrics.widthPixels;
            int displayHeight = displayMetrics.heightPixels;
            int dialogWindowWidth = (int) (displayWidth * 0.9f);
            int dialogWindowHeight = (int) (displayHeight * 0.3f);
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alert.getWindow().setAttributes(layoutParams);
            alert.getWindow().setGravity(Gravity.BOTTOM);
            Button btn_agree = dialog_join.findViewById(R.id.button_agree);
            btn_agree.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                image_status.setImageResource(R.drawable.off_screen);
                button_status.setImageResource(R.drawable.off_button);
                stopDnsVpnService();
                status_button = 1;
                alert.dismiss();
              }
            });
            Button btn_cancel = dialog_join.findViewById(R.id.button_cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                status_button = 0;
                alert.dismiss();
              }
            });
          }
        }
        else
        {
          roundImage.setVisibility(View.GONE);
          status_button = 0;
          prepareAndStartDnsVpn();
        }
      }
    });
    final QueryTracker tracker = VpnController.getInstance().getTracker(this);
      String[] urls = getResources().getStringArray(R.array.urls);
      AnalyticsWrapper.get(this).logTryAllRequested();

    updateServerName();
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      syncDnsStatus();
    }

    // Start updating the live Queries Per Minute value.
    startAnimation();

    return controlView;
  }
  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  private void setInfoClicker(final @IdRes int id, final InfoPage page) {
    controlView.findViewById(id).setOnClickListener(view -> showInfo(page));
  }

  private void updateServerName() {
    if (controlView == null) {
      return;
    }
  }

  private boolean isHistoryEnabled() {
    return VpnController.getInstance().getTracker(this).isHistoryEnabled();
  }

  private void startAnimation() {
    if (controlView == null) {
      return;
    }

    // Start updating the live Queries Per Minute value.
    if (activityTimer == null) {
      activityTimer = new Timer();
    }
    final QueryTracker tracker = VpnController.getInstance().getTracker(this);
    final Handler controlViewUpdateHandler = new Handler();
//    final TextView qpmView = (TextView) controlView.findViewById(R.id.qpm);
    final Runnable doUpdate = new Runnable() {
      @Override
      public void run() {
        long oneMinuteAgo = SystemClock.elapsedRealtime() - 60 * 1000;
      }
    };
    int intervalMs = 500;  // Update the value twice a second
    activityTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        controlViewUpdateHandler.post(doUpdate);
      }
    }, 0, intervalMs);
  }

  private void stopAnimation() {
    if (activityTimer != null) {
      activityTimer.cancel();
      activityTimer = null;
    }
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState();
  }

  @Override
  protected void onResume() {
    super.onResume();
    startAnimation();

    // Refresh DNS status.  This is mostly to update the VPN warning message state.
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      syncDnsStatus();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    stopAnimation();
  }

  @Override
  protected void onDestroy() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    PreferenceManager.getDefaultSharedPreferences(this).
        unregisterOnSharedPreferenceChangeListener(this);

    super.onDestroy();
  }

  private void maybeAutostart() {
    VpnController controller = VpnController.getInstance();
    VpnState state = controller.getState(this);
    if (state.activationRequested && !state.on) {
      LogWrapper.log(Log.INFO, LOG_TAG, "Autostart enabled");
      prepareAndStartDnsVpn();
    }
  }

  private void startDnsVpnService() {
    VpnController.getInstance().start(this);
  }

  public void stopDnsVpnService() {
    VpnController.getInstance().stop(this);
  }

  private Queue<Transaction> getHistory() {
    VpnController controller = VpnController.getInstance();
    return controller.getTracker(this).getRecentTransactions();
  }

  private long getNumRequests() {
    VpnController controller = VpnController.getInstance();
    return controller.getTracker(this).getNumRequests();
  }

  public void prepareAndStartDnsVpn() {
    if (hasVpnService()) {
      if (prepareVpnService()) {
        startDnsVpnService();
      }
      else{
        status_button = 1;
      }
    } else {
      LogWrapper.log(Log.ERROR, LOG_TAG, "Device does not support system-wide VPN mode.");
    }
  }

  // Returns whether the device supports the tunnel VPN service.
  // This is just in case someone sideloads the app onto an ancient device.
  @SuppressLint("ObsoleteSdkInt")
  private boolean hasVpnService() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
  }

  private boolean prepareVpnService() throws ActivityNotFoundException {
    Intent prepareVpnIntent = null;
    try {
       prepareVpnIntent = VpnService.prepare(this);
    } catch (NullPointerException e) {
      LogWrapper.logException(e);
      return false;
    }
    if (prepareVpnIntent != null) {
      Log.i(LOG_TAG, "Prepare VPN with activity");
      startActivityForResult(prepareVpnIntent, REQUEST_CODE_PREPARE_VPN);
      if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        syncDnsStatus();  // Set DNS status to off in case the user does not grant VPN permissions
      }
      return false;
    }
    return true;
  }

  private LinkProperties getLinkProperties() {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return null;
    }
    Network activeNetwork = connectivityManager.getActiveNetwork();
    if (activeNetwork == null) {
      return null;
    }
    return connectivityManager.getLinkProperties(activeNetwork);
  }

  private String getSystemDnsServer() {
    if (Build.VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
      // getDnsServers requires Lollipop or later.
      return null;
    }
    LinkProperties linkProperties = getLinkProperties();
    if (linkProperties == null) {
      return null;
    }
    if (VERSION.SDK_INT >= VERSION_CODES.P) {
      String privateDnsServerName = linkProperties.getPrivateDnsServerName();
      if (privateDnsServerName != null) {
        return privateDnsServerName;
      }
    }
    for (InetAddress address : linkProperties.getDnsServers()) {
      // Show the first DNS server on the list.
      return address.getHostAddress();
    }
    return null;
  }

  private enum PrivateDnsMode {
    NONE,  // The setting is "Off" or "Opportunistic", and the DNS connection is not using TLS.
    UPGRADED,  // The setting is "Opportunistic", and the DNS connection has upgraded to TLS.
    STRICT  // The setting is "Strict".
  };

  private PrivateDnsMode getPrivateDnsMode() {
    if (Build.VERSION.SDK_INT < VERSION_CODES.P) {
      // Private DNS was introduced in P.
      return PrivateDnsMode.NONE;
    }

    LinkProperties linkProperties = getLinkProperties();
    if (linkProperties == null) {
      return PrivateDnsMode.NONE;
    }

    if (linkProperties.getPrivateDnsServerName() != null) {
      return PrivateDnsMode.STRICT;
    }

    if (linkProperties.isPrivateDnsActive()) {
      return PrivateDnsMode.UPGRADED;
    }

    return PrivateDnsMode.NONE;
  }

  private boolean isAnotherVpnActive() {
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      ConnectivityManager connectivityManager =
          (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
      Network activeNetwork = connectivityManager.getActiveNetwork();
      if (activeNetwork == null) {
        return false;
      }
      NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
      if (capabilities == null) {
        // It's not clear when this can happen, but it has occurred for at least one user.
        return false;
      }
      return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
    }
    // For pre-M versions, return true if there's any network whose name looks like a VPN.
    try {
      Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
      while (networkInterfaces.hasMoreElements()) {
        NetworkInterface networkInterface = networkInterfaces.nextElement();
        String name = networkInterface.getName();
        if (networkInterface.isUp() && name != null &&
            (name.startsWith("tun") || name.startsWith("pptp") || name.startsWith("l2tp"))) {
          return true;
        }
      }
    } catch (SocketException e) {
      LogWrapper.logException(e);
    }
    return false;
  }

  @Override
  public void onActivityResult(int request, int result, Intent data) {
    super.onActivityResult(request, result, data);
    if (request == REQUEST_CODE_PREPARE_VPN) {
      if (result == RESULT_OK) {
        startDnsVpnService();
      } else {
        stopDnsVpnService();
      }
    }
  }

  private void sendNotification(String title,String body) {
    Notification.Builder builder;
    NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.warning_channel_name);
      String description = getString(R.string.warning_channel_description);
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel channel = new NotificationChannel(getString(R.string.notification_channel_id), name, importance);
      channel.setDescription(description);
      channel.enableVibration(false);
      channel.setVibrationPattern(null);

      notificationManager.createNotificationChannel(channel);
//        notificationManager.cancelAll();
      builder = new Notification.Builder(this, getString(R.string.notification_channel_id));
    } else {
      builder = new Notification.Builder(this);
      builder.setVibrate(null);
      // Deprecated in API 26.
      builder = builder.setPriority(Notification.PRIORITY_MAX);
    }

    PendingIntent mainActivityIntent = PendingIntent.getActivity(
            this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

    builder.setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setLights(Color.RED, 1000, 300)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.visafe_noti)
            .setNumber(++ MyFirebaseService.numMessages)
            .setStyle(new Notification.BigTextStyle()
                    .bigText(body))
            .setFullScreenIntent(mainActivityIntent, true);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      builder.setCategory(Notification.CATEGORY_ERROR);
    }

    notificationManager.notify(0, builder.getNotification());
  }
  private int count_noti_on = 0;
  private int count_noti_off = 0;
  // Sets the UI DNS status on/off.
  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  private void syncDnsStatus() {
    if (controlView == null) {
      return;
    }

    VpnState status = VpnController.getInstance().getState(this);

    // Change switch-button state
    final SwitchMaterial switchButton = controlView.findViewById(R.id.dns_switch);
    switchButton.setChecked(status.activationRequested);
    final ImageView roundImage = controlView.findViewById(R.id.round_image);
    Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.round);
    roundImage.startAnimation(aniRotateClk);
    final ImageView image_status = controlView.findViewById(R.id.image_status);
    final ImageView button_status = controlView.findViewById(R.id.button_status);
    // Change status and explanation text
    final int statusId;
    PrivateDnsMode privateDnsMode = PrivateDnsMode.NONE;
    if (status.activationRequested) {
      status_button = 0;
      if (status.connectionState == null) {
        statusId = R.string.status_waiting;
        image_status.setImageResource(R.drawable.off_screen);
        button_status.setImageResource(R.drawable.off_button);
      } else if (status.connectionState == ViSafeVpnService.State.NEW) {
        statusId = R.string.status_starting;
        image_status.setImageResource(R.drawable.off_screen);
        button_status.setImageResource(R.drawable.off_button);
      } else if (status.connectionState == ViSafeVpnService.State.WORKING) {
        statusId = R.string.status_protected;
        if (count_noti_on == 0 && sendNotificationWhenClickButtonOnOff == true)
        {
          sendNotification("Đã kích hoạt chế độ bảo vệ!","Chế độ chống lừa đảo, mã độc, tấn công mạng đã được kích hoạt!");
          count_noti_on++;
          count_noti_off = 0;
          sendNotificationWhenClickButtonOnOff = false;
        }
        image_status.setImageResource(R.drawable.on_screen);
        button_status.setImageResource(R.drawable.on_button);
        roundImage.clearAnimation();
      } else {
        statusId = R.string.status_failing;
        image_status.setImageResource(R.drawable.off_screen);
        button_status.setImageResource(R.drawable.off_button);
      }
    } else if (isAnotherVpnActive()) {
      statusId = R.string.status_exposed;
      image_status.setImageResource(R.drawable.off_screen);
      button_status.setImageResource(R.drawable.off_button);
      roundImage.clearAnimation();
      if (count_noti_off == 0 && sendNotificationWhenClickButtonOnOff == true)
      {
        sendNotification("Bạn đã tắt chế độ bảo vệ!","Thiết bị của bạn có thể bị ảnh hưởng bởi tấn công mạng");
        count_noti_off++;
        count_noti_on = 0;
        sendNotificationWhenClickButtonOnOff = false;
      }
    } else {
      privateDnsMode = getPrivateDnsMode();
      if (privateDnsMode == PrivateDnsMode.STRICT) {
        statusId = R.string.status_strict;
        image_status.setImageResource(R.drawable.off_screen);
        button_status.setImageResource(R.drawable.off_button);
        roundImage.clearAnimation();
      } else if (privateDnsMode == PrivateDnsMode.UPGRADED) {
        statusId = R.string.status_upgraded;
        image_status.setImageResource(R.drawable.off_screen);
        button_status.setImageResource(R.drawable.off_button);
        roundImage.clearAnimation();
        if (count_noti_off == 0 && sendNotificationWhenClickButtonOnOff == true)
        {
          sendNotification("Bạn đã tắt chế độ bảo vệ!","Thiết bị của bạn có thể bị ảnh hưởng bởi tấn công mạng");
          count_noti_off++;
          count_noti_on = 0;
          sendNotificationWhenClickButtonOnOff = false;
        }
      } else {
        statusId = R.string.status_exposed;
        image_status.setImageResource(R.drawable.off_screen);
        button_status.setImageResource(R.drawable.off_button);
        roundImage.clearAnimation();
        if (count_noti_off == 0 && sendNotificationWhenClickButtonOnOff == true)
        {
          sendNotification("Bạn đã tắt chế độ bảo vệ!","Thiết bị của bạn có thể bị ảnh hưởng bởi tấn công mạng");
          count_noti_off++;
          count_noti_on = 0;
          sendNotificationWhenClickButtonOnOff = false;
        }
      }
    }

    final int colorId;
    if (status.on) {
      colorId = status.connectionState != ViSafeVpnService.State.FAILING ? R.color.white :
          R.color.white;
    } else if (privateDnsMode == PrivateDnsMode.STRICT) {
      colorId = R.color.white;
    } else {
      colorId = R.color.white;
    }

    final TextView statusText = controlView.findViewById(R.id.status);
    final int color = ContextCompat.getColor(this, colorId);
    statusText.setTextColor(color);
    statusText.setText(statusId);
  }

  @Override
  public void onBackPressed() {
    if (isShowingHomeView()) {
      // Back button should leave the app if we are currently looking at the home view.
      super.onBackPressed();
      return;
    }
    else
    {
      super.onBackPressed();
    }
  }

  private boolean isShowingHomeView() {
    View home = findViewById(R.id.frame_main);
    return home.getVisibility() == View.VISIBLE;
  }

  private void showSettings() {
    settingsFragment = new NewSettingFragment();
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.settings, settingsFragment)
        .commitNow();
  }
  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  private View chooseView(int id) {
    View home = findViewById(R.id.frame_main);
    View settings = findViewById(R.id.settings);
    View info = findViewById(R.id.dashboard_layout);
    View extension = findViewById(R.id.extension_layout);
    home.setVisibility(View.GONE);
    settings.setVisibility(View.GONE);
    info.setVisibility(View.GONE);
    extension.setVisibility(View.GONE);
    View selected = findViewById(id);
    selected.setVisibility(View.VISIBLE);
    Bundle bundle = new Bundle();
    bundle.putInt("dashboardFragment", 0);
    ActionBar actionBar = getSupportActionBar();
    switch (id) {
      case R.id.frame_main:
        actionBar.setTitle(R.string.app_name);
        break;
    }
    getSupportActionBar().hide();
    DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
    drawerLayout.closeDrawer(GravityCompat.START);

    if (id == R.id.frame_main) {
      drawerToggle.setDrawerIndicatorEnabled(true);
    } else {
      actionBar.setDisplayHomeAsUpEnabled(false);
      drawerToggle.setDrawerIndicatorEnabled(false);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    return selected;
  }

  private enum InfoPage {
    LIFETIME_QUERIES(true,
        R.string.num_requests,
        R.drawable.ic_dns,
        R.string.num_requests_headline,
        R.string.num_requests_body),
    RECENT_QUERIES(true,
        R.string.queries_per_minute,
        R.drawable.ic_trending_up_black_24dp,
        R.string.queries_per_minute_headline,
        R.string.queries_per_minute_body),
    SECURE_PROTOCOL(true,
        R.string.transport_label,
        R.drawable.ic_lock_black_24dp,
        R.string.transport_headline,
        R.string.transport_body),
    SECURE_SERVER(true,
        R.string.server_label,
        R.drawable.ic_server,
        R.string.server_headline,
        R.string.server_body),
    DEFAULT_PROTOCOL(false,
        R.string.default_transport_label,
        R.drawable.ic_lock_open_black_24dp,
        R.string.insecure_transport_headline,
        R.string.insecure_transport_body),
    // There are two DNS-over-TLS cases: upgraded (i.e. opportunistic) and strict mode.
    // These are only shown on Android P, to users who are using DNS-over-TLS rare.  Rather than
    // writing custom explanations for these rare cases, we just reuse the status indicator text.
    UPGRADED_PROTOCOL(false,
        R.string.default_transport_label,
        R.drawable.ic_lock_black_24dp,
        R.string.status_upgraded,
        R.string.explanation_upgraded),
    STRICT_MODE_PROTOCOL(true,
        R.string.default_transport_label,
        R.drawable.ic_lock_black_24dp,
        R.string.status_strict,
        R.string.explanation_strict),
    DEFAULT_SERVER(false,
        R.string.default_server_label,
        R.drawable.ic_server,
        R.string.insecure_server_headline,
        R.string.insecure_server_body),
    STRICT_MODE_SERVER(true,
        R.string.default_server_label,
        R.drawable.ic_server,
        R.string.strict_mode_server_headline,
        R.string.strict_mode_server_body);

    final boolean good;
    final @StringRes int title;
    final @DrawableRes int drawable;
    final @StringRes int headline;
    final @StringRes int body;
    InfoPage(boolean good, int title, int drawable, int headline, int body) {
      this.title = title;
      this.good = good;
      this.drawable = drawable;
      this.headline = headline;
      this.body = body;
    }
  }

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  private void showInfo(InfoPage page) {
    View view = chooseView(R.id.info_page);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setTitle(page.title);

    ImageView image = view.findViewById(R.id.info_image);
    image.setImageResource(page.drawable);
    int color = ContextCompat.getColor(this, page.good ? R.color.accent_good : R.color.accent_bad);
    ImageViewCompat.setImageTintList(image, ColorStateList.valueOf(color));

    TextView headline = view.findViewById(R.id.info_headline);
    headline.setText(page.headline);

    TextView body = view.findViewById(R.id.info_body);
    body.setText(page.body);
  }

  private static void prepareHyperlinks(Activity activity, View view) {
    int[] idsToLink = {
        R.id.credit_text_view
    };
    for (int id : idsToLink) {
      TextView textView = (TextView) view.findViewById(id);
      textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
  }
  private void getIntroApproval() {
    if (IntroDialog.shouldShow(this)) {
      new IntroDialog().show(getSupportFragmentManager(), "intro");
    }
  }
}
