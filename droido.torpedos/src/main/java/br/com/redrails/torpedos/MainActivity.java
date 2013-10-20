package br.com.redrails.torpedos;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.MenuItemCompat;
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
import java.util.Random;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, AbsListView.OnScrollListener {
    private SearchView mSearchView;

    //Dynamic Load
    private ArrayList<String> mArrayList = new ArrayList<String>();
    private ListView lista;
    private int number = 1;
    private final int MAX_ITEMS_PER_PAGE = 10;
    private boolean isloading = false;
    private MyAdapter adapter;
    private MyTask task;
    private TextView footer;
    private int TOTAL_ITEMS = 100;
    private TextView header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        //header = (TextView) findViewById(R.id.header);
        lista = (ListView) findViewById(R.id.lista);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = (TextView) inflater.inflate(R.layout.footer, null);
        lista.addFooterView(footer);

        adapter = new MyAdapter(this, R.layout.row);
        lista.setAdapter(adapter);
        lista.setOnScrollListener(this);

        task = new MyTask();
        task.execute();
    }


    public void openMainMenu(View v){
        PopupMenu opcoesMenu = new PopupMenu(this, v);
        MenuInflater inflater = opcoesMenu.getMenuInflater();
        inflater.inflate(R.menu.opcoes_menu, opcoesMenu.getMenu());
        opcoesMenu.show();
    }

    public void openMessageMenu(View v){
        PopupMenu opcoesMenu = new PopupMenu(this, v);
        MenuInflater inflater = opcoesMenu.getMenuInflater();
        inflater.inflate(R.menu.mensagem_menu, opcoesMenu.getMenu());
        opcoesMenu.show();
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

            case R.id.action_settings:
                View actionSettingsView = findViewById(R.id.action_settings);
                openMainMenu(actionSettingsView);
                return true;
        }

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }


    //DYNAMIC LOAD MESSAGES
    class MyAdapter extends ArrayAdapter<String> {
        LayoutInflater inflater;

        public MyAdapter(Context context, int rowResourceId) {
            super(context, rowResourceId);

            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mArrayList.size();
        }

        @Override
        public String getItem(int position) {
            return mArrayList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.row, null);
            TextView mensagem = (TextView) convertView.findViewById(R.id.mensagem);
            ImageView mensagemOption = (ImageView) convertView.findViewById(R.id.mensagem_option);
            mensagem.setText(mArrayList.get(position).toString());
            final int message_position = position;


            mensagemOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMessageMenu(v);
                }
            });

            return convertView;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int loadedItems = firstVisibleItem + visibleItemCount;
        if((loadedItems == totalItemCount) && !isloading){
            if(task != null && (task.getStatus() == AsyncTask.Status.FINISHED)){
                task = new MyTask();
                task.execute();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    class MyTask extends AsyncTask<Void, Void, Void>
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
