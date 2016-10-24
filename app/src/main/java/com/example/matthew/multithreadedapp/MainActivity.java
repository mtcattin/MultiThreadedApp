package com.example.matthew.multithreadedapp;


import android.content.Context;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int READ_BLOCK_SIZE = 100;
    String MY_FILE_NAME;
    Button createButton;
    Button loadButton;
    Button clearButton;
    List<String> total;
    ListView totalView;
    ArrayAdapter<String> arrayAdapter;
    Thread thread1;
    Thread thread2;

    Button btnStartProgress;
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();

    public int fileSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createButton = (Button) findViewById(R.id.createBut);
        loadButton = (Button) findViewById(R.id.loadBut);
        clearButton = (Button) findViewById(R.id.clearBut);

        // set the listview to findby ID  of the list view
        totalView = (ListView)findViewById(R.id.listview);
        total = new ArrayList<String>();
        // setting the Array list on the list view (translates)
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,total);
        // set list view to arrayAdapter
        totalView.setAdapter(arrayAdapter);

        // deal with clicks here NOT xmL
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                // set up a background thread to manipulate the GUI
                // prepare for a progress bar dialog
                progressBar = new ProgressDialog(v.getContext());
                progressBar.setCancelable(true);
                progressBar.setMessage("Downloading necessary file ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setProgress(0);
                progressBar.setMax(100);
                progressBar.show();

                //reset progress bar status
                progressBarStatus = 0;

                //reset filesize
                fileSize = 0;
                ////////////////////////////////
                thread1 = new Thread(new Runnable() {
                    public void run() {
                        while (progressBarStatus < 100) {
                            // process some tasks
                            progressBarStatus = createFile(v);
                            // While the progressBarStatus is running too fast, pause for 1 second
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // Update the progress bar
                            progressBarHandler.post(new Runnable() {
                                public void run() {
                                    progressBar.setProgress(progressBarStatus);
                                }
                            });
                        }

                        // If progressBarStatus is >= 100 sleep for 2 seconds
                        if (progressBarStatus >= 100) {

                        }
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // Once the progressBarStatus is fully loaded, dismiss progressBar
                            progressBar.dismiss();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                arrayAdapter.notifyDataSetChanged();
                            }
                        });
                }
            });
            try {
                thread1.start();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(getBaseContext(), "You've successfully saved the file", Toast.LENGTH_SHORT).show();

        }
    });

        loadButton.setOnClickListener(new View.OnClickListener() {
            // Access View from final
            public void onClick(final View v) {
            // Prepare progress bar dialog
            progressBar = new ProgressDialog(v.getContext());
            progressBar.setCancelable(true);
            progressBar.setMessage("Downloading necessary file...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();

            //Reset progressBarStatus to default
            progressBarStatus = 0;

            //Reset fileSize to default
            fileSize = 0;
            ////////////////////////////////
            thread2 = new Thread(new Runnable() {
                public void run() {
                    while (progressBarStatus < 100) {
                        // While progressBarStatus runs tasks
                        progressBarStatus = Load(v);
                        // While progressBarStatus loads too fast, sleep for 2 seconds
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Update the progress bar
                        progressBarHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressBarStatus);
                            }
                        });
                    }

                    // If file is downloaded
                    if (progressBarStatus >= 100) {

                        // Sleep for 2 seconds in order to see progressBarStatus reach 100%
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // close the progress bar dialog
                        progressBar.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            thread2.start();
        }
    });


    // clear the data once the clear button is selected
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (arrayAdapter != null) {
                    arrayAdapter.clear();
                    arrayAdapter.notifyDataSetChanged();
                } // end if
        }// end onClick
    });



}




    public int createFile(View v) {
        // create file in internal storage area
        MY_FILE_NAME = "numbers.txt";

        // change this content to be the numbers from the counting class in the group assignment
        String content;
        // Create a new output file stream
        try {
            FileOutputStream fileos = openFileOutput(MY_FILE_NAME, Context.MODE_PRIVATE);

            for(int fileSize= 0; fileSize <= 10; fileSize++) {
                // cast in as a string
                content = Integer.toString(fileSize);
                // write the content to the file
                fileos.write(content.getBytes());
                // this creates a new line
                fileos.write(System.getProperty("line.separator").getBytes());
                System.out.println(fileSize);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setProgress(fileSize*10);
                fileSize++;
            }
            // close the file
            fileos.close();
            //Display the file saved

            //IOexception catches every thing!!!
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 100;
    }// end createFile

    // Read text from file
    public int ReadBtn(View v) {
        String content;
        //reading text from file
        try {
            // open the file
            FileInputStream fileIn = openFileInput("numbers.txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);
            BufferedReader bufferedReader = new BufferedReader(InputRead);

            String line;
            // setting the line
            while ((line = bufferedReader.readLine()) !=null) {
                total.add(line);
                System.out.println(fileSize);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setProgress(fileSize*10);
                fileSize++;

            }
            // always need to close the read
            InputRead.close();

        } catch (Exception e) {

        }
        return 100;
    }// end ReadBtn
} // end of class
//
//http://codetheory.in/android-saving-files-on-internal-and-external-storage/
// reading and writing to a file
