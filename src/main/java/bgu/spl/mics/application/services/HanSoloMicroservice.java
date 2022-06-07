package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.EndingBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.Comparator;
import java.util.List;


/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }

    //for changing the finish time of han in diary
    public void close()
    {
        Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
    }

    @Override
    //receives attacks message and uses ewoks
    protected void initialize() {

        subscribeEvent(AttackEvent.class,(AttackEvent event)->
                {//callback
                    //sorting the ewoks needed so we avoid deadlocks.
                    List<Integer> neededEwoks=event.getAttackMessage().getSerials();
                    neededEwoks.sort(Comparator.comparingInt(a -> a));
                    for(Integer i:neededEwoks)
                    {
                        Ewoks.getInstance().get(i);
                    }
                    //doing the mission:
                    try{
                        Thread.sleep(event.getDuration());
                    }catch (InterruptedException e){}
                    //finished the mission - return the ewoks.
                    for(Integer j:neededEwoks)
                    {
                        Ewoks.getInstance().get(j).release();
                    }
                    //completed the mission - update the future.
                    complete(event,true);
                    //add an attack to the diary.
                    Diary.getInstance().addAttack();
                    //finished the mission, closing. this will be activated every time
                    //but it is only needed in the last time to let the program know we finaly finished.
                    close();
                }
                );
        //ending event means we terminate all the program.

        subscribeBroadcast(EndingBroadcast.class,(EndingBroadcast broad)->
        {
            terminate();
            Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
        });

    }



}
