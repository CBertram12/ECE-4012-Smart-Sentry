package edu.gatech.ECE4012.SmartSentry.SentryApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.VideoView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    // initialize socket and input output streams
    private Socket socket            = null;
    private DataOutputStream out     = null;
    private String sendData          = "";
    private boolean clicked          = false;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    Thread clientThread = null;
    int notification_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtons();

        VideoView vidView = (VideoView)findViewById(R.id.myVideo);

        String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        Uri vidUri = Uri.parse(vidAddress);

        vidView.setVideoURI(vidUri);
        vidView.start();

        createNotificationChannel();

        notificationManager = NotificationManagerCompat.from(this);

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder = new NotificationCompat.Builder(this, "Channel 1")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        this.clientThread = new Thread(new ClientThread());
        this.clientThread.start();

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alert Channel";
            String description = "Displays intruder alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Channel 1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initButtons() {
        ImageButton b1=(ImageButton)findViewById(R.id.UpArrow);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData = "UP";
                clicked = true;
            }
        });

        ImageButton b2=(ImageButton)findViewById(R.id.RightArrow);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData = "RIGHT";
                clicked = true;
            }
        });

        ImageButton b3=(ImageButton)findViewById(R.id.LeftArrow);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData = "LEFT";
                clicked = true;
            }
        });

        ImageButton b4=(ImageButton)findViewById(R.id.DownArrow);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData = "DOWN";
                clicked = true;
                notificationManager.notify(notification_id++, builder.build());
            }
        });

        Button b5=(Button)findViewById(R.id.SwitchMain);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
    }

    public void openMap() {
        Intent intent = new Intent(this, Map_View.class);
        startActivity(intent);
    }

    class ClientThread implements Runnable {

        public void run() {
            // establish a connection
            try {
                socket = new Socket("192.168.174.1", 5000);

                // sends put to the socket
                out = new DataOutputStream(socket.getOutputStream());
            } catch (UnknownHostException u) {
                System.out.println(u);
            } catch (IOException i) {
                System.out.println(i);
            }


             while (!sendData.equals("DOWN")) {
                 if (clicked) {
                    try {
                        out.writeUTF(sendData);
                    } catch (IOException i) {
                        System.out.println(i);
                    }
                    clicked = false;
                 }
             }
            // close the connection
            try {
                out.close();
                socket.close();
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }
}
