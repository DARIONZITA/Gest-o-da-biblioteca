package com.example.bibliotecaapi.service;

public class MultaService {

    public static final int precoMultaDia = 800;

    public static double multa(int diasAtraso){
        return diasAtraso*precoMultaDia;
    }
}
