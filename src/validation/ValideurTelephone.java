package validation;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ValideurTelephone extends Validateur {
	private static final String TELEPHONE_PATTERN = "\\d{3}[( )-/]?\\d{3}[( )-/]?\\d{4}";
	
	
	public ValideurTelephone(TextField texte, Text labelErr, boolean nullable,
			ValidationErreur validationErr) {
		super(texte, labelErr, nullable, validationErr);
	}

	@Override
	public boolean valider() {
		boolean valide = false;
		valide = super.valider(TELEPHONE_PATTERN); 
		return valide;
	}
	
	// validation champ telephone
		public void validerTelephone(TextField textField, Text textErr) {
			textErr.setVisible(false);
			textField.focusedProperty().addListener(
					new ChangeListener<Boolean>() {
						@Override
						public void changed(
								ObservableValue<? extends Boolean> observable,
								Boolean oldValue, Boolean newValue) {
							if (!newValue) {
								valider();
							}
						}
					});
		}

}
