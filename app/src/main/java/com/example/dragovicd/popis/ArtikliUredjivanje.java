package com.example.dragovicd.popis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.dragovicd.popis.bluetoothConnect.BluetoothConnectActivity;
import com.example.dragovicd.popis.entity.OsnovnoSredstvo;
import com.example.dragovicd.popis.entity.OsnovnoSredstvo;

public class ArtikliUredjivanje extends AppCompatActivity {
DatabaseHelper db;

    EditText sifraArtikla;
    EditText nazivArtikla;
    EditText lokacijaArtikla;
    EditText napomenaArtikla;
    EditText statusArtikla;

    Bundle bundle;
    String newString;
    OsnovnoSredstvo artikal;
    String gotoNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artikal_uredjivanje);
        db = new DatabaseHelper(this);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Artikal uređivanje");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        nazivArtikla = (EditText) findViewById(R.id.eteNazivArtikla);
        lokacijaArtikla = (EditText) findViewById(R.id.eteLokacijaArtikla);
        napomenaArtikla = (EditText) findViewById(R.id.eteNapomenaArtikla);
        statusArtikla = (EditText) findViewById(R.id.eteStatusArtikla);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();

        gotoNext =  extras.getString("NEXT_ACTIVITY");



        if (savedInstanceState == null) {

            if(extras == null) {
                newString= null;
            } else {
                newString = extras.getString("STRING_I_NEED");
                //artikal = (Artikal) db.getArtikalBySifra(newString);
                artikal = (OsnovnoSredstvo) db.getArtikalBySifra(newString);

                nazivArtikla.setText(artikal.getNaziv().toString());
                lokacijaArtikla.setText(artikal.getLokacija().toString());
                if(!(artikal.getNapomena().toString()).equals("0")){
                    napomenaArtikla.setText(artikal.getNapomena().toString());
                }
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }


        //Toast.makeText(this, newString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToArtikliLista(View v) {

        if(gotoNext.equals("2")){
            Intent intent = new Intent(ArtikliUredjivanje.this,  BluetoothConnectActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else if (gotoNext.equals("3")){
           Intent intent = new Intent(ArtikliUredjivanje.this,  ArtikliActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


    }

    public void updateArtikal(View v) {

        artikal.setNaziv((nazivArtikla.getText()).toString());
        artikal.setLokacija((lokacijaArtikla.getText()).toString());
        artikal.setNapomena((napomenaArtikla.getText()).toString());
       //artikal.setNapomena((statusArtikla.getText()).toString());

        db.updateArtikal(artikal);
        goToArtikliLista(v);
    }
}