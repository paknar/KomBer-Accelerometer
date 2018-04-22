package com.waskito.komber.accelerometer;


import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.waskito.komber.accelerometer.R;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private TextView tv1;
    private long lastUpdate;
    private GraphView graphView1;
    private GraphView graphView2;
    private GraphView graphView3;
    private GraphViewSeries series1;
    private GraphViewSeries series2;
    private GraphViewSeries series3;
    private List<GraphView.GraphViewData> seriesX;
    private List<GraphView.GraphViewData> seriesY;
    private List<GraphView.GraphViewData> seriesZ;
    int dataCount = 1;
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;
    private Runnable mTimer3;
    private double avgX;
    private double avgY;
    private double avgZ;
    private List<Double> listX;
    private List<Double> listY;
    private List<Double> listZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //view = findViewById(R.id.textView);
        //view.setBackgroundColor(Color.GREEN);



        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        seriesX = new ArrayList<>();
        seriesY = new ArrayList<>();
        seriesZ = new ArrayList<>();

        listX = new ArrayList<>();
        listY = new ArrayList<>();
        listZ = new ArrayList<>();

        series1 = new GraphViewSeries(new GraphView.GraphViewData[] {});
        graphView1 = new LineGraphView(this, "X");
        graphView1.addSeries(series1);
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(graphView1);

        series2 = new GraphViewSeries(new GraphView.GraphViewData[] {});
        graphView2 = new LineGraphView(this, "Y");
        graphView2.addSeries(series2);
        layout = (LinearLayout) findViewById(R.id.graph2);
        layout.addView(graphView2);

        series3 = new GraphViewSeries(new GraphView.GraphViewData[] {});
        graphView3 = new LineGraphView(this, "Z");
        graphView3.addSeries(series3);
        layout = (LinearLayout) findViewById(R.id.graph3);
        layout.addView(graphView3);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        seriesX.add(new GraphView.GraphViewData(dataCount, x));
        seriesY.add(new GraphView.GraphViewData(dataCount, y));
        seriesZ.add(new GraphView.GraphViewData(dataCount, z));

        //double d = x;
        /*listX.add((double)x);
        listY.add((double)y);
        listZ.add((double)z);*/

        dataCount++;

        if (dataCount%15==0)
        {
            avgX=calculateAverage(listX);
            avgY=calculateAverage(listY);
            avgZ=calculateAverage(listZ);
           double tes = 3.0;

            String stringdouble = Double.toString(avgX);
            tv1 = findViewById(R.id.textView) ;
            tv1.setText(""+avgX);
        }

        //Warna
       /* float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            if (color) {
                view.setBackgroundColor(Color.GREEN);
            } else {
                view.setBackgroundColor(Color.RED);
            }
            color = !color;
        }*/
    }

    private double calculateAverage(List <Double> marks) {
        double sum = 0.0d;
        if(!marks.isEmpty()) {
            for (int i=0; i < marks.size(); i++) {
                sum += marks.get(i);
            }
            double avg = sum / (double)marks.size();
            return avg;
        }
        return 37.37d;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        mTimer1 = new Runnable() {
            @Override
            public void run() {
                GraphView.GraphViewData[] gvd = new GraphView.GraphViewData[seriesX.size()];
                seriesX.toArray(gvd);
                series1.resetData(gvd);
                mHandler.post(this); //, 100);
            }
        };
        mHandler.postDelayed(mTimer1, 100);

        mTimer2 = new Runnable() {
            @Override
            public void run() {

                GraphView.GraphViewData[] gvd = new GraphView.GraphViewData[seriesY.size()];
                seriesY.toArray(gvd);
                series2.resetData(gvd);

                mHandler.post(this);
            }
        };
        mHandler.postDelayed(mTimer2, 100);


        mTimer3 = new Runnable() {
            @Override
            public void run() {

                GraphView.GraphViewData[] gvd = new GraphView.GraphViewData[seriesZ.size()];
                seriesZ.toArray(gvd);
                series3.resetData(gvd);

                mHandler.post(this);
            }
        };
        mHandler.postDelayed(mTimer3, 100);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

}
