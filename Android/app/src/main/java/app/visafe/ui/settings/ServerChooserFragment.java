
package app.visafe.ui.settings;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceDialogFragmentCompat;
import app.visafe.R;
import app.visafe.sys.PersistentState;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * User interface for a the server URL selection.
 */

public class ServerChooserFragment extends PreferenceDialogFragmentCompat
    implements RadioGroup.OnCheckedChangeListener, TextWatcher, EditText.OnEditorActionListener,
    OnItemSelectedListener {
    private RadioGroup buttons = null;

    public TextView customServerUrl = null;
    private TextView warning = null;
    private String userID ="";
    static ServerChooserFragment newInstance(String key) {
        final ServerChooserFragment fragment = new ServerChooserFragment();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String getUrl() {
        return customServerUrl.getText().toString();
    }

    private void updateUI() {
        int checkedId = buttons.getCheckedRadioButtonId();
        boolean custom = checkedId == R.id.pref_server_custom;
        customServerUrl.setEnabled(custom);
        if (custom) {
            setValid(checkUrl(Untemplate.strip(getUrl())));
        } else {
            setValid(true);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        updateUI();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        updateUI();
    }

    // Check that the URL is a plausible DOH server: https with a domain, a path (at least "/"),
    // and no query parameters or fragment.
    private boolean checkUrl(String url) {
        try {
            URL parsed = new URL(url);
            return parsed.getProtocol().equals("https") && !parsed.getHost().isEmpty() &&
                !parsed.getPath().isEmpty() && parsed.getQuery() == null && parsed.getRef() == null;
        } catch (MalformedURLException e) {
            return false;
        }
    }



    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (checkUrl(Untemplate.strip(v.getText().toString()))) {
            Dialog dialog = getDialog();
            if (dialog instanceof AlertDialog) {
                Button ok = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                ok.setEnabled(true);
                ok.performClick();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        onBuiltinServerSelected(i);
    }

    private void onBuiltinServerSelected(int i) {

        CharSequence template = getResources().getText(R.string.server_choice_website_notice);
        SpannableStringBuilder websiteMessage = new SpannableStringBuilder(template);

        while (Character.isWhitespace(websiteMessage.charAt(0))) {
            websiteMessage.delete(0, 1);
        }

        URLSpan templateLink =
            websiteMessage.getSpans(0, websiteMessage.length(), URLSpan.class)[0];
        websiteMessage.removeSpan(templateLink);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        updateUI();
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        ServerChooser preference = (ServerChooser) getPreference();
        String url = preference.getUrl();
        String reqString = Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();

        buttons = view.findViewById(R.id.pref_server_radio_group);
        customServerUrl = view.findViewById(R.id.custom_server_url);
        warning = view.findViewById(R.id.url_warning);
        //lưu mã định danh
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("userID",Context.MODE_PRIVATE);
        //nếu cái mới khác với cái tên hiện tại

        if (SettingsFragment.deviceName.length()!=12) {
        buttons.check(R.id.pref_server_custom);
        if (url == null) {
            customServerUrl.setText(PersistentState.url_default);
            userID = customServerUrl.getText().toString().substring(customServerUrl.getText().toString().lastIndexOf("/")+1,customServerUrl.getText().toString().lastIndexOf("/")+13);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putString("userID", userID);
            editor.apply();
        } else {
            customServerUrl.setText(url);
        }
        }
        else
        {
            String uid = sharedPreferences.getString("userID","");
            customServerUrl.setText("https://dns.visafe.vn/dns-query/"+uid.toLowerCase()+"-"+covertToString(SettingsFragment.deviceName).replaceAll("\\s+","-").toLowerCase());
            PersistentState.setServerUrl(getContext(),"https://dns.visafe.vn/dns-query/"+uid.toLowerCase()+"-"+covertToString(SettingsFragment.deviceName).replaceAll("\\s+","-").toLowerCase());
        }
        //lấy từ hàm main
        sharedPreferences = this.getActivity().getSharedPreferences("url", Context.MODE_PRIVATE);
        String url_custom = sharedPreferences.getString("url","");
        if (url_custom.equals("")==false)
        {
            customServerUrl.setText(url_custom);
        }
        refresh_button_OK();
        updateUI();
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
    public void refresh_button_OK()
    {
        ServerChooser preference = (ServerChooser) getPreference();
        preference.setUrl(getUrl());
    }
    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            refresh_button_OK();
        }
        customServerUrl.removeTextChangedListener(this);
        customServerUrl.setOnEditorActionListener(null);
        buttons.setOnCheckedChangeListener(null);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        updateUI();
    }

    private void setValid(boolean valid) {
        warning.setVisibility(valid ? View.INVISIBLE : View.VISIBLE);
        Dialog dialog = getDialog();
        if (dialog instanceof AlertDialog) {
            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(valid);
        }
    }
}
