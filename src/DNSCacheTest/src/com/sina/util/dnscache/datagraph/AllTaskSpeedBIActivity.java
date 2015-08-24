package com.sina.util.dnscache.datagraph;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.sina.util.dnscache.R;
import com.sina.util.dnscache.simulationtask.TaskManager;
import com.sina.util.dnscache.simulationtask.TaskModel;

public class AllTaskSpeedBIActivity extends Activity implements
		OnSeekBarChangeListener, OnChartValueSelectedListener {

	public ImageButton leftBtn = null ;
	
	private PieChart mChart;
	private Typeface tf;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_task_speed_bi);

		leftBtn = (ImageButton)findViewById(R.id.left) ;
		leftBtn.setOnClickListener( new ImageButton.OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		} );
		
		
		mChart = (PieChart) this.findViewById(R.id.chart1);

		mChart.setUsePercentValues(true);
		mChart.setDescription("");

		mChart.setDragDecelerationFrictionCoef(0.95f);

		tf = Typeface.createFromAsset(this.getAssets(), "OpenSans-Regular.ttf");

		mChart.setCenterTextTypeface(Typeface.createFromAsset(this.getAssets(),
				"OpenSans-Light.ttf"));

		mChart.setDrawHoleEnabled(true);
		mChart.setHoleColorTransparent(true);

		mChart.setTransparentCircleColor(Color.WHITE);

		mChart.setHoleRadius(58f);
		mChart.setTransparentCircleRadius(61f);

		mChart.setDrawCenterText(true);

		mChart.setRotationAngle(0);
		// enable rotation of the chart by touch
		mChart.setRotationEnabled(true);

		// mChart.setUnit(" €");
		// mChart.setDrawUnitsInChart(true);

		// add a selection listener
		mChart.setOnChartValueSelectedListener(this);

		mChart.setCenterText("加速和延迟任务比值");

		setData();

		mChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);

	}

	private void setData() {

		ArrayList<TaskModel> list = TaskManager.getInstance().list;
		if (list == null)
			return;
		int accelerateNum = 0;
		int delayNum = 0;

		for (TaskModel taskModel : list) {
			if (taskModel.domainExpendTime
					- (taskModel.hostExpendTime + taskModel.httpDnsExpendTime) <= 0) {
				delayNum++;
			} else {
				accelerateNum++;
			}
		}

		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		Entry accelerate = new Entry(accelerateNum, 0);
		Entry delay = new Entry(delayNum, 1);
		yVals1.add(accelerate);
		yVals1.add(delay);

		ArrayList<String> xVals = new ArrayList<String>();
		xVals.add("加速:" + accelerateNum);
		xVals.add("延迟:" + delayNum);

		PieDataSet dataSet = new PieDataSet(yVals1, "全部:"
				+ (accelerateNum + delayNum));
		dataSet.setSliceSpace(3f);
		dataSet.setSelectionShift(5f);

		ArrayList<Integer> colors = new ArrayList<Integer>();
		for (int c : ColorTemplate.VORDIPLOM_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.JOYFUL_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.COLORFUL_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.LIBERTY_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.PASTEL_COLORS)
			colors.add(c);
		colors.add(ColorTemplate.getHoloBlue());
		dataSet.setColors(colors);

		PieData data = new PieData(xVals, dataSet);
		data.setValueFormatter(new PercentFormatter());
		data.setValueTextSize(11f);
		data.setValueTextColor(Color.BLACK);
		data.setValueTypeface(tf);
		mChart.setData(data);
		// undo all highlights
		mChart.highlightValues(null);
		mChart.invalidate();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub

	}

}
