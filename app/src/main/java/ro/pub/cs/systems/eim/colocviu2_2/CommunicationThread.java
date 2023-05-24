package ro.pub.cs.systems.eim.colocviu2_2;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    // run() method: The run method is the entry point for the thread when it starts executing.
    // It's responsible for reading data from the client, interacting with the server,
    // and sending a response back to the client.
    @Override
    public void run() {
        // It first checks whether the socket is null, and if so, it logs an error and returns.
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

        try {
            // Create BufferedReader and PrintWriter instances for reading from and writing to the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (information");

            // Read the information values sent by the client
            String information = bufferedReader.readLine();
            if (information == null || information.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (information");
                return;
            }

            // It checks whether the serverThread has already received the information for the given value.
            HashMap<String, ValueInformation> data = serverThread.getData();

            String value = information.toString();
//            String value = information.split(",")[1];
            if (value == null || value.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (information");
                return;
            }
            ValueInformation valueInformation;
            if (data.containsKey(value)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                valueInformation = data.get(value);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + value + ".json");
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                String result = "";
                if (httpGetEntity != null) {
                    // It creates a JSONObject instance from the response.
                    result = EntityUtils.toString(httpGetEntity);
                    JSONObject content = new JSONObject(result);
                    // It extracts the information from the JSON object.
                    JSONObject currentObservation = content.getJSONObject("current_observation");
                    String valueResult = currentObservation.getString("value");
                    // It creates a ValueInformation instance with the extracted information.
                    valueInformation = new ValueInformation(valueResult);
                    // It stores the information into the serverThread cache.
                    serverThread.setData(valueResult, valueInformation);
                } else {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }


                JSONObject content = new JSONObject(result);
                // It extracts the information from the JSON object.
                JSONObject currentObservation = content.getJSONObject("current_observation");
                String valueResult = currentObservation.getString("value");
                // It creates a ValueInformation instance with the extracted information.
                valueInformation = new ValueInformation(valueResult);
                // It stores the information into the serverThread cache.
                serverThread.setData(valueResult, valueInformation);

                if (valueInformation == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Values Information are null!");
                    return;
                }

                // Send the information to the client
                printWriter.println(valueInformation.getInformation());
                printWriter.flush();


            }
        } catch (IOException | JSONException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            // Close the socket
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }




}
