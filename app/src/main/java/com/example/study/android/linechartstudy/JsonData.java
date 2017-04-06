package com.example.study.android.linechartstudy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by songyan on 2017/4/6.
 */

public class JsonData implements Serializable {

    private List<HistoricalPrice> historical_price;

    public List<HistoricalPrice> getHistorical_price() {
        return historical_price;
    }

    public void setHistorical_price(List<HistoricalPrice> historical_price) {
        this.historical_price = historical_price;
    }

    public static class HistoricalPrice implements Serializable {

        private String title;
        private String mall_url;
        private HistoricalPriceData data;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMall_url() {
            return mall_url;
        }

        public void setMall_url(String mall_url) {
            this.mall_url = mall_url;
        }

        public HistoricalPriceData getData() {
            return data;
        }

        public void setData(HistoricalPriceData data) {
            this.data = data;
        }

        public static class HistoricalPriceData implements Serializable {
            private List<DataList> data_list;

            private String start_time;
            private String end_time;

            public List<DataList> getData_list() {
                return data_list;
            }

            public void setData_list(List<DataList> data_list) {
                this.data_list = data_list;
            }

            public String getStart_time() {
                return start_time;
            }

            public void setStart_time(String start_time) {
                this.start_time = start_time;
            }

            public String getEnd_time() {
                return end_time;
            }

            public void setEnd_time(String end_time) {
                this.end_time = end_time;
            }

            public static class DataList implements Serializable {

                private float price_new;
                private String price_drop_time;
                private String sub_title;

                public float getPrice_new() {
                    return price_new;
                }

                public void setPrice_new(float price_new) {
                    this.price_new = price_new;
                }

                public String getPrice_drop_time() {
                    return price_drop_time;
                }

                public void setPrice_drop_time(String price_drop_time) {
                    this.price_drop_time = price_drop_time;
                }

                public String getSub_title() {
                    return sub_title;
                }

                public void setSub_title(String sub_title) {
                    this.sub_title = sub_title;
                }
            }

        }


    }

}
