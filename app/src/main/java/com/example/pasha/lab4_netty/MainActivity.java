package com.example.pasha.lab4_netty;

import android.Manifest;
import android.content.Intent;
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

import com.example.pasha.lab4_netty.network.client.Client;

/**
 * Stores references to {@link View} elements, creates {@link ServerService} which
 * runs Server in new thread
 */
public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private EditText editMessage;
    private TextView textViewMessages;
    private Client client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        startServer();
        findViews();

        textViewMessages.setMovementMethod(new ScrollingMovementMethod());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (client == null) {
                    client = new Client();
                }

                String message = editMessage.getText().toString();
                if (!"".equals(message)) {
                    client.sendMessage(message);
                    textViewMessages.append("\n" + client.getResponse());
                }
            }
        });
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

    private void findViews() {
        sendButton = findViewById(R.id.button);
        editMessage = findViewById(R.id.editText);
        textViewMessages = findViewById(R.id.textView);
    }
}
