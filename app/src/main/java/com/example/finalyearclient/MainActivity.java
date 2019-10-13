package com.example.finalyearclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    //host
    RSAReciever rsa;
    String[] algorithmNames;
    String name,password, status;
    DataBase dataBase;
    TextView BloodPressure, HeartRate, Respiration, Temperature, BloodOxygen, Connected;
    Button viewRecord;
    public static final int ServerPort = 5000;
    String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BloodPressure = findViewById(R.id.bloodValue);
        HeartRate = findViewById(R.id.heartValue);
        Respiration = findViewById(R.id.respirationValue);
        Temperature = findViewById(R.id.temperatureValue);
        BloodOxygen = findViewById(R.id.bloodOxyValue);
        Connected = findViewById(R.id.connectedValue);
        viewRecord = findViewById(R.id.viewRecords);

        viewRecord.setVisibility(View.INVISIBLE);

        rsa = new RSAReciever();

        algorithmNames = new String[]{"Blowfish", "AES", "RC4"};

        Thread myThread = new Thread(new MyServer());
        myThread.start();


    }

    //need to make invis if name is null
    public void viewRecords(View v){
        Intent i =  new Intent(getApplicationContext(), ViewRecords.class);
        i.putExtra("name",name);
        i.putExtra("password",password);
        i.putExtra("version",version);
        startActivity(i);
    }

    class MyServer implements Runnable{
        ServerSocket ss;
        Socket mysocket;
        DataInputStream dis;
        String message;
        Handler handler = new Handler();
        boolean connected;

        @Override
        public void run(){
            try{
                ss = new ServerSocket(ServerPort);
                handler.post(new Runnable(){
                    @Override
                    public void run(){
                        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                            //we are connected to a network
                            connected = true;
                        }
                        else connected = false;
                        if (connected) {
                            status = "Waiting for client";
                        }
                        else {
                            status = "no internet connection";
                        }
                        Connected.setText(status);
                    }
                });
                while(true){
                    mysocket = ss.accept();
                    dis = new DataInputStream(mysocket.getInputStream());
                    message = dis.readUTF();
                    long recieved = System.currentTimeMillis();
                    Log.e("Recieved",recieved+"") ;

                    handler.post(new Runnable(){
                        @Override
                        public void run(){
                            long start = System.currentTimeMillis();

                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            String status = "Connected";
                            Connected.setText(status);
                            //long startRSA = System.currentTimeMillis();
                            long startRSA = System.nanoTime();
                            String decrypted = rsa.decrypt(message);
                            //long finishRSA = System.currentTimeMillis();
                            long finishRSA = System.nanoTime();
                            long totalRSA = finishRSA - startRSA;
                            Log.e("totalRSA(2203 & 1279)",totalRSA+"") ;


                            //Log.e("decrypted", decrypted);
                            String[] decryptedArray = decrypted.split(",");
                            String secretKeyString = decryptedArray[0];
                            int algorithmNumber = Integer.parseInt(decryptedArray[1]);
                            String ToDecryptMessage = decryptedArray[2];
                            String algorithmChosen = algorithmNames[algorithmNumber];
                            byte[] encodedKey = Base64.decode(secretKeyString, Base64.DEFAULT);
                            SecretKey secretKey = new SecretKeySpec(encodedKey,0,encodedKey.length,algorithmChosen);
                            try {
                                Algorithm algorithm = new Algorithm(algorithmChosen);

                                long startDecrypt = System.nanoTime();
                                String DecryptedMessage = algorithm.decrypt(ToDecryptMessage,secretKey);
                                long finishDecrypt = System.nanoTime();
                                long totalDecrypt = finishDecrypt - startDecrypt;
                                Log.e("totalDecrypt(Blowfish)",totalDecrypt+"") ;


                                String[] StrDecryptedArray = DecryptedMessage.split(" ");
                                BloodPressure.setText(StrDecryptedArray[0]);
                                HeartRate.setText(StrDecryptedArray[1]);
                                BloodOxygen.setText(StrDecryptedArray[2]);
                                Respiration.setText(StrDecryptedArray[3]);
                                if(StrDecryptedArray.length == 7) {
                                    Temperature.setText(StrDecryptedArray[6]);
                                }
                                else{
                                    Temperature.setText("n/a");
                                }
                                String Strpatient = StrDecryptedArray[4];
                                String[] StrpatientArray = Strpatient.split(",");
                                //Log.e("Strpatient", StrDecryptedArray[4]);
                                dataBase = new DataBase(getApplicationContext(),StrpatientArray[0],Integer.parseInt(StrDecryptedArray[5]),StrpatientArray[1]);
                                version = StrDecryptedArray[5];
                                name = StrpatientArray[0];
                                password = StrpatientArray[1];
                                viewRecord.setVisibility(View.VISIBLE);
                                Pateint pateint = new Pateint(StrpatientArray[0],StrpatientArray[1],StrpatientArray[2],StrpatientArray[3],
                                        StrpatientArray[4],StrpatientArray[5],StrpatientArray[6],StrpatientArray[7],StrpatientArray[8],
                                        StrpatientArray[9]);

                                dataBase.addInstance(pateint);

                                long finish = System.currentTimeMillis();

                                long total = finish - start;

                                Log.e("total",total+"") ;

                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
