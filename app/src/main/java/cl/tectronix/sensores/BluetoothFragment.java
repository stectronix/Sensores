package cl.tectronix.sensores;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import cl.tectronix.sensores.adapter.DeviceListAdapter;

import static android.app.Activity.RESULT_OK;

public class BluetoothFragment extends Fragment {

    private DeviceListAdapter deviceListAdapter;
    private static final String TAG = "SENSORES";
    private static final int REQUEST_ENABLE_BT = 100;
    private static final int PERMISSIONS_REQUEST_BLUETOOTH = 102;
    private AppCompatButton btnScan,btnDisconnect;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    private ListView lstDevices;
    private static final int STATE_LISTENNING = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_FAILED = 4;
    private static final int STATE_MESSAGE_RECEIVED = 5;
    private int REQUEST_ENABLE_BLUETOOTH = 1;
    private int requestCodeForEnable = 1;

    public BluetoothFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bluetooth,container,false);

        findviewByID(view);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        implementListener();

        return view;
    }

    private void implementListener() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Esta es la forma de preguntarle al sistema operativo si tiene soporte para BluetoothLE
                if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    // Mostramos un mensaje que no es soportado
                    Toast.makeText(getActivity(), "ALERTA: Su dispositivo no soporta BluetoothLE!", Toast.LENGTH_SHORT).show();
                } else {
                    // Preguntamos si tenemos el permiso correcto
                    int permissionBluetooth = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH);
                    int permissionCoarseLocation = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
                    int permissionBluetoothAdmin = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_ADMIN);

                    // El resultado es PERMISSION_GRANTED (si está), o DENIED si no
                    if (permissionBluetooth == PackageManager.PERMISSION_GRANTED &&
                            permissionBluetoothAdmin == PackageManager.PERMISSION_GRANTED &&
                            permissionCoarseLocation == PackageManager.PERMISSION_GRANTED) {
                        // Estamos listos para usar bluetooth...
                        if (!bluetoothAdapter.isEnabled()) {
                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableIntent, requestCodeForEnable);
                        }else
                            scanDevices();
                    }else
                        // Pedimos al usuario si nos permite usar BLUETOOTH (el permiso)
                        requestPermissions();
                }
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Desconectamos
                if (bluetoothGatt != null) {
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                }
                // Volvemos al inicio en el estado de los botones
                btnDisconnect.setEnabled(false);
            }
        });

        lstDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Log.i(TAG, "Se hizo click en la lista en " + index + " y _devices tiene " + btDevices.size() + " dispositivos");
                connectDeviceIndex(index);
            }
        });
    }

    private void connectDeviceIndex(int index) {

        if (btDevices != null && btDevices.size() >= index + 1) {
            final BluetoothDevice device = btDevices.get(index);

            Toast.makeText(getActivity(), "Conectando a dispositivo " + device.getName() + "...", Toast.LENGTH_SHORT).show();
            bluetoothGatt = device.connectGatt(getActivity(), false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        showToastOnMainThread("Se conectó a " + device.getName());

                        // Reiniciamos el array de caracteristicas para listar el del nuevo dispositivo
                        /*if (_caracteristicas == null)
                            _caracteristicas = new ArrayList<BluetoothGattCharacteristic>();
                        else
                            _caracteristicas.clear();*/

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnDisconnect.setEnabled(true);
                            }
                        });

                        bluetoothGatt.discoverServices();

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        showToastOnMainThread("Se desconectó del dispositivo Bluetooth");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnDisconnect.setEnabled(false);
                            }
                        });
                    }
                }
            });
        }
    }

    private void findviewByID(View view) {
        btnScan = view.findViewById(R.id.btnScan);
        btnDisconnect = view.findViewById(R.id.btnDisconnect);
        lstDevices = view.findViewById(R.id.lstDevices);
    }


    private void scanDevices() {
        btDevices.clear();
        if (bluetoothAdapter.isDiscovering()){
            Toast.makeText(getActivity(),"Busqueda cancelada . . . ",Toast.LENGTH_LONG).show();
            btnScan.setText(R.string.btnTxtScan);
            bluetoothAdapter.cancelDiscovery();
        }else {
            btnScan.setText("Detener");
            Toast.makeText(getActivity(),"Escaneando dispositivos . . . ",Toast.LENGTH_LONG).show();
            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(broadcastReceiver,discoverDevicesIntent);
            btnScan.setText(R.string.btnTxtScan);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!btDevices.contains(device)) {
                    btDevices.add(device);
                    deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, btDevices);
                    lstDevices.setAdapter(deviceListAdapter);
                }
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSIONS_REQUEST_BLUETOOTH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestCodeForEnable) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "Bluetooth disponible", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(getActivity(), "Permiso denegado, debe conectar Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    private void showToastOnMainThread(final String msg) {
        Log.i(TAG, msg);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
