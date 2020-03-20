package agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class AcheteurAgent extends GuiAgent {
    protected AcheteurGUI acheteurGUI;
    AID[] vendeurs;
    protected void setup(){
        if(getArguments().length==1) {
            acheteurGUI=(AcheteurGUI)getArguments()[0];
            acheteurGUI.acheteurAgent=this;
        }
        ParallelBehaviour parallelBehaviour=new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,5000) {
            @Override
            protected void onTick() {
                DFAgentDescription dfAgentDescription=new DFAgentDescription();
                ServiceDescription serviceDescription=new ServiceDescription();
                serviceDescription.setType("Book-selling");
                serviceDescription.setName("JADE book trading");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    DFAgentDescription[] results=DFService.search(myAgent,dfAgentDescription);
                    System.out.print(results.length);
                    vendeurs=new AID[results.length];
                    for(int i=0;i<vendeurs.length;i++){
                        System.out.print(results[i].getName()+"\n");
                        vendeurs[i]=results[i].getName();
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            int counter=0;
            List<ACLMessage> aclMessageList=new ArrayList<ACLMessage>();
            @Override
            public void action() {

                /*
                MessageTemplate messageTemplate=new MessageTemplate.or(
                MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)
                ), MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                        MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
                ));*/
                //ACLMessage aclMessage=receive(messageTemplate);
                ACLMessage aclMessage=receive();
                if(aclMessage!=null){
                    ACLMessage aclMessage1=new ACLMessage(ACLMessage.CFP);
                    aclMessage1.setContent(aclMessage.getContent());
                    switch (aclMessage.getPerformative()){
                        case ACLMessage.REQUEST:
                            for(AID aid:vendeurs){
                                System.out.print("============== "+aid.getName()+"\n");
                                aclMessage1.addReceiver(aid);
                                send(aclMessage1);
                            }
                            break;
                        case ACLMessage.PROPOSE:
                            ++counter;
                            aclMessageList.add(aclMessage);
                            if(counter==vendeurs.length){
                                ACLMessage bestOffer=aclMessageList.get(0);
                                double bestPrice=Double.valueOf(bestOffer.getContent());
                                for(ACLMessage aclMsg:aclMessageList){
                                    double price=Double.valueOf(aclMsg.getContent());
                                    if(price<bestPrice){
                                        bestPrice=price;
                                        bestOffer=aclMessage;
                                    }
                                }
                                ACLMessage replyToSelleer=bestOffer.createReply();
                                replyToSelleer.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                send(replyToSelleer);
                            }
                            break;
                        case ACLMessage.AGREE:
                            ACLMessage replyToConsumer=new ACLMessage(ACLMessage.CONFIRM);
                            replyToConsumer.addReceiver(new AID("Consumer",AID.ISLOCALNAME));
                            replyToConsumer.setContent(aclMessage.getContent());
                            send(replyToConsumer);
                            break;
                        case ACLMessage.REFUSE:
                            break;
                    }
                    acheteurGUI.logMsg(aclMessage);
                    ACLMessage reply=aclMessage.createReply();
                    reply.setContent("LIVRE : "+aclMessage.getContent()+" OK");
                    send(reply);


                }
                else block();
            }
        });

    }
    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
    }
}
