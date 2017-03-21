package com.headsupseven.corp.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Prosanto on 2/12/17.
 */

public class MultipartUtility {

    String newline = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    HttpURLConnection httpUrlConnection = null;
    DataOutputStream request;

    public MultipartUtility(String requestURL)
            throws IOException {

        URL url = new URL(requestURL);
        httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setUseCaches(false);
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
        httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
        httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        //===========================================
        request = new DataOutputStream(httpUrlConnection.getOutputStream());


    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) throws IOException {

        request.writeBytes(twoHyphens + boundary + newline); //    v (key here)
        request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + newline + newline);
        request.writeBytes(value);
        request.writeBytes(newline);

    }

    /**
     * Adds a upload file section to the request
     *
     * @param
     * @throws IOException
     */
    public void addFilePart(String name, String file_path)
            throws IOException {

        String[] pathSplits = file_path.split("/");
        String fileName = pathSplits[pathSplits.length - 1];
        int maxBufferSize = 1 * 1024 * 1024; // read buffer

        request.writeBytes(twoHyphens + boundary + newline); //    v (key here)
        request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"" + newline);
        request.writeBytes("Content-Type: " + newline); // set content type here ?
        request.writeBytes(newline);
        //-----------------------------------------
        // get file data
        //-----------------------------------------
        FileInputStream fileInput = new FileInputStream(new File(file_path));
        int bytesAvailable = fileInput.available();
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        //should check bytesAvailable > maxBufferSize here to know file is too big for upload
        byte[] buffer = new byte[bufferSize];
        int byteRead = fileInput.read(buffer, 0, bufferSize);
        while (byteRead > 0) {
            request.write(buffer, 0, bufferSize);
            bytesAvailable = fileInput.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byteRead = fileInput.read(buffer, 0, bufferSize);
        }
        fileInput.close();
        request.writeBytes(newline);


    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
//        writer.append(name + ": " + value).append(LINE_FEED);
//        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() throws IOException {
        request.writeBytes(twoHyphens + boundary + twoHyphens + newline);
        //-----------------------------------------
        request.flush();
        request.close();
        // get the data result
        int responseCode = httpUrlConnection.getResponseCode();
        String responseBody = "";
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 307) {
            BufferedReader br = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                responseBody += line;
            }
        }
        return responseBody.toString();
    }
}

