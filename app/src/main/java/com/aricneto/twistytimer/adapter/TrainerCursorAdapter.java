package com.aricneto.twistytimer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.aricneto.twistify.R;
import com.aricneto.twistytimer.fragment.dialog.AlgDialog;
import com.aricneto.twistytimer.puzzle.TrainerScrambler;
import com.aricneto.twistytimer.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class TrainerCursorAdapter extends AlgCursorAdapter {

    private List<Long> selectedItems;
    private FragmentManager fragmentManager;
    private Context mContext;

    TrainerScrambler.TrainerSubset currentSubset;
    String currentPuzzleCategory;

    Drawable cardBackground;
    Drawable selectedCardBackground;

    public TrainerCursorAdapter(Context context, Cursor cursor, Fragment listFragment, TrainerScrambler.TrainerSubset subset, String category) {
        super(context, cursor, listFragment);
        Log.d("TrainerCursor", "Created trainerCursor " + subset + category);
        this.mContext = context;
        this.fragmentManager = listFragment.getFragmentManager();

        cardBackground = ContextCompat.getDrawable(mContext, R.drawable.no_stroke_card);
        selectedCardBackground = ContextCompat.getDrawable(mContext, R.drawable.stroke_card);

        selectedItems = new ArrayList<>();
        selectedItems.addAll(TrainerScrambler.fetchSelectedItemsLong(subset, category));

        this.currentSubset = subset;
        this.currentPuzzleCategory = category;

    }

    private boolean isSelected(long id) {
        return selectedItems.contains(id);
    }

    public void unselectAll() {
        selectedItems.clear();
        TrainerScrambler.saveSelectedItems(currentSubset, currentPuzzleCategory, selectedItems);
    }

    public void selectAll() {
        int size = selectedItems.size();
        Log.d("TRAINER","selecteditems: " + size);
        selectedItems.clear();
        switch (currentSubset) {
            case OLL:
                if (size != 58) {
                    for (int i = 0; i < 58; i++)
                        selectedItems.add((long) i);
                }
                break;
            case PLL:
                if (size != 22) {
                    for (int i = 0; i < 22; i++)
                        selectedItems.add((long) i + 57);
                }
                break;
        }
        TrainerScrambler.saveSelectedItems(currentSubset, currentPuzzleCategory, selectedItems);
    }

    private void toggleSelection(long id, CardView card) {
        if (!isSelected(id)) {
            selectedItems.add(id);
            card.setBackground(selectedCardBackground);
        } else {
            selectedItems.remove(id);
            card.setBackground(cardBackground);
        }
        TrainerScrambler.saveSelectedItems(currentSubset, currentPuzzleCategory, selectedItems);
    }

    @Override
    public void handleTime(AlgHolder holder, Cursor cursor) {
        super.handleTime(holder, cursor);

        long id = cursor.getLong(0);

        if (isSelected(id)) {
            holder.card.setBackground(selectedCardBackground);
        } else {
            holder.card.setBackground(cardBackground);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSelection(id, holder.card);
            }
        });

        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isLocked()) {
                    setIsLocked(true);
                    AlgDialog algDialog = AlgDialog.newInstance(id);
                    algDialog.show(fragmentManager, "alg_dialog");
                    algDialog.setDialogListener(TrainerCursorAdapter.this);
                }
                return true;
            }
        });
    }



    @Override
    public Cursor swapCursor(Cursor cursor) {
        super.swapCursor(cursor);
        //unselectAll();
        return cursor;
    }
}