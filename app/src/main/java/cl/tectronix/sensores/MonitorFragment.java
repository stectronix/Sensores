package cl.tectronix.sensores;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.juang.jplot.PlotPlanitoXY;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MonitorFragment extends Fragment {

    private static final String TAG = "fragment_monitor";
    private LinearLayout graphic;
    private PlotPlanitoXY plot;
    private AppCompatSpinner cmbRange;
    private AppCompatButton btnPlay,btnGenerate,btnImport;
    private String[] xValues = new String[6];
    private float[] x,y;

    public MonitorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor,container,false);

        graphic = view.findViewById(R.id.graphic);
        cmbRange = view.findViewById(R.id.cmbRange);
        btnPlay = view.findViewById(R.id.btnPlay);
        btnGenerate = view.findViewById(R.id.btnGenerate);
        btnImport = view.findViewById(R.id.btnImport);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.cmbRange,android.R.layout.simple_spinner_item);

        cmbRange.setAdapter(adapter);

        cmbRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (adapterView.getItemAtPosition(position).toString()){
                    case "1 Segundo":
                        for (int i = 0; i <= 5; i++ ){
                            xValues[i] = i + "Seg";
                            Log.i(TAG,xValues[i]);
                        }
                        break;
                    case "10 Segundos":
                        for (int i = 0; i <= 5; i++){
                            xValues[i] = (i * 10) + "Seg";
                            Log.i(TAG,xValues[i]);
                        }
                        break;
                    case "30 Segundos":
                        for (int i = 0; i <= 5; i++ ){
                            xValues[i] = (i * 30) + "Seg";
                            Log.i(TAG,xValues[i]);
                        }
                        break;
                    case "1 Minuto":
                        for (int i = 0; i <= 5; i++ ){
                            xValues[i] = i + "Min";
                            Log.i(TAG,xValues[i]);
                        }
                        break;
                    case "5 Minutos":
                        for (int i = 0; i <= 5; i++ ){
                            xValues[i] = (i * 5) + "Min";
                            Log.i(TAG,xValues[i]);
                        }
                        break;
                    case "15 Minutos":
                        for (int i = 0; i <= 5; i++ ){
                            xValues[i] = (i * 15) + "Min";
                            Log.i(TAG,xValues[i]);
                        }
                        break;
                    case "30 Minutos":
                        for (int i = 0; i <= 5; i++ ){
                            xValues[i] = (i * 30) + "Min";
                            Log.i(TAG,xValues[i]);
                        }
                        break;
                    case "1 Hora":
                        for (int i = 0; i <= 5; i++ ){
                            xValues[i] = i + "Hr";
                            Log.i(TAG,xValues[i]);
                        }
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        x = new float[6]; y = new float[6];
        x[0] = 4.8f; y[0] = 2.5f;
        x[1] = 1.3f; y[1] = 2.7f;
        x[2] = 5.6f; y[2] = 7.2f;
        x[3] = 1.9f; y[3] = 8.8f;
        x[4] = 0.8f; y[4] = 6.5f;
        x[5] = 8.4f; y[5] = 3.1f;

        plot = new PlotPlanitoXY(getContext(),"Sensor","Tiempo","Medida");
        plot.SetSerie1(x,y,"Sensor",5,true);
        plot.SetHD(true);
        plot.SetTouch(true);
        graphic.addView(plot);

        return view;
    }

    private void showToastOnMainThread(final String msg) {
        // Escribimos también en el log para tener la información duplicada
        Log.i(TAG, msg);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
