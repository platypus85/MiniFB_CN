package com.albertocamillo.progettoSettembreReti;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Classe Server principale che gestisce memorie secondarie per la gestione di invio messaggi e la
 * gestione delle connessioni socket. Gestisce inoltre il multithreading e il grafo attraverso la
 * classe FacebookGrafo con l'obiettivo di tener traccia di utenti e amicizie.
 * 
 * @author Alberto Camillo
 */

public class FacebookServer {
	public static void main(String[] args) {
		ServerSocket ss = null;
		Socket s = null;
		try {

			// ----- Caricamento delle memorie di gestione messaggistica e amicizie ----- //

			FacebookGrafo fg = new FacebookGrafo();
			FirstMemory pm = new FirstMemory();
			System.out.println("||-----------------------------------------||");
			System.out.println("||   FACEBOOK FOR DUMMIES v 1.0 - SERVER   ||");
			System.out.println("||-----------------------------------------||\n");
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy HH:mm\n");
			String dateNow = formatter.format(currentDate.getTime());
			System.out.println(dateNow);
			System.out.println("Caricamento memoria primaria   ---> OK");
			System.out.println("Caricamento memoria secondaria ---> OK\n");
			System.out.println("Server pronto!");
			ss = new ServerSocket(8080);

			// ----- Multithreading gestito tramite la classe ServerThreads ----- //

			while (true) {
				s = ss.accept();
				new ServerThread(s, fg, pm).start();
			}
		} catch (Exception e) {
			System.err.println("Errore di avviamento server (" + e + ").\nLa porta potrebbe essere già in uso. Riavviare");

			try {
				// Chiusura del server
				ss.close();
			} catch (Exception e2) {
				System.err.println("Errore del server (" + e + ").");
			}
		}
	}
}