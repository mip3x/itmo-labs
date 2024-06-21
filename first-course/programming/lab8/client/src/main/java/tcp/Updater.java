package tcp;

import collection.CollectionService;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfer.Response;
import ui.AnimationController;
import ui.MainController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Updater extends Client {
    private final Logger logger = LogManager.getLogger();
    private static Updater instance = null;
    private MainController mainController;
    private AnimationController animationController;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static Updater getInstance() {
        if (instance == null) instance = new Updater();
        return instance;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setAnimationController(AnimationController animationController) {
        this.animationController = animationController;
    }

    @Override
    public void run() {
        scheduler.scheduleAtFixedRate(this::updateCollection, 0, 5, TimeUnit.SECONDS);
    }

    private void updateCollection() {
        Response response = getResponse();
        if (response == null) return;

        logger.info("Got response: {}, {}", response.getMessage(), response.getCollection().size());
        CollectionService.getInstance().setCollection(response.getCollection());

        if (mainController != null) mainController.refreshCollectionLater();
        if (animationController != null) animationController.refreshCollectionLater();
    }
}
