package phbmediaplayer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

public class FXMLDocumentController implements Initializable {

    private MediaPlayer mediaPlayer;

    @FXML
    private MediaView mediaView;

    @FXML
    private StackPane sPane;

    @FXML
    private Button playPause;

    @FXML
    private Slider volume;

    @FXML
    private Slider seek;

    @FXML
    private BorderPane bPane;

    List<String> playlist = new ArrayList<>();
    List<String> sourceName = new ArrayList<>();
    static int INDEX, PLAY = 0;

    @FXML
    private void openFiles(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Media File", "*.mp3", "*.mp4");
        fc.getExtensionFilters().add(filter);
        List<File> files = fc.showOpenMultipleDialog(null);

        if (files != null && !files.isEmpty()) {
            playlist.clear();
            sourceName.clear();

            for (File file : files) {
                playlist.add(file.toURI().toString());
                sourceName.add(file.getName());
            }

            INDEX = 0;
            playMedia(INDEX);
        }
    }

    private void playMedia(int index) {
        String source = playlist.get(index);
        Media media = new Media(source);

        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.stop();
        }

        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaView.autosize();

        DoubleProperty width = mediaView.fitWidthProperty();
        DoubleProperty height = mediaView.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);

        volume.setValue(50);
        volume.valueProperty().addListener((Observable observable) -> {
            mediaPlayer.setVolume(volume.getValue() / 100);
        });

        mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            seek.setValue(newValue.toSeconds());
        });

        seek.setOnMouseClicked((MouseEvent event) -> {
            mediaPlayer.seek(Duration.seconds(seek.getValue()));
        });

        seek.setOnMouseDragged((MouseEvent event) -> {
            mediaPlayer.seek(Duration.seconds(seek.getValue()));
        });

        mediaPlayer.play();
        PLAY = 1;

        Image imagePause = new Image(getClass().getResourceAsStream("/images/pause.png"));
        playPause.setGraphic(new ImageView(imagePause));
    }

    @FXML
    private void seekBackward(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.setRate(0.5);
        }
    }

    @FXML
    private void backward(ActionEvent event) {
        if (INDEX > 0) {
            INDEX--;
            playMedia(INDEX);
        }
    }

    @FXML
    private void stop(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        Image imagePlay = new Image(getClass().getResourceAsStream("/images/play.png"));
        playPause.setGraphic(new ImageView(imagePlay));
        PLAY = 0;
    }

    @FXML
    private void pausePlay(ActionEvent event) {
        if (!playlist.isEmpty()) {
            if (PLAY == 1) {
                mediaPlayer.pause();
                Image imagePlay = new Image(getClass().getResourceAsStream("/images/5.png"));
                playPause.setGraphic(new ImageView(imagePlay));
                PLAY = 0;
            } else {
                mediaPlayer.play();
                Image imagePause = new Image(getClass().getResourceAsStream("/images/00.png"));
                playPause.setGraphic(new ImageView(imagePause));
                PLAY = 1;
            }
        } else {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Message");
            dialog.setContentText("Please open media!");
            dialog.setOnCloseRequest((DialogEvent e) -> dialog.close());
            dialog.initStyle(StageStyle.DECORATED);
            dialog.initModality(Modality.NONE);
            dialog.show();
        }
    }

    @FXML
    private void forward(ActionEvent event) {
        if (INDEX >= 0 && INDEX < playlist.size() - 1) {
            INDEX++;
            playMedia(INDEX);
        }
    }

    @FXML
    private void seekForward(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.setRate(1.5);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO: Add any required initialization here
    }
}
