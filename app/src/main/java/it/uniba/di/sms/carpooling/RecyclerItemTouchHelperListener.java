package it.uniba.di.sms.carpooling;

import android.support.v7.widget.RecyclerView;

/**
 * Created by loiodice on 18/06/2018.
 */

public interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder,int direction,int position);
}
