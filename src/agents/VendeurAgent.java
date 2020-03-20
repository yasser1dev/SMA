package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class VendeurAgent extends GuiAgent {
    protected VendeurGUI vendeurGUI;
    protected void setup(){
        if(getArguments().length==1) {
            vendeurGUI=(VendeurGUI) getArguments()[0];
            vendeurGUI.vendeurAgent=this;
        }
        ParallelBehaviour parallelBehaviour=new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage=receive();
                if(aclMessage!=null){
                    vendeurGUI.logMsg(aclMessage);
                    switch (aclMessage.getPerformative()){
                        case ACLMessage.CFP:
                            ACLMessage reply=aclMessage.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(String.valueOf(500+new Random().nextInt(1000)));
                            send(reply);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage replyToBuyer=aclMessage.createReply();
                            replyToBuyer.setPerformative(ACLMessage.AGREE);
                            send(replyToBuyer);
                            break;
                    }
                }
                else block();
            }
        });

        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription=new DFAgentDescription();
                dfAgentDescription.setName(getAID());
                ServiceDescription serviceDescription=new ServiceDescription();
                serviceDescription.setType("Book-selling");
                serviceDescription.setName("JADE book trading");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent,dfAgentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
    }

    public  void takeDown(){
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
