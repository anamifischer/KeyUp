package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private final List<Categoria> categorias;

    public CategoriaAdapter(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {

        TextView tvNome;
        TextView tvQtd;
        View caixa;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeCategoria);
            tvQtd = itemView.findViewById(R.id.tvQtdAcessos);
            caixa = itemView.findViewById(R.id.caixaCategoria);
        }
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        Categoria categoria = categorias.get(position);

        holder.tvNome.setText(categoria.getNome());
        Repositorio repo = new Repositorio(holder.itemView.getContext());
        holder.tvQtd.setText(holder.itemView.getContext()
                .getString(R.string.txt_qtd_acessos, repo.contarAcessos(categoria.getId())));

        holder.caixa.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TelaCategoriaActivity.class);
            intent.putExtra("id_categoria", categoria.getId());
            intent.putExtra("nome_categoria", categoria.getNome());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }
}
