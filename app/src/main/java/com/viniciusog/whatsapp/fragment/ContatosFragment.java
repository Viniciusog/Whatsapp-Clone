package com.viniciusog.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.viniciusog.whatsapp.Helper.RecyclerItemClickListener;
import com.viniciusog.whatsapp.Helper.UsuarioFirebase;
import com.viniciusog.whatsapp.R;
import com.viniciusog.whatsapp.activity.ChatActivity;
import com.viniciusog.whatsapp.activity.GrupoActivity;
import com.viniciusog.whatsapp.adapter.ContatosAdapter;
import com.viniciusog.whatsapp.adapter.ConversasAdapter;
import com.viniciusog.whatsapp.config.ConfiguracaoFirebase;
import com.viniciusog.whatsapp.model.Conversa;
import com.viniciusog.whatsapp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import javax.xml.validation.Validator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {


    private RecyclerView recyclerViewListaContatos;
    private ContatosAdapter adapter;
    private List<Usuario> listaContatos = new ArrayList<>();
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerContatos;
    FirebaseUser usuarioAtual;

    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        /* Configurações iniciais */
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos);
        //pegando referência dos usuários
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        //Configura adapter
        adapter = new ContatosAdapter(listaContatos, getActivity());

        //Configura recyclerView - getActivity() pega o contexto da activity em que o fragment está
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaContatos.setLayoutManager(layoutManager);
        recyclerViewListaContatos.setHasFixedSize(true);
        recyclerViewListaContatos.setAdapter(adapter);

        //Configurar evento de clique no recyclerView
        recyclerViewListaContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewListaContatos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                List<Usuario> listaContatosAtualizada = adapter.getListaContatos();

                                Usuario usuarioSelecionado = listaContatosAtualizada.get(position);
                                boolean cabecalho = usuarioSelecionado.getEmail().isEmpty();

                                if (cabecalho) {

                                    Intent intent = new Intent(getActivity(), GrupoActivity.class);
                                    startActivity(intent);

                                } else {
                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    intent.putExtra("chatContato", usuarioSelecionado);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        /*Difine usuário com email vazio,
         * em caso de email vazio, o usuário
         * será utilizado como cabeçalho, exibindo novo grupo*/
        Usuario itemGrupo = new Usuario();
        itemGrupo.setNome("Novo Grupo");
        itemGrupo.setEmail("");

        listaContatos.add(itemGrupo);

        return view;
    }

    //Ao iniciar o fragment chamamos o método 'recuperarContatos'
    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Remover listener para não ficar executando de forma indefinida
        usuariosRef.removeEventListener(valueEventListenerContatos);
    }


    public void recuperarContatos() {

        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
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
                        listaContatos.add(usuario);
                    }
                }

                //Notificar que os dados foram modificados
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void pesquisarContatos(String texto) {

        List<Usuario> listaContatosBusca = new ArrayList<>();


        for (Usuario usuario : listaContatos) {
            String nome = usuario.getNome().toLowerCase();

            if (nome.contains(texto)) {
                listaContatosBusca.add(usuario);
            }

        }

        //Estamos 'refazendo' o adapter pois agora será listaConversaBusca que será exibida
        adapter = new ContatosAdapter(listaContatosBusca, getActivity());
        recyclerViewListaContatos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //Regarrega a lista de conversas original com todas as conversas
    public void recarregarContatos() {
        adapter = new ContatosAdapter(listaContatos, getActivity());
        recyclerViewListaContatos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
