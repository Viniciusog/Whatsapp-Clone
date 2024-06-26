package com.viniciusog.whatsapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.viniciusog.whatsapp.Helper.Base64Custom;
import com.viniciusog.whatsapp.Helper.UsuarioFirebase;
import com.viniciusog.whatsapp.R;
import com.viniciusog.whatsapp.adapter.MensagensAdapter;
import com.viniciusog.whatsapp.config.ConfiguracaoFirebase;
import com.viniciusog.whatsapp.model.Conversa;
import com.viniciusog.whatsapp.model.Grupo;
import com.viniciusog.whatsapp.model.Mensagem;
import com.viniciusog.whatsapp.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView circleImageViewFotoChat;
    private TextView textViewNome;
    private Usuario usuarioDestinatario;
    private EditText editMensagem;
    private ImageView imageCamera;
    private ImageView imageGaleria;
    private DatabaseReference database;
    private StorageReference storage;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;
    private Grupo grupo;
    private Usuario usuarioRemetente;

    //id usuários remetentes e destinatário
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;

    private RecyclerView recyclerMensagens;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

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
        imageCamera = findViewById(R.id.imageCameraChat);
        imageGaleria = findViewById(R.id.imageGaleriaChat);

        /* Recuperar id usuário remetente */
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();
        usuarioRemetente = UsuarioFirebase.getDadosUsuarioLogado();

        /* Recuperar dados do usuário destinatário */
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            if (bundle.containsKey("chatGrupo")) {
                //CONVERSA COM O GRUPO
                grupo = (Grupo) bundle.getSerializable("chatGrupo");
                idUsuarioDestinatario = grupo.getId();

                String fotoGrupo = grupo.getFoto();

                if (fotoGrupo != null) {
                    Uri url = Uri.parse(fotoGrupo);
                    Glide.with(ChatActivity.this)
                            .load(url)
                            .into(circleImageViewFotoChat);
                } else {
                    circleImageViewFotoChat.setImageResource(R.drawable.padrao);
                }

                textViewNome.setText(grupo.getNome());

            } else {
                //CONVERSA COM USUÁRIO
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
        }

        //Configurar adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext());

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapter);

        //Pegando referência das mensagens do firebase
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        mensagensRef = database.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        //Evento de clique na câmera
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                //Se o celular for capaz de tirar foto ou selecionar uma foto da galeria...
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });

        //Evento de clique na galeria
        imageGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                //Se o celular for capaz de tirar foto ou selecionar uma foto da galeria...
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {
                switch (requestCode) {
                    case SELECAO_CAMERA: {
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    }
                    case SELECAO_GALERIA: {
                        Uri localImagemSelecionada = data.getData();
                        //getContentResolver dá acesso ao conteúdo do aplicativo, fotos da galeria, etc
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                    }
                }

                if (imagem != null) {
                    //Recuperar dados da imagem para o Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Criar nome da imagem
                    String nomeImagem = UUID.randomUUID().toString();

                    //Configurar referência do firebase
                    StorageReference imagemRef = storage.child("fotos")
                            .child(idUsuarioRemetente)
                            .child(nomeImagem);

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro", "Erro ao fazer upload de imagem!");
                            Toast.makeText(ChatActivity.this,
                                    "Erro ao fazer upload da imagem!",
                                    Toast.LENGTH_SHORT);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String dowloadUrl = taskSnapshot.getDownloadUrl().toString();

                            Mensagem mensagem = new Mensagem();
                            mensagem.setIdUsuario(idUsuarioRemetente);
                            //Esta mensagem não será usada
                            mensagem.setMensagem("imagem.jpeg");
                            mensagem.setImagem(dowloadUrl);

                            //Salva imagem para o remetente
                            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                            //Salva imagem para o destinatário
                            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                            Toast.makeText(ChatActivity.this,
                                    "Sucesso ao enviar mensagem!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void enviarMensagem(View view) {

        String textoMensagem = editMensagem.getText().toString();

        if (!textoMensagem.isEmpty()) {

            //Se for diferente de nulo significa que estamos enviando uma mensagem convencional
            if (usuarioDestinatario != null) {

                Mensagem msg = new Mensagem();
                msg.setIdUsuario(idUsuarioRemetente);
                msg.setMensagem(textoMensagem);

                //Salvar mensagem para o remetente
                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, msg);

                //Salvar mensagem para o destinatário
                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, msg);

                //Salvar conversa para o remetente
                salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, usuarioDestinatario, msg, false);

                salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, usuarioRemetente, msg, false);
            } else {

                for (Usuario membro : grupo.getMembros()) {

                    String idRemetendeGrupo = Base64Custom.codificarBase64( membro.getEmail() );
                    String idUsuarioLogadoFirebase = UsuarioFirebase.getIdentificadorUsuario();

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario( idUsuarioLogadoFirebase );
                    mensagem.setMensagem( textoMensagem );
                    mensagem.setNome( usuarioRemetente.getNome() );

                    //Salvar mensagem para o membro
                    salvarMensagem( idRemetendeGrupo, idUsuarioDestinatario, mensagem);

                    //Salvar conversa
                    //Neste caso o idUsuarioDestinatario e o usuarioDestinatário será o grupo
                    salvarConversa(idRemetendeGrupo, idUsuarioDestinatario, usuarioDestinatario, mensagem, true);
                }
            }


        } else {
            Toast.makeText(ChatActivity.this,
                    "Insira uma mensagem para enviar! ",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void salvarConversa(String idRemetente, String idDestinatario, Usuario usuarioExibicao, Mensagem mensagem,  boolean isGroup) {

        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idRemetente);
        conversaRemetente.setIdDestinatario(idDestinatario);
        conversaRemetente.setUltimaMensagem(mensagem.getMensagem());

        if ( isGroup ) { //Conversa de grupo
            conversaRemetente.setIsGroup("true");
            conversaRemetente.setGrupo(grupo);
        } else { //Conversa normal
            conversaRemetente.setIsGroup("false"); //Não precisa
            conversaRemetente.setUsuarioExibicao( usuarioExibicao );
        }
        conversaRemetente.salvar();
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
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }

    private void recuperarMensagem() {

        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        for (int permissaoResultados : grantResults) {
            if (permissaoResultados == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Permissões negadas");
        builder.setMessage("Para  utilizar o app é necessário aceiar as permissões");
        builder.setCancelable(false);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}