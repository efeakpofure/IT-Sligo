package com.itsligo.efe.bluetoothterm2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.UUID;

public class MainActivity extends Activity {

//    Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7;
    TextView display;
    Switch loadSwitch1, loadSwitch2, loadSwitch3, loadSwitch4, allSwitch, autoSwitch;
    LinearLayout loadsGroup;
    boolean sendCodeKey = true;
    byte[] data;

    //////////////////////// START OF SECTION A TO ADD in global section ////////////////////////////////////////////////////

    // Bluetooth globals
    private static String address = "98:D3:32:F5:B3:FB";        // MAC address of HC-05                                           

    private static final String TAG = "bluetoothtx";
    // SPP UUID service - this is a unique string used for connecting - this can remain unchanged.
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    final int RECEIVE_MESSAGE = 1;        // Status  for Handler
    Handler h;          // needed for the receive thread

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    private StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;           // private class - receiving thread

    //////////////////////// END OF SECTION A TO ADD in onCreate ////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        btn0 = (Button) findViewById(R.id.btn0);
//        btn1 = (Button) findViewById(R.id.btn1);
//        btn2 = (Button) findViewById(R.id.btn2);
//        btn3 = (Button) findViewById(R.id.btn3);
//        btn4 = (Button) findViewById(R.id.btn4);
//        btn5 = (Button) findViewById(R.id.btn5);
//        btn6 = (Button) findViewById(R.id.btn6);
//        btn7 = (Button) findViewById(R.id.btn7);
        display = (TextView) findViewById(R.id.display);

        loadSwitch1 = (Switch) findViewById(R.id.loadSwitch1);
        loadSwitch2 = (Switch) findViewById(R.id.loadSwitch2);
        loadSwitch3 = (Switch) findViewById(R.id.loadSwitch3);
        loadSwitch4 = (Switch) findViewById(R.id.loadSwitch4);
        allSwitch = (Switch) findViewById(R.id.allSwitch);
        autoSwitch =  findViewById(R.id.autoSwitch);
        loadsGroup =  findViewById(R.id.loadsGroup);

        //////////////////////// START OF SECTION B TO ADD in onCreate ////////////////////////////////////////////////////

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECEIVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                            sb.delete(0, sb.length());                                      // and clear
//                            Log.d(TAG, "RX : [" + sbprint + "]");
                            display.setText(sbprint);            // update TextView
//                            display.setText("Data from PIC: " + sbprint);            // update TextView

                        }
//                        Log.d(TAG, "...RX String:" + sb.toString() + "Byte:" + msg.arg1 + "...");
//                        display.setText("RX: " + sb.toString());
                        if(strIncom.contains("h")){
                            loadSwitch1.setChecked(true);
                        } else if(strIncom.contains("l")){
                            loadSwitch1.setChecked(false);
                        }
                        else{
                            data = strIncom.getBytes(); //get the 8 bits of data from the incoming string
                        BigInteger bi = new BigInteger(data);
                        Log.d(TAG, "...RX Byte:" +  bi.intValue() + "..." +msg);
//                        display.setText("RX: " + strIncom);
                        if(msg.arg1 == 1) {
                            display.setText((bi.intValue() - 45) + "\u00B0" + "C");
                        }
                        }
                        break;
                }
            };
        };  // new handler

        // initialise the bluetooth subsystem and ensure it's enabled.
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        //////////////////////// END OF SECTION B TO ADD ////////////////////////////////////////////////////




            loadSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(sendCodeKey == true) {
                            mConnectedThread.write("2");
                        }
//                    Toast.makeText(getBaseContext(), "Heater On", Toast.LENGTH_SHORT).show();
                    } else {
                            if(sendCodeKey == true) {
                                mConnectedThread.write("3");
                            }
//                    Toast.makeText(getBaseContext(), "Heater Off", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            loadSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(sendCodeKey == true) {
                            mConnectedThread.write("4");
                        }
//                    Toast.makeText(getBaseContext(), "Bulb On", Toast.LENGTH_SHORT).show();
                    } else {
                        if(sendCodeKey == true) {
                            mConnectedThread.write("5");
                        }
//                    Toast.makeText(getBaseContext(), "Bulb Off", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            loadSwitch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(sendCodeKey == true) {
                            mConnectedThread.write("6");
                        }
//                    Toast.makeText(getBaseContext(), "Fan On", Toast.LENGTH_SHORT).show();
                    } else {
                        if(sendCodeKey == true) {
                            mConnectedThread.write("7");
                        }
//                    Toast.makeText(getBaseContext(), "Fan Off", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            loadSwitch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(sendCodeKey == true) {
                            mConnectedThread.write("8");
                        }
//                    Toast.makeText(getBaseContext(), "Radio On", Toast.LENGTH_SHORT).show();
                    } else {
                        if(sendCodeKey == true) {
                            mConnectedThread.write("9");
                        }
//                    Toast.makeText(getBaseContext(), "Radio Off", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        allSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendCodeKey = false;
                if (isChecked) {
                    mConnectedThread.write("1");
//                    Toast.makeText(getBaseContext(), "All On", Toast.LENGTH_SHORT).show();
                    loadSwitch1.setChecked(true);
                    loadSwitch2.setChecked(true);
                    loadSwitch3.setChecked(true);
                    loadSwitch4.setChecked(true);
                } else {
                    mConnectedThread.write("0");
//                    Toast.makeText(getBaseContext(), "All Off", Toast.LENGTH_SHORT).show();
                    loadSwitch1.setChecked(false);
                    loadSwitch2.setChecked(false);
                    loadSwitch3.setChecked(false);
                    loadSwitch4.setChecked(false);

                }
                // activate the key only after sending
                sendCodeKey = true;
            }
        });
        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mConnectedThread.write("a");
                    sendCodeKey = false;
                    Toast.makeText(getBaseContext(), "Auto Mode", Toast.LENGTH_SHORT).show();
                    loadsGroup.setVisibility(View.GONE);
                    allSwitch.setVisibility(View.GONE);
                } else {
                    mConnectedThread.write("m");
                    sendCodeKey = true;
                    Toast.makeText(getBaseContext(), "Manual Mode", Toast.LENGTH_SHORT).show();
                    loadsGroup.setVisibility(View.VISIBLE);
                    allSwitch.setVisibility(View.VISIBLE);
                }
            }
        });


    }


//    /*
//     * When the user presses the button, the value to send  is sent on the socket to the remote device
//     */
//    public void do0 (View view) {
//        mConnectedThread.write("2");
//        Toast.makeText(getBaseContext(), "Heater On", Toast.LENGTH_SHORT).show();
//    }
//
//    public void do1 (View view) {
//        mConnectedThread.write("3");
//        Toast.makeText(getBaseContext(), "Heater Off", Toast.LENGTH_SHORT).show();
//    }
//
//    public void do2 (View view) {
//        mConnectedThread.write("4");
//        Toast.makeText(getBaseContext(), "Bulb On", Toast.LENGTH_SHORT).show();
//    }
//
//    public void do3 (View view) {
//        mConnectedThread.write("5");
//        Toast.makeText(getBaseContext(), "Bulb Off", Toast.LENGTH_SHORT).show();
//    }
//
//    public void do4 (View view) {
//        mConnectedThread.write("6");
//        Toast.makeText(getBaseContext(), "Fan On", Toast.LENGTH_SHORT).show();
//    }
//
//    public void do5 (View view) {
//        mConnectedThread.write("7");
//        Toast.makeText(getBaseContext(), "Fan Off", Toast.LENGTH_SHORT).show();
//    }
//
//    public void do6 (View view) {
//        mConnectedThread.write("8");
//        Toast.makeText(getBaseContext(), "Radio On", Toast.LENGTH_SHORT).show();
//    }
//
//    public void do7 (View view) {
//        mConnectedThread.write("9");
//        Toast.makeText(getBaseContext(), "Radio Off", Toast.LENGTH_SHORT).show();
//    }
//    public void do8 (View view) {
//        mConnectedThread.write("1");
//        Toast.makeText(getBaseContext(), "All On", Toast.LENGTH_SHORT).show();
//    }
//    public void do9 (View view) {
//        mConnectedThread.write("0");
//        Toast.makeText(getBaseContext(), "All Off", Toast.LENGTH_SHORT).show();
//    }
//
//
//
//
    // NOTE
//    public void doSend (View view) {
//    }

    //////////////////////// START OF SECTION C TO ADD ////////////////////////////////////////////////////

    /*
     * Check if bluetooth is enabled on this device
     */
    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    /*
     * This method creates a Bluetooth socket which allows this program to communicate to
     * a remote device.
     */
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        Log.d(TAG, "...createBluetoothSocket - create the socket...");
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        // this method creates a socket to the remote device for reading and writing
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    /*
     * onResume() gets called when the app starts to run on the phone
     * and when the app is reawakened from sleeping.
     * It's goal is to successfully connect to the second device.
     *
     * It will attempt to connect to the remote paired device using a socket with
     * the MAC address specified.
     */
    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        // this gets the remote device from the paired list, using the MAC address specified.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting to remote device...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // start read / write of data thread - we are now connected.
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        // Send signal to the pic
        mConnectedThread.write("b");

    }

    /*
     * Called when app is stopped or paused.
     */
    @Override
    public void onPause() {
        mConnectedThread.write("c");
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            Log.d(TAG, "...closing socket...");
            btSocket.close();           // close the socket
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    /*
     * Error hander if error occurs in a try/catch()
     */
    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    /*
     * This thread handles all transmit and receiving of data on a connected socket.
     */
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECEIVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }

    //////////////////////// END OF SECTION C TO ADD ////////////////////////////////////////////////////

}
