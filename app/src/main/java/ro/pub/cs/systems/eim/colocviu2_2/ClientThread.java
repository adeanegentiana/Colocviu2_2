package ro.pub.cs.systems.eim.colocviu2_2;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private String address;
    private int port;
    private String value;
    private TextView operation;
    private String result;
    private Socket socket;

    public ClientThread(String address, int port, String value, TextView operation) {
        this.address = address;
        this.port = port;
        this.value = value;
        this.operation = operation;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                return;
            }
            printWriter.println(value);
            printWriter.flush();
            printWriter.println(operation);
            printWriter.flush();
            result = bufferedReader.readLine();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    public String getResult() {
        return result;
    }

    public void stopThread() {
        interrupt();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
