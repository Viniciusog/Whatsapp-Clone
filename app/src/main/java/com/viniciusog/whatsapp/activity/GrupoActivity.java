package com.viniciusog.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.viniciusog.whatsapp.Helper.RecyclerItemClickListener;
import com.viniciusog.whatsapp.Helper.UsuarioFirebase;
import com.viniciusog.whatsapp.R;
import com.viniciusog.whatsapp.adapter.ContatosAdapter;
import com.viniciusog.whatsapp.adapter.GrupoSelecionadoAdapter;
import com.viniciusog.whatsapp.config.ConfiguracaoFirebase;
import com.viniciusog.whatsapp.model.Usuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GrupoActivity extends AppCompatActivity {

    private RecyclerView recyclerMembros, recyclerMembrosSelecionados;
    private ContatosAdapter contatosAdapter;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private List<Usuario> listaMembros = new ArrayList<>();
    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private ValueEventListener valueEventListenerMembros;
    private DatabaseReference usuariosRef;
    private FirebaseUser usuarioAtual;
    private Toolbar toolbar;
    private FloatingActionButton fabAvancarCadastro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Grupo");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurações inciais
        recyclerMembros = findViewById(R.id.recyclerMembros);
        recyclerMembrosSelecionados = findViewById(R.id.recyclerMembrosSelecionados);
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();
        fabAvancarCadastro = findViewById(R.id.fab);

        //Configurar adapter
        contatosAdapter = new ContatosAdapter(listaMembros, getApplicationContext());

        //Configurar layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembros.setLayoutManager(layoutManager);
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter(contatosAdapter);

        recyclerMembros.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerMembros, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSelecionado = listaMembros.get(position);


                //Remover  usuário selecionado da dlista
                listaMembros.remove(usuarioSelecionado);
                contatosAdapter.notifyDataSetChanged();

                listaMembrosSelecionados.add(usuarioSelecionado);
                grupoSelecionadoAdapter.notifyDataSetChanged();
                atualizarMembrosToolbar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }
        ));

        //Configurar recycler view para os membros selecionados
        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listaMembrosSelecionados, getApplicationContext());

        //Configurar layout manager
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        recyclerMembrosSelecionados.setLayoutManager(layoutManagerHorizontal);
        recyclerMembrosSelecionados.setHasFixedSize(true);
        recyclerMembrosSelecionados.setAdapter(grupoSelecionadoAdapter);


        recyclerMembrosSelecionados.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerMembrosSelecionados,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSelecionado = listaMembrosSelecionados.get( position );

                //remover da listagem de membros selecionados
                listaMembrosSelecionados.remove( usuarioSelecionado );
                grupoSelecionadoAdapter.notifyDataSetChanged();

                //adicionar à listagem de membros
                listaMembros.add( usuarioSelecionado );
                contatosAdapter.notifyDataSetChanged();
                atualizarMembrosToolbar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }
        ));

        //Configurar clique em fab
        fabAvancarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GrupoActivity.this, CadastroGrupoActivity.class);
                intent.putExtra("membros", (Serializable) listaMembrosSelecionados);
                startActivity( intent );
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerMembros);
    }

    public void recuperarContatos() {

        valueEventListenerMembros = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Percorre todos os usuários no firebase
                for (DataSnapshot dados : dataSnapshot.getChildren()) {

                    FirebaseUser usuarioAtual = UsuarioFirebase.getUsuarioAtual();

                    //Pega um objeto do tipo usuário
                    Usuario usuario = dados.getValue(Usuario.class);

                    //Não adiciona o usuário atual na lista de contatos
                    String emailUsuarioAtual = usuarioAtual.getEmail();
                    if (!emailUsuarioAtual.equals(usuario.getEmail())) {
                        listaMembros.add(usuario);
                    }
                }

                //Notificar que os dados foram modificados
                contatosAdapter.notifyDataSetChanged();
                atualizarMembrosToolbar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void atualizarMembrosToolbar(){
        int totalSelecionados = listaMembrosSelecionados.size();
        int total = listaMembros.size() + totalSelecionados;
        toolbar.setSubtitle(totalSelecionados + " de " + total + " selecionados");
    }
}
