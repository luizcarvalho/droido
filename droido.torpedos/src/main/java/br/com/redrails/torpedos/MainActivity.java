package br.com.redrails.torpedos;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, AbsListView.OnScrollListener {
    private SearchView mSearchView;

    //Dynamic Load
    private ArrayList<String> mArrayList = new ArrayList<String>();
    private ListView lista;
    private int number = 1;
    private final int MAX_ITEMS_PER_PAGE = 10;
    private boolean isloading = false;
    private MessageAdapter adapter;
    private MessageLoadTask task;
    private TextView footer;
    private int TOTAL_ITEMS = 100;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        //header = (TextView) findViewById(R.id.header);
        lista = (ListView) findViewById(R.id.lista);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = (TextView) inflater.inflate(R.layout.footer, null);
        lista.addFooterView(footer);

        adapter = new MessageAdapter(this, R.layout.row);
        adapter.messageArrayList = mArrayList;
        lista.setAdapter(adapter);
        lista.setOnScrollListener(this);

        task = new MessageLoadTask();
        task.execute();
    }

     public void openMainMenu(View v){
        PopupMenu mainMenu = new PopupMenu(this, v);
        MenuInflater inflater = mainMenu.getMenuInflater();
        inflater.inflate(R.menu.filtros, mainMenu.getMenu());
        mainMenu.show();
    }

    public void openMessageMenu(final View v, final TextView mensagem, int position){
        PopupMenu opcoesMenu = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = opcoesMenu.getMenuInflater();
        opcoesMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.mensagem_favorito:
                            addFavorite(menuItem);
                            return true;
                        case R.id.mensagem_share:
                            shareMessage(v, mensagem);
                            return true;
                        case R.id.mensagem_avaliar:
                            avaliar(menuItem);
                            return true;
                        case R.id.mensagem_editar:
                            editar(menuItem);
                        case R.id.mensagem_copiar:
                            copiarMensagem((String) mensagem.getText());
                            return true;
                        default:
                            return false;
                    }

            }
        });
        inflater.inflate(R.menu.mensagem_menu, opcoesMenu.getMenu());
        opcoesMenu.show();
    }

    public void addFavorite(MenuItem menuItem){
        Toast.makeText(this, "favorite: "+menuItem.getItemId(), Toast.LENGTH_LONG).show();
    }

    public void avaliar(MenuItem menuItem){
        Toast.makeText(this, "avaliar: "+menuItem.getItemId(), Toast.LENGTH_LONG).show();
    }

    public void editar(MenuItem menuItem){
        Toast.makeText(this, "editar: "+menuItem.getItemId(), Toast.LENGTH_LONG).show();
    }

    public void shareMessage(View v, TextView mensagem){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mensagem.getText());
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "via Droido ( http://goo.gl/5fN2N )");


        copiarMensagem(mensagem.getText() + "\n\n via Droido ( http://goo.gl/5fN2N )");


        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }



    @SuppressWarnings("deprecation")
    public void copiarMensagem(String text){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES. HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = ClipData.newPlainText("simple text",text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(this, getResources().getText(R.string.success_clipboarded), Toast.LENGTH_LONG).show();

    }

    //------------- ACTIONBAR MENU CONTROLL -------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.action_search:
                mSearchView.setIconified(false);
                return true;

            case R.id.action_menu:
                View actionSettingsView = findViewById(R.id.action_menu);
                openMainMenu(actionSettingsView);
                return true;
            default:
                Toast.makeText(this,item.getTitle() , Toast.LENGTH_LONG).show();
        }

        return false;
    }

    //---------- QUERY CALLBACKS ---------
    @Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }


    //---------- SCROLL CALLBACKS ---------
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int loadedItems = firstVisibleItem + visibleItemCount;
        if((loadedItems == totalItemCount) && !isloading){
            if(task != null && (task.getStatus() == AsyncTask.Status.FINISHED)){
                task = new MessageLoadTask();
                task.execute();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }



    //------------------------------------------------------------------------------------------
    // -----------####    MESSGES ADAPTER ------------------------------------------------------
    //------------------------------------------------------------------------------------------

    class MessageAdapter extends ArrayAdapter<String> {
        LayoutInflater inflater;
        public ArrayList<String> messageArrayList = new ArrayList<String>();

        public MessageAdapter(Context context, int rowResourceId) {
            super(context, rowResourceId);

            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return messageArrayList.size();
        }

        @Override
        public String getItem(int position) {
            return messageArrayList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.row, null);
            final TextView mensagem = (TextView) convertView.findViewById(R.id.mensagem);
            ImageView mensagemOption = (ImageView) convertView.findViewById(R.id.mensagem_option);
            mensagem.setText(messageArrayList.get(position).toString());
            final int message_position = position;


            mensagemOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMessageMenu(v, mensagem, message_position);
                }
            });

            mensagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareMessage(v, mensagem);
                }
            });

            return convertView;
        }
    }



    //------------------------------------------------------------------------------------------
    // -----------     CLASSE TASK -------------------------------------------------------------
    //------------------------------------------------------------------------------------------

    class MessageLoadTask extends AsyncTask<Void, Void, Void>
    {
        String[] mensagens = getResources().getStringArray(R.array.mensagens);


        @Override
        protected Void doInBackground(Void... params) {
            if(TOTAL_ITEMS > number){
                SystemClock.sleep(1000);
                isloading = true;
                for (int i = 1; i <= MAX_ITEMS_PER_PAGE; i++) {
                    int idx = new Random().nextInt(mensagens.length);
                    String random_mensagem = (mensagens[idx]);

                    mArrayList.add(random_mensagem);
                    number += 1;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter.notifyDataSetChanged();
            isloading = false;
            //Se carregou todos os itens
            if(adapter.getCount() == TOTAL_ITEMS){
                //header.setText("All "+adapter.getCount()+" Items are loaded.");
                lista.setOnScrollListener(null);//Para a escuta do scroll
                lista.removeFooterView(footer);// remove o footer
            }
            else{
                //SHOW MORE APPS
                //header.setText("Loaded items - "+adapter.getCount()+" out of "+TOTAL_ITEMS);
            }
        }
    }
}
