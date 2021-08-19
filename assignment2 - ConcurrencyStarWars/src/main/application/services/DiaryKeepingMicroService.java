package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;

public abstract class DiaryKeepingMicroService extends MicroService {

    protected Diary diary;

    /* Abstract Class for MicroServices that keep a diary.
    It is not part of the framework and the Diary is not a "quality" of MicroService, therefore doesn't belong as it's field */
    public DiaryKeepingMicroService(String name) {
        super(name);
        diary = Diary.getInstance();
    }
}
