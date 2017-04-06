package com.example.study.android.linechartstudy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by songyan on 2017/3/2.
 */

public class LineChartWidget extends FrameLayout implements OnChartGestureListener, OnChartValueSelectedListener {

    private JsonData.HistoricalPrice.HistoricalPriceData mHistoricalPrice;
    private LineChart mLineChar;
    private float minData;
    private Context mContext;


    public LineChartWidget(Context context) {
        super(context);
    }

    public LineChartWidget(Context context, JsonData.HistoricalPrice.HistoricalPriceData price, LineChart mLineChar, float minData) {
        super(context);
        mHistoricalPrice = price;
        this.mLineChar = mLineChar;
        this.minData = minData;
        mContext = context;
        initLineChar();

    }

    ArrayList<String> xAxisValuesStr = new ArrayList<String>();

    private void initLineChar() {
        List<JsonData.HistoricalPrice.HistoricalPriceData.DataList> datalist
                = removeDuplicteData(mHistoricalPrice.getData_list());
        //设置手势滑动事件
        mLineChar.setOnChartGestureListener(this);
        //设置数值选择监听
        mLineChar.setOnChartValueSelectedListener(this);
        //后台绘制
        mLineChar.setDrawGridBackground(false);
        //设置描述文本
        mLineChar.getDescription().setEnabled(false);
        mLineChar.setTouchEnabled(true); // 设置是否可以触摸
        mLineChar.setDragEnabled(true);// 是否可以拖拽
        mLineChar.setScaleXEnabled(true); //是否可以缩放 仅x轴
        mLineChar.setScaleYEnabled(true); //是否可以缩放 仅y轴
        mLineChar.setPinchZoom(true);  //设置x轴和y轴能否同时缩放。默认是否

        mLineChar.setDragDecelerationFrictionCoef(0.99f);
        mLineChar.getAxisRight().setEnabled(false);
        // 默认动画
        mLineChar.animateX(2500);

        setMakeList(removeDuplicteData(datalist));
        initMark(makeList, Long.valueOf(mHistoricalPrice.getStart_time()));
        initXAxis(datalist.size(), xAxisValuesStr);
        initYAxis();
        initLegend();
        setLineCharData(makeList);
    }

    private List<JsonData.HistoricalPrice.HistoricalPriceData.DataList> makeList
            = new ArrayList<JsonData.HistoricalPrice.HistoricalPriceData.DataList>();

    //设置数据
    private void setMakeList(List<JsonData.HistoricalPrice.HistoricalPriceData.DataList> datalist) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dBegin = format.parse(format.format(Long.valueOf(mHistoricalPrice.getStart_time()) * 1000));
            Date dEnd = format.parse(format.format(Long.valueOf(mHistoricalPrice.getEnd_time()) * 1000));
            float prices = 0;
            List<Date> listDate = getDatesBetweenTwoDate(dBegin, dEnd);
            if (datalist.size() >= listDate.size()) {
                makeList.clear();
                makeList.addAll(datalist);
            } else {
                for (int i = 0; i < listDate.size(); i++) {
                    JsonData.HistoricalPrice.HistoricalPriceData.DataList data
                            = new JsonData.HistoricalPrice.HistoricalPriceData.DataList();
                    for (int j = 0; j < datalist.size(); j++) {
                        if (TimeToString(DateToTimestamp(listDate.get(i))).equals(TimeToString(Long.valueOf(datalist.get(j).getPrice_drop_time())))) {
                            data.setPrice_drop_time(datalist.get(j).getPrice_drop_time());
                            data.setPrice_new(datalist.get(j).getPrice_new());
                            prices = (datalist.get(j).getPrice_new());

                        } else {
                            data.setPrice_drop_time(DateToTimestamp(listDate.get(i)) + "");
                            data.setPrice_new(prices);
                        }
                    }
                    makeList.add(data);
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private String TimeToString(long time) {
        return new java.text.SimpleDateFormat("MM-dd").
                format(new java.util.Date(Long.valueOf(time) * 1000));
    }

    /**
     * 根据开始时间和结束时间返回时间段内的时间集合
     *
     * @param beginDate
     * @param endDate
     * @return List
     */
    public List<Date> getDatesBetweenTwoDate(Date beginDate, Date endDate) {
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(beginDate);// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                if (inSameDay(endDate, cal.getTime())) {
                    break;
                } else {
                    lDate.add(cal.getTime());
                }
            } else {
                break;
            }
        }
        lDate.add(endDate);// 把结束时间加入集合
        return lDate;
    }

    /**
     * 是否是同一天
     *
     * @param date1
     * @param Date2
     * @return
     */
    public static boolean inSameDay(Date date1, Date Date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int year1 = calendar.get(Calendar.YEAR);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(Date2);
        int year2 = calendar.get(Calendar.YEAR);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        if ((year1 == year2) && (day1 == day2)) {
            return true;
        }
        return false;
    }

    /**
     * Date类型转换为10位时间戳
     *
     * @param time
     * @return
     */
    public long DateToTimestamp(Date time) {
        Timestamp ts = new Timestamp(time.getTime());
        return (int) ((ts.getTime()) / 1000);
    }

    private void setLineCharData(List<JsonData.HistoricalPrice.HistoricalPriceData.DataList> datalist) {
        if (datalist != null) {
            ArrayList<Entry> values = new ArrayList<Entry>();
            for (int i = 0; i < datalist.size(); i++) {
                float y = datalist.get(i).getPrice_new() / 100;
                float x = i;
                String strTime = formatTime(Long.valueOf
                        (datalist.get(i).getPrice_drop_time()), "MM-dd ");
                Entry entry = new Entry(x, y);
                values.add(entry);
                xAxisValuesStr.add(strTime);
            }
            //设置数据
            setData(values);
        }
    }

    /**
     * Function:将时间毫秒值转化为格式化的日期形式
     *
     * @param longTime  时间毫秒值
     * @param formatStr 格式化字符串 比如："yyyy-MM-dd 'at' HH:mm:ss:ms"
     * @return
     */
    public String formatTime(long longTime, String formatStr) {
        SimpleDateFormat formater = new SimpleDateFormat(formatStr);
        Date date = new Date(longTime);
        return formater.format(date);
    }

    //设置x轴
    private void initXAxis(int dataListSize, ArrayList<String> valuesStr) {
        XAxis xAxis = mLineChar.getXAxis();
        xAxis.setEnabled(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGridColor(Color.rgb(229, 229, 229));


    }

    //设置y 轴
    private void initYAxis() {
        YAxis leftAxis = mLineChar.getAxisLeft();
        //重置所有限制线,以避免重叠线
        //leftAxis.removeAllLimitLines();
        //y轴最小
//        LimitLine ll = new LimitLine(minData, "最小值："+minData);
//        ll.setLineColor(Color.RED);
//        ll.setLineWidth(2f);
//        ll.enableDashedLine(4f, 4f, 0f);
//        ll.setTextColor(Color.BLACK);
//        ll.setTextSize(12f);
//        leftAxis.addLimitLine(ll);
//        LimitView limitView = new LimitView(mContext,minData,R.drawable.shape_dot_selected);
//        leftAxis.addLimitView(limitView);

        leftAxis.setValueFormatter(new MyYAxisValueFormatter());
        leftAxis.setAxisMinimum(0f);
        leftAxis.setSpaceTop(15f);
        leftAxis.setGridColor(Color.rgb(229, 229, 229));
        leftAxis.setTextColor(Color.rgb(181, 181, 181));
        leftAxis.setTextSize(12f);

        leftAxis.setDrawZeroLine(false);

    }


    //设置mark
    private void initMark(List<JsonData.HistoricalPrice.HistoricalPriceData.DataList> datalist, long sartTime) {
        //  IAxisValueFormatter xAxisFormatter = new MyXAxisValueFormatter(valuesStr);
        DataMarkerView mv = new DataMarkerView(getContext(), R.layout.custom_marker_view, datalist, sartTime);
        mv.setChartView(mLineChar); // For bounds control
        mLineChar.setMarker(mv); // Set the marker to the chart
    }

    //设置图标隐藏
    private void initLegend() {
        Legend l = mLineChar.getLegend();
        l.setEnabled(false);
    }

    //去除重复数据
    public ArrayList<JsonData.HistoricalPrice.HistoricalPriceData.DataList>
    removeDuplicteData(List<JsonData.HistoricalPrice.HistoricalPriceData.DataList> userList) {
        Set<JsonData.HistoricalPrice.HistoricalPriceData.DataList> s
                = new TreeSet<JsonData.HistoricalPrice.HistoricalPriceData.DataList>
                (new Comparator<JsonData.HistoricalPrice.HistoricalPriceData.DataList>() {
                    @Override
                    public int compare(JsonData.HistoricalPrice.HistoricalPriceData.DataList lhs,
                                       JsonData.HistoricalPrice.HistoricalPriceData.DataList rhs) {
                        return lhs.getPrice_drop_time().compareTo(rhs.getPrice_drop_time());
                    }


                });

        s.addAll(userList);
        return new ArrayList<JsonData.HistoricalPrice.HistoricalPriceData.DataList>(s);
    }

    //传递数据集
    private void setData(ArrayList<Entry> values) {
        LineDataSet set1 = null;
        if (mLineChar.getData() != null && mLineChar.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mLineChar.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mLineChar.getData().notifyDataChanged();
            mLineChar.notifyDataSetChanged();
        } else {
            // 创建一个数据集,并给它一个类型
            if (set1 == null) {
                set1 = new LineDataSet(values, "价格曲线图");
                set1.setColor(Color.rgb(27, 198, 181));
                set1.setCircleColor(Color.BLACK);
                set1.setLineWidth(1f);
                set1.setCircleRadius(3f);
                set1.setDrawCircleHole(false);
                set1.setValueTextSize(9f);
                set1.setDrawFilled(true);
                set1.setFormLineWidth(1f);
                set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                set1.setHighlightEnabled(true);  //允许突出显示DataSet
                set1.setDrawHighlightIndicators(false); // 取消点击线上的点展示十字标识
                set1.setDrawValues(true); // 不展示线上面点的值
                //是否显示小圆点
                set1.setDrawCircles(false);
                //修改源码 自定义的参数，可以显示最低点的View
                set1.setLowDrawCircles(true);
                set1.setCircleColors(Color.rgb(27, 198, 181));//27, 198, 181
                //顶点设置值
                set1.setDrawValues(false);
                set1.setFillColor(Color.rgb(203, 242, 238));
            }
            //修改源码 自定义的参数，可以显示最低点的View
            set1.setLowNumbers(minData);
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            //添加数据集
            dataSets.add(set1);
            //创建一个数据集的数据对象
            LineData data = new LineData(dataSets);
            //设置数据
            mLineChar.setData(data);
        }
    }

    public void updateLineChar(JsonData.HistoricalPrice.HistoricalPriceData data, float mindata) {
        makeList.clear();
        minData = mindata;
        mHistoricalPrice = data;
        List<JsonData.HistoricalPrice.HistoricalPriceData.DataList> datalist
                = removeDuplicteData(data.getData_list());
        setMakeList(removeDuplicteData(datalist));
        setLineCharData(makeList);
        mLineChar.invalidate();
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mLineChar.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {
    }

    public class MyYAxisValueFormatter implements IAxisValueFormatter {


        private DecimalFormat mFormat;

        public MyYAxisValueFormatter() {
            mFormat = new DecimalFormat("###");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (xAxisValuesStr != null) {
            xAxisValuesStr = null;
        }

    }
}
