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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.sina.util.dnscache.R;
import com.sina.util.dnscache.simulationtask.TaskManager;
import com.sina.util.dnscache.simulationtask.TaskModel;

public class AllTaskExpendTimeActivity extends Activity implements
		OnSeekBarChangeListener, OnChartValueSelectedListener {

	public ImageButton leftBtn = null ;
	private LineChart mChart = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alltask_expendtime);

		leftBtn = (ImageButton)findViewById(R.id.left) ;
		leftBtn.setOnClickListener( new ImageButton.OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		} );
		
		initData();
	
		
		mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        
        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);
        
        mChart.setDragDecelerationFrictionCoef(0.95f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        // add data
        setData();

        mChart.animateX(2500);

        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(LegendForm.LINE);
        l.setTypeface(tf);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(tf);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaxValue(MAX_VALUE + 50);
        leftAxis.setDrawGridLines(true);
        
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(tf);
        rightAxis.setTextColor(Color.RED);
        rightAxis.setAxisMaxValue(MAX_VALUE + 50);
        rightAxis.setStartAtZero(false);	
        rightAxis.setAxisMinValue(0);
        rightAxis.setDrawGridLines(false);
		
		
	}
	
	public long MAX_VALUE = -1 ; 
	public long MIN_VALUE = 99999; 
	
	public void initData(){
		
		ArrayList<TaskModel> list = TaskManager.getInstance().list;
		if (list == null)
			return;
		
		for( TaskModel taskModel : list ){
			
			if( MIN_VALUE > taskModel.domainExpendTime  ){
				MIN_VALUE = taskModel.domainExpendTime ; 
			}
			if( MIN_VALUE > (taskModel.hostExpendTime + taskModel.httpDnsExpendTime) ){
				MIN_VALUE = (taskModel.hostExpendTime + taskModel.httpDnsExpendTime)  ; 
			}
			
			if( MAX_VALUE < taskModel.domainExpendTime  ){
				MAX_VALUE = taskModel.domainExpendTime ; 
			}
			if( MAX_VALUE < (taskModel.hostExpendTime + taskModel.httpDnsExpendTime)  ){
				MAX_VALUE = (taskModel.hostExpendTime + taskModel.httpDnsExpendTime)  ; 
			}
		}
	}
	
	public void setData(){
		
		ArrayList<TaskModel> list = TaskManager.getInstance().list;
		if (list == null)
			return;
		
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            xVals.add((i) + "");
        }
        
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for (int i = 0; i < list.size(); i++) {
        	TaskModel model = list.get(i) ; 
            yVals1.add(new Entry(( model.hostExpendTime + model.httpDnsExpendTime ), i));
        }
        LineDataSet set1 = new LineDataSet(yVals1, "IP直接请求");
        set1.setAxisDependency(AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.WHITE);
        set1.setLineWidth(2f);
        set1.setCircleSize(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
        for (int i = 0; i < list.size(); i++) {
        	TaskModel model = list.get(i) ; 
            yVals2.add(new Entry(model.domainExpendTime, i));
        }
        // create a dataset and give it a type
        LineDataSet set2 = new LineDataSet(yVals2, "域名直接请求");
        set2.setAxisDependency(AxisDependency.RIGHT);
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.WHITE);
        set2.setLineWidth(2f);
        set2.setCircleSize(3f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircleHole(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));
        
        
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set2);
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
		
	}
	
	
	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub

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

}
