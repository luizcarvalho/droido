package br.com.redrails.torpedos;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
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
import java.util.List;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, AbsListView.OnScrollListener, ActionBar.OnNavigationListener {
    private SearchView mSearchView;
    private ActionBar mActionBar;


    //Dynamic Load
    private ArrayList<Mensagem> mArrayList = new ArrayList<Mensagem>();
    private ListView lista;
    private boolean isloading = false;
    private MessageAdapter adapter;
    private MessageLoadTask task;
    private TextView footer;
    MensagemDAO mensagemDao;
    int ordem = mensagemDao.ORDEM_AVALIACAO;

    int TOTAL_ITEMS ;
    int quantidade_carregada= 0;
    int pagina_atual = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        final String[] dropdownValues = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, android.R.id.text1,
                dropdownValues);

        mActionBar.setListNavigationCallbacks(dropdownAdapter, this);

        mensagemDao = MensagemDAO.getInstance(this);
        TOTAL_ITEMS = (int) mensagemDao.getQuantidadeTotal();



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

    private void reload(){
        pagina_atual = 1;
        mArrayList.clear();
        adapter.notifyDataSetChanged();
        task = new MessageLoadTask();
        task.execute();
    }
    //--------------------------------------------------------------------------------------------
    // **************** *** Call and Listeners Methods
    //--------------------------------------------------------------------------------------------
     public void openMainMenu(final View v){
        PopupMenu mainMenu = new PopupMenu(this, v);
        MenuInflater inflater = mainMenu.getMenuInflater();

        mainMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.ordem_avaliacao:
                        ordem = mensagemDao.ORDEM_AVALIACAO;
                        reload();
                        Log.w("DROIDO", "MENU : AVALIACAO" );
                        return true;
                    case R.id.ordem_enviadas:
                        ordem = mensagemDao.ORDEM_ENVIADAS;
                        reload();
                        Log.w("DROIDO", "MENU : ENVIADAS" );
                        return true;
                    case R.id.ordem_favoritos:
                        ordem = mensagemDao.ORDEM_FAVORITOS;
                        reload();
                        Log.w("DROIDO", "MENU : FAV" );
                        return true;
                    case R.id.ordem_novas:
                        ordem = mensagemDao.ORDEM_DATA;
                        reload();
                        Log.w("DROIDO", "MENU : DATA" );

                        return true;
                    case R.id.ordem_nao_enviadas:
                        ordem = mensagemDao.ORDEM_NAO_ENVIADAS;
                        reload();
                        Log.w("DROIDO", "MENU : N ENV" );
                        return true;
                }


                return false;
            }
        });
        inflater.inflate(R.menu.ordem, mainMenu.getMenu());
        mainMenu.show();
    }

    public void openMessageMenu(final View v, final Mensagem mensagem){
        PopupMenu opcoesMenu = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = opcoesMenu.getMenuInflater();
        opcoesMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.mensagem_favorito:
                            toggleFavorite(v, mensagem.getId());
                            return true;
                        case R.id.mensagem_share:
                            shareMessage(v, mensagem);
                            return true;
                        case R.id.mensagem_copiar:
                            copiarMenssagem(mensagem.getTexto());
                            return true;
                        default:
                            return false;
                    }

            }
        });
        inflater.inflate(R.menu.mensagem_menu, opcoesMenu.getMenu());
        opcoesMenu.show();
    }

    public void toggleFavorite(View v, int position){
        Mensagem mensagem = adapter.getItem(position);

        ImageView favIcon = (ImageView) findViewById(R.id.btn_favstar);
        boolean x = true;
        if(x){
            favIcon.setImageResource(R.drawable.btn_unfav);
        }else{
            favIcon.setImageResource(R.drawable.btn_fav);
        }


        //Toast.makeText(this, "Mensagem marcada/desmarcada como favorita: "+x, Toast.LENGTH_LONG).show();
    }

    public void toggleSend(View v, int position){
        ImageView sendIcon = (ImageView) v.findViewById(R.id.btn_sended);
        boolean x = true;
        if(x){
            sendIcon.setImageResource(R.drawable.btn_unsended);
        }else{
            sendIcon.setImageResource(R.drawable.btn_sended);
        }

        Toast.makeText(this, "Mensagem marcada/desmarcada como enviada : "+position, Toast.LENGTH_LONG).show();
    }

    public void shareMessage(View v, Mensagem mensagem){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mensagem.getTexto());
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "via Droido ( http://goo.gl/5fN2N )");

        copiarMenssagem(mensagem.getTexto() + "\n\n via Droido ( http://goo.gl/5fN2N )");


        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }



    @SuppressWarnings("deprecation")
    public void copiarMenssagem(String text){
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

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        return false;
    }

    //------------------------------------------------------------------------------------------
    // -----------####    MESSGES ADAPTER ------------------------------------------------------
    //------------------------------------------------------------------------------------------

    class MessageAdapter extends ArrayAdapter<Mensagem> {
        LayoutInflater inflater;
        public ArrayList<Mensagem> messageArrayList = new ArrayList<Mensagem>();

        public MessageAdapter(Context context, int rowResourceId) {
            super(context, rowResourceId);

            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return messageArrayList.size();
        }

        @Override
        public Mensagem getItem(int position) {
            return messageArrayList.get(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.row, null);
            final Mensagem mensagem = messageArrayList.get(position);
            TextView menssagemView = (TextView) convertView.findViewById(R.id.mensagem);
            ImageView mensagemOption = (ImageView) convertView.findViewById(R.id.mensagem_option);
            ImageView favButton = (ImageView) convertView.findViewById(R.id.btn_favstar);
            ImageView sendedButton = (ImageView) convertView.findViewById(R.id.btn_sended);
            menssagemView.setText(mensagem.getTexto());



            mensagemOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMessageMenu(v, mensagem);
                }
            });

            menssagemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareMessage(v, mensagem);
                }
            });

            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFavorite(v, 1);

                }
            });

            sendedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSend(v, 1);

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
        int MAX_ITEMS_PER_PAGE = 20;

        @Override
        protected Void doInBackground(Void... params) {
            if(TOTAL_ITEMS > quantidade_carregada){
                //SystemClock.sleep(1000);
                isloading = true;
                List<Mensagem> mensagens = mensagemDao.getMensagens(pagina_atual, ordem);
                Log.w("DROIDO", "Adicionando mensagens pagina: "+pagina_atual);
                quantidade_carregada += MAX_ITEMS_PER_PAGE;
                pagina_atual+=1;
                for (int i = 0; i < MAX_ITEMS_PER_PAGE; i++) {
                    //int idx = new Random().nextInt(mensagens.length);
                    //String random_mensagem = (mensagens[idx]);
                    mArrayList.add(mensagens.get(i));
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.w("DROIDO", "Notificando DATASET CHANGED");
            adapter.notifyDataSetChanged();
            Log.i("DROIDO", "Notificado!!!");
            isloading = false;
            //Se carregou todos os itens
            if(adapter.getCount() == TOTAL_ITEMS){
                lista.setOnScrollListener(null);//Para a escuta do scroll
                lista.removeFooterView(footer);// remove o footer
            }
            else{
                //SHOW MORE APPS
                //header.setText("Loaded items - "+adapter.getCount()+" out of "+TOTAL_ITEMS);
            }
        }
        @Override
        protected void onCancelled(){
            adapter.notifyDataSetChanged();
        }
    }

 }
