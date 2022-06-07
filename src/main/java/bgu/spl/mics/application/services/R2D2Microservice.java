package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.EndingBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long duration;

    public R2D2Microservice(long duration) {

        super("R2D2");
        this.duration=duration;

    }

    @Override
    protected void close() {
        Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());
    }

    @Override
    protected void initialize() {
        //we activate when we need to diactivate the shield.
        subscribeEvent(DeactivationEvent.class,(DeactivationEvent event)->
        {//callback
            try{
                //diactivating......
                Thread.sleep(duration);
                //we finished! we will update the future file leia holds so she can continue.
                complete(event,true);
                //update finishing the task! will be updated each time so we get the last time it was dieactivated.
                close();
            }catch (InterruptedException e)
            { }
        });

        //the final broadcast to end all the threads and updating the diary.
        subscribeBroadcast(EndingBroadcast.class,(EndingBroadcast broad)->
        {
            terminate();
            Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
        });

    }


    public long getDuration() {
        return duration;
    }
}
