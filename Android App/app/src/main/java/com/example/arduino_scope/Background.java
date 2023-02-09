package com.example.arduino_scope;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

class Background extends AsyncTask<HttpURLConnection,Void, String> {

    private Context context;
   public MainActivity obj;
   public Constant constant= new Constant();


    public Background(Context context) throws UnsupportedEncodingException {
        this.context=context;
        try {
            obj = new MainActivity();

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(context, "exception occured while creating obj in background class!", Toast.LENGTH_SHORT).show();;
        }
    }

//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        //note cut this code from here and put it in the connect btn setOnClickListner method
//        pd=new ProgressDialog(context);
//        pd.setTitle("Connecting ...");
//        pd.setMessage("Parsing...Please wait");
//        pd.show();
//    }

    @Override
    protected String doInBackground(HttpURLConnection... urls) {

        String result = " ";
        HttpURLConnection connection = urls[0];


        try {

            //note try to use url connection once and multiple times data parsing with it
            
//            url = new URL(requesturl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);

            String line;

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = br.readLine()) != null) {
                result += line;
            }

            br.close();


        }
        catch (Exception e)
        {
            result = "error"+e.toString();

        }


        return result;
    }



    @Override
    protected void onPostExecute(String result) {

//        result = result.trim();

        if(result.equals(""))
        {

            Toast.makeText(context, "No Data Recieved!!", Toast.LENGTH_SHORT).show();

        }

        else{
           // Toast.makeText(context, "Data Recieved = "+result, Toast.LENGTH_SHORT).show();
            constant.result = result;
        }
    }



}
