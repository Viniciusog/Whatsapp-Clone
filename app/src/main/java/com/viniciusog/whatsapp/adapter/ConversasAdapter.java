package com.viniciusog.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.viniciusog.whatsapp.R;
import com.viniciusog.whatsapp.model.Conversa;
import com.viniciusog.whatsapp.model.Grupo;
import com.viniciusog.whatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    List<Conversa> conversas;
    Context context;

    public ConversasAdapter(List<Conversa> lista, Context c) {
        conversas = lista;
        context = c;
    }

    public List<Conversa> getConversas() {
        return this.conversas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Conversa conversa = conversas.get(position);

        holder.ultimaMensagem.setText(conversa.getUltimaMensagem());

        if (conversa.getIsGroup().equals("true")) {

            Grupo grupo = conversa.getGrupo();
            holder.nome.setText(grupo.getNome());

            if (grupo.getFoto() != null) {
                Uri uri = Uri.parse(grupo.getFoto());

                Glide.with(context)
                        .load(uri)
                        .into(holder.foto);
            } else {
                holder.foto.setImageResource(R.drawable.padrao);
            }

        } else {
            Usuario usuario = conversa.getUsuarioExibicao();

            if (usuario != null) {
                holder.nome.setText(usuario.getNome());

                if (usuario.getFoto() != null) {
                    Uri uri = Uri.parse(usuario.getFoto());

                    Glide.with(context)
                            .load(uri)
                            .into(holder.foto);
                } else {
                    holder.foto.setImageResource(R.drawable.padrao);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView foto;
        private TextView nome, ultimaMensagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //Poderiamos ter abstraido o adapter de contatos para utilizar de forma correta em ambas as partes
            foto = itemView.findViewById(R.id.imageViewFotoContato);
            nome = itemView.findViewById(R.id.textNomeContato);
            ultimaMensagem = itemView.findViewById(R.id.textEmailContato);
        }
    }
}
