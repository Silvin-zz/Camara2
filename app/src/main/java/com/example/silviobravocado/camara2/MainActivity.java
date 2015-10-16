package com.example.silviobravocado.camara2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {

    Button      btnPicture;
    ImageView   imgPicture;
    static int TAKE_PICTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPicture = (Button) this.findViewById(R.id.btnTakePic);
        imgPicture = (ImageView) this.findViewById(R.id.picture);
        btnPicture.setOnClickListener(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "qCcFWfzcE4l0DX7syR9XSARS5EUB9s0CxVbyGmd8", "LeollugPtzuXaBfmhC93vr1pmzxaqSYa99DKt3F3");
        Log.d("MainResult", "Inicializamos Parse");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        Intent intent  = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, TAKE_PICTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        InputStream stream = null;
        if(requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK){
            Bundle extras = intent.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

            try{
                if(bitmap !=null){
                    bitmap.recycle();
                }

                stream = getContentResolver().openInputStream(intent.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                imgPicture.setImageBitmap(bitmap);



                ByteArrayOutputStream streamdos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, streamdos);
                byte[] byteArray = streamdos.toByteArray();
                final ParseFile tmpfile = new ParseFile("experiencia.png", byteArray);
                tmpfile.saveInBackground(new SaveCallback() {

                    public void done(com.parse.ParseException e) {

                        ArrayList<ParseFile> photos = new ArrayList<ParseFile>();
                        photos.add(tmpfile);

                        ParseObject testObject = new ParseObject("experience");
                        testObject.put("title"      , "Experiencia muy aca");
                        testObject.put("description", "Descripcion de experiencia muy aca");
                        testObject.put("cityname"   , "Ciudad de México");
                        testObject.put("photos"     , photos);
                        testObject.saveInBackground();



                    }

                }, new ProgressCallback() {
                    public void done(Integer percentDone) {
                        Log.d("MainResult", "Subiendo Imagen con porcentaje: " + percentDone);
                        // Update your progress spinner here. percentDone will be between 0 and 100.
                    }
                });



            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }

        }
    }
}
