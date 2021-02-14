import com.gluonhq.charm.glisten.control.TextField;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Controller {

    @FXML
    private TextField search_bar;

    @FXML
    private TreeView<String> treeView;

    @FXML
    void buritto() {
        try {
            String url = search_bar.getText().trim().replace(" ", "%20");
            JsonElement root = JsonParser.parseString(getJsonFromResponse(url));
            treeView.setRoot(parseJSON("json", root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static TreeItem<String> parseJSON(String name, Object json) {
        TreeItem<String> item = new TreeItem<>();
        if (json instanceof JsonObject) {
            item.setValue(name);
            JsonObject object = (JsonObject) json;
            object.entrySet().forEach(entry -> {
                String childName = entry.getKey();
                Object childJson = entry.getValue();
                TreeItem<String> child = parseJSON(childName, childJson);
                child.setExpanded(true);
                item.getChildren().add(child);
            });
        } else if (json instanceof JsonArray) {
            item.setValue(name);
            JsonArray array = (JsonArray) json;
            for (int i = 0; i < array.size(); i++) {
                String childName = String.valueOf(i);
                Object childJson = array.get(i);
                TreeItem<String> child = parseJSON(childName, childJson);
                child.setExpanded(true);
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
