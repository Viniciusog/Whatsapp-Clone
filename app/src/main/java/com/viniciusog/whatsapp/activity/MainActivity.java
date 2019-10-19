package com.viniciusog.whatsapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.viniciusog.whatsapp.R;
import com.viniciusog.whatsapp.config.ConfiguracaoFirebase;
import com.viniciusog.whatsapp.fragment.ContatosFragment;
import com.viniciusog.whatsapp.fragment.ConversasFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Configurando a toolbar
        Toolbar toolbar = findViewById(R.id.toolBarPrincipal);
        toolbar.setTitle("WhatsApp");
        //Setando suport da action bar para funcionar em versões anteriores do android
        setSupportActionBar(toolbar);

        //Configurando abas
        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("Conversas", ConversasFragment.class)
                        .add("Contatos", ContatosFragment.class)
                        .create()
        );

        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);

        searchView = findViewById(R.id.materialSearchPrincipal);

        //Listener para o search view
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);
                fragment.recarregarConversas();
            }
        });

        //Listener para a caixa de texto
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            //É chamado quando o usuário confirma a digitação
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            //Este é chamado em tempo de execução
            @Override
            public boolean onQueryTextChange(String newText) {

                //Verifica se está pesquisando em Conversas ou Contatos
                //dependendo da tab que estiver ativa
                switch ( viewPager.getCurrentItem() ) {
                    case 0: {

                        ConversasFragment conversasFragment = (ConversasFragment) adapter.getPage(0);
                        if (newText != null && !newText.isEmpty()) {
                            //Passar o novo texto para pesquisa como letras minúsculas
                            conversasFragment.pesquisarConversas(newText.toLowerCase());
                        } else {
                            conversasFragment.recarregarConversas();
                        }
                        break;
                    }
                    case 1: {
                        ContatosFragment contatosFragment = (ContatosFragment) adapter.getPage( 1 );
                        if (newText != null && !newText.isEmpty()) {
                            //Passar o novo texto para pesquisa como letras minúsculas
                            contatosFragment.pesquisarContatos(newText.toLowerCase());
                        } else {
                            contatosFragment.recarregarContatos();
                        }
                        break;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //Configurar botão de pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuPesquisa: {
                break;
            }
            case R.id.menuConfiguracoes: {
                abrirConfiguracoes();
                break;

            }
            case R.id.menuSair: {
                deslogarUsuario();
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario() {
        try {
            autenticacao.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abrirConfiguracoes() {
        Intent intent = new Intent(MainActivity.this, ConfiguracaoActivity.class);
        startActivity(intent);

    }
}
