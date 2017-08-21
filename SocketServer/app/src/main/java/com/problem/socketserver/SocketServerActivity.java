package com.problem.socketserver;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.problem.sockettester.SocketTransport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

public class SocketServerActivity extends Activity  implements Runnable {

    private static final int ECHO_PORT = 13999;
    Thread serverThread;
    ServerSocket serverSocket;
    Socket clientSocket;
    private boolean running;

    TextView theText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_server);

        theText = (TextView) findViewById(R.id.message);
        runServer();
    }

    private void runServer() {
        running = true;
        serverThread= new Thread(this);
        serverThread.start();
    }


    @Override
    public void run() {

        try {
            onStatusChange("Waiting for connection...", 0);

            serverSocket = new ServerSocket(ECHO_PORT);
            clientSocket = serverSocket.accept();

            SocketTransport transport = new SocketTransport(clientSocket);

            while (running) {
                onStatusChange("Waiting for message...",0);
                String message = transport.read();

                onStatusChange("Echoing message: " + message, 0);
                transport.write(message);
            }
        } catch (UnknownHostException e) {
            onStatusChange(e.getMessage(), 0);
        } catch (IOException e) {
            onStatusChange(e.getMessage(), 0);
        } finally {
            close(clientSocket);
            close(serverSocket);
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
            }
        });
    }


    private void close(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {}
        }
    }

    private void close(ServerSocket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {}
        }
    }

}

