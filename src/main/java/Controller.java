import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.http.impl.client.HttpClientBuilder;
import com.gluonhq.charm.glisten.control.TextField;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.apache.http.HttpResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import javafx.scene.image.Image;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

import javafx.fxml.FXML;

public class Controller {

    public Button search_button;
    public AnchorPane anchorPane;

    @FXML
    private TextField search_bar;

    @FXML
    private TreeView<String> treeView;

    Image icon = new Image(getClass().getResourceAsStream("/img/icon.png"));


    @FXML
    void initialize() {
    }

    @FXML
    void buttonClicked() {
        try {
            URL url = new URL(search_bar.getText());
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), null);
            JsonElement root = JsonParser.parseString(getJsonFromResponse(uri.toASCIIString()));
            treeView.setRoot(parseJSON("json", root));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private TreeItem<String> parseJSON(String name, Object json) {
        TreeItem<String> item = new TreeItem<>();
        if (json instanceof JsonObject) {
            item.setValue(name);
            item.setGraphic(new ImageView(icon));
            JsonObject object = (JsonObject) json;
            object.entrySet().forEach(entry -> {
                String childName = entry.getKey();
                Object childJson = entry.getValue();
                TreeItem<String> child = parseJSON(childName, childJson);
                item.getChildren().add(child);
            });
        } else if (json instanceof JsonArray) {
            item.setValue(name);
            item.setGraphic(new ImageView(icon));
            JsonArray array = (JsonArray) json;
            for (int i = 0; i < array.size(); i++) {
                String childName = String.valueOf(i);
                Object childJson = array.get(i);
                TreeItem<String> child = parseJSON(childName, childJson);
                item.getChildren().add(child);
            }
        } else {
            item.setValue(name + " : " + json);
        }
        return item;
    }

    public String getJsonFromResponse(String url) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String json = "";
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            json += line;
            System.out.println(json);
        }
        return json;
    }
}
