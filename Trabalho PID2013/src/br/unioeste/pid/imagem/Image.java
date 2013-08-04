package br.unioeste.pid.imagem;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jfree.data.xy.IntervalXYDataset;

import br.unioeste.pid.imagem.pixel.MPixel;
import br.unioeste.pid.imagem.pixel.MPixel24B;
import br.unioeste.pid.imagem.pixel.MPixel8B;

public class Image {
	  //Para imagem 8bits

    boolean p8bits;
    boolean p4bits;
    //Cabeçalho de Arquivo
    static byte[] BfType; //2
    static byte[] BfSize; //4
    static byte[] BfReser1; //2
    static byte[] BfReser2; //2
    static byte[] BfOffSetBits; //4
    //Cabeçalho de Imagem
    static byte[] BiSize; //4
    static byte[] BiWidth; //4
    static byte[] BiHeight; //4
    static byte[] BiPlanes; //2
    static byte[] BiBitCount; //2
    static byte[] BiCompress; //4
    static byte[] BiSizeImag; //4
    static byte[] BiXPPMeter; //4
    static byte[] BiYPPMeter; //4
    static byte[] BiClrUsed; //4
    static byte[] BiClrImpor; //4
    static MPixel mPixel;
    //Altura e Largura da imagem
    int Width;
    int Height;

    public Image() {
        //Para imagem 8bits
        p8bits = false;
        //Cabeçalho de Arquivo
        BfType = new byte[2];
        BfSize = new byte[4];
        BfReser1 = new byte[2];
        BfReser2 = new byte[2];
        BfOffSetBits = new byte[4];

        //Cabeçalho de Imagem
        BiSize = new byte[4];
        BiWidth = new byte[4];
        BiHeight = new byte[4];
        BiPlanes = new byte[2];
        BiBitCount = new byte[2];
        BiCompress = new byte[4];
        BiSizeImag = new byte[4];
        BiXPPMeter = new byte[4];
        BiYPPMeter = new byte[4];
        BiClrUsed = new byte[4];
        BiClrImpor = new byte[4];
        //Área de dados como null
        mPixel = new MPixel() {
			
			@Override
			public void salvarImagem(File img) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void pintarImagem(Graphics g) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void passaBaixa(int value) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void passaAlta() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void lerImagem(FileInputStream fis) throws IOException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void grayScale() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public IntervalXYDataset getHistograma() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Color[][] getMatrix() {
				// TODO Auto-generated method stub
				return null;
			}
		}; 
        //Altura e Largura da imagem
        Width = 0;
        Height = 0;
    }

    public boolean ReadFile(FileInputStream fis) {
        //Le os cabeçalhos
        try {
            //Cabeçalho de Arquivo
            fis.read(BfType);
            fis.read(BfSize);
            fis.read(BfReser1);
            fis.read(BfReser2);
            fis.read(BfOffSetBits);

            //Cabeçalho de Imagem
            fis.read(BiSize);
            fis.read(BiWidth);
            fis.read(BiHeight);
            fis.read(BiPlanes);
            fis.read(BiBitCount);
            fis.read(BiCompress);
            fis.read(BiSizeImag);
            fis.read(BiXPPMeter);
            fis.read(BiYPPMeter);
            fis.read(BiClrUsed);
            fis.read(BiClrImpor);
        } catch (IOException ex) {
            Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Erro Fatal: Não foi possível abrir o arquivo da imagem.\n" + ex);
        }
        //Confere os dados


        //Tipo de arquivo 424D
        if ((Integer.compare(((byte) BfType[0] & 0xFF), ((byte) 66 & 0xFF)) != 0) || (Integer.compare(((byte) BfType[1] & 0xFF), ((byte) 77 & 0xFF)) != 0)) {
            JOptionPane.showMessageDialog(null, "Erro Ab01: Não foi possível abrir a imagem\nO formato não foi reconhecido\nDica: Confira se o formato da imagem é bitmap de 8 ou 24bit de cores.");
            return false;
        }

        //Confere Reser1 e 2
        if ((Integer.compare(((byte) BfReser1[0] & 0xFF), ((byte) 00 & 0xFF)) != 0) || (Integer.compare((byte) BfReser2[1] & 0xFF, ((byte) 00 & 0xFF)) != 0)) {
            JOptionPane.showMessageDialog(null, "Erro Ab02: Não foi possível abrir a imagem\nCampos reservados da imagem não possuem o valor esperado.\n\nEsperado\nBfReser1: 00\nBfReser2: 00");
            return false;
        }

        //Tamanho do cabeçalho
        //Verifica se é exatamente 8bits
        if (Integer.compare((byte) BiBitCount[0] & 0xFF, (byte) 8 & 0xFF) == 0) {
            p8bits = true;
        } else {
//            //Verifica se é exatamente 4bits
//            if (Integer.compare((byte) BiBitCount[0] & 0xFF, (byte) 4 & 0xFF) == 0) {
//                p4bits = true;
//            } else {
            //Verifica se é exatamente 24bits
            if (Integer.compare((byte) BiBitCount[0] & 0xFF, (byte) 24 & 0xFF) != 0) {
                JOptionPane.showMessageDialog(null, "Erro Ab03: Não foi possível abrir a imagem\nQuantidade de cores esperadas é incompatível.\nDica: Confira se o formato da imagem é bitmap de 8 ou 24bit de cores.");
                return false;
            }
//            }
        }

        //Calcula altura e largura
        Width = ((256 * ((byte) BiWidth[1] & 0xFF))
                + ((byte) BiWidth[0] & 0xFF)
                + (65536 * ((byte) BiWidth[2] & 0xFF))
                + (16777216 * ((byte) BiWidth[3] & 0xFF)));

        Height = ((256 * ((byte) BiHeight[1] & 0xFF))
                + ((byte) BiHeight[0] & 0xFF)
                + (65536 * ((byte) BiHeight[2] & 0xFF))
                + (16777216 * ((byte) BiHeight[3] & 0xFF)));

        try {
            if (p8bits) {
                mPixel = new MPixel8B(Width, Height, cabecalho());
                mPixel.lerImagem(fis);

            } else {
                mPixel = new MPixel24B(Height, Width, cabecalho());
                mPixel.lerImagem(fis);
            }
        } catch (IOException ex) {
            Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Erro Fatal: Não foi possível abrir o arquivo da imagem.\n" + ex);
        }

        return true;
    }

//    public void convertYUV() {
//        MPixelYUV mp = new MPixelYUV(Height, Width);
//        mp.RGBtoYUV((MPixel24B) mPixel);
//        mPixel = mp;
//    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int Height) {
        this.Height = Height;
    }

    public int getWidth() {
        return Width;
    }

    public void setWidth(int Width) {
        this.Width = Width;
    }

    public boolean isP8bits() {
        return p8bits;
    }

    public void setP8bits(boolean p8bits) {
        this.p8bits = p8bits;
    }

    public MPixel getmPixel() {
        return mPixel;
    }

    public void setmPixel(MPixel mPixel) {
        this.mPixel = mPixel;
    }

    public byte[] cabecalho() {
        byte[] cabecalho = new byte[54];
        cabecalho[0] = BfType[0];
        cabecalho[1] = BfType[1];

        cabecalho[2] = BfSize[0];
        cabecalho[3] = BfSize[1];
        cabecalho[4] = BfSize[2];
        cabecalho[5] = BfSize[3];

        cabecalho[6] = BfReser1[0];
        cabecalho[7] = BfReser1[1];
        cabecalho[8] = BfReser2[0];
        cabecalho[9] = BfReser2[1];

        cabecalho[10] = BfOffSetBits[0];
        cabecalho[11] = BfOffSetBits[1];
        cabecalho[12] = BfOffSetBits[2];
        cabecalho[13] = BfOffSetBits[3];

        //Cabeçalho de Imagem
        cabecalho[14] = BiSize[0];
        cabecalho[15] = BiSize[1];
        cabecalho[16] = BiSize[2];
        cabecalho[17] = BiSize[3];

        cabecalho[18] = BiWidth[0];
        cabecalho[19] = BiWidth[1];
        cabecalho[20] = BiWidth[2];
        cabecalho[21] = BiWidth[3];

        cabecalho[22] = BiHeight[0];
        cabecalho[23] = BiHeight[1];
        cabecalho[24] = BiHeight[2];
        cabecalho[25] = BiHeight[3];

        cabecalho[26] = BiPlanes[0];
        cabecalho[27] = BiPlanes[1];

        cabecalho[28] = BiBitCount[0];
        cabecalho[29] = BiBitCount[1];

        cabecalho[30] = BiCompress[0];
        cabecalho[31] = BiCompress[1];
        cabecalho[32] = BiCompress[2];
        cabecalho[33] = BiCompress[3];

        cabecalho[34] = BiSizeImag[0];
        cabecalho[35] = BiSizeImag[1];
        cabecalho[36] = BiSizeImag[2];
        cabecalho[37] = BiSizeImag[3];

        cabecalho[38] = BiXPPMeter[0];
        cabecalho[39] = BiXPPMeter[1];
        cabecalho[40] = BiXPPMeter[2];
        cabecalho[41] = BiXPPMeter[3];

        cabecalho[42] = BiYPPMeter[0];
        cabecalho[43] = BiYPPMeter[1];
        cabecalho[44] = BiYPPMeter[2];
        cabecalho[45] = BiYPPMeter[3];

        cabecalho[46] = BiClrUsed[0];
        cabecalho[47] = BiClrUsed[1];
        cabecalho[48] = BiClrUsed[2];
        cabecalho[49] = BiClrUsed[3];

        cabecalho[50] = BiClrImpor[0];
        cabecalho[51] = BiClrImpor[1];
        cabecalho[52] = BiClrImpor[2];
        cabecalho[53] = BiClrImpor[3];

        return cabecalho;
    }

    public void Salvar(File Img) {
        mPixel.salvarImagem(Img);
    }

}
