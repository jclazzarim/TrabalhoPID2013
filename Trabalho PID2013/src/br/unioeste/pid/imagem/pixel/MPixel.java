package br.unioeste.pid.imagem.pixel;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jfree.data.xy.IntervalXYDataset;

public interface MPixel {
    public void lerImagem(FileInputStream fis) throws IOException;

    public void pintarImagem(Graphics g);

    public void grayScale();

    public void passaBaixa(int value);

    public void passaAlta();

    public void salvarImagem(File img);

    public IntervalXYDataset getHistograma();
    
}
