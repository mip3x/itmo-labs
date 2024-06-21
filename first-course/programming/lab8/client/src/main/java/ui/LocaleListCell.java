package ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Locale;
import java.util.Objects;

public class LocaleListCell extends javafx.scene.control.ListCell<Locale> {
    private final ImageView imageView = new ImageView();

    @Override
    public void updateItem(Locale item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
            setText(null);
        } else {
            try {
                String iconPath = "/icons/" + item.getLanguage() + ".jpg";
                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
                imageView.setImage(icon);
                setText(item.getDisplayLanguage(item));
                setGraphic(imageView);
            } catch (Exception e) {
                setText(item.getDisplayLanguage(item) + " (icon not found)");
                setGraphic(null);
            }
        }
    }
}
