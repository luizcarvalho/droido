package br.com.redrails.torpedos;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.google.ads.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;


import java.util.ArrayList;
import java.util.List;

import br.com.redrails.torpedos.models.Categoria;
import br.com.redrails.torpedos.daos.CategoriaDAO;
import br.com.redrails.torpedos.daos.MensagemDAO;
import br.com.redrails.torpedos.models.Mensagem;
import br.com.redrails.torpedos.parse.SyncActivity;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, AbsListView.OnScrollListener, ActionBar.OnNavigationListener {
    private SearchView mSearchView;


    //Dynamic Load
    private ArrayList<Mensagem> mArrayList = new ArrayList<Mensagem>();//Array de Objetos Mensagem para o adapter
    private ListView lista; //Lista de Mensagens
    private MessageAdapter adapter;//Adapter que carrega as mensagens para a ListView
    //private MessageLoadTask task; //Controle de exibição das mensagens em thread
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

        reloadDropdown();
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

        reload();
        firstRunActions(this);

    }

    private void reload(){

        pagina_atual = 1;
        mensagemDao.reloadQuantidadeTotal();
        quantidade_carregada=0;
        quantidade_restante=0;
        mArrayList.clear();
        loadMensagens();
        adapter.notifyDataSetChanged();
        lista.setSelectionAfterHeaderView();
    }

    public void reloadDropdown(){
        CategoriaDAO categoriaDao = CategoriaDAO.getInstance(this);
        List<Categoria> categorias = categoriaDao.recuperarTodas();
        dropdownAdapter = new ArrayAdapter<Categoria>(this,
                android.R.layout.simple_dropdown_item_1line, android.R.id.text1,
                categorias);
        dropdownAdapter.notifyDataSetChanged();//Notifica a interface que o adapter recebeu dados novos
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
                            shareMessage(v, mensagem,position );
                            return true;
                        case R.id.mensagem_copiar:
                            copiarMenssagem(mensagem.getTexto());
                            return true;
                        case R.id.mensagem_gostar:
                            sendReport(mensagem, "gostar");
                            return true;
                        case R.id.mensagem_reportar:
                            reportDialog(v,mensagem);
                            return true;

                        default:
                            return false;
                    }

            }
        });
        inflater.inflate(R.menu.mensagem_menu, opcoesMenu.getMenu());

        opcoesMenu.show();
    }

    public void sendReport(Mensagem mensagem, String evento){
        EasyTracker easyTracker = EasyTracker.getInstance(this);

        easyTracker.send(MapBuilder
                .createEvent("mensagem_action",     // Event category (required)
                        evento,  // Event action (required)
                        mensagem.getSlug(),   // Event label
                        null)            // Event value
                .build()
        );
    }

    public void reportDialog(final View v, final Mensagem mensagem){
        final Context viewContext = v.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(viewContext);
        final String[] reports = getResources().getStringArray(R.array.report_types);
        builder.setTitle(R.string.report_title)
        .setItems(reports, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sendReport(mensagem, reports[which]);
                AlertDialog.Builder popup = new AlertDialog.Builder(mainContext);
                popup.setTitle("Obrigado!");
                popup.setMessage(R.string.report_response);
                popup.setNeutralButton(R.string.novidade_dismiss, null);
                popup.create().show();
            }
        });
        builder.create();
        builder.show();
    }

    public void toggleFavorite(View v, int position){
        Mensagem mensagem = adapter.getItem(position);
        mensagem.toggleFavorite();

        ImageView favIcon = (ImageView) v.findViewById(R.id.btn_favstar);
        mensagemDao.atualizar(mensagem);

        if(mensagem.getFavoritada()){
            sendReport(mensagem, "marcada_favorito");
            favIcon.setImageResource(R.drawable.ic_fav);
            Toast.makeText(this, "Marcada como favorita", Toast.LENGTH_LONG).show();
        }else{
            sendReport(mensagem, "desmarcada_favorito");
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
            sendReport(mensagem, "enviada");
            sendIcon.setImageResource(R.drawable.ic_sended);
            Toast.makeText(this, "Marcada como enviada", Toast.LENGTH_LONG).show();
        }else{
            sendIcon.setImageResource(R.drawable.ic_unsended);
            Toast.makeText(this, "Marcada como não enviada", Toast.LENGTH_LONG).show();
        }

    }

    public void shareMessage(View v, Mensagem mensagem, int position){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mensagem.getTexto());
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "- ( http://goo.gl/xninpd )");


        copiarMenssagem(mensagem.getTexto() + "\n\n - ( http://goo.gl/xninpd )");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

    }

    @SuppressWarnings("deprecation")
    public void copiarMenssagem(String text){
        Toast.makeText(this, getResources().getText(R.string.success_clipboarded), Toast.LENGTH_LONG).show();
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES. HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = ClipData.newPlainText("simple text",text);
            clipboard.setPrimaryClip(clip);
        }


    }

    //------------- ACTIONBAR MENU CONTROLL -------------------------------------------------

    @Override
    public boolean onNavigationItemSelected(int i, long l) {

        Categoria categoria = dropdownAdapter.getItem(i);
        mensagemDao.filtro.setCategoria(categoria);
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
            case R.id.menu_atualizar:
                Intent syncIntent = new Intent(this, SyncActivity.class);
                startActivity(syncIntent);
                return true;
            case R.id.menu_avaliar:
                Intent intentRating = new Intent(Intent.ACTION_VIEW);
                intentRating.setData(Uri.parse("market://details?id=br.com.redrails.torpedos"));
                startActivity(intentRating);
                return true;
            case R.id.menu_mais_apps:
                Intent intentmoreApps = new Intent(Intent.ACTION_VIEW);
                intentmoreApps.setData(Uri.parse("market://search?q=pub:RedRails"));
                startActivity(intentmoreApps);
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
//        if((loadedItems == totalItemCount) && !isloading){
//            if(task != null && (task.getStatus() == AsyncTask.Status.FINISHED)){
//                task = new MessageLoadTask();
//                task.execute();
//            }
//        }
        if(loadedItems == totalItemCount){
            if(adapter!=null){
                loadMensagens();
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
            final View finalView = inflater.inflate(R.layout.row, null);
            convertView = finalView;
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
            autorText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentRating = new Intent(Intent.ACTION_VIEW);
                    intentRating.setData(Uri.parse("http://goo.gl/svnC1L"));
                    startActivity(intentRating);
                }
            });


            menssagemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSend(finalView, position);
                    shareMessage(v, mensagem,position);
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

    public void loadMensagens(){
        int MAX_ITEMS_PER_PAGE = 20;
        if(mensagemDao.getQuantidadeTotal() > quantidade_carregada){
            //SystemClock.sleep(1000);


            List<Mensagem> mensagens = mensagemDao.getMensagens(pagina_atual);

            TOTAL_ITEMS = (int) mensagemDao.getQuantidadeTotal();
            //Log.w("RedRails", "Adicionando mensagens pagina: "+pagina_atual);


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
                    String dados = "qtd_restante: "+quantidade_restante+" qtde_carregada:  "
                            +quantidade_carregada+" pg "+pagina_atual+" MAX_P_PAGE "+MAX_ITEMS_PER_PAGE
                            +"TOTAL "+TOTAL_ITEMS;
                    Log.e("RedRails","Out bound Error: "+e.getStackTrace()+"\n "+dados);
                }
            }
        }
        finalizeLoad();
    }

    protected void finalizeLoad(){
        adapter.notifyDataSetChanged();
        //Log.i("RedRails", "Notificado!!!");

        //Se carregou todos os itens
        if(adapter.getCount() == mensagemDao.getQuantidadeTotal()){
            if(adapter.getCount()==0){
                footer.setText("Não há mensagens aqui ainda!");
            }else{
                lista.setOnScrollListener(null);//Para a escuta do scroll
                lista.removeFooterView(footer);// remove o footer
            }
        }
        else{
            if(lista.getFooterViewsCount()==0){
                lista.setOnScrollListener((AbsListView.OnScrollListener) mainContext);
                lista.addFooterView(footer);
            }
        AdView adView = (AdView)this.findViewById(R.id.adView);
        adView.loadAd(new AdRequest());
            //SHOW MORE APPS
            //header.setText("Loaded items - "+adapter.getCount()+" out of "+TOTAL_ITEMS);
        }
    }

    public void firstRunActions(Context context){
        SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        boolean newVersion = prefs.getBoolean("newVersion", true);
        if(newVersion){
            boolean firstRun = prefs.getBoolean("firstRun", true);
            SharedPreferences.Editor ed = prefs.edit();
            if(firstRun){
                createShortCut();
                ed.putBoolean("firstRun", false);
            }
            openNews(context);
            ed.putBoolean("newVersion", false);
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


    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

 }
