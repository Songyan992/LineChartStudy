package com.example.study.android.linechartstudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RadioGroup mRadioGroup;
    private LineChart mLineChart;
    private TextView detailMinTimeTv, detailMaxTimeTv;

    private String jsonStr = "{\n" +
            "    \"historical_price\": [\n" +
            "        {\n" +
            "            \"title\": \"京东\",\n" +
            "            \"mall_url\": \"https://detail.m.tmall.com/item.htm?id=\",\n" +
            "            \"data\": {\n" +
            "                \"data_list\": [\n" +
            "                    {\n" +
            "                        \"price_new\": \"5999\",\n" +
            "                        \"price_drop_time\": \"1488419309\",\n" +
            "                        \"sub_title\": \"降价说明1\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"price_new\": \"4690\",\n" +
            "                        \"price_drop_time\": \"1488516784\",\n" +
            "                        \"sub_title\": \"降价说明1\"\n" +
            "                    }\n" +
            "                ],\n" +
            "                \"start_time\": \"1488419309\",\n" +
            "                \"end_time\": 1488603184\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"where_from\": \"tmall\",\n" +
            "            \"title\": \"天猫\",\n" +
            "            \"data\": {\n" +
            "                \"data_list\": [\n" +
            "                    {\n" +
            "                        \"price_new\": \"2490\",\n" +
            "                        \"price_drop_time\": \"1488753242\",\n" +
            "                        \"sub_title\": \"降价说明1\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"price_new\": \"2590\",\n" +
            "                        \"price_drop_time\": \"1489886047\",\n" +
            "                        \"sub_title\": \"降价说明1\"\n" +
            "                    }\n" +
            "                ],\n" +
            "                \"start_time\": \"1488753242\",\n" +
            "                \"end_time\": 1489972447\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();

    }

    private void findView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);
        mLineChart = (LineChart) findViewById(R.id.mLineChart);
        detailMinTimeTv = (TextView) findViewById(R.id.detailMinTimeTv);
        detailMaxTimeTv = (TextView) findViewById(R.id.detailMaxTimeTv);
        setView();
    }

    private void setView() {
        JsonData jsonDetail = new Gson().fromJson(jsonStr.toString(), new TypeToken<JsonData>() {
        }.getType());
        if (jsonDetail.getHistorical_price() != null && jsonDetail.getHistorical_price().size() > 0) {
            setGroupLay(jsonDetail.getHistorical_price());
        }
    }

    private void setGroupLay(List<JsonData.HistoricalPrice> list) {
        mRadioGroup.setVisibility((list.size() > 1) ? View.VISIBLE : View.GONE);
        addViewForGroup(list);
    }


    private ArrayList<Float> priceList = new ArrayList<Float>();

    private float setMinPrice(JsonData.HistoricalPrice.HistoricalPriceData data) {
        if (priceList != null && priceList.size() > 0) {
            priceList.clear();
        }
        for (int i = 0; i < data.getData_list().size(); i++) {
            priceList.add(Float.valueOf(data.getData_list().get(i).getPrice_new()));
        }
        return Collections.min(priceList) / 100;
    }

    private LineChartWidget mLineCharWidget;

    private void addViewForGroup(final List<JsonData.HistoricalPrice> list) {
        for (int i = 0; i < list.size(); i++) {
            final RadioButton view = (RadioButton) LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.item_gr_add_but_layout, mRadioGroup, false);
            view.setId(i);
            view.setText(list.get(i).getTitle());
            if (i==0){
                view.performClick();
                radioGroupTextChange(list.get(0).getData(), list.get(0).getTitle());
                mLineCharWidget = new LineChartWidget(MainActivity.this,
                        list.get(0).getData(), mLineChart, setMinPrice(list.get(0).getData()));
            }
            mRadioGroup.addView(view);

        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = (RadioButton) findViewById(checkedId);
                button.setText(list.get(checkedId).getTitle());

                for (int i = 0; i < list.size(); i++) {
                    if (button.getText().toString().equals(list.get(i).getTitle())) {
                       radioGroupTextChange(list.get(i).getData(), list.get(i).getTitle());
                        if (mLineCharWidget == null) {
                            mLineCharWidget = new LineChartWidget(MainActivity.this,
                                    list.get(i).getData(), mLineChart, setMinPrice(list.get(i).getData()));
                        } else {
                            mLineCharWidget.updateLineChar(list.get(i).getData(), setMinPrice(list.get(i).getData()));
                        }

                    }
                }
            }
        });
    }
    private void radioGroupTextChange(JsonData.HistoricalPrice.HistoricalPriceData data, String title) {
        String dateStart = TimeToString(Long.valueOf(data.getStart_time()));
        String dateEnd = TimeToString(Long.valueOf(data.getEnd_time()));
        detailMinTimeTv.setText(dateStart);
        detailMaxTimeTv.setText(dateEnd);
    }

    private String TimeToString(long time) {
        return new java.text.SimpleDateFormat("MM-dd").
                format(new java.util.Date(Long.valueOf(time) * 1000));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (priceList!=null){
            priceList.clear();
            priceList=null;
        }
        if (mLineCharWidget!=null){
            mLineCharWidget = null;
        }

    }
}
