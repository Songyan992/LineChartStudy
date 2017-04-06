package com.example.study.android.linechartstudy;

import android.content.Context;
import android.text.Html;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by songyan on 2017/3/1.
 */

public class DataMarkerView extends MarkerView {
    private RelativeLayout markLay;
    private TextView tvContent;
    private List<JsonData.HistoricalPrice.HistoricalPriceData.DataList> datalist;
    private boolean isLeft = true;
    private String satrtTime;
    private DecimalFormat format;

    public DataMarkerView(Context context, int layoutResource, List<JsonData.HistoricalPrice.HistoricalPriceData.DataList> datalist, long sart) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.mark_tv);
        markLay = (RelativeLayout) findViewById(R.id.mark_lay);
        satrtTime = new java.text.SimpleDateFormat("yyyy-MM-dd").
                format(new java.util.Date(Long.valueOf(sart) * 1000));
        this.datalist = datalist;
        format = new DecimalFormat("###.0");

    }

    private void setMarkLay() {
        markLay.setBackgroundResource(isLeft ? R.mipmap.icon_mark_right : R.mipmap.icon_mark_left);
        invalidate();
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {

       // tvContent.setText(getxStr(format.format(e.getX())) + ":" + format.format(e.getY()) + "￥");
        tvContent.setText(Html.fromHtml("<font color='#666666'>"+getxStr(format.format(e.getX()))
                + ":"+"</font><font color='#fa2626'>" + "￥"+format.format(e.getY()) +"</font>"));
        super.refreshContent(e, highlight);
    }

    private String getxStr(String xStr) {
        for (int i = 0; i < datalist.size(); i++) {
            if (xStr.equals(i + ".0")) {
                if (datalist.size() > 2) {
                    setIsConten(i);
                } else {
                    markLay.setBackgroundResource(xStr.equals(".0") ? R.mipmap.icon_mark_right : R.mipmap.icon_mark_left);
                }
                xStr = formatTime(Long.valueOf(datalist.get(i).getPrice_drop_time()) * 1000, "yyyy-MM-dd ");
            }
        }
        if (xStr.equals(".0")) {
            xStr = satrtTime;
            markLay.setBackgroundResource(R.mipmap.icon_mark_right);
        }
        return xStr;
    }
    public String formatTime(long longTime, String formatStr) {
        SimpleDateFormat formater = new SimpleDateFormat(formatStr);
        Date date = new Date(longTime);
        return formater.format(date);
    }

    private void setIsConten(int count) {
        if (count <= datalist.size() / 2) {
            isLeft = true;
        } else {
            isLeft = false;
        }
        setMarkLay();

    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }


    @Override
    public float getX() {
        return super.getX();
    }

    @Override
    public float getY() {
        return super.getY();
    }


}
