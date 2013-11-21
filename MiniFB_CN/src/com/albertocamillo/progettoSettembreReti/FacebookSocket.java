package com.albertocamillo.progettoSettembreReti;

import java.net.Socket;

/**
 * Classe che utilizza FristMemory e attraverso la quale viene realizzato il servizio di
 * messaggistica istantanea.
 * 
 * @author Alberto Camillo
 */

public class FacebookSocket {
	private Socket s;
	private String id;

	/**
	 * Istanzia un oggetto di tipo FacebookSocket.
	 * 
	 * @param s definisce la Socket
	 * @param id definisce l'id della Socket
	 */
	public FacebookSocket(Socket s, String id) {
		this.s = s;
		this.id = id;
	}

	/**
	 * Ritorna l'id della Socket su cui il metodo è invocato.
	 * 
	 * @return l'id della Socket
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Ritorna la Socket su cui il metodo è invocato.
	 * 
	 * @return la Socket
	 */
	// Contenitore di socket per tracciamento
	public Socket getSocket() {
		return this.s;
	}

	public String toString() {
		return this.s.toString() + ", " + id;
	}

}