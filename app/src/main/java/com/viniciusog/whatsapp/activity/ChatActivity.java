package com.viniciusog.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.viniciusog.whatsapp.R;
import com.viniciusog.whatsapp.model.Usuario;

import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView circleImageViewFotoChat;
    private TextView textViewNome;
    private Usuario usuarioDestinatario;

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

        /* Recuperar dados do usuário destinatário */
        Bundle bundle = getIntent().getExtras();

        if ( bundle != null ) {
            usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato");

            textViewNome.setText( usuarioDestinatario.getNome() );

            String foto = usuarioDestinatario.getFoto();

            if (foto != null) {
                Uri url = Uri.parse( foto );
                Glide.with(ChatActivity.this)
                        .load( url )
                        .into( circleImageViewFotoChat );
            } else {
                circleImageViewFotoChat.setImageResource( R.drawable.padrao );
            }
        }
    }
}