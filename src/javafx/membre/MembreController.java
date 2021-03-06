package javafx.membre;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.loadview.LoadManagerView;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import dao.MembreDao;
import daoimpl.MembreDaoImpl;
import entites.Membre;

public class MembreController implements Initializable {

	@FXML
	private ImageView imageViewHome;

	@FXML
	private SplitPane splitPaneVerticale;

	@FXML
	private TextField rechercherField;

	@FXML
	private Button btnOk;
	@FXML
	private Button btnAjouter;
	@FXML
	private Button btnEditer;
	@FXML
	private Button btnSupprimer;
	@FXML
	private Button btnCotisation;
	@FXML
	private Button btnImpot;
	@FXML
	private Button btnAnnuler;
	@FXML
	private Button btnQuitter;

	@FXML
	private TableView<Membre> tableViewMembre;
	@FXML
	private ListView<String> listViewMembre;

	@FXML
	private TableColumn<Membre, String> tablePrenom;
	@FXML
	private TableColumn<Membre, String> tableNom;
	@FXML
	private TableColumn<Membre, String> tableTelephone;

	private Stage stage;
	private Stage parent;
	private Membre membreActif;
	

	private static IntegerProperty index = new SimpleIntegerProperty();

	public static final ObservableList<String> dataMembre = FXCollections
			.observableArrayList();
	public static ObservableList<Membre> membreDonnee = FXCollections
			.observableArrayList();

	// 1. Wrap the ObservableList in a FilteredList (initially display all
	// data).
	public static ObservableList<Membre> filteredData = FXCollections
			.observableArrayList();

	private final String LIST_MEMBRE = "select c from Membre c";

	boolean etatEdit = false;// dans etat editer membre çà donne true

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setParentStage(Stage parent) {
		this.parent = parent;
	}

	public static ObservableList<Membre> getMembreDonnee() {
		return membreDonnee;
	}

	public Membre getMembreActif() {
		return membreActif;
	}

	public void setMembreActif(Membre membreActif) {
		this.membreActif = membreActif;
	}

	public ListView<String> getListViewMembre() {
		return listViewMembre;
	}

	public void setListViewMembre(ListView<String> listViewMembre) {
		this.listViewMembre = listViewMembre;
	}

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		// mettre inactif les boutons
		setUnvisibleButton(true);

		tableViewMembre.getSelectionModel().setSelectionMode(
				SelectionMode.SINGLE);
		;

		// set bouton non actif
		btnEditer.setDisable(true);
		btnSupprimer.setDisable(true);
		tableViewMembre.getSelectionModel().setSelectionMode(
				SelectionMode.SINGLE);

		// c
		tablePrenom
				.setCellValueFactory(new Callback<CellDataFeatures<Membre, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(
							CellDataFeatures<Membre, String> m) {
						return new SimpleStringProperty(m.getValue()
								.getPrenom());
					}
				});
		tableNom.setCellValueFactory(new Callback<CellDataFeatures<Membre, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(
					CellDataFeatures<Membre, String> m) {
				return new SimpleStringProperty(m.getValue().getNom());
			}
		});
		tableTelephone
				.setCellValueFactory(new Callback<CellDataFeatures<Membre, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(
							CellDataFeatures<Membre, String> m) {
						return new SimpleStringProperty(m.getValue()
								.getTelephone());
					}
				});

		// charger la liste des membres
		chargerMembres();
		// afficher un membre
		afficherUnMembre();
		// action filtrer membre
		filterMembre();
		HandleButtonHome();
		// filtrer les membres
		membreDonnee.addListener(new ListChangeListener<Membre>() {
			@Override
			public void onChanged(
					ListChangeListener.Change<? extends Membre> change) {
				
				updateFilteredData();
			}
		});

		// action sur bouton ajouter
		btnAjouter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				afficherVueAjoutMembre();
			}
		});

		tableViewMembre.getSelectionModel().getSelectedIndices()
				.addListener(new ListChangeListener<Integer>() {
					@Override
					public void onChanged(Change<? extends Integer> change) {
						if (etatEdit) {
							setUnvisibleButton(true);
							etatEdit = false;
						}
					}

				});

		// action sur bouton editer
		btnEditer.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// System.out.println("Ok");
				etatEdit = true;
				afficherVueEditerMembre();
			}
		});

		// action sur bouton supprimmer
		btnSupprimer.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clearUnMembre();
				tableViewMembre.getSelectionModel().clearSelection();
				listViewMembre.getItems().clear();
				setUnvisibleButton(true);
			}
		});

		// action sur bouton cotisation
		btnCotisation.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				LoadManagerView.getInstance()
						.afficherVueCotisation(membreActif);
			}
		});

		// action sur bouton impot
		btnImpot.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				LoadManagerView.getInstance().afficherVueEmail(membreActif);
			}
		});

		// action sur bouton quitter
		btnQuitter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				parent.show();
				stage.close();
			}
		});
		
		
	}// fin de la fonction initialiser
	
	
	//action sur l image home
	private void HandleButtonHome() {
		imageViewHome.setOnMouseReleased(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				parent.show();
				stage.close();
			}
		});
	}

	// afficher la fenetre pour ajouter des membres
	public void afficherVueAjoutMembre() {
		LoadManagerView.getInstance().afficherVueAjoutMembre(this);
	}

	// afficher la fenetre pour ajouter des membres
	public void afficherVueEditerMembre() {
		LoadManagerView.getInstance()
				.afficherVueEditerMembre(this, membreActif);
	}

	// charger liste des membres
	public void chargerMembres() {
		ObservableList<Membre> memberData = FXCollections.observableArrayList();
		MembreDao memberDao = new MembreDaoImpl();

		for (Object p : memberDao.getAll(LIST_MEMBRE)) {
			Membre member = (Membre) p;
			memberData.add(member);
		}
		membreDonnee = memberData;
		// filteredData.addAll(membreDonnee);
		tableViewMembre.getItems().clear();
		tableViewMembre.setItems(membreDonnee);
	}

	// afficher information d'un membre
	public void afficherUnMembre() {
		tableViewMembre.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Membre>() {

					@Override
					public void changed(
							ObservableValue<? extends Membre> observable,
							Membre oldValue, Membre newValue) {
						setUnvisibleButton(false);
						index.set(membreDonnee.indexOf(newValue));
						membreActif = newValue;
						makeDataMembre(newValue);
					}
				});
	}

	// mettre les boutons inactifs
	public void setUnvisibleButton(Boolean actif) {
		btnEditer.setDisable(actif);
		btnSupprimer.setDisable(actif);
		btnCotisation.setDisable(actif);
		btnImpot.setDisable(actif);
	}

	// preparer les donnees d un membre pour l'affcihage
	public void makeDataMembre(Membre membre) {
		if (membre != null) {
			String nomMembre = membre.getPrenom() + " " + membre.getNom();
			String rueMembre = membre.getAdresse().getRue();
			String codePostalMembre = membre.getAdresse().getCodepostale()
					+ " " + membre.getAdresse().getVille();
			String paysMembre = membre.getAdresse().getProvince() + " "
					+ membre.getAdresse().getPays();
			String telMembre = membre.getTelephone();
			String emaillMembre = membre.getEmail();
			listViewMembre.getItems().clear();
			dataMembre.addAll(nomMembre, rueMembre, codePostalMembre,
					paysMembre, telMembre, emaillMembre);
			listViewMembre.setItems(dataMembre);
		}

	}

	// effacer l'affichage des données d un membre
	public void clearUnMembre() {
		MembreDao membreDao = new MembreDaoImpl();
		membreDao.delete(membreActif);
		listViewMembre.getItems().clear();
		membreDonnee.remove(index.get());

	}

	// fonction pour rechercher un membre en filtrant les membres
	public void filterMembre() {
		filteredData.addAll(membreDonnee);
		// Add filtered data to the table
		tableViewMembre.setItems(filteredData);
		rechercherField.textProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldValue, String newValue) {
						updateFilteredData();
						listViewMembre.getItems().clear();
						setUnvisibleButton(true);
					}
				});
	}

	/**
	 * Updates the filteredData to contain all data from the masterData that
	 * matches the current filter.
	 */
	private void updateFilteredData() {
		filteredData.clear();

		for (Membre p : membreDonnee) {
			if (matchesFilter(p)) {
				filteredData.add(p);
			}
		}

		// Must re-sort table after items changed
		reapplyTableSortOrder();
	}

	/**
	 * Returns true if the person matches the current filter. Lower/Upper case
	 * is ignored.
	 * 
	 * @param person
	 * @return
	 */
	private boolean matchesFilter(Membre membre) {
		String filterString = rechercherField.getText();
		if (filterString == null || filterString.isEmpty()) {
			// No filter --> Add all.
			return true;
		}

		String lowerCaseFilterString = filterString.toLowerCase();

		if (membre.getPrenom().toLowerCase().indexOf(lowerCaseFilterString) != -1) {
			return true;
		} else if (membre.getNom().toLowerCase().indexOf(lowerCaseFilterString) != -1) {
			return true;
		}else if ((membre.getPrenom() +" "+ membre.getNom()).toLowerCase().indexOf(lowerCaseFilterString) != -1)
			return true;
		return false; // Does not match
	}

	private void reapplyTableSortOrder() {
		ArrayList<TableColumn<Membre, ?>> sortOrder = new ArrayList<>(
				tableViewMembre.getSortOrder());
		tableViewMembre.getSortOrder().clear();
		tableViewMembre.getSortOrder().addAll(sortOrder);
	}

	// mettre a jour la table de view
	public void updateMembreTableView() {
		ObservableList<Membre> tempData = FXCollections.observableArrayList();
		tempData.addAll(membreDonnee);
		tableViewMembre.getItems().clear();
		tableViewMembre.setItems(tempData);
	}

}
