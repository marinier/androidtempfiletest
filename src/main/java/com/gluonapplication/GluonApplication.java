package com.gluonapplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GluonApplication extends MobileApplication {

	public GluonApplication() {
		try {
			Path tempPath = Files.createTempFile(null, ".tmp");
			System.out.println("Created temp file at: " + tempPath);
		} catch (IOException e) {
			System.out.println("Failed to create temp file");
			e.printStackTrace();
		}
	}
	
    @Override
    public void init() {
        addViewFactory(HOME_VIEW, BasicView::new);
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.BLUE.assignTo(scene);

        ((Stage) scene.getWindow()).getIcons().add(new Image(GluonApplication.class.getResourceAsStream("/icon.png")));
    }

    public static void main(String args[]) {
        launch(args);
    }
}
