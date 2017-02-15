package com.chan.sethisollu;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment
{

    View root;
    EditText phoneEdit;
    EditText messageEdit;

    TextView selectedAppText;

    static final int SEND_WHATS_APP = 0x1;
    static final int SEND_LINE = 0x2;
    static final int SEND_WE_CHAT = 0x3;

    int SELECTED_APP;
    String selectedPackageName;

    public MainFragment()
    {
    }

    public static MainFragment newInstance()
    {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        root = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        phoneEdit = (EditText) root.findViewById(R.id.phone_no_text);
        messageEdit = (EditText) root.findViewById(R.id.message_text);
        selectedAppText = (TextView) root.findViewById(R.id.selected_app);
        SELECTED_APP = SEND_WHATS_APP;
        setSelectedAppText();

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendToWhatsApp();
            }
        });
        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_whatsapp:
                SELECTED_APP = SEND_WHATS_APP;
                setSelectedAppText();
                break;
            case R.id.action_line:
                SELECTED_APP = SEND_LINE;
                setSelectedAppText();
                break;
            case R.id.action_we_chat:
                SELECTED_APP = SEND_WE_CHAT;
                setSelectedAppText();
                break;
            default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void sendToWhatsApp()
    {
        String phoneNo = phoneEdit.getText().toString();
        String message = messageEdit.getText().toString();
        if(!validate(phoneNo, message))
        {
            return;
        }

         /*Intent sendIntent = new Intent();
         sendIntent.setPackage("com.whatsapp");
         sendIntent.setAction(Intent.ACTION_SEND);
         sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
         sendIntent.setType("text/plain"); startActivity(sendIntent);*/

        boolean isWhatsAppAvailable = isWhatsAppAvailable(selectedPackageName);
        if(isWhatsAppAvailable)
        {
            /*Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");
            startActivity(sendIntent);*/
            Intent sendIntent = new Intent();
            //sendIntent.setPackage("com.whatsapp");
            sendIntent.setPackage(selectedPackageName);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("91"+phoneNo) + "@s.whatsapp.net");
            //sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("91"+phoneNo));
            //sendIntent.putExtra("displayname", "Cheetah");
            sendIntent.setType("text/plain");
            startActivityForResult(sendIntent, SEND_WHATS_APP);
        }
        else
        {
            /*Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);*/
            showMessage("WhatsApp not Installed");
        }
    }

    private boolean isWhatsAppAvailable(String uri)
    {
        PackageManager pm = getContext().getPackageManager();
        boolean app_installed = false;
        try
        {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        return app_installed;
    }

    private boolean validate(String phoneNo, String message)
    {
        if(TextUtils.isEmpty(phoneNo))
        {
            showMessage("Phone No should not empty");
            return false;
        }

        if(TextUtils.isEmpty(message))
        {
            showMessage("Message should not empty");
            return false;
        }

        return true;
    }

    private void setSelectedAppText()
    {
        selectedPackageName = "com.whatsapp";
        String text = getResources().getString(R.string.action_whatsapp);
        if(SELECTED_APP == SEND_LINE)
        {
            text = getResources().getString(R.string.action_line);
            selectedPackageName = "jp.naver.line.android";
        }
        else if(SELECTED_APP == SEND_WE_CHAT)
        {
            text = getResources().getString(R.string.action_we_chat);
            selectedPackageName = "com.tencent.mm";
        }

        selectedAppText.setText(":" + text);
    }

    private void showMessage(String message)
    {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }
}
