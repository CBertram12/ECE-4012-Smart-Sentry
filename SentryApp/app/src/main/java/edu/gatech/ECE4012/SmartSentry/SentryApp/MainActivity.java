package edu.gatech.ECE4012.SmartSentry.SentryApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    // initialize socket and input output streams
    private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;
    private String sendData          = "";
    private boolean clicked          = false;
    Thread clientThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtons();
        this.clientThread = new Thread(new ClientThread());
        this.clientThread.start();

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
            }
        });
    }

    class ClientThread implements Runnable {

        public void run() {
            // establish a connection
            try {
                socket = new Socket("192.168.174.1", 5000);

                // sends output to the socket
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
