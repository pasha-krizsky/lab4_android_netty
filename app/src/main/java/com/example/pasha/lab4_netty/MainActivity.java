package com.example.pasha.lab4_netty;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    /**
     * Key of message to ClientService
     */
    final static String KEY_MESSAGE_SERVICE = "MESSAGE_SERVICE";

    /**
     * Action to send message to ClientService
     */
    final static String ACTION_SEND_MESSAGE_SERVICE = "SEND_MESSAGE_SERVICE";

    @BindView(R.id.button)
    Button sendButton;

    @BindView(R.id.editText)
    EditText editMessage;

    @BindView(R.id.textView)
    TextView textViewMessages;

    MainActivityReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Check permissions and start server and client
        checkPermissions();
        startServer();
        startClient();

        textViewMessages.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onStart() {
        super.onStart();
        createReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @OnClick(R.id.button)
    public void onClick(View view) {
        String message = editMessage.getText().toString();

        Intent intent = new Intent();
        intent.setAction(ACTION_SEND_MESSAGE_SERVICE);
        intent.putExtra(KEY_MESSAGE_SERVICE, message);
        sendBroadcast(intent);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.INTERNET},
                    1
            );
        }
    }

    private void startServer() {
        Intent intent = new Intent(this, ServerService.class);
        startService(intent);
    }

    private void startClient() {
        Intent intent = new Intent(this, ClientService.class);
        startService(intent);
    }

    private class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (ClientService.ACTION_SEND_MESSAGE_ACTIVITY.equals(action)) {
                String message = intent.getStringExtra(ClientService.KEY_MESSAGE_ACTIVITY);
                textViewMessages.append("\n" + message);
            }
        }
    }

    private void createReceiver() {
        receiver = new MainActivityReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ClientService.ACTION_SEND_MESSAGE_ACTIVITY);
        registerReceiver(receiver, intentFilter);
    }


}
