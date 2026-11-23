package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AcessoAdapter extends RecyclerView.Adapter<AcessoAdapter.AcessoViewHolder> {

    private final List<Acesso> acessos;
    private final Runnable aoAtualizar;

    public AcessoAdapter(List<Acesso> acessos, Runnable aoAtualizar) {
        this.acessos = acessos;
        this.aoAtualizar = aoAtualizar;
    }

    public static class AcessoViewHolder extends RecyclerView.ViewHolder {

        TextView tvPlataforma;
        TextView tvAcesso;

        public AcessoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlataforma = itemView.findViewById(R.id.tvPlataforma);
            tvAcesso = itemView.findViewById(R.id.tvAcesso);
        }
    }

    @NonNull
    @Override
    public AcessoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_acesso, parent, false);
        return new AcessoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AcessoViewHolder holder, int position) {
        Acesso acesso = acessos.get(position);

        holder.tvPlataforma.setText(acesso.getPlataforma());
        holder.tvAcesso.setText(acesso.getAcesso());

        // abre a janela do acesso (imagem 5)
        holder.itemView.setOnClickListener(v ->
                JanelaAcesso.mostrar(v.getContext(), acesso, aoAtualizar));
    }

    @Override
    public int getItemCount() {
        return acessos.size();
    }
}
