package com.viniciusog.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.viniciusog.whatsapp.Helper.RecyclerItemClickListener;
import com.viniciusog.whatsapp.Helper.UsuarioFirebase;
import com.viniciusog.whatsapp.R;
import com.viniciusog.whatsapp.activity.ChatActivity;
import com.viniciusog.whatsapp.adapter.ConversasAdapter;
import com.viniciusog.whatsapp.config.ConfiguracaoFirebase;
import com.viniciusog.whatsapp.model.Conversa;
import com.viniciusog.whatsapp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {


    private RecyclerView recyclerViewConversas;
    private List<Conversa> listaConversa = new ArrayList<>();
    private ConversasAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private ChildEventListener childEventListenerConversas;

    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        recyclerViewConversas = view.findViewById(R.id.recyclerListaConversas);

        //Configura adapter
        adapter = new ConversasAdapter(listaConversa, getActivity());

        //Configurar recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager(layoutManager);
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter(adapter);

        //Configurar envento de clique
        recyclerViewConversas.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewConversas,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //Agora o clique será sempre na lista atualizada
                        List<Conversa> listaConversaAtualizada = adapter.getConversas();
                        Conversa conversaSelecionada = listaConversaAtualizada.get(position);

                        if (conversaSelecionada.getIsGroup().equals("true")) {
                            Intent intent = new Intent(getActivity(), ChatActivity.class)
                                    .putExtra("chatGrupo", conversaSelecionada.getGrupo());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getActivity(), ChatActivity.class)
                                    .putExtra("chatContato", conversaSelecionada.getUsuarioExibicao());
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
        ));

        //Configurar conversasRef
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        conversasRef = database.child("conversas")
                .child(identificadorUsuario);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listaConversa.clear();
        recuperarConversas();

    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }

    public void pesquisarConversas(String texto) {

        List<Conversa> listaConversasBusca = new ArrayList<>();

        String nome = null;
        String ultimaMensagem = null;

        for (Conversa conversa : listaConversa) {

            //Conversa convencional
            if (conversa.getUsuarioExibicao() != null) {
                nome = conversa.getUsuarioExibicao().getNome().toLowerCase();
                ultimaMensagem = conversa.getUltimaMensagem().toLowerCase();

            } else { //Conversa de grupo
                nome = conversa.getGrupo().getNome().toLowerCase();
                ultimaMensagem = conversa.getUltimaMensagem().toLowerCase();
            }
            if (nome.contains(texto) || ultimaMensagem.contains(texto)) {
                listaConversasBusca.add(conversa);
            }
        }

        //Estamos 'refazendo' o adapter pois agora será listaConversaBusca que será exibida
        adapter = new ConversasAdapter(listaConversasBusca, getActivity());
        recyclerViewConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //Regarrega a lista de conversas original com todas as conversas
    public void recarregarConversas() {
        adapter = new ConversasAdapter(listaConversa, getActivity());
        recyclerViewConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void recuperarConversas() {

        childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Conversa conversa = dataSnapshot.getValue(Conversa.class);
                listaConversa.add(conversa);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}