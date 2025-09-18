package com.fit2cloud.itsm.common.constants;

import java.util.Objects;

public class ApiSupportVersion {

    public enum YOUYUN {
        V1("V1.0"),;

        YOUYUN(String version) {
            this.version = version;
        }

        private final String version;

        public String getVersion() {
            return version;
        }
    }

    public enum YOUWEI {
        V3("V3.0"),;

        YOUWEI(String version) {
            this.version = version;
        }

        private final String version;

        public String getVersion() {
            return version;
        }
    }

    public enum ULTRAPOWER {
        V1("V3.2"),;

        ULTRAPOWER(String version) {
            this.version = version;
        }

        private final String version;

        public String getVersion() {
            return version;
        }
    }

    public enum CLOUDWISE {
        V2("V2"),;

        CLOUDWISE(String version) {
            this.version = version;
        }

        private final String version;

        public String getVersion() {
            return version;
        }
    }

    public enum LANDRAY {
        V1("V1"),;

        LANDRAY(String version) {
            this.version = version;
        }

        private final String version;

        public String getVersion() {
            return version;
        }
    }

    public enum LANJINGCOMPANY {
        V1("V2.0.0"),
        V2_Custom("V2-Custom"),;

        LANJINGCOMPANY(String version) {
            this.version = version;
        }

        private final String version;

        public String getVersion() {
            return version;
        }
    }

    public enum LANJING {
        V1("V3"),;

        LANJING(String version) {
            this.version = version;
        }

        private final String version;

        public String getVersion() {
            return version;
        }
    }

    public enum LENOVO {
        V2("V2.0"),;

        LENOVO(String version) {
            this.version = version;
        }

        private final String version;

        public String getVersion() {
            return version;
        }
    }

    public enum WEAVER {
        V3_1_0("V3.1.0"),;

        WEAVER(String version) {
            this.version = version;
        }

        private final String version;

        public String getVersion() {
            return version;
        }
    }

    public enum BKCompany {
        V2("V2"),;

        BKCompany(String version) {
            this.version = version;
        }

        private final String version;

        public String getVersion() {
            return version;
        }
    }

}