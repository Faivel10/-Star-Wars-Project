package bgu.spl.mics.application.services;


import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.LinkedList;


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;

    }

    @Override
    //Leia sends attack events to han-solo and C3PO
    protected void initialize() {

        //leia start the program by sending the attack events.
        //so we subscribe her to the start broadcast so she will know she needs to start - we than send the event once this initialization starts.
        subscribeBroadcast(StartBroadcast.class,(StartBroadcast broadcast)->{
          try{
              Thread.sleep(100);
          }catch (InterruptedException e){}

            LinkedList<Future> results  =new LinkedList<>();
            for(int i=0;i<attacks.length;i=i+1) {
                //we create the attacks given from the input file.
                AttackEvent attackEvent = new AttackEvent(attacks[i]);
                //we save the future files in results.
                results.addLast(sendEvent(attackEvent));
            }

            for(Future f:results)
            {
                //we wait for each event to be delt with.
                f.get();
            }
            //all the attacks were delt with because get is a blocking method so we wait once it ends.
            //call R2D2 to diactivate shield generator.
            Future f =sendEvent(new DeactivationEvent());
            //WAiT FOR R2D2 TO FINISH.
            f.get();
            //send to lando the final event.
            sendEvent(new BombDestroyerEvent());
        });

        //LANDO WILL CALL EVERYONE THE ENDING BROADCAST AND THE PROGRAM WILL FINISH.
        subscribeBroadcast(EndingBroadcast.class,(EndingBroadcast broadcast)->
        {
            close();
            terminate();
        });

        //starting the run of leia. to start the program.
        sendBroadcast(new StartBroadcast());
    }

    @Override
    protected void close() {
        //update diary.
        Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());

    }
}
