package com.problem.socketclient;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.problem.sockettester.SocketTransport;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

public class SocketClientActivity extends Activity implements Runnable {

    private static final int ECHO_PORT = 13999;
    private static final String serverIp = "192.168.10.100";
    Thread clientThread;
    Socket clientSocket;
    private boolean running;

    TextView theText;
    TextView slowestTime;

    private long slowest = 0;
    private Timer timer;
    private ArrayBlockingQueue<String> theQ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = new Timer("client");
        theQ = new ArrayBlockingQueue<>(1000);

        theText = (TextView) findViewById(R.id.message);
        slowestTime = (TextView) findViewById(R.id.slowest_triptime);

        runClient();
    }

    private void runClient() {
        running = true;
        clientThread = new Thread(this);
        clientThread.start();
        scheduleNext();
    }


    @Override
    public void run() {

        try {
            clientSocket = new Socket(serverIp, ECHO_PORT);
            SocketTransport transport = new SocketTransport(clientSocket);

            while (running) {
                // Blocking call ;-)
                onStatusChange("Waiting for next message", 0);
                String msg = theQ.take();

                onStatusChange("Writing new message", 0);

                long timeStart = System.currentTimeMillis();
                transport.write(msg);

                onStatusChange("Waiting for response", 0);
                String result = transport.read();

                onStatusChange(result, System.currentTimeMillis() - timeStart);

                if (result == null) {
                    running = false;
                }
            }
        } catch (UnknownHostException e) {
            onStatusChange(e.getMessage(), 0);
        } catch (IOException e) {
            onStatusChange(e.getMessage(), 0);
        } catch (InterruptedException e) {
            onStatusChange(e.getMessage(), 0);
        } finally {
            close(clientSocket);
        }
    }

    /**
     * Display status updates on screen
     */
    private void onStatusChange(final String status, final long timeMs) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                theText.setText(status);

                if (timeMs > slowest ) {
                    slowest = timeMs;
                    slowestTime.setText("Slowest: " + timeMs + "ms");
                }
            }
        });
    }

    /**
     * Start a timer that will add the next message after a specified
     * delay and set the timer for the next iteration
     */
    private void scheduleNext() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                theQ.add("Here are some words...");
                scheduleNext();
            }
        }, 1000);
    }


    private void close(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {}
        }
    }
}

