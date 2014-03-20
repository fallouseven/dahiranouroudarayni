package tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import entites.Adresse;
import entites.Membre;

public class InitDB {
	// constantes
	private final static String TABLE_NAME = "MEMBRE";

	public static void main(String[] args) throws ParseException {
		// r�cup�rer un EntityManagerFactory � partir de l'unit� de persistance
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
		// r�cup�rer un EntityManager � partir de l'EntityManagerFactory
		EntityManager em = emf.createEntityManager();
		// d�but transaction
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		// supprimer les �l�ments de la table Test
		em.createNativeQuery("delete from " + TABLE_NAME).executeUpdate();
		// mettre des �l�ments dans la table Test
		Membre p1 = new Membre("Thimbo", "Moussa", new SimpleDateFormat("dd/MM/yy").parse("03/08/1985"), "5142960552", "test@gmail.com");
		Adresse a1 = new Adresse("arthur-peloquin", "montreal", "quebec", "h3s1r7", "Canada");
		p1.setAdresse(a1);
		//Test p2 = new Test("Durant", "Sylvie");
		// persistance des personnes
		em.persist(p1);
		//em.persist(p2);
		// affichage personnes
		System.out.println("[membres]");
		for (Object p : em.createQuery("select p from Membre p order by p.nom asc").getResultList()) {
			System.out.println(p);
		}
		// fin transaction
		tx.commit();
		// fin EntityManager
		em.close();
		// fin EntityManagerFactory
		emf.close();
		// log
		System.out.println("termin� ...");
	}
}
