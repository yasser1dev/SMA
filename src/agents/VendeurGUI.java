package agents;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;

public class VendeurGUI extends Application {
    protected VendeurAgent vendeurAgent;
    protected ObservableList observableList;
    AgentContainer agentContainer;
    public static void main(String[] args) {
        launch(args);
    }
    public void startContainer() throws StaleProxyException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        agentContainer=runtime.createAgentContainer(profile);

    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();
        BorderPane borderPane=new BorderPane();
        VBox vBox=new VBox();
        observableList= FXCollections.observableArrayList();
        ListView<String> listView=new ListView<String>(observableList);
        borderPane.setCenter(vBox);
        vBox.getChildren().add(listView);

        HBox hBox=new HBox();
        Label vendeurLabel=new Label("Vendeur");
        TextField vendeurTxt=new TextField();
        Button deploy=new Button("deploy");

        hBox.getChildren().addAll(vendeurLabel,vendeurTxt,deploy);
        borderPane.setTop(hBox);

        deploy.setOnAction(evt->{
            AgentController agentController= null;
            try {
                String vTxt=vendeurTxt.getText();
                agentController = agentContainer.createNewAgent(vTxt,"agents.VendeurAgent",new Object[] {this});
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
            try {
                agentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        });


        Scene scene=new Scene(borderPane,400,300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Vendeurs");
        primaryStage.show();
    }

    public void logMsg(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(aclMessage.getContent());
        });
    }
}
