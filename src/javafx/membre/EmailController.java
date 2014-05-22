package javafx.membre;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import validation.ValidationErreur;
import validation.ValideurEmail;

import com.itextpdf.text.DocumentException;

import dao.DahiraDao;
import daoimpl.DahiraDaoImpl;
import entites.Dahira;
import entites.Membre;
import javafx.GenererPdf;
import javafx.SendMessage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EmailController implements Initializable{

		@FXML private Text connexionErr;
		
		@FXML private TextField emailConexionField;
		@FXML private TextField passWordField;
		
		@FXML private Button btnEnvoyer;
		@FXML private Button btnAnnuler;

		private Stage stage;
		private Membre membreActif;
		private Dahira dahira;
		private Boolean bool = false;
		
		private final String DAHIRA = "select c from Dahira c";
		
		public Stage getStage() {
			return stage;
		}

		public void setStage(Stage stage) {
			this.stage = stage;
		}
		
		public Membre setMembreEnvoyer() {
			return membreActif;
		}

		public void setMembreEnvoyer(Membre membreActif) {
			this.membreActif = membreActif;
		}

		@Override
		public void initialize(URL location, ResourceBundle resources) {
			//charger dahira
			chargerDahira();
			
			//Valider le mail
			ValideurEmail validerEmail = new ValideurEmail(emailConexionField,
					connexionErr, false, ValidationErreur.EMAIL_ERR);
			validerEmail.validerEmail(emailConexionField,connexionErr);
			//action sur bouton envoyer
			btnEnvoyer.setOnAction(new EventHandler<ActionEvent>() {
		 	    @Override public void handle(ActionEvent event) {
		 	    	bool = validerEmail.valider();
		 	    	envoieMess();
		 	    	}
		 	});
			//action sur bouton envoyer
			btnAnnuler.setOnAction(new EventHandler<ActionEvent>() {
		 	    @Override public void handle(ActionEvent event) {
		 	    	stage.close();
		 	    	}
		 	});
				   
		}
			
		public void envoieMess(){
			if(bool){
	 	    	String nameFile = membreActif.getPrenom() +
	 	    			"_" + membreActif.getNom() + ".pdf";
	 	    	//GenererPdf(String dateIpmot, String dateDelivrance,String montant)
	 	    	GenererPdf impot = new GenererPdf("2014", "10/12/2012",
	 					"2000$");
	 	    	try {
	 	   		impot.createPdf(nameFile,membreActif, dahira,"",001);
	 	    	} catch (DocumentException | IOException e) {
	 	   		// TODO Auto-generated catch block
	 	   		e.printStackTrace();
	 	    	}
	 	    	
	 	    	SendMessage mess = new SendMessage();
	 	    	mess.setObjet("Impôt");
	 	    	mess.setMessage("Bonjour, Voici votre relevé d'impôt.");
	 	    	mess.setEmailDestination("oussou.dieng@gmail.com");
	 	    	mess.setPathFile(nameFile);
	 	    	mess.sendMessage(emailConexionField.getText(),passWordField.getText());
	 	    	stage.close();
			}
		} 
		
		//charger les informations de la dahira
		public void chargerDahira(){
			DahiraDaoImpl dahiraDao = new DahiraDaoImpl();
				dahira = dahiraDao.get(DAHIRA);
		}
}