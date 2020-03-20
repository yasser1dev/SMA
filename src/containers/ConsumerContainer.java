package containers;

import agents.ConsumerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsumerContainer extends Application {
    private  ConsumerAgent consumerAgent;
    ObservableList<String> observableList;
    public static void main(String[] args) throws ControllerException {
        launch(args);


    }
    public void startContainer() throws Exception{
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");

        AgentContainer agentContainer=runtime.createAgentContainer(profile);

        AgentController agentController=agentContainer.
                createNewAgent("Consumer","agents.ConsumerAgent",new Object[] {this});
        agentController.start();
    }
    @Override
    public void start(Stage window) throws Exception {
        startContainer();
        BorderPane borderPane=new BorderPane();
        Scene scene=new Scene(borderPane,600,400);

        HBox hBox=new HBox();
        borderPane.setTop(hBox);

        Label titre=new Label("Livre : ");
        TextField livre=new TextField();
        Button button=new Button("OK");

        hBox.getChildren().addAll(titre,livre,button);

        VBox vBox=new VBox();
        vBox.setPadding(new Insets(10));
        observableList= FXCollections.observableArrayList();
        ListView<String> listViewMsg=new ListView<String>(observableList);
        vBox.getChildren().add(listViewMsg);
        borderPane.setCenter(vBox);

        button.setOnAction(evt->{
            String livreTxt=livre.getText();
            //observableList.add(livreTxt);

            GuiEvent event=new GuiEvent(this,1);
            event.addParameter(livreTxt);
            consumerAgent.onGuiEvent(event);
        });


        window.setTitle("Consumer");
        window.setScene(scene);
        window.show();

    }

    public ConsumerAgent getConsumerAgent() {
        return consumerAgent;
    }

    public void logMsg(ACLMessage aclMessage){
        Platform.runLater(()->{
            if(aclMessage!=null){
                observableList.add(aclMessage.getContent()+" : "+
                        aclMessage.getSender().getName());
            }
        });
    }
    public void setConsumerAgent(ConsumerAgent consumerAgent) {
        this.consumerAgent = consumerAgent;
    }
}
