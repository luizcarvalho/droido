package br.com.redrails.torpedos;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.text.method.ScrollingMovementMethod;
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

import br.com.redrails.torpedos.categoria.Categoria;
import br.com.redrails.torpedos.categoria.CategoriaDAO;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, AbsListView.OnScrollListener, ActionBar.OnNavigationListener {
    private SearchView mSearchView;


    //Dynamic Load
    private ArrayList<Mensagem> mArrayList = new ArrayList<Mensagem>();//Array de Objetos Mensagem para o adapter
    private ListView lista; //Lista de Mensagens
    private boolean isloading = false;//Verifica se a lista está no modo LOAD
    private MessageAdapter adapter;//Adapter que carrega as mensagens para a ListView
    private MessageLoadTask task; //Controle de exibição das mensagens em thread
    private TextView footer; //Adicionar o "carregando" no final da ListView
    MensagemDAO mensagemDao;//Gerencia todos os métodos do banco de dados
    Context mainContext;//Usado para 

    ArrayAdapter<Categoria> dropdownAdapter;//Adapter para o Dropdown menu de Categorias


    int TOTAL_ITEMS=0;//Total de Itens máximo com e sem filtro.
    int quantidade_carregada= 0;//Quantidade de itens que foi carregado até o momento
    int quantidade_restante = 0;
    int pagina_atual = 1;//Controle de páginas Setado como 20 no MensagemDAO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);//Desativa o Título
        mActionBar.setDisplayShowHomeEnabled(true);//Define que o icone HOME apareça
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);//Permite a utilização do Dropdown List para Categorias
        
        CategoriaDAO categoriaDao = CategoriaDAO.getInstance(this);
        List<Categoria> categorias = categoriaDao.recuperarTodas();
        dropdownAdapter = new ArrayAdapter<Categoria>(this,
                android.R.layout.simple_dropdown_item_1line, android.R.id.text1,
                categorias);
        dropdownAdapter.notifyDataSetChanged();//Notifica a interface que o adapter recebeu dados novos


        mActionBar.setListNavigationCallbacks(dropdownAdapter, this);//Seta o Dropdown de Categorias no ActionBar

        mainContext = this;// Adicionar o Contexto MAIN para ser utilziado na questão do Listener da ListView

        mensagemDao = MensagemDAO.getInstance(this);
        TOTAL_ITEMS = (int) mensagemDao.getQuantidadeTotal();//Adicionar a quantidade total de itens máximo que pode ser carregado

        
        lista = (ListView) findViewById(R.id.lista);//Lista de Mensagens do ListView
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = (TextView) inflater.inflate(R.layout.footer, null); //Carregando...
        lista.addFooterView(footer);
        lista.setOnScrollListener(this);//Adicionar o Listener implementado no MainActivity

        adapter = new MessageAdapter(this, R.layout.row);
        adapter.messageArrayList = mArrayList;
        lista.setAdapter(adapter);


        task = new MessageLoadTask();
        reload();
        firstRunActions(this);
    }

    private void reload(){

        pagina_atual = 1;
        mensagemDao.reloadQuantidadeTotal();
        quantidade_carregada=0;
        quantidade_restante=0;
        mArrayList.clear();

        task = new MessageLoadTask();
        task.execute();
        adapter.notifyDataSetChanged();
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
                        mensagemDao.filtro.setOrdem(mensagemDao.ORDEM_AVALIACAO);
                        reload();
                        return true;
                    case R.id.ordem_enviadas:
                        mensagemDao.filtro.setOrdem(mensagemDao.ORDEM_ENVIADAS);
                        reload();
                        return true;
                    case R.id.ordem_favoritos:
                        mensagemDao.filtro.setOrdem(mensagemDao.ORDEM_FAVORITOS);
                        reload();
                        return true;
                    case R.id.ordem_novas:
                        mensagemDao.filtro.setOrdem(mensagemDao.ORDEM_DATA);
                        reload();
                        return true;
                    case R.id.ordem_nao_enviadas:
                        mensagemDao.filtro.setOrdem(mensagemDao.ORDEM_NAO_ENVIADAS);
                        reload();
                        return true;
                    case R.id.ordem_aleatoria:
                        mensagemDao.filtro.setOrdem(mensagemDao.ORDEM_ALEATORIA);
                        reload();
                        return true;
                }


                return false;
            }
        });
        inflater.inflate(R.menu.ordem, mainMenu.getMenu());
        mainMenu.show();
    }

    public void openMessageMenu(final View v, final Mensagem mensagem, final int position){
        PopupMenu opcoesMenu = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = opcoesMenu.getMenuInflater();
        final View rowView = (View) v.getParent();
        Menu menuObj = opcoesMenu.getMenu();

        opcoesMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.mensagem_favorito:
                            toggleFavorite(rowView, position);
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
        mensagem.toggleFavorite();

        ImageView favIcon = (ImageView) v.findViewById(R.id.btn_favstar);
        mensagemDao.atualizar(mensagem);

        if(mensagem.getFavoritada()){
            favIcon.setImageResource(R.drawable.ic_fav);
            Toast.makeText(this, "Marcada como favorita", Toast.LENGTH_LONG).show();
        }else{
            favIcon.setImageResource(R.drawable.ic_unfav);
            Toast.makeText(this, "Desmarcada como favorita", Toast.LENGTH_LONG).show();
        }
    }

    public void toggleSend(View v, int position){
        Mensagem mensagem = adapter.getItem(position);
        ImageView sendIcon = (ImageView) v.findViewById(R.id.btn_sendcheck);

        mensagem.toggleSended();
        mensagemDao.atualizar(mensagem);

        if(mensagem.getEnviada()){
            sendIcon.setImageResource(R.drawable.ic_sended);
            Toast.makeText(this, "Marcada como enviada", Toast.LENGTH_LONG).show();

        }else{
            sendIcon.setImageResource(R.drawable.ic_unsended);
            Toast.makeText(this, "Marcada como não enviada", Toast.LENGTH_LONG).show();
        }

    }

    public void shareMessage(View v, Mensagem mensagem){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mensagem.getTexto());
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "via Droido ( http://goo.gl/5fN2N )");

        copiarMenssagem(mensagem.getTexto() + "\n\n via Droido ( http://goo.gl/5fN2N )");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        try {
            mensagem.setEnviada(true);
            mensagemDao.atualizar(mensagem);
        } catch (Exception e) {
            Toast.makeText(this,"Não conseguimos marcar sua mensagem como lida =(",Toast.LENGTH_LONG);
        }
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
    public boolean onNavigationItemSelected(int i, long l) {

        Categoria categoria = dropdownAdapter.getItem(i);
        mensagemDao.filtro.setCategoria(categoria.getId());
        reload();
        return false;
    }

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
            case R.id.menu_sobre:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                Toast.makeText(this,item.getTitle() , Toast.LENGTH_LONG).show();
        }

        return false;
    }

    //---------- QUERY CALLBACKS ---------
    @Override
    public boolean onQueryTextSubmit(String s) {
        mensagemDao.filtro.setBusca(s);
        reload();
        return true;
    }



    @Override
    public boolean onQueryTextChange(String s) {
        if(s.equals("")){
            mensagemDao.filtro.setBusca(null);
            reload();
        }
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
            TextView autorText = (TextView) convertView.findViewById(R.id.author);
            ImageView mensagemOption = (ImageView) convertView.findViewById(R.id.mensagem_option);
            ImageView sendedButton = (ImageView) convertView.findViewById(R.id.btn_sendcheck);
            menssagemView.setText(mensagem.getTexto());
            autorText.setText(mensagem.getAutor());

            ImageView favButton = (ImageView) convertView.findViewById(R.id.btn_favstar);
            if(mensagem.getFavoritada()){
                favButton.setImageResource(R.drawable.ic_fav);
            }

            if(mensagem.getEnviada()){
                sendedButton.setImageResource(R.drawable.ic_sended);
            }

            mensagemOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMessageMenu(v, mensagem, position);
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
                    toggleFavorite(v, position);

                }
            });

            sendedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSend(v, position);
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
            if(mensagemDao.getQuantidadeTotal() > quantidade_carregada){
                //SystemClock.sleep(1000);
                isloading = true;
                List<Mensagem> mensagens = mensagemDao.getMensagens(pagina_atual);

                TOTAL_ITEMS = (int) mensagemDao.getQuantidadeTotal();
                //Log.w("DROIDO", "Adicionando mensagens pagina: "+pagina_atual);


                quantidade_restante = TOTAL_ITEMS;
                if(MAX_ITEMS_PER_PAGE>quantidade_restante){
                    MAX_ITEMS_PER_PAGE= quantidade_restante;
                    quantidade_restante=0;
                }else{
                    quantidade_restante-=quantidade_carregada;
                }

                quantidade_carregada += MAX_ITEMS_PER_PAGE;


                pagina_atual+=1;
                for (int i = 0; i < MAX_ITEMS_PER_PAGE; i++) {
                    try{
                        mArrayList.add(mensagens.get(i));
                    }catch (Exception e){
                        Log.e("Droido","Error: "+e.getStackTrace());
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Log.w("DROIDO", "Notificando DATASET CHANGED");
            adapter.notifyDataSetChanged();
            //Log.i("DROIDO", "Notificado!!!");
            isloading = false;
            //Se carregou todos os itens
            if(adapter.getCount() == mensagemDao.getQuantidadeTotal()){
                lista.setOnScrollListener(null);//Para a escuta do scroll
                lista.removeFooterView(footer);// remove o footer
            }
            else{
                if(lista.getFooterViewsCount()==0){
                    lista.setOnScrollListener((AbsListView.OnScrollListener) mainContext);
                    lista.addFooterView(footer);
                }
                //SHOW MORE APPS
                //header.setText("Loaded items - "+adapter.getCount()+" out of "+TOTAL_ITEMS);
            }
        }
        @Override
        protected void onCancelled(){
            adapter.notifyDataSetChanged();
        }
    }

    public void firstRunActions(Context context){
        SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        boolean firstRun = prefs.getBoolean("firstRun", true);
        if(firstRun){
            openNews(context);
            createShortCut();
            SharedPreferences.Editor ed = prefs.edit();
            ed.putBoolean("firstRun", false);
            ed.commit();
        }
    }

    public void createShortCut(){
        Intent shortcutIntent = new Intent(getApplicationContext(),
                MainActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Droido SMS");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.drawable.ic_launcher));

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);


    }

    public void openNews(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View novidadesView = inflater.inflate(R.layout.news, null);
        TextView novidadeText = (TextView) novidadesView.findViewById(R.id.novidade_texto);
        novidadeText.setMovementMethod(new ScrollingMovementMethod());


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(novidadesView);
        builder.setPositiveButton(R.string.novidade_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create();
        builder.show();



    }

 }
