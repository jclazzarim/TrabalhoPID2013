package br.unioeste.pid.utils;

public class Circulo {

	private int x; // coordenada do centro do circulo
	private int y;
	private int raio;

	public Circulo(int x, int y, int raio) {
		this.x = x;
		this.y = y;
		this.raio = raio;
	}

	public int getRaio() {
		return raio;
	}

	public void setRaio(int raio) {
		this.raio = raio;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

}
