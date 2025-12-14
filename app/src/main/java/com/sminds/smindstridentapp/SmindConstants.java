package com.sminds.smindstridentapp;

public class SmindConstants {
    // web service url constants
    public class ServiceType {
        //public static final String BASE_URL = "https://sminds-ghi-v2.dyndns.org/extensions/";

        public static final String BASE_URL = "http://13.203.146.186/extensions/";
        //public static final String BASE_URL = "http://35.154.103.200/extensions/";
        //public static final String BASE_URL = "http://35.154.103.200/sminds_ghi_mobile_app/";
        //public static final String BASE_URL = "http://smirp.dyndns.org:81/smi_inquiry/test_gh/";
        public static final String LOGIN = BASE_URL + "simplelogin.php";
        public static final String PARAM_NEW_SALES_ORDER = BASE_URL + "new_sales_order.php";
        public static final String PARAM_CUSTOMER_LIST = BASE_URL + "select_customer_list.php";
        public static final String PARAM_COMPO_LIST = BASE_URL + "select_compo_list.php";
        //public static final String LOGIN = BASE_URL + "/app_login";
    }
    // webservice key constants
    public class Params {

        public static final String NAME = "name";
        //public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
    }

}
