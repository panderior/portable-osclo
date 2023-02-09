package com.example.arduino_scope;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

    private com.github.mikephil.charting.charts.LineChart mchart;

    private static int ON_OFF=0;

    Button connect,screen_shot_btn,draw_btn,howto;
    public EditText url_txt;
//    public EditText data;

    public float[] signal_value = new float[15];

    public static String result = "";
    public static  URL url = null;

    public static HttpURLConnection connection = null;





    ArrayList<Entry> values = new ArrayList<>();
    LineDataSet set1;
    LineData line_data;//note if there is only one line data set

    public MainActivity() throws UnsupportedEncodingException {

    }

    // note multiple ArrayList<ILineDataSet> LineDataSets = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            connect = (Button) findViewById(R.id.button);
            howto = (Button) findViewById(R.id.howto);
            screen_shot_btn = (Button)findViewById(R.id.button2);
            draw_btn = (Button)findViewById(R.id.draw_btn);


            url_txt = (EditText) findViewById(R.id.url_txt);
            mchart =(LineChart)findViewById(R.id.linechart);


            mchart.setOnChartGestureListener(MainActivity.this);
            mchart.setOnChartValueSelectedListener(MainActivity.this);

            mchart.setDragEnabled(true);

            mchart.setPinchZoom(true);


        mchart.getAxisLeft().setAxisMaxValue(5f);
        mchart.getAxisLeft().setAxisMinValue(-5f);



            for ( int i = 0; i<15; i++ )


                signal_value[i] = 0;

            if (!values.isEmpty())
            {
                values.clear();
            }

            for (int i =0; i < signal_value.length; i++)
            {

                values.add(new Entry(i, 0));
            }

            if( !mchart.isEmpty() )
                mchart.clear();

            draw_signal(values);

            connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(url_txt.getText().toString().equals(""))
                        Toast.makeText(MainActivity.this, "Enter the Source Url!", Toast.LENGTH_SHORT).show();

                    else
                    {

                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
                            //after this, it goes to onRequestPermissionsResult(){}
                        }
                        else
                            Toast.makeText(MainActivity.this, "Permission Denied!!!", Toast.LENGTH_SHORT).show();
                    }

                }
            });

howto.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, How_to.class);
        startActivity(intent);
    }
});

        screen_shot_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Date date = new Date();
                    CharSequence now = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss",date);

                    Random random = new Random();
                    String filename = Environment.getExternalStorageDirectory()+"/Adrdunio_Scope_Pictures"+now+random.nextInt(10)+""+random.nextInt(10000)+".JPG";
                    View root = getWindow().getDecorView();
                    root.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(root.getDrawingCache());
                    root.setDrawingCacheEnabled(false);

                    File file = new File(filename);
                    file.getParentFile().mkdirs();

                    try{
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        Uri uri = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri,"image/*");
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    // sinusoidal();
                        Toast.makeText(MainActivity.this, "screen shot", Toast.LENGTH_SHORT).show();


                }
            });


            draw_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
            for (int k= 0 ;k<4;k++)
               recursive_parser();
                }
            });

        }

        private void recursive_parser()
        {
            try {

                connection = (HttpURLConnection) url.openConnection();

                draw();

                Background Background = new Background(MainActivity.this);

                Background.execute(connection);



            }

            catch (Exception e) {
                Toast.makeText(MainActivity.this, "Connection Lost!" + e.toString(), Toast.LENGTH_SHORT).show();
            }

        }

        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == 1) {
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
               {
                    String source_url = url_txt.getText().toString();

                   try {

                       Toast.makeText(this, "Connecting... Please wait", Toast.LENGTH_SHORT).show();

                       url = new URL(source_url);
                       connection = (HttpURLConnection) url.openConnection();
                   }

                  catch (Exception e) {
                      Toast.makeText(this, "There was a problem with http connecting", Toast.LENGTH_SHORT).show();
                   }

                   try{
                      //to be looped
                       Background Background =new Background(MainActivity.this);
                       Background.execute(connection);
                       Toast.makeText(this, "Connection Created", Toast.LENGTH_SHORT).show();
                       //need some time delay until result is filled with data

                   }
                   catch(Exception e)
                   {
                       Toast.makeText(this, "There was a problem with parsing data!", Toast.LENGTH_SHORT).show();
                   }


                }

            }
            else
                Toast.makeText(this, "2nd permission denied!", Toast.LENGTH_SHORT).show();


        }

    private void sinusoidal()
    {

        float[] array= new float[360];
        ArrayList<Entry> sin_values = new ArrayList<>();

        for ( int i = 0; i<360; i++ )
        {
            array[i] = (float) Math.sin( Math.toRadians(i));
        }

        for (int i =0; i < array.length; i++)
        {

            sin_values.add(new Entry(i, array[i]));
        }

        if( !mchart.isEmpty() )
            mchart.clear();

        draw_signal(sin_values);

    }


    private void draw()
    {

       // data.setText("");
        //Toast.makeText(this, "Draw Data ="+Constant.result, Toast.LENGTH_SHORT).show();

        if (!values.isEmpty())
        {
           // data.append("values has data");
            values.clear();
        }

        parse();//puts values in signal_value

        for (int i =0; i < signal_value.length; i++)
        {

            values.add(new Entry(i, signal_value[i]));
        }

            if(!mchart.isEmpty())
                mchart.clear();

        draw_signal(values);
    }



        private void parse()
        {


            if(Constant.result=="")

            {
                Toast.makeText(this, "no result", Toast.LENGTH_SHORT).show();

            }

                // JSONObject jo = new JSONObject(result);

                else{


                    try{
                            JSONArray ja= new JSONArray(Constant.result);
                            // ja = jo.getJSONArray("0");


                            signal_value = new float[ja.length()];
                            for (int i = 0; i < ja.length(); i++)
                            {
                                signal_value[i] = (ja.getInt(i)*5)/1024;
                            }
                    }
                    catch (Exception e) {
                        Toast.makeText(this, "Connection Lost!", Toast.LENGTH_SHORT).show();

                    }

            }


        }


        private void draw_signal(ArrayList<Entry> value)
        {


            set1 = new LineDataSet(value, "");
            //note
            set1.setDrawCircles(false);
         //   set1.setAxisDependency(YAxis.AxisDependency.LEFT);


            set1.setColor(Color.RED);

            set1.setDrawValues(false);

            line_data = new LineData(set1);

            mchart.setData(line_data);


        }






    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
