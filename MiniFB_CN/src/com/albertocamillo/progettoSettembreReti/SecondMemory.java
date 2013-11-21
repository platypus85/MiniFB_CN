package com.albertocamillo.progettoSettembreReti;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;

/**
 * Classe che gestisce la memoria secondaria, ovvero la parte di server che si occupa dell'invio /
 * ricezione dei vari messaggi da parte degli utenti.
 * 
 * @author Alberto Camillo
 */

public class SecondMemory {
	private ArrayList<Email> mails;
	private Socket socket;
	private String destin;
	private BufferedReader lettura;

	/**
	 * Istanzia un oggetto di tipo SecondMemory per l'invio di email a un determinato utente.
	 * 
	 * @param s definisce la Socket del destinatario del messaggio
	 * @param destin definisce il destinatario del messaggio
	 * 
	 */
	public SecondMemory(Socket s, String destin) {
		this.destin = destin;
		socket = s;
		mails = new ArrayList<Email>();

		// ----- Lettura dal file 'FacebookMail.txt' delle email e registrazione in memoria ----- //

		try {
			lettura = new BufferedReader(new FileReader("FacebookMail.txt"));
			String leggileggi = "";
			boolean invio;
			while ((leggileggi = lettura.readLine()) != null) {
				String permail[] = leggileggi.split("_");
				if (permail[3].equals("true"))
					invio = true;
				else
					invio = false;
				Email e = new Email(permail[0], permail[1], permail[2], invio);
				mails.add(e);
			}
		} catch (IOException e) {
			System.out.println("File non trovato...");
		}

	}

	/**
	 * Crea un DataOutputStream verso la Socket destinatario. Dopo aver verificato se ci sono
	 * possibili messaggi da inviare, procede in quest'operazione.
	 */
	@SuppressWarnings("unchecked")
	public void scriviMail() {

		// ----- Consegna delle email al destinatario ----- //

		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			if (mails.size() > 0) {
				JSONObject email = new JSONObject();
				for (int i = 0; i < mails.size(); i++)
					if (mails.get(i).getDestinatario().equals(destin)) {
						if (mails.get(i).getStato() == false) {
							email.put("sender", mails.get(i).getMittente());
							email.put("text", mails.get(i).getEmail());
							out.writeBytes(email.toString() + "\r\n");
							mails.get(i).setInvio(); // email settata a true (per ovviare a un invio
														// doppio)
						}
					}
				// Fine lettura
				out.writeBytes("nulla" + "\r\n");
			} else
				// Fine lettura in caso di assenza di messaggi nel file
				out.writeBytes("nulla" + "\r\n");

		} catch (IOException e) {

		}
	}

	/**
	 * Viene utilizzato in uscita al termine dell'utilizzo della Memoria Secondaria. In questo modo,
	 * scrivendo su memoria fisica gli aggiornamenti, ci assicuriamo che nel prossimo login le
	 * modifiche appena effettuate restino permanenti.
	 */
	public void chiudiMemory() {
		try {

			// ----- Riscrittura aggiornamenti e chiusura memoria secondaria ----- //

			PrintWriter writer = new PrintWriter(new FileWriter("FacebookMail.txt"));
			for (int i = 0; i < mails.size(); i++) {
				writer.println(mails.get(i).toString());
			}
			writer.close();
		} catch (IOException e) {

		}
	}

}