/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO.Hibernate;

import ConnectionPools.DBConnector;
import POJO.Bestelling;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 *
 * @author Gebruiker
 */
public class BestellingDAOHibernate {
    
    private Session currentSession;
    private Transaction currentTransaction;

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    public Session openCurrentSession(){
        currentSession = getSessionFactory().openSession();
        return currentSession;
    }
    
    public Session openCurrentSessionWithTransaction(){
        currentSession = getSessionFactory().openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }
    
    public void closeCurrentSession(){
        currentSession.close();
    }
    
    public void closeCurrentSessionWithTransaction(){
        currentSession = getSessionFactory().openSession();
        currentTransaction.commit();
        currentSession.close();
    }

    public SessionFactory getSessionFactory(){
        StandardServiceRegistry ssrb = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        MetadataSources ms = new MetadataSources(ssrb).addAnnotatedClass(Bestelling.class);
        SessionFactory sf = ms.buildMetadata().buildSessionFactory();
        return sf;
    }

    public Bestelling save (Bestelling bestelling) {
        getCurrentSession().save(bestelling);
        return bestelling;
    }
    
    public Bestelling findById(int BestellingId){
        Bestelling bestelling = (Bestelling) getCurrentSession().get(Bestelling.class, BestellingId);
        return bestelling;
    }

    public ArrayList<Bestelling> findAll(){
        ArrayList<Bestelling> bestellingen = (ArrayList<Bestelling>) getCurrentSession().createQuery("from bestelling").list();
        return bestellingen;
    }
    public ArrayList<Bestelling> findByKlantId(int klantId){
        Query query = getCurrentSession().createQuery("from Bestelling where id = :klantId");
        query.setParameter("klantId", klantId);
        ArrayList<Bestelling> bestellingen = (ArrayList<Bestelling>) query.list();
        return bestellingen;
    }
    
    public void update(Bestelling bestelling){
        getCurrentSession().update(bestelling);
    }

    public void updateBestelling(Bestelling bestelling){
        String query =  "UPDATE Bestelling SET klant_id=? WHERE bestelling_id = ?;";

        try(Connection con = new DBConnector().getConnection();){
   
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setInt(1, bestelling.getKlantID());
            stmt.setInt(2, bestelling.getBestellingID());
            
            stmt.executeUpdate();
        }
        catch(SQLException | ClassNotFoundException  e){
            System.out.println("\nProbeer opnieuw.\n");
            e.printStackTrace();
        }
    }
    
    public void updateBestellingPrijs(Bestelling bestelling){
        String query =  "UPDATE Bestelling SET totaal_prijs = ? WHERE bestelling_id = ?;";

        try(Connection con = new DBConnector().getConnection();){
   
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setBigDecimal(1, bestelling.getTotaalPrijs());
            stmt.setInt(2, bestelling.getBestellingID());
            
            stmt.executeUpdate();
        }
        catch(SQLException | ClassNotFoundException  e){
            System.out.println("\nProbeer opnieuw.\n");
            e.printStackTrace();
        }
    }
    
    public void deleteBestelling(Bestelling bestelling){
        getCurrentSession().delete(bestelling);
    }
}