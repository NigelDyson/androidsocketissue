package com.problem.sockettester;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * wrap up the reading and writing to sockets.
 */
public class SocketTransport {

    static final int IPTOS_LOWCOST = 0x02;
    static final int IPTOS_RELIABILITY = 0x04;
    static final int IPTOS_THROUGHPUT = 0x08;
    static final int IPTOS_LOWDELAY = 0x10;

    private DataInputStream reader;
    private DataOutputStream writer;

    private byte[] readBuffer = new byte[4096];

    public SocketTransport(Socket socket) throws IOException {

        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        socket.setTrafficClass(IPTOS_LOWDELAY | IPTOS_RELIABILITY);
        socket.setReceiveBufferSize(4096);
        socket.setSendBufferSize(4096);

        reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public String read() throws IOException {

        int messageSize = reader.readInt();
        int bytesRead;
        int totalRead = 0;
        int offset = 0;
        boolean moreToRead = true;

        while (moreToRead) {
            bytesRead = reader.read(readBuffer, offset, messageSize);
            totalRead += bytesRead;

            if (totalRead == messageSize) {
                moreToRead = false;
            }

            offset += bytesRead;
        }

        return new String(readBuffer, 0, messageSize);
    }


    public int write(String message) throws IOException {

        writer.writeInt(message.length());
        writer.writeBytes(message);
        writer.flush();
        return message.length();
    }
}

