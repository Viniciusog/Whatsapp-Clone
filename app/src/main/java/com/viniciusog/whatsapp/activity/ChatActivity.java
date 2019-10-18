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
    }

    public void enviarMensagem(View view) {

        String textoMensagem = editMensagem.getText().toString();

        if (!textoMensagem.isEmpty()) {
            Mensagem msg = new Mensagem();
            msg.setIdUsuario( idUsuarioRemetente );
            msg.setMensagem( textoMensagem );

            //Salvar mensagem para o remetente
            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, msg);

        } else {
            Toast.makeText(ChatActivity.this,
                    "Insira uma mensagem para enviar! ",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg) {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = database.child("mensagens");

        mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);

        //limpar texto
        editMensagem.setText("");
    }
}