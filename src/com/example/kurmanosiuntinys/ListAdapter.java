package com.example.kurmanosiuntinys;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Item> {

	public ListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public ListAdapter(Context context, int resource, List<Item> items) {
		super(context, resource, items);
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		if (v == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.list, null);
		}

		Item myItem = getItem(position);
		ItemInfo myItemInfo = myItem.getLastItemInfo();

		if (myItem != null) {
			TextView aliasText = (TextView) v.findViewById(R.id.alias);
			TextView numberText = (TextView) v.findViewById(R.id.number);
			TextView dateText = (TextView) v.findViewById(R.id.date);
			TextView placeText = (TextView) v.findViewById(R.id.place);
			TextView explainText = (TextView) v.findViewById(R.id.explain);
			ImageView logoImg = (ImageView) v.findViewById(R.id.img);

			if (aliasText != null)
				aliasText.setText(myItem.getAlias());
			if (numberText != null)
				numberText.setText(myItem.getNumber());
			if (dateText != null)
				dateText.setText((myItemInfo != null) ? myItemInfo.getDate() : myItem.getDate());
			if (placeText != null)
				placeText.setText((myItemInfo != null) ? myItemInfo.getPlace() : "");
			if (explainText != null)
				explainText.setText((myItemInfo != null) ? myItemInfo.getExplain() : "");

			Item.Status status = myItem.getStatus();
			int icon = 0;
			switch (status) {
			case BLOGAS:
			case NERA:
				icon = R.drawable.ic_box_red;
				break;
			case VILNIUS:
				icon = R.drawable.ic_box_yellow;
				break;
			case PASTE:
			case PASIIMTA:
				icon = R.drawable.ic_box_green;
				break;

			default:
				break;
			}
			if (logoImg != null)
				logoImg.setImageResource(icon);

		}
		return v;
	}
}