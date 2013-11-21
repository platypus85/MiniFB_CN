package com.albertocamillo.progettoSettembreReti;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Classe-thread principale che viene lanciata dal Server.
 * 
 * @author Alberto Camillo
 */

public class ServerThread extends Thread {
	private Socket s;
	private FacebookGrafo fg;
	private FirstMemory pm;

	/**
	 * Istanzia un oggetto di tipo ServerThread, thread principale eseguito dal Server.
	 * 
	 * @param s definisce la socket di connessione
	 * @param gg il Grafo da cui prelevare le informazioni e su cui scriverle
	 * @param hs definisce la memoria primaria che gestisce le connessioni
	 */
	public ServerThread(Socket s, FacebookGrafo gg, FirstMemory hs) {
		this.s = s;
		this.fg = gg;
		this.pm = hs;
	}

	// ----- Funzione random, per il suggerimento amici di amici ----- //

	/**
	 * Permette di ottenere un numero random per il suggerimento di un amico di amico
	 * 
	 * @param num è un valore basato sul numero di amici
	 * @return un numero casuale che permetterà il suggerimento di un utente
	 */
	public int randomNumber(int num) {
		int ritorno = -1;
		if (num == 1 || num < 0)
			ritorno = 0;
		else {
			double r = Math.random();
			ritorno = (int) (r * num);
		}
		return ritorno;
	}

	// ----- Analisi e risposta al comando inserito dall'utente ----- //

	/**
	 * Determina le possibili risposte del Server alle azioni compiute dal Client definendo i vari
	 * messaggi e azioni in base al comando digitato.
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		PrintWriter wr = null;
		try {
			FacebookSocket c = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			DataOutputStream scrivi = new DataOutputStream(s.getOutputStream());
			wr = new PrintWriter(new FileWriter("GrafoFacebook.dgs", true));

			while (true) {
				try {
					String lettura = br.readLine();
					String comando[] = lettura.split(":");

					// Registrazione
					if (comando[0].equals("register")) {
						boolean aggiungere = true;
						JSONObject obj = new JSONObject();
						String ut[] = (comando[1].replaceAll(" ", "")).split("-");
						String nome = ut[0].substring(0, 1).toUpperCase() + ut[0].substring(1).toLowerCase();
						String cognome = ut[1].substring(0, 1).toUpperCase() + ut[1].substring(1).toLowerCase();
						try {

							// Controllo sui nodi (per eventuali doppioni)
							fg.addNode(nome + " " + cognome);
						} catch (org.graphstream.graph.IdAlreadyInUseException e) {
							obj.put("reply", "Utente già presente; non è possibile registrarsi.");
							scrivi.writeBytes(obj.toString() + "\r\n");
							aggiungere = false;
						}
						if (aggiungere) {
							obj.put("reply", "Registrazione avvenuta con successo.");
							scrivi.writeBytes(obj.toString() + "\r\n");
							wr.println("an " + "\"" + nome + " " + cognome + "\"");
							wr.flush();
						}
					}

					// Login
					if (comando[0].equals("login")) {
						JSONObject obj = new JSONObject();
						String ut[] = (comando[1].replaceAll(" ", "")).split("-");
						String nome = ut[0].substring(0, 1).toUpperCase() + ut[0].substring(1).toLowerCase();
						String cognome = ut[1].substring(0, 1).toUpperCase() + ut[1].substring(1).toLowerCase();
						if (fg.verificaLogin(nome + " " + cognome)) {
							String destinante = nome + " " + cognome;
							obj.put("reply", "Hai effettuato il login con successo\r\nBenvenuto " + nome + " " + cognome);
							scrivi.writeBytes(obj.toString() + "\r\n");
							c = new FacebookSocket(s, destinante);
							pm.memoriaConnect(c);

							// Memoria secondaria per invio mail al destinatario

							SecondMemory sm = new SecondMemory(s, destinante);
							sm.scriviMail();
							sm.chiudiMemory();

							// ----- Ricerca randomica amici di amici ----- //

							String amici = fg.friendOfMine((c.getId()));
							if (amici.equals("")) { // SE AMICI NON NE HO INVIARE NON POTRO'
								obj.put("amico", amici);
								scrivi.writeBytes(obj.toString() + "\r\n");
							} else {
								String amicigruppo[] = amici.split(";");
								int n = amicigruppo.length;
								int questo = this.randomNumber(n);
								obj.put("amico", amicigruppo[questo].toString());
								scrivi.writeBytes(obj.toString() + "\r\n");
							}
							System.out.println(obj.toString());

							// ----- Dialogo Client-Server una volta avvenuto il login ----- //

							while (true) {
								lettura = br.readLine();
								comando = lettura.split(":");

								// ----- Elenco vari comandi ----- //

								// Ricerca utente
								if (comando[0].equals("search")) {
									JSONObject obj2 = new JSONObject();
									ut = (comando[1].replaceAll(" ", "")).split("-");
									nome = ut[0].substring(0, 1).toUpperCase() + ut[0].substring(1).toLowerCase();
									cognome = ut[1].substring(0, 1).toUpperCase() + ut[1].substring(1).toLowerCase();
									if (fg.verificaLogin(nome + " " + cognome)) {
										obj2.put("reply", nome + " " + cognome + " trovato!");
										scrivi.writeBytes(obj2.toString() + "\r\n");
									} else {
										obj2.put("reply", nome + " " + cognome + " non trovato!");
										scrivi.writeBytes(obj2.toString() + "\r\n");
									}
								}

								// Richiesta amicizia
								if (comando[0].equals("friend")) {
									JSONObject obj2 = new JSONObject();
									ut = (comando[1].replaceAll(" ", "")).split("-");
									nome = ut[0].substring(0, 1).toUpperCase() + ut[0].substring(1).toLowerCase();
									cognome = ut[1].substring(0, 1).toUpperCase() + ut[1].substring(1).toLowerCase();
									if (fg.verificaLogin(nome + " " + cognome)) {
										try {
											obj2.put("reply", nome + " " + cognome + " aggiunto alle tue amicizie!");
											fg.addEdge(c.getId() + "-" + nome + " " + cognome, c.getId(), nome + " " + cognome);

											// Aggiunta dell'edge per definire la relazione di
											// amicizia
											wr.println("ae " + "\"" + c.getId() + "-" + nome + " " + cognome + "\"  " + "\"" + c.getId() + "\"" + " "
													+ "\"" + nome + " " + cognome + "\"");
											scrivi.writeBytes(obj2.toString() + "\r\n");
											wr.flush();
										} catch (Exception e) {
											obj2.put("reply", "Impossibile aggiungere " + nome + " " + cognome
													+ "!\nForse siete già amici!\nControlla con il comando 'listfriend'");
											scrivi.writeBytes(obj2.toString() + "\r\n");
										}
									} else {
										obj2.put("reply", "Impossibile aggiungere " + nome + " " + cognome + "!\nProbabilmente non esiste!");
										scrivi.writeBytes(obj2.toString() + "\r\n");
									}
								}

								// Elenco amici
								if (comando[0].equals("listfriend")) {
									amici = fg.listFriend(c.getId());
									String amicigruppo[] = amici.split(";");
									JSONObject obj2 = new JSONObject();
									JSONArray arr = new JSONArray();
									for (int i = 0; i < amicigruppo.length; i++) {
										arr.add(amicigruppo[i]);
									}
									obj2.put("friendlist", arr);
									scrivi.writeBytes(obj2.toString() + "\r\n");
								}

								// Invio messaggio a un amico
								if (comando[0].equals("msg")) {
									String ft[] = comando[1].split("_");
									ut = ft[0].split(" ");
									if (ft[1].trim().isEmpty()) {
										ft[1] = "MESSAGGIO VUOTO";
									}
									nome = ut[0].substring(0, 1).toUpperCase() + ut[0].substring(1).toLowerCase();
									cognome = ut[1].substring(0, 1).toUpperCase() + ut[1].substring(1).toLowerCase();
									String dest = nome + " " + cognome;
									if (fg.verificaLogin(dest)) {
										boolean email = true;
										for (int i = 0; i < pm.socket.size(); i++) {
											if (pm.socket.get(i).getId().equals(dest)) {
												Socket r = pm.socket.get(i).getSocket();
												email = false;
												JSONObject obj2 = new JSONObject();
												obj2.put("sender", c.getId());
												obj2.put("text", ft[1]);
												DataOutputStream scrivi2 = new DataOutputStream(r.getOutputStream());
												scrivi2.writeBytes(obj2.toString() + "\r\n");

												JSONObject obj3 = new JSONObject();
												obj3.put("reply", "Messaggio inviato!");

												scrivi.writeBytes(obj3.toString() + "\r\n");
												break;
											}
										}
										if (email) { // Se l'utente non è connesso
											Email e = new Email(c.getId(), dest, ft[1]);
											PrintWriter pw = new PrintWriter(new FileWriter("FacebookMail.txt", true));

											// Scrittura email nella memoria secondaria.
											// Una volta connesso, il destinatario riceverà il
											// messaggio.
											pw.println(e.toString());
											pw.close();
											JSONObject obj2 = new JSONObject();
											obj2.put("reply", "Utente non connesso, il messaggio verrà recapitato al suo login...");
											scrivi.writeBytes(obj2.toString() + "\r\n");
										}
									} else {
										JSONObject obj2 = new JSONObject();
										obj2.put("reply", "Errore: l'utente non è registrato, impossibile inviare il messaggio!");
										scrivi.writeBytes(obj2.toString() + "\r\n");
									}
								}

								// Help comandi
								if (comando[0].equals("help")) {

									JSONObject obj2 = new JSONObject();
									JSONArray arr = new JSONArray();

									arr.add("REGISTRAZIONE -->      register:<NOME>-<COGNOME>");
									arr.add("LOGIN -->              login:<NOME>-<COGNOME>");
									arr.add("ELENCO AMICI -->       listfriend");
									arr.add("RICERCA UTENTE -->     search:<NOMEAMICO>-<COGNOMEAMICO>");
									arr.add("RICHIESTA AMICIZIA --> friend:<NOMEAMICO>-<COGNOMEAMICO>");
									arr.add("INVIO MESSAGGIO -->    msg:<NOMEAMICO>-<COGNOMEAMICO> TESTOMESSAGGIO");

									obj2.put("help", arr);
									scrivi.writeBytes(obj2.toString() + "\r\n");
								}
							}
						}

						else {
							obj.put("reply", "Impossibile eseguire login: utente non registrato.");
							scrivi.writeBytes(obj.toString() + "\r\n");
						}
					} // fine comandi login

				} catch (ArrayIndexOutOfBoundsException e) {
					JSONObject obj = new JSONObject();
					obj.put("reply", "Attenzione, errore nel server, ripetere l'operazione prego");
					scrivi.writeBytes(obj.toString() + "\r\n");
				}
			}
		} catch (IOException e) {
			System.out.println("Errore in comunicazione! Client scollegato");
			wr.close();
		}

	}

}
