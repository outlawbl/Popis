package com.example.dragovicd.popis;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dragovicd.popis.entity.OsnovnoSredstvo;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.ArrayList;
import java.util.List;


public class ArtikliActivity extends AppCompatActivity  {


    private ArrayAdapter<OsnovnoSredstvo> adapter;
    private TextView data;
    private DatabaseHelper db;
    private MaterialSearchView searchView;
    private ListView listView;
    private List<OsnovnoSredstvo> artikli;
    private List<OsnovnoSredstvo> artikliResults;
    private List<OsnovnoSredstvo> artikliResultsFilter;
    private Dialog myDialog;
    private String filter_text;
    private EditText eteFlter;
    private int result;
    private int filtered;
    private String message = "";
    private String sifraArtiklaBk = "";
    private String sifraArtikla = "";
    private String sifLokacije = "";
    private String nazivArtikla = "";
    private String nextActivity="3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artikli_svi_lista);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Artikli");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        filtered = 0;
        db = new DatabaseHelper(ArtikliActivity.this);
        artikli = readDatabaseAllArtikli();
        myDialog = new Dialog(this);
        listView = (ListView)findViewById(R.id.listView);
        adapter = new ArtikliAdapter(ArtikliActivity.this, R.layout.item_layout, artikli, nextActivity);
        listView.setAdapter(adapter);
        searchView = (MaterialSearchView)findViewById(R.id.search_view);
        artikliResults = new ArrayList<OsnovnoSredstvo>();
        artikliResultsFilter = new ArrayList<OsnovnoSredstvo>();
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                //If closed Search View , lstView will return default
                //creating the adapter object
                //artikli = readDatabaseAllArtikli();
                if(filtered == 0){
                    adapter = new ArtikliAdapter(ArtikliActivity.this, R.layout.item_layout, artikli, nextActivity);
                }else{
                    adapter = new ArtikliAdapter(ArtikliActivity.this, R.layout.item_layout, artikliResultsFilter, nextActivity);
                }
                //adding the adapter to listview
                listView.setAdapter(adapter);
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String newText) {
                if(newText != null && !newText.isEmpty()){
                    sifraArtiklaBk =newText;
                    result = 0;
                    for(OsnovnoSredstvo item:artikli){
                        // Toast.makeText(context, item.getSifra()+"/"+sifraArtiklaBk, Toast.LENGTH_SHORT).show();

                        if((item.getLokacija()).equals(sifraArtiklaBk)){
                            result = 3;
                        }else if((item.getSifra()).equals(sifraArtiklaBk)){

                            sifraArtikla = item.getSifra();
                            sifLokacije = item.getLokacija();
                            nazivArtikla = item.getNaziv();

                            if(item.getPopisan().equals("1")){

                                if(filtered==0){
                                    message =  "Proizvod je vec popisan " + nazivArtikla;
                                    result = 1;
                                }else if (filtered==1 && filter_text.equals(item.getLokacija())){
                                    message =  "Proizvod je vec popisan i ne pripada parametrima filtera" + nazivArtikla;
                                    result = 1;

                                }else if (filtered==1 && !filter_text.equals(item.getLokacija())){
                                    message =  "Proizvod je vec popisan ali ne pripada ovoj lokaciji " + nazivArtikla;
                                    result = 1;
                                }

                            }else{

                                if(filtered==0 ){
                                    message = "Popisali ste proizvod " + nazivArtikla;
                                    artikliResults.add(item);
                                    result = 2;
                                }else if(filtered==1 && filter_text.equals(item.getLokacija())){
                                    message = "Popisali ste proizvod " + nazivArtikla;
                                    artikliResults.add(item);

                                    result = 2;
                                }else if (filtered==1 && !filter_text.equals(item.getLokacija())){
                                    message = "Proizvod "+nazivArtikla+" ne pripada ovoj lokaciji";
                                    result = 4;
                                    popisiArtikalIzvanListe(ArtikliActivity.this, sifraArtikla);

                                }

                            }
                        }
                    }

                    if(result == 2 && filtered==0){
                        artikli.clear();
                        db.popisArtikal(sifraArtikla);
                        artikli =  readDatabaseAllArtikli();
                        adapter = new ArtikliAdapter(ArtikliActivity.this, R.layout.item_layout, artikli, nextActivity);
                        listView.setAdapter(adapter);

                    }else if (result == 2 && filtered==1){
                        artikli.clear();
                        artikliResultsFilter.clear();
                        db.popisArtikal(sifraArtikla);
                        artikli =  readDatabaseAllArtikli();
                        for(OsnovnoSredstvo item:artikli){
                            if(item.getLokacija().equals(filter_text)){
                                artikliResultsFilter.add(item);
                            }
                        }
                        adapter = new ArtikliAdapter(ArtikliActivity.this, R.layout.item_layout, artikliResultsFilter, nextActivity);
                        listView.setAdapter(adapter);
                    }else if(result == 0){
                        message = "Proizvod nema u evidenciji " + sifraArtiklaBk;
                        dodajArtikal(this, sifraArtiklaBk);
                    }else if(result == 3){
                        //filtrirajAuto(this, sifraArtiklaBk);
                    }


                    //Toast.makeText(ArtikliActivity.this, message, Toast.LENGTH_SHORT).show();
                    message ="";
                    sifraArtiklaBk ="";
                    sifraArtikla = "";
                    sifLokacije = "";
                    nazivArtikla = "";

                }
                else{
                    //if search text is null
                    if(filtered == 0){
                        adapter = new ArtikliAdapter(ArtikliActivity.this, R.layout.item_layout, artikli, nextActivity);

                    }else{
                        adapter = new ArtikliAdapter(ArtikliActivity.this, R.layout.item_layout, artikliResultsFilter, nextActivity);

                    }
                    //adding the adapter to listview
                    listView.setAdapter(adapter);
                }
                searchView.setQuery("", false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case R.id.filter_lokacije:
                PrikaziFilter(this);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public List<OsnovnoSredstvo> readDatabaseAllArtikli(){


        artikli= db.getAllArtikal();
        //listView = (ListView) findViewById(R.id.listView);
        //creating the adapter object
        //adapter_a = new ArtikliAdapter(ArtikliActivity.this, R.layout.item_layout, artikli);
        //adding the adapter to listview
        //listView.setAdapter(adapter_a);

    return artikli;
    }

    public void goToArtikalDodavanje(){
    Context context = this;
    Intent intent = new Intent(context, ArtikliDodavanje.class);
    intent.putExtra("NEXT_ACTIVITY", "3");
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
    }

    public void goToSkenerOpcije(){
        //Context context = this;
        //Intent intent = new Intent(context, BluetoothConnectActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);
    }

    public void PrikaziFilter(ArtikliActivity v) {
        Button btn_zatvori;
        Button btn_filtriraj;
        myDialog.setContentView(R.layout.artikal_popisi);
        btn_zatvori =(Button) myDialog.findViewById(R.id.btn_zatvori);
        btn_filtriraj = (Button) myDialog.findViewById(R.id.btn_filtriraj);
        //final String filter_text = ((EditText)().getText().toString();
        btn_filtriraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtriraj(view);
                //myDialog.dismiss();
            }
        });
        btn_zatvori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        //myDialog.setTitle("Filter po lokaciji");
        final Window window = myDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void filtriraj(View view){
        eteFlter = (EditText) myDialog.findViewById(R.id.filterText);
        filter_text = eteFlter.getText().toString();
        artikli =  readDatabaseAllArtikli();
        artikliResultsFilter.clear();


         for(OsnovnoSredstvo item:artikli){
            //if(filter_text != null || filter_text !=""){
                if(item.getLokacija().equals(filter_text)){
                    artikliResultsFilter.add(item);
                }
         }
        if(artikliResultsFilter.size()>0) {
            adapter = new ArtikliAdapter(ArtikliActivity.this, R.layout.item_layout, artikliResultsFilter, nextActivity);
            listView.setAdapter(adapter);
            filtered = 1;
        }else{
            adapter = new ArtikliAdapter(ArtikliActivity.this, R.layout.item_layout, artikli, nextActivity);
            listView.setAdapter(adapter);
            filtered = 0;
        }

        myDialog.dismiss();

    }

    public void dodajArtikal(MaterialSearchView.OnQueryTextListener view, String sifraArtiklaBk){

        CharSequence colors[] = new CharSequence[]{"Da", "Ne"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ArtikliActivity.this);
        builder.setTitle("Želite li da dodate novi artikal");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                        goToArtikalDodavanje();
                } else {

                }
            }
        });
        builder.show();


    }
    public void popisiArtikalIzvanListe(ArtikliActivity view, final String sifraArtiklaS) {

        CharSequence colors[] = new CharSequence[]{"Da", "Ne"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ArtikliActivity.this);
        builder.setTitle("Želite li da popisete ovaj proizvod");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    db.popisArtikal(sifraArtiklaS);

                } else {
                    sifraArtikla = "";
                }
            }
        });
        builder.show();
    }







}