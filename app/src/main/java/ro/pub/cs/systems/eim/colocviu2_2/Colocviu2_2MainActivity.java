package ro.pub.cs.systems.eim.colocviu2_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Colocviu2_2MainActivity extends AppCompatActivity {

    private EditText serverPortEditText = null;
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText valueEditText = null;
    private Button connectButton = null;
    private Button getValuesButton = null;
    private TextView valueTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();

    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {

            // Retrieves the server port. Checks if it is empty or not
            // Creates a new server thread with the port and starts it
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();

        }

    }

    private GetValuesButtonClickListener getValuesButtonClickListener = new GetValuesButtonClickListener();

    private class GetValuesButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {

            // Retrieves the client address and port. Checks if they are empty or not
            //  Checks if the server thread is alive. Then creates a new client thread with the address, port, city and information type
            //  and starts it
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
//            String value = valueEditText.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String value = valueEditText.getText().toString();
            if (value.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (value) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            valueTextView.setText(Constants.EMPTY_STRING);
            ClientThread clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), value, valueTextView);
            clientThread.start();
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");

        setContentView(R.layout.activity_practical_test02_2_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        Button connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        valueEditText = findViewById(R.id.value_edit_text);
        getValuesButton = findViewById(R.id.get_value_button);
        getValuesButton.setOnClickListener(getValuesButtonClickListener);
        valueTextView = findViewById(R.id.value_text_view);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");

        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }


}