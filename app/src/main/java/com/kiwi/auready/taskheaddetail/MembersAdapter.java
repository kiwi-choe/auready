package com.kiwi.auready.taskheaddetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kiwi.auready.R;
import com.kiwi.auready.data.Member;
import com.kiwi.auready.util.view.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kiwi on 12/19/16.
 */

public class MembersAdapter extends ArrayAdapter<Member> {

    private HashMap<Integer, Boolean> mSelection = new HashMap<>();
    float mCheckboxStartX = -1;
    float mCheckboxEndX = -1;

    public MembersAdapter(Context context, int resource, List<Member> members) {
        super(context, resource, members);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Member getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        final ViewHolder viewHolder;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.member_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.delete_member_check_box);
            viewHolder.memberName = (TextView) rowView.findViewById(R.id.member_name);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        final Member member = getItem(position);
        if (member != null) {
            viewHolder.memberName.setText(member.getName());
        }

        HoldPosition(rowView);

        if (mSelection.get(position) != null) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }

        return rowView;
    }

    // for delete Item
    public void clearSelection() {
        mSelection = new HashMap<>();
        notifyDataSetChanged();
    }

    public void setNewSelection(int position, boolean checked) {
        mSelection.put(position, checked);
        notifyDataSetChanged();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelection.size();
    }

    private class ViewHolder {
        CheckBox checkBox;
        TextView memberName;
    }

    // animation
    public void startAnimation(View view, boolean isDelete) {
        final float distance = view.getResources().getDimension(R.dimen.checkbox_start_trans_x);
        mCheckboxStartX = isDelete ? distance : 0;
        mCheckboxEndX = isDelete ? 0 : distance;

        ArrayList<View> views = new ArrayList<>();
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.delete_member_check_box);
        if (checkbox == null) {
            return;
        }
        checkbox.setTranslationX(mCheckboxStartX);
        checkbox.animate().translationX(mCheckboxEndX);
        views.add(checkbox);

        TextView textview = (TextView) view.findViewById(R.id.member_name);
        if (textview == null) {
            return;
        }
        textview.setTranslationX(mCheckboxStartX - distance);
        textview.animate().translationX(mCheckboxEndX - distance);
        views.add(textview);

        for (View animatedView : views) {
            animatedView.animate().setDuration(ViewUtils.ANIMATION_DURATION)
                    .setInterpolator(ViewUtils.INTERPOLATOR).start();
        }
    }

    private void HoldPosition(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        final float distance = view.getResources().getDimension(R.dimen.checkbox_start_trans_x);
        if (mCheckboxStartX == -1) {
            return;
        }

        if (holder.checkBox.getTranslationX() != mCheckboxEndX) {
            holder.checkBox.setTranslationX(mCheckboxEndX);
            holder.memberName.setTranslationX(mCheckboxEndX - distance);
        }
    }
}

