package cn.edu.nju.dapenti;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabWidget;

public class VerticalTabWidget extends TabWidget {
	Resources res;

	public VerticalTabWidget(Context context, AttributeSet attrs) {

		super(context, attrs);

		res = context.getResources();

		setOrientation(LinearLayout.VERTICAL);

	}

	@Override
	public void addView(View child) {

		LinearLayout.LayoutParams lp = new LayoutParams(

		LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);

		lp.setMargins(0, 0, 0, 0);

		child.setLayoutParams(lp);

		super.addView(child);

		child.setBackgroundDrawable(res.getDrawable(R.drawable.vertical_tab_selector));

	}
}