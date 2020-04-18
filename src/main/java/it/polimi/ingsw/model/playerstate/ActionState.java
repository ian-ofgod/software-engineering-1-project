package it.polimi.ingsw.model.playerstate;

import it.polimi.ingsw.model.Pawn;
import it.polimi.ingsw.model.action.Action;

import java.util.ArrayList;

/**
 * Match state ActionState: during a match the player that is currently
 * playing his turn is in this state, the other players are in IdleState
 */
public class ActionState extends PlayerState {
    private ArrayList<Action> actionList;
    private Action currentAction;
    private Pawn selectedPawn;

    public ActionState(ArrayList<Action> actionList) {
        super(PlayerStateType.ActionState);
        this.setActionList(actionList);
    }

    /**
     * Get method of the variable currentAction, returns a copy of the currentAction
     */
    public Action getCurrentActionCopy() {
        return currentAction.duplicate();
    }

    /**
     * Set method of the variable currentAction, sets a copy of provided currentAction
     */
    public void setCurrentActionCopy(Action currentAction) {
        this.currentAction = currentAction.duplicate();
    }

    /**
     * Get method of the variable actionList, returns a copy of the actionList
     */
    public ArrayList<Action> getActionListCopy() {
        if (actionList == null) {
            return null;
        } else {
            ArrayList<Action> copiedActionList = new ArrayList<>();
            for (Action action : actionList) {
                copiedActionList.add(action.duplicate());
            }
            return copiedActionList;
        }
    }

    /**
     * Get method of the variable actionList, returns the reference of the actionList
     */
    public ArrayList<Action> getActionList() {
        return actionList;
    }

    /**
     * Set method of the variable actionList, sets a copy of provided actionList
     */
    public void setActionList(ArrayList<Action> actionList) {
        if (actionList != null) {
            for (Action action : actionList) {
                this.actionList.add(action.duplicate());
            }
        } else {
            this.actionList = null;
        }
    }

    /**
     * Get method of the variable selectedPawn, returns a copy of the selectedPawn
     */
    public Pawn getSelectedPawn() {
        return selectedPawn.duplicate();
    }

    /**
     * Set method of the variable selectedPawn, sets a copy of provided selectedPawn
     */
    public void setSelectedPawn(Pawn selectedPawn) {
        this.selectedPawn = selectedPawn.duplicate();
    }
}
