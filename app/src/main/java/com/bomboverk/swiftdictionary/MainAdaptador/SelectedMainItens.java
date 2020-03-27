package com.bomboverk.swiftdictionary.MainAdaptador;

import com.bomboverk.swiftdictionary.db.Dicionario;

public class SelectedMainItens {

    private ViewHolderDicionarios viewHolderDicionarios;
    private Dicionario dicionario;

    public SelectedMainItens(ViewHolderDicionarios viewHolderDicionarios, Dicionario dicionario) {
        this.viewHolderDicionarios = viewHolderDicionarios;
        this.dicionario = dicionario;
    }

    public ViewHolderDicionarios getViewHolderDicionarios() {
        return viewHolderDicionarios;
    }

    public void setViewHolderDicionarios(ViewHolderDicionarios viewHolderDicionarios) {
        this.viewHolderDicionarios = viewHolderDicionarios;
    }

    public Dicionario getDicionario() {
        return dicionario;
    }

    public void setDicionario(Dicionario dicionario) {
        this.dicionario = dicionario;
    }
}
