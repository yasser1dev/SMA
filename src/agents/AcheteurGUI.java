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
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AcheteurGUI extends Application {
    protected AcheteurAgent acheteurAgent;
    protected ObservableList observableList;
    public static void main(String[] args) {
        launch(args);
    }
    public void startContainer() throws StaleProxyException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer agentContainer=runtime.createAgentContainer(profile);
        AgentController agentController=agentContainer.createNewAgent("Acheteur","agents.AcheteurAgent",new Object[] {this});
        agentController.start();
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

        Scene scene=new Scene(borderPane,400,300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Acheteurs");
        primaryStage.show();
    }

    public void logMsg(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(aclMessage.getContent()+" : "+
                    aclMessage.getSender().getName());
        });
    }
}
