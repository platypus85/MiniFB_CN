package com.albertocamillo.progettoSettembreReti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.simple.*;

/**
 * Classe-thread che permette di ciclare sulle possibili risposte del client, una volta loggato.
 * 
 * @author Alberto Camillo
 */

public class InputThread extends Thread {
	private Socket s;

	/**
	 * Istanzia un oggetto di tipo InputThread che estende Thread.
	 * 
	 * @param s definisce la Socket tramite cui agià il Thread
	 */
	public InputThread(Socket s) {
		this.s = s;
	}

	/**
	 * Metodo che cicla sulle possibili risposte del client una volta loggato.
	 * 
	 */
	public void run() {
		try {
			BufferedReader leggiServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String lettura = "";
			boolean sempre = true;
			do {

				// ----- Parsing JSON delle risposte del Server ----- //

				lettura = leggiServer.readLine();
				JSONObject obj = (JSONObject) JSONValue.parse(lettura);

				// Messaggio istantaneo (o email)
				if (obj.containsKey("sender") && obj.containsKey("text")) {
					System.out.println("Il tuo amico " + obj.get("sender") + " ti ha scritto: '" + obj.get("text") + "'");
				}
				// Messaggio di risposta basato sui vari comandi
				if (obj.containsKey("reply")) {
					System.out.println(obj.get("reply"));
				}

				// Messaggio contenente le istruzioni di 'help'
				if (obj.containsKey("help")) {
					JSONArray arr = (JSONArray) obj.get("help");
					for (int i = 0; i < arr.size(); i++) {
						System.out.println(arr.get(i));
					}
				}

				// Messaggio contenente la lista di amici
				if (obj.containsKey("friendlist")) {
					JSONArray arr = (JSONArray) obj.get("friendlist");
					String amici = "";

					for (int i = 0; i < arr.size(); i++) {
						if (i == arr.size() - 1)
							amici = amici + arr.get(i);
						else
							amici = amici + arr.get(i) + ", ";
					}

					if (amici.isEmpty()) {
						System.out.println("Non hai ancora amici!" + "\nAggiungine qualcuno con il comando 'friend:NOMEAMICO-COGNOMEAMICO'");
					} else {
						System.out.println("I tuoi amici: " + amici);
					}
				}

				System.out.println();

			} while (sempre);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
