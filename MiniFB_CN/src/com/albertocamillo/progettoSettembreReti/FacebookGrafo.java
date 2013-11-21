package com.albertocamillo.progettoSettembreReti;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

/**
 * Classe che si occupa della costruzione del grafo.
 * 
 * @author Alberto Camillo
 */

public class FacebookGrafo {
	Graph grafo;

	/**
	 * Permette la creazione del Grafo attraverso il metodo costruisciGrafo().
	 */
	public FacebookGrafo() {
		this.costruisciGrafo();
	}

	// ----- Costruzione del grafo 'GrafoFacebook.dgs' ----- //

	/**
	 * Metodo utilizzato per la creazione del un Grafo.
	 */
	public void costruisciGrafo() {
		try {
			File risorsa = new File("GrafoFacebook.dgs");
			grafo = new SingleGraph("grafo");
			FileSource fs = FileSourceFactory.sourceFor(risorsa.getAbsolutePath());
			fs.addSink(grafo);
			fs.readAll("GrafoFacebook.dgs");
		} catch (IOException e) {
			try {
				System.out.println("Attenzione: un errore è avvenuto mentre il server costruiva il grafo!");
				PrintWriter wr = new PrintWriter(new FileWriter("GrafoFacebook.dgs"));
				wr.println("DGS004");
				wr.println("null 0 0");
				wr.close();
			} catch (IOException exc) {
				System.err.println("E' avvenuto un errore nella costruzione del grafo! ( " + exc + " )");
			}
		}
	}

	// ----- Aggiunta nodo amicizia ----- //

	/**
	 * Permette la l'aggiunta di un nodo (amico) al Grafo.
	 * 
	 * @param n è il nodo da aggiungere al Grafo
	 */
	public synchronized void addNode(String n) {
		grafo.addNode(n);
	}

	// ----- Aggiunta edge tra due nodi n1 e n2 ----- //

	/**
	 * Permette la l'aggiunta di un'edge tra due nodi n1 e n2 (amicizia) nel Grafo.
	 * 
	 * @param link rappresenta il collegamento tra i due nodi
	 * @param n1 rappresenta il primo nodo
	 * @param n2 rappresenta il secondo nodo
	 */
	public synchronized void addEdge(String link, String n1, String n2) {
		grafo.addEdge(link, n1, n2);
	}

	/**
	 * Permette di verificare l'esistenza di un nodo all'interno di un Grafo e di conseguenza
	 * l'esistenza o meno di un utente.
	 * 
	 * @param id rappresenta il nodo, l'id utente
	 * @return l'esistena o meno del nodo
	 */
	public boolean verificaLogin(String id) {
		boolean esiste = true;
		try {
			Node n;
			n = grafo.getNode(id);
			n.getId();
		} catch (NullPointerException e) {
			esiste = false;
		}
		return esiste;
	}

	// ----- Amicizie dirette ----- //

	/**
	 * Permette ottenere gli edges (le amicizie) di un determinato nodo (utente).
	 * 
	 * @param n rappresenta il nodo, l'utente
	 * @return l'elenco di amici dell'utente
	 */
	public String listFriend(String n) {
		String amici = "";
		for (Edge e : grafo.getEachEdge()) {
			if (e.getId().contains(n)) {
				if (e.getNode1().hasEdgeBetween(n))
					amici += e.getNode1() + ";";
				else
					amici += e.getNode0() + ";";
			}
		}
		return amici;
	}

	// ----- Controlli sui nodi ----- //

	/**
	 * Verifica la presenza di un nodo all'interno di un gruppo di nodi.
	 * 
	 * @param n rappresenta il nodo, l'utente
	 * @param nodi rappresenta il gruppo di nodi
	 * @return la presenza o meno di quel nodo all'interno del gruppo
	 */
	public boolean nodoInGruppo(String n, String[] nodi) {
		boolean presenza = false;
		for (int i = 0; i < nodi.length; i++) {
			if (n.equals(nodi[i])) {
				presenza = true;
				break;
			}
		}
		return presenza;
	}

	// ----- Suggerimento amicizie ----- //

	/**
	 * Permette di ottenere l'elenco di amici di amici, quei nodi , cioè, a due hop di distanza dal
	 * hop connesso al Server.
	 * 
	 * @param id rappresenta il nodo, l'utente
	 * @return l'elenco di amici di amici dell'utente
	 */
	public String friendOfMine(String id) {

		// Gli amici da segnalare (quelli a due hop --> amici di amici)
		String ffo = "";
		String friends = this.listFriend(id);
		String[] ffo2; // Gli amici diretti
		ffo2 = friends.split(";");

		for (int i = 0; i < ffo2.length; i++) {
			for (Edge e : grafo.getEachEdge()) {
				if (e.getId().contains(ffo2[i])) {

					if (e.getNode1().hasEdgeBetween(ffo2[i]))
						if (!(e.getNode1().getId().equals(id)))
							if (!this.nodoInGruppo(e.getNode1().getId(), ffo2))
								ffo += e.getNode1() + ";";

					if (e.getNode0().hasEdgeBetween(ffo2[i]))
						if (!(e.getNode0().getId().equals(id)))
							if (!this.nodoInGruppo(e.getNode0().getId(), ffo2))
								ffo += e.getNode0() + ";";
				}
			}
		}
		return ffo;
	}

	
	/**
	 * Permette di disegnare il grafo delle amicizie
	 * 
	 */
	public void display() {
		for (Node n : grafo) {
			n.addAttribute("ui.label", n.getId());
		}
		grafo.addAttribute("ui.stylesheet", "node { size: 20px; z-index: 0; fill-color: #8F8FD0; text-size: 12px; } edge { z-index: 1; fill-color: #000; size: 2px; }");
		
		grafo.display();
	}

	
	public static void main(String[] args) {
		FacebookGrafo fg = new FacebookGrafo();
		fg.display();
		String qui = fg.friendOfMine("Alberto Camillo");
		String amici[] = qui.split(";");
		for (int i = 0; i < amici.length; i++)
			System.out.print(amici[i] + ";");
	}

}