package org.wikipedia.imagesearch;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mnhn3 on 2018-04-09.
 */

public class ImageLabeler extends AsyncTask<String, Void, String> {
    private final String KEY;
    private final String PATH;
    private String imageURL;


    public ImageLabeler() {
        KEY = "Key cf037dad49144c56b882dec39ef8a832";
        PATH = "https://api.clarifai.com/v2/models/aaa03c23b3724a16a56b629203edc62c/outputs";
    }

    // calls to the API are made within this method
    @Override
    protected String doInBackground(String... strings) {//change to take strings[0] as imageURL
        imageURL = strings[0];
        BufferedReader reader = null;
        String response;
        String result = "";

        //create JSON request, inputs
        JSONObject url = new JSONObject();
        JSONObject image = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray inputsArr = new JSONArray();
        JSONObject inputs = new JSONObject();
        try {
            inputs.put("inputs",  inputsArr);
                inputsArr.put(data);
                    data.put("data", image);
                        image.put("image", url);
                            url.put("url", imageURL);
        } catch(JSONException e){
            e.printStackTrace();
        }

        try {
            URL httpurl = new URL(PATH);
            HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Key cf037dad49144c56b882dec39ef8a832");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setUseCaches(false);
            //add json as requestbody
            OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
            wr.write(inputs.toString());

            int statusCode = conn.getResponseCode();
            if (statusCode ==  10000) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));//inputstreamreader?
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                response = sb.toString();
                JSONObject jsonObject = new JSONObject(response);

                // result = jsonObject.getJSONArray("text").getString(0);
                JSONArray outputs = jsonObject.getJSONArray("outputs");
                JSONArray concepts = outputs.getJSONObject(0).getJSONObject("data").getJSONArray("concepts");

                for (int i = 0; i < concepts.length(); i++) {//parses through json for concepts with high correlation
                    if(i==5){break;}//max 5 results
                    JSONObject concept = concepts.getJSONObject(i);
                    double value = concept.getDouble("value");
                    String name = concept.getString("name");
                    if (value>0.9){
                        result+=name+", ";
                    }
                }
            }

        } catch (Exception e) {
            return  "-1";
        }

        return result;
    }

    // Grabs the result from doInBackground and returns it to the caller
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
