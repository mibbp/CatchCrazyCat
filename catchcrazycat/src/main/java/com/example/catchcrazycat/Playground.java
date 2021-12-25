package com.example.catchcrazycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.Image;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Vector;

public class Playground extends SurfaceView implements View.OnTouchListener {//游戏背景棋盘


    private static int WIDTH=100;

    private static final int ROW=15;//定义行列数
    private static final int COL=15;
    private static final int Blocks=25;//路障数量
    boolean firstStep;

    private Dot matrix[][];//创建二维矩阵棋盘
    private Dot Cat;//猫


    public Playground(Context context) {//背景创建初始化
        super(context);
        getHolder().addCallback(callback);
        matrix=new Dot[ROW][COL];
        for (int i=0;i<ROW;i++){
            for (int j=0;j<COL;j++){
                matrix[i][j]=new Dot(j,i);
            }
        }
        setOnTouchListener(this);
        initGame();
    }

    private  Dot getDot(int x,int y){
        return matrix[y][x];
    }

    private  boolean isAtEdge(Dot d){//判断是否到边界
        if(d.getX()*d.getY()==0||d.getX()+1==COL||d.getY()+1==ROW)
            return true;
        return false;
    }

    private Dot getNeighbor(Dot one,int dir){//移动到相邻点
        switch(dir){
            case 1:
                return getDot(one.getX()-1,one.getY());
            case 2:
                if(one.getY()%2==0){
                    return getDot(one.getX()-1,one.getY()-1);
                }
                else{
                    return getDot(one.getX(),one.getY()-1);
                }
            case 3:
                if(one.getY()%2==0){
                    return getDot(one.getX(),one.getY()-1);
                }
                else{
                    return getDot(one.getX()+1,one.getY()-1);
                }
            case 4:
                return getDot(one.getX()+1,one.getY());
            case 5:
                if(one.getY()%2==0){
                    return getDot(one.getX(),one.getY()+1);
                }
                else{
                    return getDot(one.getX()+1,one.getY()+1);
                }
            case 6:
                if(one.getY()%2==0){
                    return getDot(one.getX()-1,one.getY()+1);
                }
                else{
                    return getDot(one.getX(),one.getY()+1);
                }
            default:
                break;
        }


        return null;
    }

    private int getDistance (Dot one ,int dir){
        int distance =1;
        if(isAtEdge(one))return distance;
//        if(one.getStatus()!=Dot.STATUS_IN)return -1;
        Dot ori =one,next;
        while(true){//BFS搜索距离（后面有时间的话再用A*优化）
//            distance++;
            next=getNeighbor(ori,dir);
            if(next.getStatus()==Dot.STATUS_ON){
                return distance*-1;
            }
            if(isAtEdge(next)){
                distance++;
                return distance;
            }
            distance++;
            ori=next;
        }

    }
    //移动操作
    private void MoveTo(Dot one){
        one.setStatus(Dot.STATUS_IN);
        getDot(Cat.getX(),Cat.getY()).setStatus(Dot.STATUS_OFF);
        Cat.setXY(one.getX(),one.getY());
    }
    //逃脱算法
    private void move(){
        if(isAtEdge(Cat)){
            lose();
            return ;
        }
        Vector <Dot> avaliable=new Vector<>();
        Vector <Dot> positive=new Vector<>();
        HashMap<Dot,Integer> al =new HashMap<>();

        for (int i = 1; i < 7; i++) {
            Dot n=getNeighbor(Cat,i);
            if(n.getStatus()==Dot.STATUS_OFF){
                avaliable.add(n);
                int Dis=getDistance(n,i);
                al.put(n,Dis);

//                System.out.println("x:"+n.getX()+" Y:"+n.getY()+" 方向："+i+" 距离:"+Dis);
                if(Dis>0)
                    positive.add(n);
            }
        }
        if(avaliable.size()==0){
            win();
        }
        else if(avaliable.size()==1){
            MoveTo((avaliable.get(0)));
        }
        else{
            Dot best=null;
            if(positive.size()!=0){//存在直接逃脱的方向
                int min=0x3f3f3f3f;
                for (int i = 0; i < positive.size(); i++) {
                    int a=al.get(positive.get(i));
                    if(a<min){
                        min=a;
                        best=positive.get(i);
                    }
                }
            }
            else{//所有方向都有路障
                int max=0;
                for (int i = 0; i < avaliable.size(); i++) {
                    int k=al.get(avaliable.get(i));
                    if(k<max){
                        max=k;
                        best=avaliable.get(i);
                    }
                }
            }
            MoveTo(best);
        }
        return ;
    }

    private void lose(){
        Toast toast=Toast.makeText(getContext(),"You Lose",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();


    }


    private void win(){
        Toast toast=Toast.makeText(getContext(),"You Win!",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);

        toast.show();
    }


    private void redraw(){
        Canvas c=getHolder().lockCanvas();
        c.drawColor(Color.LTGRAY);//绘制背景为灰色
        Toast.makeText(getContext(),"红色点表示神经猫，白色点表示空位，黄色点表示路障"+
                "游戏目的：阻止红点逃脱地图" +
                "游戏玩法：每回合玩家可以选择一个空位放置路障，神经猫则会移动一位",Toast.LENGTH_SHORT).show();
        Paint paint =new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < ROW; i++) {//绘制所有点
            for (int j = 0; j <COL ; j++) {
                int offset=0;
                if(j%2!=0){
                    offset=WIDTH/2;
                }
                Dot one = getDot(i,j);
                switch (one.getStatus()){
                    case Dot.STATUS_OFF:
                        paint.setColor(0xFFEEEEEE);
                        break;
                    case Dot.STATUS_ON:
                        paint.setColor(0xFFFFAA00);
                        break;
                    case Dot.STATUS_IN:
                        paint.setColor(0xFFFF0000);
                        break;
                    default:
                        break;

                }
                c.drawOval(new RectF(one.getX()*WIDTH+offset,one.getY()*WIDTH
                        ,(one.getX()+1)*WIDTH+offset,(one.getY()+1)*WIDTH),paint);
            }
        }

        getHolder().unlockCanvasAndPost(c);
    }

    SurfaceHolder.Callback callback =new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
            redraw();
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            WIDTH=i1/(COL+1);
            redraw();
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

        }
    };

    private void initGame(){//游戏初始化
        for (int i=0;i<ROW;i++){
            for(int j=0;j<COL;j++){
                matrix[i][j].setStatus(Dot.STATUS_OFF);
            }
        }
        Cat =new Dot(7,8);
        getDot(7,8).setStatus(Dot.STATUS_IN);
        for (int i = 0; i < Blocks; ) {
            int x=(int)((Math.random()*1000)%COL);
            int y=(int)((Math.random()*1000)%ROW);
            if(getDot(x,y).getStatus()==Dot.STATUS_OFF){
                getDot(x,y).setStatus(Dot.STATUS_ON);
                i++;
//                System.out.println("Block"+i);
            }
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent e) {//点击操作
        if(e.getAction()==MotionEvent.ACTION_UP){//判断是否点击
//            Toast.makeText(getContext(),e.getX()+":"+e.getY(),Toast.LENGTH_SHORT).show();
            int x,y;
            y=(int)(e.getY()/WIDTH);//根据点击奇偶行分别处理
            if(y%2==0){
                x=(int)(e.getX()/WIDTH);
            }
            else{
                x=(int)((e.getX()-WIDTH/2)/WIDTH);
            }

            //判断点击是否再边界内
            if(x+1>COL||y+1>ROW){//边界外则初始化
                initGame();
            }
            else if(getDot(x,y).getStatus()==Dot.STATUS_OFF){//判断是否为空点
                getDot(x,y).setStatus(Dot.STATUS_ON);
                move();
            }
            redraw();
        }
        return true;
    }
}
