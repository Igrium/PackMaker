package com.igrium.packmaker.ui;

import java.io.IOException;
import java.util.function.Consumer;

import com.igrium.packmaker.util.EventDispatcher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WebBrowser {

    @FXML
    private WebView webView;

    public WebView getWebView() {
        return webView;
    }

    private EventDispatcher<Consumer<String>> selectPageEvent = EventDispatcher.createSimple();

    public EventDispatcher<Consumer<String>> getSelectPageEvent() {
        return selectPageEvent;
    }

    @FXML
    public void selectPage() {
        selectPageEvent.invoker().accept(webView.getEngine().getLocation());
    }
    
    public static WebBrowser open() {
        try {
            FXMLLoader loader = new FXMLLoader(WebBrowser.class.getResource("/ui/webBrowser.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Browse Modpacks");

            WebBrowser browser = loader.getController();
            browser.getSelectPageEvent().addListener(page -> {
                stage.close();
            });

            stage.show();
            browser.getWebView().getEngine().load("https://modrinth.com/modpacks");
            return browser;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
