package com.albertocamillo.progettoSettembreReti;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Classe che rappresenta il client attraverso cui l'utente si connette al Server
 * 
 * @author Alberto Camillo
 */

public class FacebookClient {
	public static void main(String[] args) {
		Socket s = null;
		try {

			// ----- Configurazione della comunicazione con il Server ----- //

			s = new Socket("localhost", 8080);
			System.out.println("||-----------------------------------------||");
			System.out.println("||   FACEBOOK FOR DUMMIES v 1.0 - CLIENT   ||");
			System.out.println("||-----------------------------------------||\n");
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy HH:mm\n");
			String dateNow = formatter.format(currentDate.getTime());
			System.out.println(dateNow);
			System.out.println("Per connettersi inserire il comando 'login:NOME-COGNOME'.");

			// ----- Inizializzazione canali di input ----- //

			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			DataOutputStream scrivi = new DataOutputStream(s.getOutputStream());
			BufferedReader leggi = new BufferedReader(new InputStreamReader(s.getInputStream()));

			while (true) {

				// ----- Comunicazione col server e verifica comandi ----- //

				System.out.println();
				String inpu = input.readLine();
				String comando[] = inpu.split(":");
				boolean errato = true;

				// Registrazione
				if (comando[0].equals("register")) {
					errato = false;
					scrivi.writeBytes(inpu + "\r\n");
					String lettura = leggi.readLine();
					JSONObject obj = (JSONObject) JSONValue.parse(lettura);
					System.out.println(obj.get("reply"));
				}

				// Login
				if (comando[0].equals("login")) {
					errato = false;
					System.out.println();
					scrivi.writeBytes(inpu + "\r\n");
					String lettura = leggi.readLine();
					JSONObject obj = (JSONObject) JSONValue.parse(lettura);
					String benvenuto = (String) obj.get("reply");

					if (benvenuto.contains("Benvenuto")) {
						System.out.println(obj.get("reply"));

						// ----- Lettura messaggi ----- //

						while (true) {
							benvenuto = leggi.readLine();
							if (benvenuto.equals("nulla"))
								break;
							else {
								JSONObject email = (JSONObject) JSONValue.parse(benvenuto);
								System.out.println("Il tuo amico " + email.get("sender") + " ti ha scritto: '" + email.get("text") + "'\n");
							}
						}

						// ----- Consiglio dell'amico di un amico ----- //

						String consiglio = leggi.readLine();
						obj = (JSONObject) JSONValue.parse(consiglio);
						if (obj.get("amico").equals(""))
							System.out.println("Non hai amici di amici da poter suggerire al momento...\n");
						else {
							System.out.println("Ti suggerisco " + obj.get("amico"));

						}

						// ----- Thread per la lettura dei comandi dell'utente ----- //

						new InputThread(s).start();

						System.out.println();

						while (true) {

							boolean errato2 = true;
							inpu = input.readLine();

							// ----- Elenco vari comandi ----- //

							// Elenco amici
							if (inpu.equals("listfriend"))
								inpu += ":";
							comando = inpu.split(":");

							if (comando[0].equals("listfriend")) {
								errato2 = false;
								scrivi.writeBytes(inpu + "\r\n");
							}

							// Richiesta amicizia
							if (comando[0].equals("friend")) {
								errato2 = false;
								scrivi.writeBytes(inpu + "\r\n");
							}

							// Ricerca utente
							if (comando[0].equals("search")) {
								errato2 = false;
								scrivi.writeBytes(inpu + "\r\n");
							}

							// Richiesta comandi aiuto
							if (comando[0].equals("help")) {
								errato2 = false;
								scrivi.writeBytes(inpu + "\r\n");
							}

							// Invio messaggio a un amico
							if (comando[0].equals("msg")) {
								errato2 = false;
								int i = 0;
								for (i = 0; i < comando[1].length(); i++) {
									if (Character.isSpaceChar(comando[1].charAt(i)))
										break;
								}
								try {
									String mess = comando[1].substring(i + 1);
									String iniz = comando[1].substring(0, i);
									String utenza[] = iniz.split("-");
									String utente = utenza[0] + " " + utenza[1];
									scrivi.writeBytes(comando[0] + ":" + utente + "_" + mess + "\r\n");
								} catch (Exception e) {
									System.out.println("Errore nella composizione del messaggio!!\nPrego, riscrivere.\n");
								}
							} else if (errato2) {
								System.out.println("Comando o digitazione errati.");
								System.out.println("Per conoscere i comandi possibili digitare 'help'.\n");

							}
						}
					} else
						System.out.println(obj.get("reply"));
				} else if (errato) {
					System.out.println("Comando o digitazione errati.");

				}
			}
		} catch (IOException e) {
			System.err.println("Rilevati errori. (" + e + ")");

		}
	}
}
