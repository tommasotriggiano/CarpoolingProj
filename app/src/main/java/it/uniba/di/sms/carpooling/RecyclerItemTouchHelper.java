package it.uniba.di.sms.carpooling;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import it.uniba.di.sms.carpooling.rideOffered.PassaggiViewHolder;
import it.uniba.di.sms.carpooling.rideRequired.RequiredViewHolder;

/**
 * Created by loiodice on 18/06/2018.
 */

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback{
    private RecyclerItemTouchHelperListener listener;
    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs,RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener=listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if(listener!= null){
           listener.onSwiped(viewHolder,direction,viewHolder.getAdapterPosition());
        }

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
       if (viewHolder!=null){
           if(viewHolder instanceof PassaggiViewHolder){
              View  viewForeground= ((PassaggiViewHolder)viewHolder).viewForeground;
               getDefaultUIUtil().onSelected(viewForeground);
           }else if (viewHolder instanceof RequiredViewHolder) {
               View viewForeground =((RequiredViewHolder)viewHolder).viewForeground;
               getDefaultUIUtil().onSelected(viewForeground);
           }

       }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof PassaggiViewHolder) {
            View viewForeground = ((PassaggiViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().clearView(viewForeground);
        }else if (viewHolder instanceof RequiredViewHolder) {
            View viewForeground = ((RequiredViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().clearView(viewForeground);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if(viewHolder instanceof PassaggiViewHolder) {
            View viewForeground= ((PassaggiViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().onDraw(c,recyclerView,viewForeground,dX,dY,actionState,isCurrentlyActive);
        }else if (viewHolder instanceof RequiredViewHolder) {
            View viewForeground= ((RequiredViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().onDraw(c,recyclerView,viewForeground,dX,dY,actionState,isCurrentlyActive);
        }

    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(viewHolder instanceof PassaggiViewHolder) {
            View viewForeground = ((PassaggiViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, viewForeground, dX, dY, actionState, isCurrentlyActive);
        }else if (viewHolder instanceof RequiredViewHolder) {
            View viewForeground = ((RequiredViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, viewForeground, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
