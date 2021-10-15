package app.visafe.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import app.visafe.R;
import app.visafe.domain.DomainVisafe;
import app.visafe.ui.Item;
import app.visafe.ui.ItemAdapter;

public class NewSettingFragment extends Fragment {
    private View registerView;
    private TextView textDeviceName;
    private TextView textStatusRegister;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Window window = getActivity().getWindow();
        window.setStatusBarColor(ContextCompat.getColor(getContext(),R.color.white));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        registerView = inflater.inflate(R.layout.fragment_new_setting, container, false);
        Button btn_register = registerView.findViewById(R.id.button_register);
        textDeviceName = registerView.findViewById(R.id.textDeviceName);
        textDeviceName.setText(Build.MANUFACTURER
                + " " + Build.MODEL.toUpperCase());
        SharedPreferences sharedPreference = getActivity().getSharedPreferences("userID",Context.MODE_PRIVATE);
        String userID = sharedPreference.getString("userID", null);
        textStatusRegister = registerView.findViewById(R.id.textStatusDeviceConnection);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("registerDeviceStatus", Context.MODE_PRIVATE);
        if ( sharedPreferences.getBoolean("status",false) == true)
        {
            textStatusRegister.setText("Thiết bị đã được đăng ký nhóm");
            textStatusRegister.setTextColor(getResources().getColor(R.color.teal_200));
        }
        else
        {
            textStatusRegister.setTextColor(getResources().getColor(R.color.black));
            textStatusRegister.setText("Thiết bị chưa đăng ký nhóm");
        }

        SharedPreferences sharedPreferences_login = getActivity().getSharedPreferences("login",Context.MODE_PRIVATE);
        String token = sharedPreferences_login.getString("login_token", "");

        final ListView list = registerView.findViewById(R.id.listSetting);
        ArrayList<Item> arrayList = new ArrayList<>();
        arrayList.add(new Item(R.drawable.manage_passcode,"Quản lý mã bảo vệ"));
        arrayList.add(new Item(R.drawable.info,"Thông tin sản phẩm"));
        arrayList.add(new Item(R.drawable.app_share,"Chia sẻ ứng dụng"));
        arrayList.add(new Item(R.drawable.vip_icon,"Chế độ VIP"));
        System.out.println("token2: "+token);
        if (!token.equals("")) {
             arrayList.add(new Item(R.drawable.logout_icons, "Đăng xuất"));
        }
        else {arrayList.add(new Item(R.drawable.login_icons, "Đăng nhập"));}
        ItemAdapter arrayAdapter = new ItemAdapter(getContext(), arrayList);
        list.setAdapter(arrayAdapter);

        return registerView;
    }
    public void onStart(){
        super.onStart();
        final ListView list = registerView.findViewById(R.id.listSetting);
        list.invalidateViews();
    }

}