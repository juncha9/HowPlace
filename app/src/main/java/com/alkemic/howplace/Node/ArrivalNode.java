package com.alkemic.howplace.Node;

import android.util.Log;

public class ArrivalNode {
    // 해당 역에 도착예정인 열차
    int trainID = -1; //열차번호
    String upDown = ""; //상행, 하행
    String lineName = ""; //열차 방향
    String arrivalLeft = ""; //열차 도착까지 남은시간
    String curStation = ""; //열차의 현재위치


    public int getTrainID() {
        return trainID;
    }

    public String getUpDown() {
        return upDown;
    }

    public String getLineName() {
        return lineName;
    }

    public String getArrivalLeft() {
        return arrivalLeft;
    }

    public String getCurStation() {
        return curStation;
    }


    public void Logging()
    {
        Log.d("ArrivalNode>","-- SubwayInformation --");
        Log.d("ArrivalNode>","trainID:"+Integer.toString(trainID));
        Log.d("ArrivalNode>","arrivalLeft:"+arrivalLeft);
    }

    private ArrivalNode(builder builder) {
        this.trainID = builder.trainId;
        this.upDown = builder.upDown;
        this.lineName = builder.lineName;
        this.arrivalLeft = builder.arrivalLeft;
        this.curStation = builder.curStation;
    }

    public static final class builder
    {
        int trainId = -1;
        String upDown = ""; //상행, 하행
        String lineName = "";
        String arrivalLeft = "";
        String curStation = "";

        public builder() {

        }

        public builder setTrainId(int trainID) {
            if(trainID >= 0)
                this.trainId = trainID;
            return this;
        }

        public builder setUpDown(String upDown) {
            if(upDown != null)
                this.upDown = upDown;
            return this;
        }

        public builder setLineName(String lineName) {
            if(lineName != null)
                this.lineName = lineName;
            return this;
        }

        public builder setArrivalLeft(String arrivalLeft) {
            if(arrivalLeft != null)
                this.arrivalLeft = arrivalLeft;
            return this;
        }

        public builder setCurStation(String curStation) {
            if(curStation != null)
                this.curStation = curStation;
            return this;
        }

        public ArrivalNode build()
        {
            return new ArrivalNode(this);
        }
    }
}
