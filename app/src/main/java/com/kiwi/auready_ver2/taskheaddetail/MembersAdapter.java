package com.kiwi.auready_ver2.taskheaddetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Member;

import java.util.List;

/**
 * Created by kiwi on 12/19/16.
 */

public class MembersAdapter extends ArrayAdapter<Member> {

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
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.member_item, parent, false);
        }
        final Member member = getItem(position);

        TextView memberNameTV = (TextView) rowView.findViewById(R.id.member_name);
        if (member != null) {
            memberNameTV.setText(member.getName());
        }

        return rowView;
    }
}

