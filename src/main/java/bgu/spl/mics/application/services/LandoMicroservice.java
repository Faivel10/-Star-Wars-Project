package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.EndingBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice is in charge of the handling {@link BombDestroyerEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link BombDestroyerEvent}.
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;


    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration=duration;

    }

    @Override
    protected void initialize() {
        //this broadcast means everyone are going to finish.
        subscribeBroadcast(EndingBroadcast.class,(EndingBroadcast ending)->
        {//callback of broadcast
            close();
        });
        subscribeEvent(BombDestroyerEvent.class,(BombDestroyerEvent event)->
        {//callback of event bomb
            try{
                //do the mission
                Thread.sleep(duration);
                //we finished the last act. now we command everyone to terminate.
                sendBroadcast(new EndingBroadcast());
            }catch (InterruptedException e)
            {
            }
        });

    }

    @Override
    protected void close() {
        //terminating and updating the diary.
        terminate();
        Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
    }

    public long getDuration() {
        return duration;
    }
}
