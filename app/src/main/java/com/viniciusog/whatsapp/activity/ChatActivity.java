package com.viniciusog.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.viniciusog.whatsapp.Helper.Base64Custom;
import com.viniciusog.whatsapp.Helper.UsuarioFirebase;
import com.viniciusog.whatsapp.R;
import com.viniciusog.whatsapp.adapter.MensagensAdapter;
import com.viniciusog.whatsapp.config.ConfiguracaoFirebase;
import com.viniciusog.whatsapp.model.Mensagem;
import com.viniciusog.whatsapp.model.Usuario;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView circleImageViewFotoChat;
    private TextView textViewNome;
    private Usuario usuarioDestinatario;
    private EditText editMensagem;
    private DatabaseReference database;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;

    //id usuários remetentes e destinatário
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;

    private RecyclerView recyclerMensagens;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        /* Configurações da ToolBar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Tira título da toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Código gerado através da hierarquia
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Configurações iniciais */
        textViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFotoChat = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);

        /* Recuperar id usuário remetente */
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();

        /* Recuperar dados do usuário destinatário */
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato");

            textViewNome.setText(usuarioDestinatario.getNome());

            String foto = usuarioDestinatario.getFoto();

            if (foto != null) {
                Uri url = Uri.parse(foto);
                Glide.with(ChatActivity.this)
                        .load(url)
                        .into(circleImageViewFotoChat);
            } else {
                circleImageViewFotoChat.setImageResource(R.drawable.padrao);
            }

            /* Recuperar id do usuário destinatário */
            idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioDestinatario.getEmail());
        }

        //Configurar adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext());

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager( layoutManager );
        recyclerMensagens.setHasFixedSize( true );
        recyclerMensagens.setAdapter( adapter );

        //Pegando referência das mensagens do firebase
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        mensagensRef = database.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);
    }

    public void enviarMensagem(View view) {

        String textoMensagem = editMensagem.getText().toString();

        if (!textoMensagem.isEmpty()) {
            Mensagem msg = new Mensagem();
            msg.setIdUsuario( idUsuarioRemetente );
            msg.setMensagem( textoMensagem );

            //Salvar mensagem para o remetente
            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, msg);

            //Salvar mensagem para o destinatário
            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, msg);

        } else {
            Toast.makeText(ChatActivity.this,
                    "Insira uma mensagem para enviar! ",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg) {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        mensagensRef = database.child("mensagens");

        mensagensRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);

        //limpar texto
        editMensagem.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagem();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener( childEventListenerMensagens );
    }

    private void recuperarMensagem() {

    childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Mensagem mensagem = dataSnapshot.getValue( Mensagem.class );
            mensagens.add( mensagem );
            adapter.notifyDataSetChanged(); //Adapter será atualizado
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