package com.kiwi.auready_ver2.taskheaddetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiwi on 12/19/16.
 */

public class MembersAdapter extends ArrayAdapter<Friend> {

    private List<Friend> mMembers;

    public MembersAdapter(Context context, int resource, List<Friend> friends) {
        super(context, resource);
        setList(friends);
    }

    private void setList(List<Friend> members) {
        if(mMembers == null) {
            mMembers = new ArrayList<>(0);
        }
        mMembers.addAll(members);
    }

    public void replaceData(List<Friend> members) {
        setList(members);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMembers.size();
    }

    @Override
    public Friend getItem(int position) {
        return mMembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.member_item, parent, false);
        }
        final Friend member = getItem(position);

        TextView memberNameTV = (TextView) rowView.findViewById(R.id.member_name);
        memberNameTV.setText(member.getName());

        return rowView;
    }
}

