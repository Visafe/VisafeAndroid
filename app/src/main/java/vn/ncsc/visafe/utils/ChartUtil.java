package vn.ncsc.visafe.utils;

import com.highsoft.highcharts.common.hichartsclasses.HIColumn;
import com.highsoft.highcharts.common.hichartsclasses.HICredits;
import com.highsoft.highcharts.common.hichartsclasses.HIExporting;
import com.highsoft.highcharts.common.hichartsclasses.HILabels;
import com.highsoft.highcharts.common.hichartsclasses.HILegend;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChartUtil {

    public static final String NAME = "name";
    public static final String Y_VALUE = "y";

    public static void initBarChart(HIChartView chartView, LinkedHashMap<String, Integer> data, String[] colorStringId) {
        if (chartView.getOptions() == null) {
            HIOptions options = new HIOptions();

            options.setExporting(new HIExporting());
            options.getExporting().setEnabled(false);
            options.setCredits(new HICredits());
            options.getCredits().setEnabled(false);
            options.setTitle(new HITitle());
            options.getTitle().setText("");

            HIXAxis xAxis = new HIXAxis();
            xAxis.setLabels(new HILabels());
            xAxis.getLabels().setEnabled(false);
            options.setXAxis(new ArrayList<HIXAxis>() {{
                add(xAxis);
            }});

            HIYAxis yAxis = new HIYAxis();
            yAxis.setMin(0);
            yAxis.setShowFirstLabel(false);
            yAxis.setGridLineDashStyle("longdash");
            yAxis.setOpposite(true);
            yAxis.setTitle(new HITitle());
            yAxis.getTitle().setText("");
            options.setYAxis(new ArrayList<HIYAxis>() {{
                add(yAxis);
            }});

            HILegend legend = new HILegend();
            legend.setEnabled(false);
            options.setLegend(legend);

//            HITooltip tooltip = new HITooltip();
//            tooltip.setShadow(true);
//            tooltip.setUseHTML(true);
//            tooltip.setBorderRadius(8);
//            tooltip.setBorderColor(HIColor.initWithHexValue("FFFFFF"));
//            tooltip.setShape("<div style=\"border-radius: 10px; padding: 8px 16px 8px 16px;" +
//                    " box-shadow: 0px 8px 12px 0px #16000000;\"></div>");
//            tooltip.setBackgroundColor(HIColor.initWithHexValue("FFFFFF"));
//            tooltip.setHeaderFormat("");
//            tooltip.setPointFormat("<span style=\"color:{point.color}; width: 8px; height: 8px; " +
//                    "margin: 0px 16px 0px 0px;\">\u25A0</span>{point.name}<br/>" +
//                    "<p style=\"font-size: 16px; font-weight:bold; color:#3A454D;" +
//                    " margin: 4px 0px 0px 24px;\">{point.y} " + context.getString(R.string.all_day) + "</p>");
//            options.setTooltip(tooltip);

            HIColumn series = new HIColumn();
            series.setColorByPoint(true);
            series.setColors(new ArrayList<>(Arrays.asList(colorStringId)));

            ArrayList<HashMap<String, Object>> dataChart = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(NAME, entry.getKey());
                map.put(Y_VALUE, entry.getValue());

                dataChart.add(map);
            }

            series.setData(dataChart);
            options.setSeries(new ArrayList<>(Arrays.asList(series)));

            chartView.setOptions(options);
            chartView.invalidate();
        } else {
            ArrayList<HashMap<String, Object>> dataChart = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(NAME, entry.getKey());
                map.put(Y_VALUE, entry.getValue());

                dataChart.add(map);
            }
            HIColumn series = (HIColumn) chartView.getOptions().getSeries().get(0);
            series.setData(dataChart);
            chartView.getOptions().setSeries(new ArrayList<>(Arrays.asList(series)));
            chartView.invalidate();
        }
    }

    public static String[] getArrayColor(int size) {
        String[] color = new String[1];
        color[0] = "#FFB31F";
        if (color.length > size) {
            return Arrays.copyOf(color, size);
        } else {
            String[] colorNew = new String[size];
            for (int i = 0; i < size; i++) {
                if (i <color.length) {
                    colorNew[i] = color[i];
                } else {
                    colorNew[i] = "#FFB31F";
                }
            }
            return colorNew;
        }
    }
}
