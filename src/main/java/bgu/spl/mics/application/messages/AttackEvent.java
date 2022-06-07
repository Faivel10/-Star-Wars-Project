package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

public class AttackEvent implements Event<Boolean> {

    private  Attack attackMessage;
    private  int duration;


    public AttackEvent(Attack attack){
        attackMessage=attack;
        duration=attack.getDuration();
        
    }
    //empty constructor
//for tester
    public AttackEvent() {

    }

    public Attack getAttackMessage() {
        return attackMessage;
    }


public int  getDuration()
{
    return duration;
}

}