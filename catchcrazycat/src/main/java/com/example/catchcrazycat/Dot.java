package com.example.catchcrazycat;

public class Dot {//落点

    int x,y;//坐标
    int status;//当前棋子状态

    public static final int STATUS_ON=1;//路障
    public static final int STATUS_OFF=0;//空位
    public static final int STATUS_IN=9;//猫

    public Dot(int x, int y) {//有参构造初始化坐标和状态
        this.x = x;
        this.y = y;
        status=STATUS_OFF;
    }
    //getter，setter
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setXY(int x,int y) {
        this.x=x;
        this.y = y;
    }

}
