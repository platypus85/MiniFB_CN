package com.albertocamillo.progettoSettembreReti;

/**
 * Classe rappresenta i messaggi email.
 * 
 * @author Alberto Camillo
 */

public class Email {
	private String mittente;
	private String destinatario;
	private String data;
	boolean invio;

	/**
	 * Istanzia un oggetto di tipo Email.
	 * 
	 * @param m definisce il mittente dell'email
	 * @param d definisce il destinatario dell'email
	 * @param dt definisce il contenuto del messaggio dell'email
	 */
	public Email(String m, String d, String dt) {
		mittente = m;
		destinatario = d;
		data = dt;
		invio = false;
	}

	/**
	 * Istanzia un oggetto di tipo Email.
	 * 
	 * @param m definisce il mittente dell'email
	 * @param d definisce il destinatario dell'email
	 * @param dt definisce il contenuto del messaggio dell'email
	 * @param b specifica se il messaggio è stata ricevuto o meno
	 */
	public Email(String m, String d, String dt, boolean b) {
		mittente = m;
		destinatario = d;
		data = dt;
		invio = b;
	}

	/**
	 * Ritorna il mittente dell'email su cui il metodo è invocato.
	 * 
	 * @return il mittente dell'email
	 */
	public String getMittente() {
		return mittente;
	}

	/**
	 * Ritorna il destinatario dell'email su cui il metodo è invocato.
	 * 
	 * @return il destinatario dell'email
	 */
	public String getDestinatario() {
		return destinatario;
	}

	/**
	 * Ritorna il contenuto del messaggio dell'email su cui il metodo è invocato.
	 * 
	 * @return il contenuto del messaggio dell'email
	 */
	public String getEmail() {
		return data;
	}

	/**
	 * Ritorna lo stato della ricezione dell'email su cui il metodo è invocato.
	 * 
	 * @return lo stato della ricezione dell'email
	 */
	public boolean getStato() {
		return invio;
	}

	/**
	 * Permette di settare a true l'invio dell'email
	 */
	public void setInvio() {
		invio = true;
	}

	/**
	 * Restituisce una stringa rappresentate l'oggetto Email.
	 * 
	 * @return una stringa rappresentate l'oggetto Email
	 */
	public String toString() {
		return mittente + "_" + destinatario + "_" + data + "_" + invio;
	}

}