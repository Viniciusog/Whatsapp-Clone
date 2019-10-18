package com.viniciusog.whatsapp.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.viniciusog.whatsapp.R;
import com.viniciusog.whatsapp.adapter.ContatosAdapter;
import com.viniciusog.whatsapp.config.ConfiguracaoFirebase;
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

    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_contatos, container, false);

        /* Configurações iniciais */
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos);
        //pegando referência dos usuários
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");

        //Configura adapter
        adapter = new ContatosAdapter( listaContatos, getActivity());

        //Configura recyclerView - getActivity() pega o contexto da activity em que o fragment está
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerViewListaContatos.setLayoutManager( layoutManager );
        recyclerViewListaContatos.setHasFixedSize( true );
        recyclerViewListaContatos.setAdapter( adapter );

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
        usuariosRef.removeEventListener( valueEventListenerContatos );
    }

    public void recuperarContatos() {

        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Percorre todos os usuários no firebase
                for (DataSnapshot dados : dataSnapshot.getChildren()) {

                    //Pega um objeto do tipo usuário
                    Usuario usuario = dados.getValue( Usuario.class );
                    listaContatos.add( usuario );
                }

                //Notificar que os dados foram modificados
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
