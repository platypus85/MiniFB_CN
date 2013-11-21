package com.albertocamillo.progettoSettembreReti;

import java.util.ArrayList;

/**
 * Classe che rappresenta la memoria primaria del nostro Server in cui le Socket verranno
 * predisposte per lo scambio di messaggi istantanei.
 * 
 * @author Alberto Camillo
 */

public class FirstMemory {
	protected ArrayList<FacebookSocket> socket;

	/**
	 * Istanzia un oggetto di tipo FirstMemory che, a sua volta, definisce un ArrayList di
	 * FacebookSocket.
	 * 
	 */
	public FirstMemory() {
		socket = new ArrayList<FacebookSocket>();
	}

	/**
	 * Permette connettere più Socket aggiungendole all'ArrayList di FacebookSocket.
	 */
	public void memoriaConnect(FacebookSocket c) {
		socket.add(c);
	}

}