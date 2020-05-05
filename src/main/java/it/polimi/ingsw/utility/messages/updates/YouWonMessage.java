package it.polimi.ingsw.utility.messages.updates;

import it.polimi.ingsw.model.RequestsAndUpdatesMessageVisitor;
import it.polimi.ingsw.model.SetMessageVisitor;
import it.polimi.ingsw.utility.messages.Message;

import java.util.ArrayList;

public class YouWonMessage extends Message {

    public YouWonMessage(ArrayList<String> recipients) {
        super(recipients);
    }


}
