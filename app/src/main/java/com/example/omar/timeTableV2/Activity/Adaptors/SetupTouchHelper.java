package com.example.omar.timeTableV2.Activity.Adaptors;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;


public class SetupTouchHelper extends ItemTouchHelper.SimpleCallback{


    private TimetableSetupAdaptor timetableSetupAdaptor;


    public SetupTouchHelper(TimetableSetupAdaptor timetableSetupAdaptor){

        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
              ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.timetableSetupAdaptor = timetableSetupAdaptor;
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target){

        timetableSetupAdaptor
                .moveSession(viewHolder.getAdapterPosition(), target.getAdapterPosition());

        return true;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){

        timetableSetupAdaptor.removeSession(viewHolder.getAdapterPosition());

    }
}
