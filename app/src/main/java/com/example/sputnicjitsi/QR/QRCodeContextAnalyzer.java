package com.example.sputnicjitsi.QR;

import androidx.annotation.NonNull;

import java.util.Objects;

public class QRCodeContextAnalyzer {

    public static boolean isWiFi(@NonNull String str) {
        return str.contains("WIFI:");
    }
    public static boolean isLogin(@NonNull String str) {
        return str.contains("L:");
    }

    private QrWifi qrWifi;
    private QrLogIn qrLogIn;

    public void addQr(String str){
        if(isWiFi(str)){
            QrWifi check = new QrWifi(str);
            if(!check.equals(qrWifi))
                qrWifi = new QrWifi(str);
        }
        if(isLogin(str)){
            QrLogIn check = new QrLogIn(str);
            if(!check.equals(qrLogIn))
                qrLogIn = new QrLogIn(str);
        }
    }

    public QrWifi getQrWifi() {
        return qrWifi;
    }

    public QrLogIn getQrLogIn() {
        return qrLogIn;
    }

    public class QrWifi{
        private final String ssid;
        private final String technology;
        private final String pass;
        private final boolean h;
        public QrWifi(@NonNull String str) {
            String[] strings, temp;
            strings = str.split(";");
            temp = strings[0].split("WIFI:S:");
            ssid = temp[1];
            temp = strings[1].split("T:");
            technology = temp[1];
            temp = strings[2].split("P:");
            pass = temp[1];
            temp = strings[3].split("H:");
            h = temp[1].equals("true");
        }

        public String getSsid() {
            return ssid;
        }

        public String getTechnology() {
            return technology;
        }

        public String getPass() {
            return pass;
        }

        public boolean isH() {
            return h;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            QrWifi qrWifi = (QrWifi) o;
            return h == qrWifi.h && Objects.equals(ssid, qrWifi.ssid) && Objects.equals(technology, qrWifi.technology) && Objects.equals(pass, qrWifi.pass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ssid, technology, pass, h);
        }
    }

    public class QrLogIn{
        private final String login;
        private final String pass;
        public QrLogIn(@NonNull String str) {
            String[] strings, temp;
            strings = str.split(";");
            temp = strings[0].split("L:");
            login = temp[1];
            temp = strings[1].split("P:");
            pass = temp[1];
        }
        public String getLogin() {
            return login;
        }
        public String getPass() {
            return pass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            QrLogIn qrLogIn = (QrLogIn) o;
            return Objects.equals(login, qrLogIn.login) && Objects.equals(pass, qrLogIn.pass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(login, pass);
        }
    }
}

